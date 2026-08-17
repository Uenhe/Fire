package fire.logic;

import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.logic.*;
import mindustry.ui.Styles;

import static fire.logic.MessageTypePlus.*;

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

    public static class FetchPlusPlusStatement extends LStatement{
        public static String name = "fpp";

        public String block = "@router";

        @Override
        public String name(){
            return "Fetch++";
        }

        public FetchPlusPlusStatement(){}

        public FetchPlusPlusStatement(String[] tokens){
            block = tokens[1];
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public void build(Table table){
            table.add("block ");
            field(table, block, str -> block = str);
        }

        @Override
        public LExecutor.LInstruction build(LAssembler builder){
            return new FRLogicExecutor.FetchPlusPlusI(builder.var(block));
        }

        @Override
        public LCategory category(){
            return LCategory.world;
        }

        @Override
        public void write(StringBuilder builder){
            builder.append(name)
                .append(" ").append(block);
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

    public static class FusionBombSpawn extends LStatement{
        public static String name = "FusionBombSpawn";

        public String lifetime = "2";
        public String shootX = "11";
        public String shootY = "45";
        public String rot = "14";
        public String team = "1";

        public FusionBombSpawn(){}

        public FusionBombSpawn(String[] tokens){
            lifetime = tokens[1];
            shootX = tokens[2];
            shootY = tokens[3];
            rot = tokens[4];
            team = tokens[5];
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public void build(Table table){
            table.clearChildren();
            table.add("Spawn a fusion bomb for");
            field(table, lifetime, str -> lifetime = str);
            table.add(" sec");
            table.row();
            table.add("at (");
            field(table, shootX, str -> shootX = str);
            table.add(",");
            field(table, shootY, str -> shootY = str);
            table.add(") ");
            table.row();
            table.add("rot");
            field(table, rot, str -> rot = str);
            table.add("team");
            field(table, team, str -> team = str);
        }

        @Override
        public LExecutor.LInstruction build(LAssembler builder){
            return new FRLogicExecutor.FusionBombSpawnI(builder.var(lifetime),builder.var(shootX),builder.var(shootY),builder.var(rot),builder.var(team));
        }

        @Override
        public LCategory category(){
            return LCategory.world;
        }

        @Override
        public void write(StringBuilder builder){
            builder.append(name)
                .append(" ").append(lifetime)
                .append(" ").append(shootX)
                .append(" ").append(shootY)
                .append(" ").append(rot)
                .append(" ").append(team);
        }
    }



    public static class FlushMessagePlus extends LStatement{
        public static String name = "FlushMessagePlus";
        public MessageTypePlus type = ANNOUNCE;
        public String duration = "3", speaker = "you";
        public String x = "0.5", y = "0.5";

        public FlushMessagePlus(){}

        public FlushMessagePlus(String[] tokens){
            switch(tokens[1]){
                case "ANNOUNCE":
                    type = ANNOUNCE;
                    break;
                case "BUTTON":
                    type = BUTTON;
                    break;
                case "FREE":
                    type = FREE;
                    break;
                case "SMOOTH":
                    type = SMOOTH;
                    break;
                case "DIALOGBOXSHOW":
                    type = DIALOGBOXSHOW;
                    break;
            }
            duration = tokens[2];
            speaker = tokens[3];
            x = tokens[4];
            y = tokens[5];
        }

        @Override
        public void build(Table table){
            rebuild(table);
        }

        void rebuild(Table table){
            table.clearChildren();

            table.button(b -> {
                b.label(() -> type.name()).growX().wrap().labelAlign(Align.center);
                b.clicked(() -> showSelect(b, MessageTypePlus.all, type, o -> {
                    type = o;
                    rebuild(table);
                }, 2, c -> c.width(150f)));
            }, Styles.logict, () -> {}).size(160f, 40f).padLeft(2).color(table.color);

            switch(type){
                case ANNOUNCE, BUTTON, FREE, SMOOTH  -> {
                    table.add(" for ");
                    fields(table, duration, str -> duration = str);
                    table.add(" sec");
                    if(type == FREE || type == SMOOTH){
                        table.add(" at(");
                        fields(table, x, str -> x = str);
                        table.add(",");
                        fields(table, y, str -> y = str);
                        table.add(")");
                    }
                }
                case DIALOGBOXSHOW  -> {
                    table.add(" by ");
                    fields(table, speaker, str -> speaker = str);
                    table.add(" for ");
                    fields(table, duration, str -> duration = str);
                    table.add(" sec");
                }
            }
            row(table);
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public LExecutor.LInstruction build(LAssembler builder){
            return new FRLogicExecutor.FlushMessagePlusI(type, builder.var(duration), builder.var(speaker),builder.var(x),builder.var(y));
        }

        @Override
        public LCategory category(){
            return LCategory.world;
        }

        @Override
        public void write(StringBuilder builder){
            builder.append(name)
                .append(" ").append(type)
                .append(" ").append(duration)
                .append(" ").append(speaker)
                .append(" ").append(x)
                .append(" ").append(y);
        }
    }
}
