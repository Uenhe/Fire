package fire.world.blocks.environment;

import arc.func.Prov;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.meta.BuildVisibility;

import static mindustry.Vars.state;

/** Too lazy to set weather in world processor. */
public class EnvBlock extends mindustry.world.Block{

    public EnvBlock(String name, Prov<Building> type){
        super(name);
        requirements(Category.effect, BuildVisibility.debugOnly, ItemStack.with());
        update = true;
        targetable = false;
        forceDark = true;
        hasShadow = false;
        buildType = type;
    }

    @Override
    public boolean canBreak(Tile tile){
        return false;
    }

    public static class EnvBlockBuild extends Building{

        @Override
        public void update(){
            super.update();
            if(state.isCampaign() && state.rules.sector.info.wasCaptured){
                rm();
                return;
            }

            if(isStarter()) updateStart();
            else updateStop();
        }

        protected void rm(){
            tile.setNet(tile.floor().wall instanceof StaticWall w ? w : Blocks.stoneWall);
        }

        protected void updateStart(){}

        protected void updateStop(){}

        /** Use position to decide to start or stop. */
        protected boolean isStarter(){
            return tileX() % 2 == 0;
        }
    }
}
