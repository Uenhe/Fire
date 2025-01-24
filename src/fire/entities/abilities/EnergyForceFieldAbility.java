package fire.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import fire.world.meta.FStat;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.content;

public class EnergyForceFieldAbility extends mindustry.entities.abilities.ForceFieldAbility{

    /** see {@link mindustry.entities.bullet.LightningBulletType#calculateRange()} */
    public final short lightningLength;
    public final byte lightningAmount;
    public final short lightningDamage;
    public final float chanceDeflect;
    public float rotateSpeed;
    public Color lightningColor = Color.clear;
    public boolean unlocks;

    static final String name = "ability.fire-energyforcefield";
    float timer;
    boolean broken, regenable;
    final Seq<Bullet> bullets = new Seq<>();
    /** Prevent endless and awful creating instances. */
    static final ObjectMap<Short, Short> bulletMap = new ObjectMap<>();

    public EnergyForceFieldAbility(float radius, float regen, float max, float cooldown, int length, int amount, int damage, float chance){
        super(radius, regen, max, cooldown);
        lightningLength = (short)length;
        lightningAmount = (byte)amount;
        lightningDamage = (short)damage;
        chanceDeflect = chance;
    }

    @Override
    public String localized(){
        return Core.bundle.get(name);
    }

    /** TODO Reconstruct this <a href="https://github.com/Anuken/Mindustry/pull/9654">if v147 is released</a>; May clash with BE? */
    @Override
    public void addStats(Table t){
        final float wid = 432.0f;

        t.add(Core.bundle.get(name + ".description")).wrap().width(wid);
        t.row();
        if(unlocks){
            t.add(Core.bundle.get(name + ".unlocks")).wrap().width(wid);
            t.row();
        }

        super.addStats(t);
        t.add("[lightgray]" + Stat.baseDeflectChance.localized() + ": [white]" + Strings.autoFixed(chanceDeflect, 2));
        t.row();
        t.add("[lightgray]" + Stat.lightningDamage.localized() + ": [white]" + Strings.autoFixed(lightningDamage, 2));
        t.row();
        t.add("[lightgray]" + FStat.lightningLength.localized() + ": [white]" + Strings.autoFixed(lightningLength * 0.75f, 2) + " " + StatUnit.blocks.localized());
        t.row();
        t.add("[lightgray]" + FStat.lightningAmount.localized() + ": [white]" + Strings.autoFixed(lightningAmount, 2));
    }

    @Override
    public void displayBars(Unit unit, Table bars){
        bars.add(new mindustry.ui.Bar(
            "stat.shieldhealth",
            broken ? Color.gray : mindustry.graphics.Pal.accent,
            () -> broken ? -unit.shield / (regen * cooldown) : unit.shield / max)
        ).row();
    }

    @Override
    public void update(Unit unit){
        alpha = Math.max(alpha - Time.delta / 10.0f, 0.0f);
        broken = unit.shield <= 0.0f;

        if(rotateSpeed > 0.0f){
            rotation += rotateSpeed * Time.delta;
            rotation %= 360.0f;
        }

        if(unit.shield < max && timer == 0.0f){
            unit.shield += regen * Time.delta;

            // regen shield to full after cooldown
            if(unit.shield > 0.0f && radiusScale == 0.0f)
                unit.shield = max;
        }

        if(!broken){
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
                        broken = true;
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

        }else{
            if(unlocks && !regenable){

                boolean isOverloaded = bullets.sumf(b -> b.damage) >= unit.maxHealth * 0.5f;

                final float speed = 0.2f;
                float scl = Math.min(1.0f - ((timer - 240.0f) / 60.0f), 1.0f);
                float
                    x = Mathf.cos(timer * speed) * radius * scl + unit.x,
                    y = Mathf.sin(timer * speed) * radius * scl + unit.y;

                timer += Time.delta * (isOverloaded ? 1.333f : 1.0f);

                if(timer <= 180.0f){
                    Groups.bullet.intersect(unit.x - radius, unit.y - radius, radius * 2.0f, radius * 2.0f, bullet -> {
                        if(
                            !isOverloaded
                            && bullet.team != unit.team && bullet.type.absorbable && bullet.type.hittable
                            && !(bullet.type instanceof mindustry.entities.bullet.LiquidBulletType)
                            && Intersector.isInRegularPolygon(sides, unit.x, unit.y, radius, rotation, bullet.x, bullet.y)
                        ){
                            bullet.owner = unit;
                            bullet.team = unit.team;
                            bullet.time = 0.0f;
                            bullet.lifetime = 310.0f;

                            // reuse instances as possible
                            BulletType type = bulletMap.containsKey(bullet.type.id)
                                ? content.getByID(ContentType.bullet, bulletMap.get(bullet.type.id))
                                : bullet.type.copy();

                            if(!bulletMap.containsKey(bullet.type.id)){

                                // make artillery collides building
                                type.collidesTiles = type.collides = true;

                                // add a default trail to bullets that without a trail
                                if(type.trailLength <= 0){
                                    if(type instanceof BasicBulletType bt){
                                        type.trailWidth = bt.width * 0.22f;
                                        type.trailLength = (int)bt.height / 2;
                                        type.trailColor = bt.backColor;
                                    }else{
                                        type.trailLength = 12;
                                    }
                                }
                            }

                            bullet.type = type;
                            bullets.add(bullet);
                        }
                    });

                }else if(timer > 310.0f){
                    regenable = true;
                    timer = 0.0f;
                    bullets.clear();
                }

                bullets.each(b -> {

                    if(b.type != null){

                        final float vel = 12.0f;
                        float dst = Math.min(Mathf.dst(x, y, b.x, b.y), radius);
                        float range = b.type.speed * b.type.lifetime;

                        if(timer <= 240.0f){
                            b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(x, y), 80.0f * Time.delta));
                            b.vel.setLength(5.0f + 3.0f * timer / 240.0f * (1.0f + dst / radius)); // velocity terminal = 8, has a boost depending on distance(b, dest) up to 200%

                        }else if(timer > 240.0f && timer <= 300.0f){
                            b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(x, y), 80.0f * Time.delta));
                            b.vel.setLength(8.0f * dst / radius); // use velocity terminal here

                        }else{
                            b.time = 0.0f;
                            b.lifetime = range / vel;

                            var target =
                                b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && b.type.collidesGround && !b.hasCollided(b.aimTile.build.id) ?
                                    b.aimTile.build :
                                    Units.closestTarget(b.team, b.x, b.y, range,
                                        u -> u != null && u.checkTarget(b.type.collidesAir, b.type.collidesGround) && !b.hasCollided(u.id),
                                        t -> t != null && b.type.collidesGround && !b.hasCollided(t.id));

                            b.set(unit.x, unit.y);
                            if(target != null && Mathf.chance(0.48)){
                                final float mag = 5.0f;
                                final float velTer = vel * 1.16f;

                                b.vel.setLength(velTer);
                                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), 1000.0f));
                                b.mover = bullet -> bullet.moveRelative(0f, Mathf.cos(b.time * velTer * Mathf.PI / Mathf.dst(unit.x, unit.y, target.x(), target.y())) * mag * Mathf.sign(b.id % 2 == 0));

                            }else{
                                float angle = Mathf.random(360.0f);

                                if(target != null)
                                    while(Angles.near(angle, Angles.angle(unit.x, unit.y, target.x(), target.y()), 30.0f))
                                        angle = Mathf.random(360.0f);

                                b.initVel(angle, vel);
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
