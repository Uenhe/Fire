package fire.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.scene.ui.layout.Table;
import arc.struct.IntIntMap;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import fire.FRUtils;
import mindustry.ai.types.CommandAI;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;

import static mindustry.Vars.content;

public class EnergyForceFieldAbility extends mindustry.entities.abilities.ForceFieldAbility{

    /** @see mindustry.entities.bullet.LightningBulletType#calculateRange() */
    public final short lightningLength;
    public final byte lightningAmount;
    public final short lightningDamage;
    public final float chanceDeflect;
    public float rotateSpeed;
    public final Color lightningColor = new Color();

    public boolean extended;
    public float ext_bearingFactor;
    public float ext_counterBulletSpeedFactor;
    public float ext_counterBulletDamageFactor;
    /** Length = 4 -> Collect; Stop collecting; Charge; Homing fire; Random fire. */
    public FRUtils.TimeNode ext_node;

    private float timer;
    private boolean regenable;
    private Seq<Bullet> bullets = new Seq<>(false);

    private static final IntIntMap bulletMap = new IntIntMap();

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
        bars.add(new Bar("stat.shieldhealth", wasBroken ? Color.gray : Pal.accent,
            () -> wasBroken ? 1.0f + unit.shield / (regen * cooldown) : unit.shield / max)
        ).row();
    }

    @Override
    public void update(Unit u){
        if(rotateSpeed > 0.0f)
            rotation += rotateSpeed * Time.delta;

        if(u.shield < max && timer == 0.0f){
            //regen shield to full after cooldown
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
                    u.shield -= bullet.type.shieldDamage(bullet) * (collision ? 1.0f : 2.0f);
                    alpha = 1.0f;

                    if(u.shield <= 0.0f){
                        wasBroken = true;
                        regenable = false;
                        radiusScale = 0.0f;
                        u.shield -= cooldown * regen;
                        Fx.shieldBreak.at(u.x, u.y, radius, u.team.color, u);

                        Sounds.spark.at(u, Mathf.random(0.45f, 0.55f));
                        for(int i = 0, lightningAmount = this.lightningAmount; i < lightningAmount; i++)
                            Lightning.create(u.team, lightningColor, lightningDamage, u.x, u.y, i * (360.0f / lightningAmount), lightningLength);
                    }
                }
            });

        }else if(extended && !regenable){
            float sum = 0.0f;
            boolean isOverloaded = false;
            var bullets = this.bullets;
            for(var b : bullets){
                if(b.type == null) continue;
                sum += b.type.shieldDamage(b);
                if(sum < u.maxHealth * ext_bearingFactor) continue;
                isOverloaded = true;
                break;
            }

            timer += Time.delta * (isOverloaded ? 1.2f : 1.0f);

            if(!isOverloaded && ext_node.checkBelonging(timer, 0)){
                Groups.bullet.intersect(u.x - radius, u.y - radius, radius * 2.0f, radius * 2.0f, bullet -> {
                    if(Intersector.isInRegularPolygon(sides, u.x, u.y, radius, rotation, bullet.x, bullet.y)
                        && bullet.time > 15.0f && bullet.team != u.team && bullet.type.absorbable && bullet.type.hittable && !(bullet.type instanceof LiquidBulletType)
                        && !bullets.contains(bullet)
                    ){
                        final float tt = 36.01f;
                        bullet.owner = u;
                        bullet.team = u.team;
                        bullet.damage *= ext_counterBulletDamageFactor;
                        bullet.time = 0.0f;
                        bullet.lifetime = ext_node.last() + tt;

                        if(bulletMap.containsValue(bullet.type.id)){
                            bullet.type = content.bullet(bulletMap.get(bullet.type.id));

                        }else{
                            var type = bullet.type.copy();

                            //make artillery collides building
                            type.collidesTiles = type.collides = true;
                            type.buildingDamageMultiplier *= 1.6f;

                            //pierce here is just annoying
                            type.pierce = type.pierceBuilding = false;

                            //handled by mover later
                            type.drag = 0.0f;

                            //add a default trail to bullet that doesn't have one
                            if(type.trailLength <= 0){
                                if(type instanceof BasicBulletType bt){
                                    type.trailWidth = bt.width * 0.21f;
                                    type.trailLength = (int)(bt.height * 0.5f);
                                    type.trailColor = bt.backColor;
                                }else{
                                    type.trailLength = 11;
                                }
                            }

                            bulletMap.put(bullet.type.id, type.id);
                            bullet.type = type;
                            bullets.add(bullet);
                        }

                        bullet.mover = b -> {
                            if(b.type == null) return;
                            if(!u.isValid()){
                                bullets.remove(b);
                                b.remove();
                                return;
                            }

                            if(ext_node.checkBelonging(b.time, 0, 2) && timer < ext_node.last() + 54.0f){
                                float scale = ext_node.checkBelonging(timer, 0) ? timer / ext_node.first()
                                            : ext_node.checkBelonging(timer, 1) ? 1.0f
                                            : 1.0f - (timer - ext_node.get(1)) / ext_node.getQuantum(2),
                                    realrad = radius * scale,
                                    val = u.angleTo(b) + b.type.speed * Time.delta / realrad * Mathf.radDeg;

                                b.vel.setLength(b.type.speed * scale * ext_counterBulletSpeedFactor);
                                b.rotation(b.angleTo(u.x + Mathf.cosDeg(val) * realrad, u.y + Mathf.sinDeg(val) * realrad));

                            }else{
                                float mark = ext_node.last() + tt + 0.1f;
                                if(b.lifetime != mark){
                                    var target = u.controller() instanceof CommandAI c && c.attackTarget != null
                                        ? c.attackTarget
                                        : b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && b.type.collidesGround && !b.hasCollided(b.aimTile.build.id)
                                            ? b.aimTile.build
                                            : Units.closestTarget(b.team, b.x, b.y, radius * 2.0f,
                                                e -> e != null && e.checkTarget(b.type.collidesAir, b.type.collidesGround) && !b.hasCollided(e.id),
                                                t -> t != null && b.type.collidesGround && !b.hasCollided(t.id));

                                    if(target != null && timer < ext_node.last() + 24.0f && ext_node.checkBelonging(b.time, 3)){
                                        b.lifetime = mark;
                                        b.vel.setLength(8.0f * ext_counterBulletSpeedFactor);
                                        b.rotation(b.angleTo(target));
                                        b.mover = bb -> bb.moveRelative(0.0f, Mathf.cos(b.time - ext_node.get(2), u.dst(target.getX(), target.getY()) / b.vel.len() / Mathf.PI, Mathf.sign(b.id % 2 == 0) * 5.0f));

                                    }else{
                                        b.lifetime = mark;
                                        b.vel.rnd(6.0f * ext_counterBulletSpeedFactor);
                                        b.mover = bb -> bb.vel.scl(Math.max(1.0f - 0.024f * Time.delta, 0.0f));
                                    }
                                }
                            }
                        };
                    }
                });

            }else if(timer > ext_node.first() + ext_node.last()){
                regenable = true;
                timer = 0.0f;
                bullets.clear();
            }
        }
    }

    private float realRad(){
        return radius * radiusScale;
    }

    private EnergyForceFieldAbility setBullets(Seq<Bullet> seq){
        bullets = seq;
        return this;
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        return ((EnergyForceFieldAbility)super.clone()).setBullets(bullets);
    }
}
