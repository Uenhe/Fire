package fire.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
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

    private float timer, damageSum;
    private boolean regenable;
    private static final IntIntMap bulletMap = new IntIntMap();
    private static final Rand rand = new Rand();
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
    public void update(Unit unit){
        if(rotateSpeed > 0.0f)
            rotation += rotateSpeed * Time.delta;

        if(unit.shield < max && timer == 0.0f){
            // regen shield to full after cooldown
            if(unit.shield < 0.0f && unit.shield + regen * Time.delta > 0.0f)
                unit.shield = max;
            else
                unit.shield += regen * Time.delta;
        }

        alpha = Math.max(alpha - Time.delta / 10.0f, 0.0f);
        wasBroken = unit.shield <= 0.0f;

        if(!wasBroken){
            radiusScale = Mathf.lerpDelta(radiusScale, 1.0f, 0.08f);
            Groups.bullet.intersect(unit.x - realRad(), unit.y - realRad(), realRad() * 2.0f, realRad() * 2.0f, bullet -> {
                if(bullet.team != unit.team && bullet.type.absorbable && Intersector.isInRegularPolygon(sides, unit.x, unit.y, realRad(), rotation, bullet.x, bullet.y)){
                    Fx.absorb.at(bullet);

                    boolean collision = true;
                    if(bullet.vel.len() > 0.1f && bullet.type.reflectable && Mathf.chance(chanceDeflect / bullet.damage)){
                        bullet.trns(-bullet.vel.x, -bullet.vel.y);

                        if(Math.abs(unit.x - bullet.x) > Math.abs(unit.y - bullet.y))
                            bullet.vel.x *= -1.0f;
                        else
                            bullet.vel.y *= -1.0f;

                        bullet.owner = unit;
                        bullet.team = unit.team;
                        bullet.time += 1.0f;
                        collision = false;
                    }

                    bullet.absorb();
                    unit.shield -= collision ? bullet.damage : bullet.damage * 2.0f;
                    alpha = 1.0f;

                    if(unit.shield <= 0.0f){
                        wasBroken = true;
                        regenable = false;
                        radiusScale = 0.0f;
                        unit.shield -= cooldown * regen;
                        Fx.shieldBreak.at(unit.x, unit.y, radius, unit.team.color, unit);

                        Sounds.spark.at(unit, Mathf.random(0.45f, 0.55f));
                        for(byte i = 0; i < lightningAmount; i++)
                            Lightning.create(unit.team, lightningColor, lightningDamage, unit.x, unit.y, i * (360.0f / lightningAmount), lightningLength);
                    }
                }
            });

        }else if(extended && !regenable){
            boolean isOverloaded = damageSum >= unit.maxHealth * 0.4f;

            timer += Time.delta * (isOverloaded ? 1.3333f : 1.0f);

            if(node.checkBelonging(timer, 0)){
                Groups.bullet.intersect(unit.x - radius, unit.y - radius, radius * 2.0f, radius * 2.0f, bullet -> {
                    if(
                        !isOverloaded
                        && bullet.team != unit.team && bullet.type.absorbable && bullet.type.hittable
                        && !(bullet.type instanceof LiquidBulletType)
                        && Intersector.isInRegularPolygon(sides, unit.x, unit.y, radius, rotation, bullet.x, bullet.y)
                    ){
                        bullet.owner = unit;
                        bullet.team = unit.team;
                        bullet.damage *= 0.7f;
                        bullet.time = 0.0f;
                        bullet.lifetime = node.last();

                        if(bulletMap.containsKey(bullet.type.id)){
                            bullet.type = content.bullet(bulletMap.get(bullet.type.id));

                        }else{
                            var type = bullet.type.copy();

                            // make artillery collides building
                            type.collidesTiles = type.collides = true;

                            // add a default trail to bullets that without a trail
                            if(type.trailLength <= 0){
                                if(type instanceof BasicBulletType bt){
                                    type.trailWidth = bt.width * 0.22f;
                                    type.trailLength = (int)(bt.height / 2);
                                    type.trailColor = bt.backColor;
                                }else{
                                    type.trailLength = 12;
                                }
                            }

                            bulletMap.put(bullet.type.id, type.id);
                            bullet.type = type;
                        }

                        damageSum += bullet.damage;

                        bullet.mover = b -> {
                            if(b.time >= 100.0f * Mathf.PI) return;

                            rand.setSeed(b.type.id * 2L);
                            final float rotSpeed = 0.2f, vel = 12.0f;

                            float scl = Math.min(1.0f - ((timer - node.get(1)) / 60.0f), 1.0f);
                            float x = Mathf.cos(timer * rotSpeed * rand.random(0.7f, 1.3f)) * radius * scl + unit.x;
                            float y = Mathf.sin(timer * rotSpeed * rand.random(0.7f, 1.3f)) * radius * scl + unit.y;
                            float dst = Math.min(Mathf.dst(x, y, b.x, b.y), radius);
                            float range = b.type.speed * b.type.lifetime;

                            if(timer <= node.get(1)){
                                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(x, y), 600.0f));
                                b.vel.setLength(6.0f + 2.0f * timer / 240.0f * (1.0f + 2.0f * dst / radius)); // velocity terminal = 8, has an extra boost depending on dst(bullet, targetPos) up to 300%

                            }else if(node.checkBelonging(timer, 2)){
                                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(x, y), 600.0f));
                                b.vel.setLength(8.0f * dst / radius); // use velocity terminal here

                            }else{
                                // to check whether bullet is fired
                                b.time = 100.0f * Mathf.PI;
                                b.lifetime = 100.0f * Mathf.PI + range / vel;

                                var target =
                                    b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && b.type.collidesGround && !b.hasCollided(b.aimTile.build.id)
                                        ? b.aimTile.build
                                        : Units.closestTarget(b.team, b.x, b.y, range,
                                        u -> u != null && u.checkTarget(b.type.collidesAir, b.type.collidesGround) && !b.hasCollided(u.id),
                                        t -> t != null && b.type.collidesGround && !b.hasCollided(t.id));

                                b.set(unit.x, unit.y);
                                if(target != null && Mathf.chance(0.48)){
                                    final float mag = 5.0f;
                                    final float velTer = vel * 1.16f;

                                    b.vel.setLength(velTer);
                                    b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), 600.0f));
                                    b.mover = bb -> bb.moveRelative(0.0f, Mathf.cos(b.time * velTer * Mathf.PI / Mathf.dst(unit.x, unit.y, target.x(), target.y())) * mag * Mathf.sign(b.id % 2 == 0));

                                }else{
                                    float angle = Mathf.random(360.0f);

                                    if(target != null)
                                        while(Angles.near(angle, Angles.angle(unit.x, unit.y, target.x(), target.y()), 30.0f))
                                            angle = Mathf.random(360.0f);

                                    b.initVel(angle, vel);
                                }

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
        lightningLength = lightningDamage = lightningAmount = 0;
        chanceDeflect = rotateSpeed = timer = damageSum = 0.0f;
        extended = regenable = false;
        lightningColor = null;
    }

    private float realRad(){
        return radius * radiusScale;
    }
}
