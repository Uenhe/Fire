package fire.world.blocks.defense.turrets;

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
            var target = Groups.bullet.intersect(x - range, y - range, range * 2, range * 2).min(b -> b.team != team && b.type().hittable, b -> b.dst2(this));
            return target != null ? target : super.findEnemy(range);
        }
    }
}
