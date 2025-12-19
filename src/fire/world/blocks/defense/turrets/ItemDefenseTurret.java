package fire.world.blocks.defense.turrets;

import fire.entities.bullets.SegmentalBulletType;
import mindustry.gen.Groups;
import mindustry.gen.Posc;

public class ItemDefenseTurret extends mindustry.world.blocks.defense.turrets.ItemTurret{

    public ItemDefenseTurret(String name){
        super(name);
        buildType = ItemDefenseBuild::new;
    }

    public class ItemDefenseBuild extends ItemTurretBuild{

        @Override
        protected Posc findEnemy(float range){
            if(peekAmmo() instanceof SegmentalBulletType){
                var target = Groups.bullet.intersect(x - range, y - range, range * 2.0f, range * 2.0f).min(b -> b.team != team && b.type().hittable, b -> b.dst2(this));
                return target != null && target.isAdded() ? target : super.findEnemy(range);
            }else{
                return super.findEnemy(range);
            }
        }
    }
}
