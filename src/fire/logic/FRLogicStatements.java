package fire.logic;

import arc.func.Prov;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Reflect;
import mindustry.gen.LogicIO;
import mindustry.logic.*;
import mindustry.ui.Styles;

import static fire.logic.MessageTypePlus.*;
import static mindustry.Vars.schematicBaseStart;
import static mindustry.logic.LCanvas.isCompact;

/** Special thanks to New Horizon Mod. */
@SuppressWarnings("unused")
public class FRLogicStatements{

    public static class TransitionEffectStatement extends LStatement{
        public static final String name = "transfx";

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
            }).size(140, 40).pad(2).color(table.color);
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
        public static final String name = "maskcutscene";

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
            }).size(120, 40).pad(2).color(table.color);
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
        public static final String name = "fpp";

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
        public static final String name = "removeprocessor";

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

    public static class FlushMessagePlusStatement extends LStatement{
        public static final String name = "FlushMessagePlus";

        public MessageTypePlus type = ANNOUNCE;
        public String duration = "3", speaker = "you";
        public String x = "0.5", y = "0.5";

        public FlushMessagePlusStatement(){}

        public FlushMessagePlusStatement(String[] tokens){
            type = switch(tokens[1]){
                case "BUTTON" -> BUTTON;
                case "FREE" -> FREE;
                case "SMOOTH" -> SMOOTH;
                case "DIALOGBOXSHOW" -> DIALOGBOXSHOW;
                case "SETSPEAKER" -> SETSPEAKER;
                default -> ANNOUNCE;
            };
            duration = tokens[2];
            speaker = tokens[3];
            x = tokens[4];
            y = tokens[5];
        }

        @Override
        public void build(Table table){
            table.clearChildren();

            table.button(b -> {
                b.label(() -> type.name()).growX().wrap().labelAlign(Align.center);
                b.clicked(() -> showSelect(b, all, type, o -> {
                    type = o;
                    build(table);
                }, 2, c -> c.width(220)));
            }, Styles.logict, () -> {}).size(210, 40).padLeft(2).color(table.color);

            switch(type){
                case ANNOUNCE, BUTTON, FREE, SMOOTH -> {
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
                case DIALOGBOXSHOW -> {
                    table.add(" by ");
                    fields(table, speaker, str -> speaker = str);
                    table.add(" for ");
                    fields(table, duration, str -> duration = str);
                    table.add(" sec");
                }
                case SETSPEAKER -> {
                    table.add(" by ");
                    fields(table, speaker, str -> speaker = str);
                }
            }
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public LExecutor.LInstruction build(LAssembler builder){
            return new FRLogicExecutor.FlushMessagePlusI(type, builder.var(duration), builder.var(speaker), builder.var(x), builder.var(y));
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

    public static class ShowIllustrationStatement extends LStatement{
        public static final String name = "illust";

        public IllustrationType type = IllustrationType.In;
        public boolean left = true; //right otherwise
        public String image = "\"fire-illust-alpha\"";

        public ShowIllustrationStatement(){}

        public ShowIllustrationStatement(String[] tokens){
            type = IllustrationType.all[Integer.parseInt(tokens[1])];
            left = Boolean.parseBoolean(tokens[2]);
            image = tokens[3];
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public void build(Table table){
            table.clearChildren();

            table.button(b -> {
                b.label(() -> type.name()).growX().wrap().labelAlign(Align.center);
                b.clicked(() -> showSelect(b, IllustrationType.all, type, o -> {
                    type = o;
                    build(table);
                }, 1, c -> c.width(100)));
            }, Styles.logict, () -> {}).size(80, 40).padLeft(2).color(table.color);
            table.add(" direction ");
            table.button(left ? "Left" : "Right", Styles.logict, () -> {
                left = !left;
                build(table);
            }).size(80, 40).pad(2).color(table.color);
            table.add(" image ");
            field(table, image, str -> image = str);
        }

        @Override
        public LExecutor.LInstruction build(LAssembler builder){
            return new FRLogicExecutor.ShowIllustrationI(type, left, builder.var(image));
        }

        @Override
        public LCategory category(){
            return LCategory.world;
        }

        @Override
        public void write(StringBuilder builder){
            builder.append(name)
                .append(" ").append(type.ordinal())
                .append(" ").append(left)
                .append(" ").append(image);
        }
    }

    /** @see mindustry.logic.LStatements.SetBlockStatement */
    public static class FillBlockStatement extends LStatement{
        public static final String name = "fillblock";
        public TileLayer layer = TileLayer.block;
        public String block = "@air", x1 = "0", y1 = "0", x2 = "@mapw", y2 = "@maph", team = "@derelict", rotation = "0";
        public boolean effect;

        public FillBlockStatement(){}

        public FillBlockStatement(String[] tokens){
            layer = TileLayer.all[Integer.parseInt(tokens[1])];
            block = tokens[2];
            x1 = tokens[3];
            y1 = tokens[4];
            x2 = tokens[5];
            y2 = tokens[6];
            team = tokens[7];
            rotation = tokens[8];
            effect = Boolean.parseBoolean(tokens[9]);
        }

        @Override
        public void build(Table table){
            table.clearChildren();

            table.add(LStatement.bundle("set")).padRight(4f).padLeft(6f);

            table.button(b -> {
                b.label(() -> bundle(layer));
                b.clicked(() -> showSelect(b, TileLayer.settable, layer, o -> {
                    layer = o;
                    build(table);
                }));
            }, Styles.logict, () -> {}).size(100f, 40f).pad(4f).color(table.color);

            if(isCompact()) table.add().width(200f);

            fields(table, "x1", x1, str -> x1 = str);
            fields(table, "y1", y1, str -> y1 = str);
            fields(table, "x2", x2, str -> x2 = str);
            fields(table, "y2", y2, str -> y2 = str);
            fields(table, "block", block, str -> block = str);
            if(layer == TileLayer.block){
                fields(table, "team", team, str -> team = str);
                fields(table, "rotation", rotation, str -> rotation = str);
            }
            table.add(" effect ");
            table.button(effect ? "true" : "false", Styles.logict, () -> {
                effect = !effect;
                build(table);
            }).size(80, 40).pad(2).color(table.color);
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public LExecutor.LInstruction build(LAssembler builder){
            return new FRLogicExecutor.FillBlockI(layer, builder.var(x1), builder.var(y1), builder.var(x2), builder.var(y2), builder.var(block), builder.var(team), builder.var(rotation), effect);
        }

        @Override
        public LCategory category(){
            return LCategory.world;
        }

        @Override
        public void write(StringBuilder builder){
            builder.append(name)
                .append(" ").append(layer.ordinal())
                .append(" ").append(block)
                .append(" ").append(x1)
                .append(" ").append(y1)
                .append(" ").append(x2)
                .append(" ").append(y2)
                .append(" ").append(team)
                .append(" ").append(rotation)
                .append(" ").append(effect);
        }
    }

    public static class ParseSchematicStatement extends LStatement{
        public static final String name = "parsesche";
        public String base64code = "\"" + schematicBaseStart + "..\"", outWidth = "outWidth", outHeight = "outHeight";

        public ParseSchematicStatement(){}

        public ParseSchematicStatement(String[] tokens){
            base64code = tokens[1];
            outWidth = tokens[2];
            outHeight = tokens[3];
        }

        @Override
        public void build(Table table){
            fields(table, "base64code", base64code, str -> base64code = str);
            fields(table, "outWidth", outWidth, str -> outWidth = str);
            fields(table, "outHeight", outHeight, str -> outHeight = str);
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public LExecutor.LInstruction build(LAssembler builder){
            return new FRLogicExecutor.ParseSchematicI(builder.var(base64code), builder.var(outWidth), builder.var(outHeight));
        }

        @Override
        public LCategory category(){
            return LCategory.world;
        }

        @Override
        public void write(StringBuilder builder){
            builder.append(name)
                .append(" ").append(base64code)
                .append(" ").append(outWidth)
                .append(" ").append(outHeight);
        }
    }

    public static class PlaceSchematicStatement extends LStatement{
        public static final String name = "placesche";
        public String x = "0", y = "0", team = "@sharded", rotation = "0";
        public SchematicFlipType flip = SchematicFlipType.None;

        public PlaceSchematicStatement(){}

        public PlaceSchematicStatement(String[] tokens){
            x = tokens[1];
            y = tokens[2];
            rotation = tokens[3];
            team = tokens[4];
            flip = SchematicFlipType.all[Integer.parseInt(tokens[5])];
        }

        @Override
        public void build(Table table){
            table.clearChildren();

            fields(table, "x", x, str -> x = str);
            fields(table, "y", y, str -> y = str);
            fields(table, "team", team, str -> team = str);
            fields(table, "rotation", rotation, str -> rotation = str);
            table.add(" flip ");
            table.button(b -> {
                b.label(() -> flip.name()).growX().wrap().labelAlign(Align.center);
                b.clicked(() -> showSelect(b, SchematicFlipType.all, flip, o -> {
                    flip = o;
                    build(table);
                }, 1, c -> c.width(170)));
            }, Styles.logict, () -> {}).size(130, 40).padLeft(2).color(table.color);
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public LExecutor.LInstruction build(LAssembler builder){
            return new FRLogicExecutor.PlaceSchematicI(builder.var(x), builder.var(y), builder.var(team), builder.var(rotation), flip);
        }

        @Override
        public LCategory category(){
            return LCategory.world;
        }

        @Override
        public void write(StringBuilder builder){
            builder.append(name)
                .append(" ").append(x)
                .append(" ").append(y)
                .append(" ").append(rotation)
                .append(" ").append(team)
                .append(" ").append(flip.ordinal());
        }
    }

    @SuppressWarnings("unchecked")
    public static void load(){
        Class<?>[] innerClasses = FRLogicStatements.class.getDeclaredClasses();

        //sequence of inner classes is not guaranteed, but on my device if you iterate it backwards it's sorted
        for(int i = innerClasses.length - 1; i >= 0; i--){
            var clazz = innerClasses[i];
            LAssembler.customParsers.put(Reflect.get(clazz, "name"), tokens -> {
                try{
                    return (LStatement)clazz.getDeclaredConstructor(String[].class).newInstance((Object)tokens);
                }catch(Throwable e){
                    throw new RuntimeException(e);
                }
            });
            LogicIO.allStatements.addUnique(((Prov<LStatement>)Reflect.cons(clazz)));
        }
    }
}
