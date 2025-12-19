package fire.world;

import arc.Core;
import arc.struct.Seq;
import arc.util.OS;
import fire.FRUtils;
import fire.content.FRFx;
import fire.world.blocks.sandbox.AdaptiveSource;
import mindustry.ai.BlockIndexer;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.BuildVisibility;

import java.lang.reflect.Field;

import static mindustry.Vars.*;

public class DEBUG{

    private static final BuildVisibility DEBUG_BuildVisibility = new BuildVisibility(() -> state.rules.infiniteResources && isDeveloper());
    private static final BuildVisibility DEBUG_CheatBuildVisibility = new BuildVisibility(() -> player.team().rules().cheat && isDeveloper());

    public static class DEBUG_Turret extends mindustry.world.blocks.defense.turrets.Turret{

        private static final PointBulletType type = new PointBulletType();

        public DEBUG_Turret(String name){
            super(name);
            requirements(Category.logic, DEBUG_BuildVisibility, ItemStack.empty);
            alwaysUnlocked = true;
            reload = 3.0f;
            range = 1600.0f;
            rotateSpeed = 60.0f;
            targetInterval = 0.01f;
            shootSound = Sounds.shootForeshadow;
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
                if(!isDeveloper()) return;
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

    public static class DEBUG_Mend extends Block{

        public DEBUG_Mend(String name){
            super(name);
            requirements(Category.logic, DEBUG_BuildVisibility, ItemStack.empty);
            alwaysUnlocked = true;
            solid = destructible = true;
            buildType = DEBUG_MendBuild::new;
        }

        @Override
        public void setStats(){}

        public static class DEBUG_MendBuild extends Building{

            private static final Field field_activeTeams;

            static{
                try{
                    field_activeTeams = FRUtils.field(BlockIndexer.class, "activeTeams");
                }catch(NoSuchFieldException e){
                    throw new RuntimeException("?", e);
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public void placed(){
                if(!isDeveloper()) return;
                try{
                    Seq<Team> activeTeams = (Seq<Team>)field_activeTeams.get(indexer);
                    for(var team : activeTeams)
                        for(var build : team.data().buildings)
                            build.heal();
                    tile.setNet(Blocks.air);

                }catch(IllegalAccessException e){
                    throw new RuntimeException("?", e);
                }
            }
        }
    }

    public static class DEBUG_ItemTurretSupplier extends Block{

        public DEBUG_ItemTurretSupplier(String name){
            super(name);
            requirements(Category.logic, DEBUG_CheatBuildVisibility, ItemStack.empty);
            alwaysUnlocked = true;
            solid = destructible = update = true;
            buildType = DEBUG_ItemTurretSupplierBuild::new;
        }

        @Override
        public void setStats(){}

        public static class DEBUG_ItemTurretSupplierBuild extends Building{

            @Override
            public void updateTile(){
                if(isDeveloper() && cheating() && timer(0, 90.0f)){
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
        return "KochiyaUeneh".equals(OS.username)
            || "12879".equals(OS.username)
            || "aaaaaa".equals(Core.settings.getString("name")); //username="root" on Android
    }
}
