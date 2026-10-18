package fire.logic;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import fire.content.FRFx;
import fire.ui.FRUI;
import mindustry.content.Blocks;
import mindustry.core.UI;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.logic.LExecutor;
import mindustry.logic.LVar;
import mindustry.logic.TileLayer;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OverlayFloor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;

public class FRLogicExecutor{

    static final IntMap<Schematic> execScheMap = new IntMap<>();
    static final ObjectMap<String, Schematic> strScheMap = new ObjectMap<>();
    static{
        Events.on(EventType.ResetEvent.class, e -> {
            execScheMap.clear();
            strScheMap.clear();
        });
    }

    public static class TransitionEffectI implements LExecutor.LInstruction{
        boolean out;
        LVar unit;

        public TransitionEffectI(boolean out, LVar unit){
            this.out = out;
            this.unit = unit;
        }

        @Override
        public void run(LExecutor exec){
            if(!(unit.obj() instanceof Unit u)) return;
            FRFx.transitionEffect.at(u.x, u.y, u.rotation, u.team.color, new FRFx.TransitionFxData(u.type, out));
        }
    }

    public static class MaskCutsceneI implements LExecutor.LInstruction{
        public static final float ratio = 0.2f;
        public static boolean previousShown, maskOut;
        public static final Image[] masks = {new Image(), new Image()};
        static{
            Events.on(EventType.ResetEvent.class, e -> {
                for(var m : masks){
                    m.clear();
                    m.remove();
                }
            });
        }

        boolean out;
        LVar duration;

        public MaskCutsceneI(boolean out, LVar duration){
            this.out = out;
            this.duration = duration;
        }

        @Override
        public void run(LExecutor exec){
            maskOut = out;
            if(!out) previousShown = ui.hudfrag.shown;
            float t = duration.numf();
            for(int i = 0; i < 2; i++){
                var mask = masks[i];
                float height = Core.graphics.getHeight(),
                y0 = height * (i == 0 ? -ratio : 1.0f),
                yt = height * (i == 0 ? 0.0f : 1.0f - ratio);

                if(!out){
                    mask.color.set(Color.black);
                    mask.touchable = Touchable.disabled;
                    mask.setSize(Core.graphics.getWidth(), height * ratio);
                    mask.y = y0;

                    mask.actions(Actions.moveTo(0.0f, yt, t, Interp.smoother));
                    mask.update(() -> {
                        mask.toFront();
                        if(state.isMenu()) mask.remove();
                    });

                    Core.scene.add(mask);

                }else{
                    mask.actions(
                        Actions.moveTo(0.0f, y0, t, Interp.smoother),
                        Actions.delay(t, Actions.remove()));
                }
            }
        }
    }

    public static class FetchPlusPlusI implements LExecutor.LInstruction{
        LVar block;

        public FetchPlusPlusI(LVar block){
            this.block = block;
        }

        @Override
        public void run(LExecutor exec){
            if(!(block.obj() instanceof Block b)) return;

            for(int i = 0, sum = 0; i < 256; i++){
                var builds = Team.get(i).data().buildingTypes.get(b);
                if(builds == null) continue;

                sum += builds.size;
                if(sum > 1) return;
            }

            exec.counter.numval--;
        }
    }

    /** Too lazy. */
    public static class RemoveProcessorI implements LExecutor.LInstruction{

        @Override
        public void run(LExecutor exec){
            var tile = exec.thisv.building().tile;
            var wall = tile.floor().wall;
            tile.setNet(wall instanceof StaticWall ? wall : Blocks.stoneWall);
        }
    }

    public static class FlushMessagePlusI implements LExecutor.LInstruction{
        MessageTypePlus type;
        LVar duration;
        LVar speaker;
        LVar x;
        LVar y;

        public FlushMessagePlusI(MessageTypePlus type, LVar duration, LVar speaker, LVar x, LVar y){
            this.type = type;
            this.duration = duration;
            this.speaker = speaker;
            this.x = x;
            this.y = y;
        }

        @Override
        public void run(LExecutor exec){
            String text = UI.formatIcons(exec.textBuffer.toString());
            if(text.startsWith("@")){
                String substr = text.substring(1);
                if(Core.bundle.has(substr))
                    text = Core.bundle.get(substr);
            }

            switch(type){
                case ANNOUNCE -> FRUI.announce(text, duration.numf());
                case BUTTON -> FRUI.bottom(text, duration.numf());
                case FREE -> FRUI.freeShow(text, duration.numf(), x.numf(), y.numf());
                case SMOOTH -> FRUI.smoothShow(text, duration.numf(), x.numf(), y.numf());
                case DIALOGBOXSHOW -> {
                    setSpeaker(speaker);
                    FRUI.tableShow(duration.numf());
                }
                case SETSPEAKER -> setSpeaker(speaker);
            }

            exec.textBuffer.setLength(0);
        }

        static void setSpeaker(LVar lvar){
            if(lvar.obj() instanceof Unit u){
                FRUI.label = u.type.localizedName;
                FRUI.image = u.type.uiIcon;
                FRUI.speakerTeam = u.team;

            }else{
                UnitType type;
                String spk = lvar.obj() instanceof String s ? s : lvar.name;
                if("you".equals(spk)){
                    if(player.unit() == null){
                        var core = player.bestCore();
                        if(core == null){
                            FRUI.label = "";
                            return;
                        }
                        type = ((CoreBlock)core.block).unitType;
                    }else{
                        type = player.unit().type;
                    }
                }else{
                    FRUI.label = spk;
                    FRUI.image = Icon.admin.getRegion();
                    FRUI.speakerTeam = player.team();
                    return;
                }
                FRUI.label = type.localizedName;
                FRUI.image = type.uiIcon;
                FRUI.speakerTeam = player.team();
            }
        }
    }

    public static class ShowIllustrationI implements LExecutor.LInstruction{
        static final Image[] illusts = {new Image(), new Image()};
        static{
            Events.on(EventType.ResetEvent.class, e -> {
                for(var il : illusts){
                    il.setPosition(0, 0);
                    il.remove();
                    il.clear();
                }
            });
        }

        IllustrationType type;
        boolean left;
        LVar image;

        public ShowIllustrationI(IllustrationType type, boolean left, LVar image){
            this.type = type;
            this.left = left;
            this.image = image;
        }

        @Override
        public void run(LExecutor exec){
            var illust = illusts[left ? 0 : 1];
            var region = Core.atlas.find(image.obj() instanceof String s ? s : "error");
            final float duration = 0.75f,
                ratio = Core.graphics.getHeight() / 1494f,
                illustWidth = region.width * ratio, illustHeight = 600 * ratio,
                screenWidth = Core.graphics.getWidth();
            float w;

            if(illust.hasActions()) illust.clearActions();
            switch(type){
                case In -> {
                    illust.setDrawable(region);
                    illust.touchable = Touchable.disabled;
                    illust.setSize(illustWidth, illustHeight);
                    illust.setColor(Color.white);

                    if(illust.x == 0){ //init
                        illust.setPosition(left ? -illustWidth : screenWidth, -illustHeight/3);
                        illust.update(() -> {
                            illust.toFront();
                            if(state.isMenu()) illust.remove();
                        });
                        Core.scene.add(illust);
                    }

                    w = 50 * ratio;
                    illust.actions(Actions.moveTo(left ? w : screenWidth - illustWidth - w, 100, duration, Interp.pow3Out));
                }

                case Fade -> {
                    illust.setColor(Color.lightGray);

                    w = 15 * ratio;
                    illust.actions(Actions.moveTo(left ? w : screenWidth - illustWidth - w, 25, duration, Interp.pow3Out));
                }

                case Out -> illust.actions(
                    Actions.moveTo(left ? -illustWidth : screenWidth, -illustHeight/3, duration, Interp.pow3Out),
                    Actions.delay(duration, Actions.remove()));
            }
        }
    }

    /** @see mindustry.logic.LExecutor.SetBlockI  */
    public static class FillBlockI implements LExecutor.LInstruction{
        /** Passes packed width and height as rotation.
         * @see mindustry.content.Fx#placeBlock */
        static final Effect placeEffect = new Effect(16, 400, e -> {
            Draw.color(Pal.accent);
            Lines.stroke(3f - e.fin() * 2f);
            float f = tilesize + e.fin() * 3f;
            Point2 unpacked = Point2.unpack(Float.floatToRawIntBits(e.rotation));
            Lines.rect(e.x, e.y, unpacked.x * tilesize + f, unpacked.y * tilesize + f);
        });

        TileLayer layer;
        LVar x1, y1, x2, y2;
        LVar block;
        LVar team, rotation;
        boolean effect;

        public FillBlockI(TileLayer layer, LVar x1, LVar y1, LVar x2, LVar y2, LVar block, LVar team, LVar rotation, boolean effect){
            this.layer = layer;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.block = block;
            this.team = team;
            this.rotation = rotation;
            this.effect = effect;
        }

        @Override
        public void run(LExecutor exec){
            if(net.client() || !(block.obj() instanceof Block b)) return;

            int m = x1.numi(), n = y1.numi(), u = x2.numi(), v = y2.numi(), rot = rotation.numi(),
                x = Math.min(m, u), y = Math.min(n, v), y0 = y, fx = Math.max(m, u), fy = Math.max(n, v);

            if(effect)
                placeEffect.at(x * tilesize - 3, y * tilesize - 3, Float.intBitsToFloat(Point2.pack(fx - x, fy - y)));

            for(;x <= fx; x++) for(y = y0; y <= fy; y++){
                Tile tile = world.tile(x, y);
                if(tile == null) continue;

                switch(layer){
                    case ore -> {
                        if((b instanceof OverlayFloor || b == Blocks.air) && tile.overlay() != b)
                            tile.setOverlayNet(b);
                    }
                    case floor -> {
                        if(b instanceof Floor f && tile.floor() != f && !f.isOverlay() && !f.isAir())
                            tile.setFloorNet(f);
                    }
                    case block -> {
                        if(b.isFloor() && b != Blocks.air) continue;

                        Team t = team.team();
                        if(t == null) t = Team.derelict;

                        if(tile.block() != b || tile.team() != t)
                            tile.setNet(b, t, rot);
                    }
                }
            }
        }
    }

    public static class ParseSchematicI implements LExecutor.LInstruction{
        //occurs an IO Exception on save if a LVar wraps a Schematic,
        //so I have to store it in a buffer
        LVar scheBase64;
        LVar outWidth, outHeight;

        public ParseSchematicI(LVar scheBase64, LVar outWidth, LVar outHeight){
            this.scheBase64 = scheBase64;
            this.outWidth = outWidth;
            this.outHeight = outHeight;
        }

        @Override
        public void run(LExecutor exec){
            if(!(scheBase64.obj() instanceof String s)) return;
            try{
                var sche = strScheMap.get(s);
                if(sche == null)
                    strScheMap.put(s, sche = Schematics.readBase64(s));

                execScheMap.put(exec.thisv.building().pos(), sche);
                outWidth.setnum(sche.width);
                outHeight.setnum(sche.height);

            }catch(RuntimeException ignored){}
        }
    }

    public static class PlaceSchematicI implements LExecutor.LInstruction{
        LVar x, y;
        LVar team;
        LVar rotation;
        SchematicFlipType flip;

        static final Seq<BuildPlan> plans = new Seq<>(false, 32, BuildPlan.class);

        public PlaceSchematicI(LVar x, LVar y, LVar team, LVar rotation, SchematicFlipType flip){
            this.x = x;
            this.y = y;
            this.team = team;
            this.rotation = rotation;
            this.flip = flip;
        }

        @Override
        public void run(LExecutor exec){
            if(net.client()) return;
            var sche = execScheMap.get(exec.thisv.building().pos());
            if(sche == null) return;

            int x0 = x.numi(), y0 = y.numi();

            plans.clear().addAll(schematics.toPlans(sche, x0 + sche.width/2, y0 + sche.height/2, false));

            for(int i = 0, n = rotation.numi() % 4; i < n; i++)
                rotatePlans(x0, y0);

            if(flip != SchematicFlipType.None)
                flipPlans(flip == SchematicFlipType.Horizontal, x0, y0);

            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            for(var plan : plans){
                int bot = -(plan.block.size - 1) / 2;
                minX = Math.min(minX, plan.x + bot);
                minY = Math.min(minY, plan.y + bot);
            }

            Team t = team.team();
            if(t == null) t = Team.derelict;

            for(var plan : plans){
                int px = plan.x, py = plan.y;
                if(minX != Integer.MAX_VALUE){
                    px += x0 - minX;
                    py += y0 - minY;
                }
                var tile = world.tile(px, py);
                if(tile == null) continue;

                tile.setNet(plan.block, t, plan.rotation);
                if(tile.build != null)
                    tile.build.configured(null, plan.config);
            }
        }

        static void rotatePlans(int ox, int oy){
            for(var plan : plans){
                float off = plan.block.size % 2 == 0 ? -0.5f : 0f;

                plan.pointConfig(p ->
                    p.set(Mathf.floor(p.y), Mathf.floor(-p.x - off * 2)));

                float wx = (plan.y - oy) * tilesize + plan.block.offset,
                    wy = (ox - plan.x) * tilesize - plan.block.offset;

                plan.x = World.toTile(wx - plan.block.offset) + ox;
                plan.y = World.toTile(wy - plan.block.offset) + oy;
                plan.rotation = plan.block.planRotation((plan.rotation - 1) % 4);
            }
        }

        static void flipPlans(boolean horizontal, int ox, int oy){
            int origin = (horizontal ? ox : oy) * tilesize;

            for(var plan : plans){
                float value = -((horizontal ? plan.x : plan.y) * tilesize - origin + plan.block.offset) + origin;

                if(horizontal)
                    plan.x = (int)((value - plan.block.offset) / tilesize);
                else
                    plan.y = (int)((value - plan.block.offset) / tilesize);

                plan.pointConfig(p -> {
                    if(horizontal){
                        if(plan.block.size % 2 == 0) p.x--;
                        p.x = -p.x;
                    }else{
                        if(plan.block.size % 2 == 0) p.y--;
                        p.y = -p.y;
                    }
                });

                plan.block.flipRotation(plan, horizontal);
            }
        }
    }
}
