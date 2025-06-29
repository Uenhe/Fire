package fire.world;

import arc.math.Mathf;
import arc.util.OS;
import fire.content.FRFx;
import fire.world.blocks.sandbox.AdaptiveSource;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.BuildVisibility;

import static mindustry.Vars.*;

public class DEBUG{

    /** OS usernames. */
    private static final String[] developers = {
        "KochiyaUeneh", "12879"
    };

    private static final BuildVisibility DEBUG_BuildVisibility = new BuildVisibility(() -> state.rules.infiniteResources && isDeveloper());
    private static final BuildVisibility DEBUG_CheatBuildVisibility = new BuildVisibility(() -> player.unit() != null && player.unit().cheating() && isDeveloper());

    public static class DEBUG_Turret extends mindustry.world.blocks.defense.turrets.Turret{

        private static final PointBulletType type = new PointBulletType();

        public DEBUG_Turret(String name){
            super(name);
            requirements(Category.logic, DEBUG_BuildVisibility, ItemStack.empty);
            reload = 3.0f;
            range = 1600.0f;
            rotateSpeed = 60.0f;
            targetInterval = 0.01f;
            shootSound = Sounds.railgun;
            targetable = false;
            canOverdrive = false;
            predictTarget = false;
            buildType = DEBUG_TurretBuild::new;

            type.damage = Float.POSITIVE_INFINITY;
            type.splashDamage = Float.POSITIVE_INFINITY;
            type.splashDamageRadius = 40.0f;
            type.hitShake = 0.0f;
            type.trailSpacing = 80.0f;
            type.shootEffect = Fx.none;
            type.smokeEffect = Fx.none;
            type.hitEffect = Fx.none;
            type.trailEffect = FRFx.instTrailPurple;
            type.despawnEffect = FRFx.crossEffect(15.0f, 3.0f, 45.0f, false, Pal.reactorPurple2);
        }

        @Override
        public void load(){
            super.load();
        }

        @Override
        public void setStats(){}

        public class DEBUG_TurretBuild extends TurretBuild{

            @Override
            public void updateTile(){
                unit.ammo(60.0f);
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

            /** If this hits units that are invincible, they'll become invincible... forever. */
            @Override
            protected void findTarget(){
                super.findTarget();
                if(!(target instanceof Unit u)) return;

                if(u.hasEffect(StatusEffects.invincible)){
                    target = null;
                }else if(Float.isNaN(u.shield)){
                    u.shield = 0.0f;
                }
            }
        }
    }

    public static class DEBUG_Mend extends mindustry.world.Block{

        public DEBUG_Mend(String name){
            super(name);
            requirements(Category.logic, DEBUG_BuildVisibility, ItemStack.empty);
            solid = destructible = true;
            buildType = DEBUG_MendBuild::new;
        }

        @Override
        public void setStats(){}

        public static class DEBUG_MendBuild extends Building{

            @Override
            public void placed(){
                indexer.allBuildings(world.width() * tilesize * 0.5f, world.height() * tilesize * 0.5f, Mathf.dst(world.width(), world.height()) * tilesize * 0.5f, Building::heal);
                killed();
            }
        }
    }

    public static class DEBUG_ItemTurretSupplier extends mindustry.world.Block{

        public DEBUG_ItemTurretSupplier(String name){
            super(name);
            requirements(Category.logic, DEBUG_CheatBuildVisibility, ItemStack.empty);
            solid = destructible = update = true;
            buildType = DEBUG_ItemTurretSupplierBuild::new;
        }

        @Override
        public void setStats(){}

        public static class DEBUG_ItemTurretSupplierBuild extends Building{

            @Override
            public void updateTile(){
                if(!cheating()) return;

                if(timer(0, 120.0f)){
                    for(var build : team.data().buildings){
                        if(!(build instanceof ItemTurret.ItemTurretBuild)) continue;

                        var ammo = ((ItemTurret.ItemTurretBuild)build).ammo;
                        var item = content.item(AdaptiveSource.turretItemMap.get(build.block.id));
                        if(ammo.isEmpty()) build.handleItem(build, item);

                        var entry = (ItemTurret.ItemEntry)ammo.peek();
                        entry.amount = ((ItemTurret.ItemTurretBuild)build).totalAmmo = 100;
                        entry.item = item;
                    }
                }
            }
        }
    }

    public static boolean isDeveloper(){
        for(String dev : developers)
            if(dev.equals(OS.username)) return true;

        return false;
    }
}
