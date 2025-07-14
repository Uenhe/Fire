package fire.logic;

import mindustry.content.Blocks;
import mindustry.logic.LExecutor;
import mindustry.world.blocks.environment.StaticWall;

public class FRLogicExecutor{

    /** Too lazy. */
    public static class RemoveProcessorI implements LExecutor.LInstruction{

        @Override
        public void run(LExecutor exec){
            var tile = exec.thisv.building().tile;
            tile.setNet(tile.floor().wall instanceof StaticWall w ? w : Blocks.stoneWall);
        }
    }
}
