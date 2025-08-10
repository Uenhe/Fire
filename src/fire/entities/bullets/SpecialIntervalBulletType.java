package fire.entities.bullets;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class SpecialIntervalBulletType extends BasicBulletType {

    public SpecialIntervalBulletType(float speed, float damage){
        super(speed, damage);
    }

    @Override
    public Bullet create(Bullet parent, float x, float y, float angle) {
        return this.create(parent, parent.shooter, parent.team, x, y, angle, -1.0F, Mathf.random(0.8f, 1.2f), 1.0F, (Object)null, (Mover)null, -1.0F, -1.0F);
    }

    @Override
    public void update(Bullet b){
        this.updateTrail(b);
        this.updateTrailEffects(b);
        this.updateBulletInterval(b);
        if(b.time <= 30f){
            this.updateWeaving(b);
        }
        else{
            b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo((Position) b.owner), homingPower * Time.delta * 50.0f));
            if(b.within((Position) b.owner, splashDamageRadius)) b.remove();
        }
    }
}
