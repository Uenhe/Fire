package fire.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import fire.world.meta.FireStat;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class EnergyForceFieldAbility extends mindustry.entities.abilities.ForceFieldAbility{

    /** see {@link mindustry.entities.bullet.LightningBulletType#calculateRange()} */
    private final short lightningLength;
    private final short lightningAmount;
    private final float lightningDamage;
    private final float chanceDeflect;
    protected float rotateSpeed = -1f;
    protected Color lightningColor = Pal.surge;
    protected boolean unlocks;

    private float timer;
    private boolean broken, canRegen;
    private final Seq<Bullet> bullets = new Seq<>();

    public EnergyForceFieldAbility(float radius, float regen, float max, float cooldown, float chance, float damage, int length, int amount){
        super(radius, regen, max, cooldown);
        chanceDeflect = chance;
        lightningDamage = damage;
        lightningLength = (short)length;
        lightningAmount = (short)amount;
    }

    @Override
    public String localized(){
        return Core.bundle.get("ability.fire-energyforcefield");
    }

    @Override
    public void addStats(Table t){
        super.addStats(t);
        t.add("[lightgray]" + Stat.baseDeflectChance.localized() + ": [white]" + Strings.autoFixed(chanceDeflect, 2));
        t.row();
        t.add("[lightgray]" + Stat.lightningDamage.localized() + ": [white]" + Strings.autoFixed(lightningDamage, 2));
        t.row();
        t.add("[lightgray]" + FireStat.lightningLength.localized() + ": [white]" + Strings.autoFixed(lightningLength * 0.75f, 2) + " " + StatUnit.blocks.localized());
        t.row();
        t.add("[lightgray]" + FireStat.lightningAmount.localized() + ": [white]" + Strings.autoFixed(lightningAmount, 2));
        t.row();
    }

    @Override
    public void displayBars(Unit unit, Table bars){
        bars.add(new Bar(
            "stat.shieldhealth",
            broken ? Color.gray : Pal.accent,
            () -> broken ? -unit.shield / (regen * cooldown) : unit.shield / max)
        ).row();
    }

    @Override
    public void update(Unit unit){
        alpha = Math.max(alpha - Time.delta / 10f, 0f);
        broken = unit.shield <= 0f;

        if(rotateSpeed > 0f){
            rotation += rotateSpeed * Time.delta;
            rotation %= 360f;
        }
        if(unit.shield < max && timer == 0f){
            unit.shield += regen * Time.delta;
        }

        if(!broken){
            radiusScale = Mathf.lerpDelta(radiusScale, 1f, 0.06f);
            Groups.bullet.intersect(unit.x - realRad(), unit.y - realRad(), realRad() * 2f, realRad() * 2f, bullet -> {
                if(bullet.team != unit.team && bullet.type.absorbable && Intersector.isInRegularPolygon(sides, unit.x, unit.y, realRad(), rotation, bullet.x, bullet.y)){
                    boolean collision = true;
                    Fx.absorb.at(bullet);

                    if(bullet.vel.len() > 0.1f && bullet.type.reflectable && Mathf.chance(chanceDeflect / bullet.damage)){

                        bullet.trns(-bullet.vel.x, -bullet.vel.y);

                        if(Math.abs(unit.x - bullet.x) > Math.abs(unit.y - bullet.y)){
                            bullet.vel.x *= -1f;
                        }else{
                            bullet.vel.y *= -1f;
                        }

                        bullet.owner = unit;
                        bullet.team = unit.team;
                        bullet.time += 1f;
                        collision = false;
                    }

                    float dmg = bullet.damage;
                    if(!collision){
                        dmg *= 2f;
                    }
                    bullet.absorb();
                    unit.shield -= dmg;
                    alpha = 1f;

                    if(unit.shield <= 0f){

                        broken = true;
                        canRegen = false;
                        radiusScale = 0f;
                        unit.shield -= cooldown * regen;
                        Fx.shieldBreak.at(unit.x, unit.y, radius, unit.team.color, unit);

                        Sounds.spark.at(unit, Mathf.random(0.45f, 0.55f));
                        for(short i = 0; i < lightningAmount; i++){
                            Lightning.create(unit.team, lightningColor, lightningDamage, unit.x, unit.y, (i - 1) * (360f / lightningAmount), lightningLength);
                        }
                    }
                }
            });

        }else{
            if(unlocks && !canRegen){
                timer += Time.delta;

                final float scl = Math.min(1f - ((timer - 240f) / 60f), 1f);
                final float speed = 0.2f;
                final float
                    x = Mathf.cos(timer * speed) * radius * scl + unit.x,
                    y = Mathf.sin(timer * speed) * radius * scl + unit.y;

                if(timer <= 180f){

                    Groups.bullet.intersect(unit.x - radius, unit.y - radius, radius * 2f, radius * 2f, bullet -> {
                        if(
                            bullets.sumf(b -> b.damage) < unit.maxHealth * 0.5f
                            && bullet.team != unit.team && bullet.type.absorbable && bullet.type.hittable
                            && Intersector.isInRegularPolygon(sides, unit.x, unit.y, radius, rotation, bullet.x, bullet.y)
                        ){
                            bullet.owner = unit;
                            bullet.team = unit.team;
                            bullet.time = 0f;
                            bullet.lifetime = 310f;

                            final var type = bullet.type.copy();

                            // make artillery collides building
                            type.collidesTiles = type.collides = true;

                            // add a default trail to bullets without a trail
                            if(type.trailLength <= 0){
                                if(type instanceof final BasicBulletType bt){
                                    type.trailWidth = bt.width * 0.22f;
                                    type.trailLength = (int)(bt.height * 0.5f);
                                    type.trailColor = bt.backColor;
                                }else{
                                    type.trailLength = 12;
                                }
                            }

                            bullet.type = type;
                            bullets.add(bullet);
                        }
                    });

                }else if(timer > 310f){
                    canRegen = true;
                    timer = 0f;
                    bullets.clear();
                }

                bullets.each(b -> {

                    if(b.type != null){

                        final float dst = Math.min(Mathf.dst(x, y, b.x, b.y), radius);
                        final float range = b.type.speed * b.type.lifetime;
                        final float v = 12f;

                        if(timer <= 240f){
                            b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(x, y), 80f * Time.delta));
                            b.vel.setLength(5f + 3f * timer / 240f * (1f + dst / radius)); // velocity terminal = 8, has a boost depending on distance(b, dest) up to 200%

                        }else if(timer > 240f && timer <= 300f){
                            b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(x, y), 80f * Time.delta));
                            b.vel.setLength(8f * dst / radius); // use velocity terminal here

                        }else{
                            b.time = 0f;
                            b.lifetime = range / v;

                            Teamc target;

                            if(b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && b.type.collidesGround && !b.hasCollided(b.aimTile.build.id)){
                                target = b.aimTile.build;
                            }else{
                                target = Units.closestTarget(
                                    b.team, b.x, b.y, range,
                                    u -> u != null && u.checkTarget(b.type.collidesAir, b.type.collidesGround) && !b.hasCollided(u.id),
                                    t -> t != null && b.type.collidesGround && !b.hasCollided(t.id));
                            }

                            b.set(unit.x, unit.y);
                            if(target != null && Mathf.chance(0.6)){
                                final float mag = 5f;
                                final float vt = v * 1.15f;

                                b.vel.setLength(vt);
                                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), 1000f));
                                b.mover = bullet -> bullet.moveRelative(0f, (float)(Math.cos(b.time * vt * Math.PI / Mathf.dst(unit.x, unit.y, target.x(), target.y())) * mag * Mathf.sign(b.id % 2 == 0)));

                            }else{
                                float angle = Mathf.random(360f);
                                if(target != null){
                                    while(Angles.near(angle, Angles.angle(unit.x, unit.y, target.x(), target.y()), 30f)){
                                        angle = Mathf.random(360f);
                                    }
                                }
                                b.initVel(angle, v);
                            }

                            bullets.remove(b);
                        }
                    }
                });
            }
        }
    }

    private float realRad(){
        return radius * radiusScale;
    }
}
