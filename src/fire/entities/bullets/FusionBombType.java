package fire.entities.bullets;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.struct.IntFloatMap;
import arc.util.Time;
import fire.annotation.Modified;
import fire.content.FRFx;
import fire.content.FRMath;
import mindustry.ai.types.CommandAI;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.bullet.*;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.meta.Env;

public class FusionBombType extends mindustry.type.UnitType{
    private static final IntFloatMap timerMap = new IntFloatMap();

    public FusionBombType(float lifetime, float startSpeed){
        super("fusion-bomb");
        this.health = lifetime * 100; //Actually, it's the lifetime of the bomb... 100 health = 1 tick
        this.hittable = false;
        this.rotateSpeed = 0;
        this.flying = true;
        this.speed = startSpeed;
        //controller = u -> new FusionBombAI();
        wobble = false;
        playerControllable = false;
        createWreck = false;
        createScorch = false;
        logicControllable = false;
        isEnemy = false;
        useUnitCap = false;
        drawCell = false;
        allowedInPayloads = false;
        envEnabled = Env.any;
        envDisabled = Env.none;
        physics = false;
        bounded = false;
        hidden = true;
        hoverable = false;
        canAttack = false;
        range = 256f;
        drag = 0.02f;
        targetPriority = -1f;
        fogRadius = 256f;
    }

    @Override
    @Modified
    public Unit create(Team team){
        var unit = constructor.get();
        unit.team = team;
        unit.setType(this);
        if(unit.controller() instanceof CommandAI command && defaultCommand != null)
            command.command = defaultCommand;

        for(var ability : unit.abilities)
            ability.created(unit);

        unit.elevation = flying ? 1 : 0;
        unit.heal();
        timerMap.put(unit.id, 0.0f);
        return unit;
    }

    @Override
    public void draw(Unit b){
        //super.draw(b);
        Draw.z(Layer.effect);
        Draw.color(Pal.surge);
        Fill.circle(b.x, b.y, b.hitSize * 0.5f);
        Draw.color();
    }


    @Override
    public void update(Unit b){
        float time = b.health * 0.01f;
        float size = FRMath.getRange_fusionBomb(time);
        float radius = size * 35.0f;
        final float G = 3.4f;

        b.hitSize(size * 2);
        b.maxHealth(Math.max(b.maxHealth, b.health));

        if(time > 60.0f){
            Groups.bullet.intersect(b.x - radius, b.y - radius, radius * 2.0f, radius * 2.0f, other -> {
                if(b.isAdded() && !(other.type instanceof LaserBulletType) && !(other.type instanceof ContinuousBulletType) && !(other.type instanceof LightningBulletType)){
                    float force = Time.delta * Mathf.pow(size, 2f) * 150.0f * G / Mathf.dst2(b.x, b.y, other.x, other.y) / other.damage;
                    if(force >= 10.0f)
                        force = 10.0f;
                    /*
                    if(other.type instanceof FusionBombType){
                        force *= 3;
                        if(other.vel.len() >= 3.0f)other.vel.set(other.vel.x / other.vel.len() * 3.0f,other.vel.y / other.vel.len() * 3.0f);
                    }*/
                    float angle = Mathf.angle(other.x - b.x, other.y - b.y);
                    other.vel.x -= force * Mathf.cosDeg(angle);
                    other.vel.y -= force * Mathf.sinDeg(angle);
                    if(Mathf.within(b.x, b.y, other.x, other.y, size * 0.4f)){
                        if(other.team != b.team)
                            b.damagePierce(other.damage * 0.5f);
                        other.remove();
                    }
                }
            });
        }
        Groups.unit.intersect(b.x - radius, b.y - radius, radius * 2.0f, radius * 2.0f, unit -> {
            if(unit != b){
                float force = Time.delta * Mathf.pow(size, 2f) * G / Mathf.dst(b.x, b.y, unit.x, unit.y) / Mathf.pow(unit.hitSize, 2f);
                float angle = Mathf.angle(unit.x - b.x, unit.y - b.y);
                unit.vel.x -= force * Mathf.cosDeg(angle);
                unit.vel.y -= force * Mathf.sinDeg(angle);
                if(unit.vel.len() >= 20.0f)
                    unit.vel.set(unit.vel.x / unit.vel.len() * 20.0f, unit.vel.y / unit.vel.len() * 20.0f);
                if(unit.team != b.team){
                    unit.damagePierce(time * Time.delta / Mathf.dst(b.x, b.y, unit.x, unit.y) * 0.2f);
                    unit.apply(StatusEffects.burning, time * 2.0f);
                    if(time >= 360.0f)
                        unit.apply(StatusEffects.melting, (time - 360.0f) * 1.5f);
                    if(time >= 600.0f)
                        unit.apply(StatusEffects.electrified, time - 600.0f);
                }

                if(Mathf.within(b.x, b.y, unit.x, unit.y, size * 0.4f)){
                    if(unit.team == b.team && unit.type instanceof FusionBombType && b.health > unit.health){
                        final float aimH = b.health + Math.min(unit.health, Math.max(0, unit.health * 3 - b.health));
                        if(aimH >= b.maxHealth)
                            b.maxHealth(aimH);
                        b.health(aimH);
                        b.apply(StatusEffects.burning, 60f);
                        unit.remove();
                    }
                }

                if(Mathf.within(b.x, b.y, unit.x, unit.y, radius * 0.4f)){
                    if(unit.team == b.team && unit.type instanceof FusionBombType && b.health > unit.health){
                        FRFx.lineTrailEffect(30.0f, b.x, b.y, unit.x, unit.y, 1.0f, Pal.surge, 4).at(b.x, b.y);
                        float willHeal = Math.min(Time.delta * 300, Math.max(0, unit.health * 3 - b.health));
                        if(unit.health >= willHeal * 2.0f){
                            unit.damagePierce(willHeal);
                            final float aimH = b.health + willHeal;
                            if(aimH >= b.maxHealth)
                                b.maxHealth(aimH);
                            b.health(aimH);
                            b.apply(StatusEffects.burning, 4f);
                        }else{
                            final float aimH = b.health + unit.health;
                            if(aimH >= b.maxHealth)
                                b.maxHealth(aimH);
                            b.health(aimH);
                            b.apply(StatusEffects.burning, 2f);
                            unit.remove();
                        }
                    }
                }
            }
        });

        if(time >= 600.0f){
            float timer = timerMap.get(b.id);
            if(timer >= FRMath.getReload1_fusionBomb(time)){
                float rot = Mathf.randomSeed((long)(Time.time), 360);
                float rot2 = Mathf.randomSeed((long)(Time.time), -30, 30);
                new BasicBulletType(){
                    @Override
                    public Bullet create(Bullet parent, float x, float y, float angle){
                        return create(parent, parent.shooter, parent.team, x, y, angle, -1.0F, Mathf.random(0.8f, 1.2f), 1.0F, null, null, -1.0F, -1.0F);
                    }

                    @Override
                    public void update(Bullet b){
                        var owner = (Unit)b.owner;
                        b.time = 0;
                        if(!Mathf.within(b.x, b.y, owner.x, owner.y, size * 12f)){
                            b.remove();
                        }
                        super.update(b);
                    }
                    {
                        backRegion = Core.atlas.find("bullet-back");
                        frontRegion = Core.atlas.find("bullet");
                        damage = size * 12 + 50.0f;
                        speed = 6 + size * 0.01f;
                        trailColor = Pal.surge;
                        trailLength = (int)(size * 1.8f);
                        trailWidth = size * 0.08f;
                        lifetime = 60.0f;
                    }
                }.create(b, b.x + size * Mathf.cosDeg(rot), b.y + size * Mathf.sinDeg(rot), rot + rot2);
                timer -= FRMath.getReload1_fusionBomb(time);
            }
            timerMap.put(b.id, timer + Time.delta);
        }

        if(!b.hasEffect(StatusEffects.burning))
            b.damagePierce(Time.delta * 100.0f);
        super.update(b);
    }

    @Override
    public void killed(Unit b){
        float time = b.maxHealth * 0.01f;
        FRFx.powerfulBlastEffect(60.0f + FRMath.getRange_fusionBomb(time) * 0.5f, FRMath.getRange_fusionBomb(time) * 5, 0, 0, Pal.surge, Color.clear).at(b.x, b.y);
        Damage.damage(b.team, b.x, b.y, FRMath.getRange_fusionBomb(time) * 5, FRMath.getDamage_fusionBomb(time, time));
        if(time >= 1200.0f){
            float size = FRMath.getRange_fusionBomb(time);
            var blast1 = new BasicBulletType(){{
                backRegion = Core.atlas.find("bullet-back");
                frontRegion = Core.atlas.find("bullet");
                damage = size * 12 + 50.0f;
                speed = 5 + size * 0.02f;
                trailColor = Pal.surge;
                trailLength = (int)(size * 4.2f);
                trailWidth = size * 0.18f;
                lifetime = 120.0f;
                drag = 0.04f;
                splashDamage = size * 22 + 50.0f;
                splashDamageRadius = size * 1.2f;
                despawnEffect = hitEffect = FRFx.powerfulBlastEffect(50.0f + FRMath.getRange_fusionBomb(time) * 0.5f, splashDamageRadius, 0, 0, Pal.surge, Color.clear);
                if(time >= 2000.0f){
                    fragBullets = 3;
                    if(time >= 3000.0f)
                        fragBullets++;
                    if(time >= 4500.0f)
                        fragBullets++;
                    if(time >= 6000.0f)
                        fragBullets++;
                    if(time >= 8000.0f)
                        fragBullets++;
                    if(time >= 12000.0f)
                        fragBullets++;
                    fragBullet = new BasicBulletType(){{
                        backRegion = Core.atlas.find("bullet-back");
                        frontRegion = Core.atlas.find("bullet");
                        damage = size * 12 + 60.0f;
                        speed = 8 + size * 0.04f;
                        trailColor = Pal.surge;
                        trailLength = (int)(size * 2.2f);
                        trailWidth = size * 0.12f;
                        lifetime = 90.0f;
                        pierce = true;
                        pierceCap = 2;
                        drag = 0.05f;
                    }};
                }
            }};
            for(int i = 0; i * i * 100 <= time; i++)
                blast1.create(b, b.team, b.x, b.y, Mathf.randomSeed((long)(i + time), 360), Mathf.randomSeed((long)(i + time * 2.0f), 0.2f, 1.2f), Mathf.randomSeed((long)(i + time), 0.95f, 1.85f));
        }
        b.remove();
    }
}
