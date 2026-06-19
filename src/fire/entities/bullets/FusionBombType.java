package fire.entities.bullets;

import arc.func.Cons;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.IntFloatMap;
import arc.util.Time;
import fire.content.FRFx;
import fire.content.FRMath;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Bullet;
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
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        timer1.put(b.id, b.lifetime);
    }

    @Override
    public void update(Bullet b){
        float actualTime = b.lifetime - b.time;
        float size = FRMath.getRange_fusionBomb(actualTime);
        float radius = size * 10.0f;
        if(b.lifetime > 60.0f){
            Groups.bullet.intersect(b.x - radius, b.y - radius, radius * 2.0f, radius * 2.0f, (Cons<? super Bullet>)other -> {
                if(b.isAdded() && b.lifetime >= other.lifetime && other.type instanceof FusionBombType){
                    float force = Time.delta * b.lifetime / Mathf.dst2(b.x, b.y, other.x, other.y);
                    float angle = Mathf.angle(other.x - b.x, other.y - b.y);
                    other.vel.x += b.x > other.x ? force * Mathf.cosDeg(angle) : -force * Mathf.cosDeg(angle);
                    other.vel.y += b.y > other.y ? force * Mathf.sinDeg(angle) : -force * Mathf.sinDeg(angle);
                    if(Mathf.within(b.x,b.y,other.x,other.y,size)){
                        if(other.team == b.team){
                            b.time = Math.max(0,b.time - other.time);
                        }
                        other.remove();
                    }
                }
            });
        }
        if(b.lifetime >= 300.0f){
            Groups.unit.intersect(b.x - radius, b.y - radius, radius * 2.0f, radius * 2.0f, unit -> {
                unit.damagePierce(b.lifetime * Time.delta);
                unit.apply(StatusEffects.burning, b.lifetime * 2.0f);
                if(b.lifetime >= 360.0f)
                    unit.apply(StatusEffects.melting, (b.lifetime - 360.0f) * 1.5f);
                if(b.lifetime >= 600.0f)
                    unit.apply(StatusEffects.electrified, b.lifetime - 600.0f);
            });
        }
        if(b.lifetime >= 600.0f){
            float timer = timer1.get(b.id);
            if(timer >= FRMath.getReload1_fusionBomb(b.lifetime))timer -= FRMath.getReload1_fusionBomb(b.lifetime);
            Mathf.randomSeed((long)b.lifetime);
            new BasicBulletType(){{

            }}.create(b, b.x, b.y, Mathf.random(360));
            timer1.put(b.id,timer + Time.delta);
        }
        super.update(b);
    }

    @Override
    public void removed(Bullet b){
        super.removed(b);
    }
}
