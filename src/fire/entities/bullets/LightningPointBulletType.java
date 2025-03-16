package fire.entities.bullets;

import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;

public class LightningPointBulletType extends mindustry.entities.bullet.BulletType{

    /** Chance to let the lightning take effect. Using this to control lightning releasing for some reason... */
    public float lightningChance = 0.5f;

    public LightningPointBulletType(float damage){
        super(0.0f, damage);
        lifetime = Mathf.FLOAT_ROUNDING_ERROR;
        hitEffect = despawnEffect = Fx.none;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        if(!Mathf.chance(lightningChance)) return;
        Teamc target;

        if(b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id))
            target = b.aimTile.build;
        else
            target = Units.closestTarget(
                b.team, b.x ,b.y, homingRange,
                e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                t -> t != null && collidesGround && !b.hasCollided(t.id)
            );

        if(target != null){
            Fx.chainLightning.at(b.x, b.y, 0f, lightningColor, target);

            // target is either unit or building
            if(target instanceof Unit u)
                b.collision(u, u.x, u.y);
            else
                ((Building)target).collision(b);
        }

        b.remove();
    }
}
