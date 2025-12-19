package fire.entities.bullets;

import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.util.Time;
import fire.content.FRFx;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;

/** @author fy */
public class SegmentalBulletType extends mindustry.entities.bullet.BasicBulletType{

    public float radius = 100f;
    public float damageMultiplier;
    public float speedMultiplier;
    public float destroyDistanceFactor = 0.5f;

    public SegmentalBulletType(float speed, float damage, float splashDamage){
        super(speed, damage);
        this.splashDamage = splashDamage;
    }

    @Override
    public void updateHoming(Bullet b){
        var target = Groups.bullet.intersect(b.x - range, b.y - range, range * 2.0f, range * 2.0f)
            .min(tgt -> tgt.team != b.team && tgt.type.hittable, tgt -> b.dst2(tgt));
        if(target == null){
            super.updateHoming(b);
        }else{
            b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50.0f));
            if(b.within(target, splashDamageRadius * destroyDistanceFactor))
                b.remove(); //"collides"
        }
    }

    @Override
    public void hit(Bullet b, float x, float y){
        super.hit(b, x, y);
        if(b.absorbed) return;

        Groups.bullet.intersect(x - radius, y - radius, radius * 2.0f, radius * 2.0f, other -> {
            if(b.isAdded() && b.team != other.team && other.type.hittable){
                FRFx.chainEmpThin.at(x, y, 0.0f, hitColor, new Vec2().set(other));

                float v = splashDamage * damageMultiplier;
                if(other.damage > v){
                    other.damage -= v;
                    other.vel.scl(speedMultiplier);
                }else{
                    other.remove();
                }
            }
        });
    }
}
