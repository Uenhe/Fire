package fire.logic;

import arc.scene.ui.layout.Table;
import mindustry.logic.LAssembler;
import mindustry.logic.LCategory;
import mindustry.logic.LExecutor;
import mindustry.logic.LStatement;
import mindustry.ui.Styles;

/** Special thanks to New Horizon Mod. */
public class FRLogicStatements{

    public static class TransitionEffectStatement extends LStatement{
        public static String name = "transfx";

        public boolean out;
        public String unit = "unit";

        public TransitionEffectStatement(){}

        public TransitionEffectStatement(String[] tokens){
            out = Boolean.parseBoolean(tokens[1]);
            unit = tokens[2];
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public void build(Table table){
            table.clearChildren();

            table.button(out ? "TransOut" : "TransIn", Styles.logict, () -> {
                out = !out;
                build(table);
            }).size(160.0f, 40.0f).pad(4.0f).color(table.color);
            table.add(" unit ");
            field(table, unit, str -> unit = str);
        }

        @Override
        public LExecutor.LInstruction build(LAssembler builder){
            return new FRLogicExecutor.TransitionEffectI(out, builder.var(unit));
        }

        @Override
        public LCategory category(){
            return LCategory.world;
        }

        @Override
        public void write(StringBuilder builder){
            builder.append(name)
                .append(" ").append(out)
                .append(" ").append(unit);
        }
    }

    public static class MaskCutsceneStatement extends LStatement{
        public static String name = "maskcutscene";

        public boolean out;
        public String duration = "2";

        public MaskCutsceneStatement(){}

        public MaskCutsceneStatement(String[] tokens){
            out = Boolean.parseBoolean(tokens[1]);
            duration = tokens[2];
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public void build(Table table){
            table.clearChildren();

            table.button(out ? "MaskOut" : "MaskIn", Styles.logict, () -> {
                out = !out;
                build(table);
            }).size(160.0f, 40.0f).pad(4.0f).color(table.color);
            table.add(" for ");
            field(table, duration, str -> duration = str);
            table.add(" sec");
        }

        @Override
        public LExecutor.LInstruction build(LAssembler builder){
            return new FRLogicExecutor.MaskCutsceneI(out, builder.var(duration));
        }

        @Override
        public LCategory category(){
            return LCategory.world;
        }

        @Override
        public void write(StringBuilder builder){
            builder.append(name)
                .append(" ").append(out)
                .append(" ").append(duration);
        }
    }

    public static class RemoveProcessorStatement extends LStatement{
        public static String name = "removeprocessor";

        public RemoveProcessorStatement(){}

        public RemoveProcessorStatement(String[] tokens){}

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public void build(Table table){}

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
