package fire.entities.bullets;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.struct.IntFloatMap;
import arc.util.Time;
import fire.content.FRFx;
import fire.content.FRMath;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.ContentRegions;
import mindustry.gen.Groups;
import mindustry.graphics.Pal;

public class FusionBombType extends BulletType{
    private static final IntFloatMap timer1 = new IntFloatMap();
    private static final IntFloatMap timer2 = new IntFloatMap();
    public FusionBombType(float lifetime){
        super();
        this.lifetime = lifetime;
        this.hittable = false;
        this.reflectable = false;
        this.absorbable = false;
        this.drag = 0.02f;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        timer1.put(b.id, 0);
    }

    @Override
    public void draw(Bullet b){
        float actualTime = b.lifetime - b.time;

        Draw.color(Pal.surge);
        Fill.circle(b.x, b.y, FRMath.getRange_fusionBomb(actualTime));
        Draw.color();
    }

    @Override
    public void update(Bullet b){
        float actualTime = b.lifetime - b.time;
        float size = FRMath.getRange_fusionBomb(actualTime);
        float radius = size * 35.0f;
        final float G = 3.4f;
        b.damage = Mathf.pow(size, 2f) * 20.0f;
        if(b.lifetime > 60.0f){
            Groups.bullet.intersect(b.x - radius, b.y - radius, radius * 2.0f, radius * 2.0f, other -> {
                if(b.isAdded() && b.lifetime >= other.lifetime && other != b){
                    float force = Time.delta * Mathf.pow(size, 2f) * 150.0f * G / Mathf.dst2(b.x, b.y, other.x, other.y) / other.damage;
                    if(force >= 10.0f)
                        force = 10.0f;
                    if(other.type instanceof FusionBombType){
                        force *= 3;
                        if(other.vel.len() >= 3.0f)
                            other.vel.set(other.vel.x / other.vel.len() * 3.0f, other.vel.y / other.vel.len() * 3.0f);
                    }
                    float angle = Mathf.angle(other.x - b.x, other.y - b.y);
                    other.vel.x -= force * Mathf.cosDeg(angle);
                    other.vel.y -= force * Mathf.sinDeg(angle);
                    if(Mathf.within(b.x, b.y, other.x, other.y, size * 0.4f)){
                        if(other.team == b.team && other.type instanceof FusionBombType && b.lifetime - b.time >= other.lifetime - other.time){
                            b.lifetime = Math.min(Math.max(b.lifetime, other.lifetime * 3), b.lifetime - other.time + other.lifetime);
                            b.time = Math.max(0, b.time - other.lifetime + other.time);
                        }
                        other.remove();
                    }

                    if(Mathf.within(b.x, b.y, other.x, other.y, radius * 0.4f)){
                        if(other.team == b.team && other.type instanceof FusionBombType && b.lifetime - b.time >= other.lifetime - other.time){
                            FRFx.lineTrailEffect(30.0f, b.x, b.y, other.x, other.y, 1.0f, Pal.surge, 4).at(b.x, b.y);
                            if(other.lifetime - other.time >= Time.delta * 3){
                                b.time = Math.max(0, b.time - Time.delta * 3);
                                other.time = other.time + Time.delta * 3;
                                if(other.lifetime * 3 < b.lifetime)
                                    b.lifetime += Time.delta * 3;
                            }else{
                                b.time = Math.max(0, b.time - other.lifetime + other.time);
                                if(other.lifetime * 3 < b.lifetime)
                                    b.lifetime += other.lifetime - other.time;
                                other.remove();
                            }
                        }
                    }
                }
            });
        }
        if(b.lifetime >= 300.0f){
            Groups.unit.intersect(b.x - radius, b.y - radius, radius * 2.0f, radius * 2.0f, unit -> {
                float force = Time.delta * Mathf.pow(size,2f) * G / Mathf.dst(b.x, b.y, unit.x, unit.y) / Mathf.pow(unit.hitSize,2f);
                float angle = Mathf.angle(unit.x - b.x, unit.y - b.y);
                unit.vel.x -= force * Mathf.cosDeg(angle);
                unit.vel.y -= force * Mathf.sinDeg(angle);
                if(unit.vel.len() >= 20.0f)unit.vel.set(unit.vel.x / unit.vel.len() * 20.0f,unit.vel.y / unit.vel.len() * 20.0f);
                if(unit.team != b.team){
                    unit.damagePierce(b.lifetime * Time.delta / Mathf.dst(b.x, b.y, unit.x, unit.y) * 0.2f);
                    unit.apply(StatusEffects.burning, b.lifetime * 2.0f);
                    if(b.lifetime >= 360.0f)
                        unit.apply(StatusEffects.melting, (b.lifetime - 360.0f) * 1.5f);
                    if(b.lifetime >= 600.0f)
                        unit.apply(StatusEffects.electrified, b.lifetime - 600.0f);
                }
            });
        }
        if(b.lifetime >= 600.0f){
            float timer = timer1.get(b.id);
            if(timer >= FRMath.getReload1_fusionBomb(b.lifetime)){
                float rot = Mathf.randomSeed((long)(b.lifetime - b.time),360);
                float rot2 = Mathf.randomSeed((long)(b.lifetime - b.time),-30,30);
                new BasicBulletType(){
                    @Override
                    public Bullet create(Bullet parent, float x, float y, float angle) {
                        return this.create(parent, parent.shooter, parent.team, x, y, angle, -1.0F, Mathf.random(0.8f, 1.2f), 1.0F, null, null, -1.0F, -1.0F);
                    }
                    @Override
                    public void update(Bullet b){
                        var owner = (Bullet)b.owner;
                        b.time = 0;
                        if(!Mathf.within(b.x,b.y,owner.x,owner.y,size * 12f)){
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
                }}.create(b, b.x + size * Mathf.cosDeg(rot), b.y + size * Mathf.sinDeg(rot), rot + rot2);
                timer -= FRMath.getReload1_fusionBomb(b.lifetime);
            }
            timer1.put(b.id,timer + Time.delta);
        }
        super.update(b);
    }

    @Override
    public void removed(Bullet b){
        FRFx.powerfulBlastEffect(60.0f + FRMath.getRange_fusionBomb(b.lifetime) * 0.5f, FRMath.getRange_fusionBomb(b.lifetime) * 5, 0, 0, Pal.surge, Color.clear).at(b.x,b.y);
        Damage.damage(b.team,b.x,b.y,FRMath.getRange_fusionBomb(b.lifetime) * 5,FRMath.getDamage_fusionBomb(b.lifetime,b.lifetime));
        super.removed(b);
    }
}
