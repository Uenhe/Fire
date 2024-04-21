package fire.entities.bullets;

import mindustry.content.Fx;
import mindustry.gen.*;

public class LightningPointBulletType extends mindustry.entities.bullet.BulletType{

    /** Chance to let the lightning take effect. Using this to control lightning releasing for some reason... */
    public float lightningChance = 0.5f;

    public LightningPointBulletType(float damage){
        super(0f, damage);
        lifetime = 1f;
        hitEffect = Fx.none;
        despawnEffect = Fx.none;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        Teamc target;

        if(b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)){
            target = b.aimTile.build;
        }else{
            target = mindustry.entities.Units.closestTarget(
                b.team, b.x ,b.y, homingRange,
                e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                t -> t != null && collidesGround && !b.hasCollided(t.id)
            );
        }

        if(target != null && arc.math.Mathf.chance(lightningChance)){
            Fx.chainLightning.at(b.x, b.y, 0f, lightningColor, target);

            // target is either unit or building
            if(target instanceof Unit u){
                b.collision(u, u.x, u.y);
            }else{
                ((Building)target).collision(b);
            }
        }
        b.remove();
    }
}
