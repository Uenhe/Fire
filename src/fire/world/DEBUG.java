package fire.world;

import arc.struct.Seq;
import fire.content.FBlocks;
import fire.content.FFx;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.BuildVisibility;

public class DEBUG{

    public static final Seq<UnlockableContent> DEBUGS = Seq.with(
        FBlocks.DEBUG_TURRET
    );

    public static class DEBUG_Turret extends mindustry.world.blocks.defense.turrets.Turret{

        public static final PointBulletType type = (PointBulletType)((ItemTurret)Blocks.foreshadow).ammoTypes.get(Items.surgeAlloy).copy();

        public DEBUG_Turret(String name){
            super(name);
            requirements(Category.turret, BuildVisibility.debugOnly, ItemStack.empty);
            targetable = false;
            canOverdrive = false;

            reload = 3.0f;
            range = 1600.0f;
            rotateSpeed = 60.0f;
            targetInterval = 1.0f;
            shootSound = Sounds.railgun;
            buildType = DEBUG_TurretBuild::new;

            // if this hits units that are invincible, they'll become invincible... forever
            type.damage = Float.POSITIVE_INFINITY * 0.5f;
            type.splashDamage = Float.POSITIVE_INFINITY * 0.5f;
            type.splashDamageRadius = 80.0f;
            type.hitShake = 0.0f;
            type.trailSpacing = 80.0f;
            type.buildingDamageMultiplier = 1.0f;
            type.shootEffect = Fx.none;
            type.smokeEffect = Fx.none;
            type.hitEffect = Fx.none;
            type.trailEffect = FFx.instTrailPurple;
            type.despawnEffect = FFx.instBombPurple;
        }

        @Override
        public void setStats(){

        }

        public class DEBUG_TurretBuild extends TurretBuild{

            @Override
            public void updateTile(){
                unit.ammo(200.0f);
                super.updateTile();
            }

            @Override
            public boolean hasAmmo(){
                return true;
            }

            @Override
            public BulletType useAmmo(){
                return type;
            }

            @Override
            public BulletType peekAmmo(){
                return type;
            }
        }
    }
}
