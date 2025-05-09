package fire.world.blocks.environment;

import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.meta.BuildVisibility;

/** Too lazy to set weather in world processor. */
public class EnvBlock extends mindustry.world.Block{

    public EnvBlock(String name){
        super(name);
        requirements(Category.effect, BuildVisibility.debugOnly, ItemStack.with());
        update = true;
        targetable = false;
        forceDark = true;
        hasShadow = false;
        size = 1;
        buildType = EnvBlockBuild::new;
    }

    @Override
    public boolean canBreak(Tile tile){
        return false;
    }

    public static class EnvBlockBuild extends mindustry.gen.Building{

        @Override
        public void update(){
            super.update();
            if(isStarter()) updateStart();
            else updateStop();
        }

        protected void updateStart(){}

        protected void updateStop(){}

        /** Use position to decide to start or stop. */
        protected boolean isStarter(){
            return tileX() % 2 == 0;
        }
    }
}
