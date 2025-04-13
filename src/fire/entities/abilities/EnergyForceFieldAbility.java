package fire.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.scene.ui.layout.Table;
import arc.struct.IntIntMap;
import arc.util.Strings;
import arc.util.Time;
import fire.FRUtils;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;

import static mindustry.Vars.content;

public class EnergyForceFieldAbility extends mindustry.entities.abilities.ForceFieldAbility{

    /** @see mindustry.entities.bullet.LightningBulletType#calculateRange() */
    public short lightningLength;
    public byte lightningAmount;
    public short lightningDamage;
    public float chanceDeflect;
    public float rotateSpeed;
    public Color lightningColor = Color.clear;

    public boolean extended;
    public byte ext_bearingFactorPercentage; //a value of 50 -> 50%
    public byte ext_counterBulletDamageFactorPercentage;
    public byte ext_counterBulletHomingChancePercentage;

    private float timer, damageSum;
    private boolean regenable;
    private static final IntIntMap bulletMap = new IntIntMap();
    /** Stop collecting; Charge; Fire; Clean. */
    private static final FRUtils.TimeNode node = new FRUtils.TimeNode(180, 240, 300, 310);

    public EnergyForceFieldAbility(float radius, float regen, float max, float cooldown, int length, int amount, int damage, float chance){
        super(radius, regen, max, cooldown);
        lightningLength = (short)length;
        lightningAmount = (byte)amount;
        lightningDamage = (short)damage;
        chanceDeflect = chance;
    }

    @Override
    public String getBundle(){
        return "ability.fire-energyforcefield";
    }

    @Override
    public void addStats(Table t){
        super.addStats(t);
        t.row().add(abilityStat("deflectchance", chanceDeflect)).row();
        t.add(abilityStat("lightningdamage", lightningDamage)).row();
        t.add(abilityStat("lightninglength", Strings.autoFixed(lightningLength * 0.75f, 1))).row();
        t.add(abilityStat("lightningamount", lightningAmount));
        if(extended) t.row().add(Core.bundle.get(getBundle() + ".extended")).wrap().width(descriptionWidth);
    }

    @Override
    public void displayBars(Unit unit, Table bars){
        bars.add(new Bar(
            "stat.shieldhealth",
            wasBroken ? Color.gray : Pal.accent,
            () -> wasBroken ? 1.0f + unit.shield / (regen * cooldown) : unit.shield / max)
        ).row();
    }

    @Override
    public void update(Unit u){
        if(rotateSpeed > 0.0f)
            rotation += rotateSpeed * Time.delta;

        if(u.shield < max && timer == 0.0f){
            // regen shield to full after cooldown
            if(u.shield < 0.0f && u.shield + regen * Time.delta > 0.0f)
                u.shield = max;
            else
                u.shield += regen * Time.delta;
        }

        alpha = Math.max(alpha - Time.delta / 10.0f, 0.0f);
        wasBroken = u.shield <= 0.0f;

        if(!wasBroken){
            radiusScale = Mathf.lerpDelta(radiusScale, 1.0f, 0.08f);
            Groups.bullet.intersect(u.x - realRad(), u.y - realRad(), realRad() * 2.0f, realRad() * 2.0f, bullet -> {
                if(bullet.team != u.team && bullet.type.absorbable && Intersector.isInRegularPolygon(sides, u.x, u.y, realRad(), rotation, bullet.x, bullet.y)){
                    Fx.absorb.at(bullet);

                    boolean collision = true;
                    if(bullet.vel.len() > 0.1f && bullet.type.reflectable && Mathf.chance(chanceDeflect / bullet.damage)){
                        bullet.trns(-bullet.vel.x, -bullet.vel.y);

                        if(Math.abs(u.x - bullet.x) > Math.abs(u.y - bullet.y))
                            bullet.vel.x *= -1.0f;
                        else
                            bullet.vel.y *= -1.0f;

                        bullet.owner = u;
                        bullet.team = u.team;
                        bullet.time += 1.0f;
                        collision = false;
                    }

                    bullet.absorb();
                    u.shield -= collision ? bullet.damage : bullet.damage * 2.0f;
                    alpha = 1.0f;

                    if(u.shield <= 0.0f){
                        wasBroken = true;
                        regenable = false;
                        radiusScale = 0.0f;
                        u.shield -= cooldown * regen;
                        Fx.shieldBreak.at(u.x, u.y, radius, u.team.color, u);

                        Sounds.spark.at(u, Mathf.random(0.45f, 0.55f));
                        for(byte i = 0; i < lightningAmount; i++)
                            Lightning.create(u.team, lightningColor, lightningDamage, u.x, u.y, i * (360.0f / lightningAmount), lightningLength);
                    }
                }
            });

        }else if(extended && !regenable){
            final boolean isOverloaded = damageSum >= u.maxHealth * ext_bearingFactorPercentage * 0.01f;
            timer += Time.delta * (isOverloaded ? 1.3f : 1.0f);

            if(node.checkBelonging(timer, 0)){
                Groups.bullet.intersect(u.x - radius, u.y - radius, radius * 2.0f, radius * 2.0f, bullet -> {
                    if(
                        !isOverloaded && Intersector.isInRegularPolygon(sides, u.x, u.y, radius, rotation, bullet.x, bullet.y)
                        && bullet.team != u.team && bullet.type.absorbable && bullet.type.hittable
                        && !(bullet.type instanceof LiquidBulletType)
                    ){
                        bullet.owner = u;
                        bullet.team = u.team;
                        bullet.damage *= ext_counterBulletDamageFactorPercentage * 0.01f;
                        bullet.time = 0.0f;
                        bullet.lifetime = node.last();
                        bullet.drag = 0.0f;

                        if(bulletMap.containsValue(bullet.type.id)){
                            bullet.type = content.bullet(bulletMap.get(bullet.type.id));

                        }else{
                            var type = bullet.type.copy();

                            // make artillery collides building
                            type.collidesTiles = type.collides = true;

                            // pierce here is just annoying
                            type.pierce = type.pierceBuilding = false;

                            // handled by mover later
                            type.drag = 0.0f;

                            // add a default trail to bullets that without a trail
                            if(type.trailLength <= 0){
                                if(type instanceof BasicBulletType bt){
                                    type.trailWidth = bt.width * 0.21f;
                                    type.trailLength = (int)(bt.height / 2);
                                    type.trailColor = bt.backColor;
                                }else{
                                    type.trailLength = 11;
                                }
                            }

                            bulletMap.put(bullet.type.id, type.id);
                            bullet.type = type;
                        }

                        damageSum += bullet.damage;
                        bullet.mover = b -> {
                            if(b.type == null) return;
                            if(u.dead) b.remove();

                            if(node.checkBelonging(timer, 0, 2)){
                                final float
                                    scale =
                                        node.checkBelonging(timer, 0) ? timer / node.first()
                                        : node.checkBelonging(timer, 1) ? 1.0f
                                        : node.checkBelonging(timer, 2) ? 1.0f - (timer - node.get(1)) / node.getQuantum(2)
                                        : 0.0f,
                                    realrad = radius * scale,
                                    val = u.angleTo(b) + b.type.speed * Time.delta / realrad * Mathf.radDeg;

                                b.vel.setLength(b.type.speed * scale);
                                b.rotation(b.angleTo(u.x + Mathf.cosDeg(val) * realrad, u.y + Mathf.sinDeg(val) * realrad));

                            }else if(b.lifetime != 60.01f){
                                b.time = 0.0f;
                                b.lifetime = 60.01f;
                                b.vel.setLength(8.0f);
                                b.set(u.x, u.y);

                                var tgt =
                                    b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && b.type.collidesGround && !b.hasCollided(b.aimTile.build.id)
                                        ? b.aimTile.build
                                        : Units.closestTarget(b.team, b.x, b.y, b.lifetime * b.vel.len(),
                                            e -> e != null && e.checkTarget(b.type.collidesAir, b.type.collidesGround) && !b.hasCollided(e.id),
                                            t -> t != null && b.type.collidesGround && !b.hasCollided(t.id));

                                final boolean homing = tgt != null && Mathf.chance(ext_counterBulletHomingChancePercentage * 0.01);
                                final float theta = homing ? 0.0f : Mathf.random(Mathf.PI2);
                                if(tgt != null && homing) b.rotation(b.angleTo(tgt));
                                b.mover = bb -> bb.moveRelative(0.0f, Mathf.cos(
                                    b.time,
                                    (tgt != null
                                        ? u.dst(homing ? tgt.x() : u.x + u.dst(tgt) * Mathf.cos(theta), homing ? tgt.y() : u.y + u.dst(tgt) * Mathf.sin(theta))
                                        : radius
                                    ) / b.vel.len() / Mathf.PI,
                                    Mathf.sign(b.id % 2 == 0) * 5.0f
                                ));

                                damageSum -= bullet.damage;
                            }
                        };
                    }
                });

            }else if(timer > node.get(3)){
                regenable = true;
                timer = 0.0f;
                damageSum = 0.0f;
            }
        }
    }

    @Override
    public void death(Unit unit){
        lightningLength = lightningDamage = lightningAmount = ext_bearingFactorPercentage = ext_counterBulletDamageFactorPercentage = 0;
        chanceDeflect = rotateSpeed = timer = damageSum = 0.0f;
        extended = regenable = false;
        lightningColor = null;
    }

    private float realRad(){
        return radius * radiusScale;
    }
}
