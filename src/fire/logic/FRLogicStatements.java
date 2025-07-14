package fire.logic;

import arc.scene.ui.layout.Table;
import mindustry.logic.LAssembler;
import mindustry.logic.LCategory;
import mindustry.logic.LExecutor;

/** Special thanks to New Horizon Mod. */
public class FRLogicStatements extends mindustry.logic.LStatement{

    public FRLogicStatements(){}

    @Override
    public void build(Table table){}

    @Override
    public LExecutor.LInstruction build(LAssembler builder){
        return null;
    }

    public static class RemoveProcessorStatement extends FRLogicStatements{
        public static String name = "removeprocessor";

        public RemoveProcessorStatement(){}

        public RemoveProcessorStatement(String[] tokens){}

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public LExecutor.LInstruction build(LAssembler builder){
            return new FRLogicExecutor.RemoveProcessorI();
        }

        @Override
        public LCategory category(){
            return LCategory.world;
        }

        @Override
        public void write(StringBuilder builder){
            builder.append(name);
        }
    }
}
