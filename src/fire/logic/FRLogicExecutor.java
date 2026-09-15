package fire.logic;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.math.Interp;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import fire.content.FRFx;
import fire.ui.FRUI;
import mindustry.content.Blocks;
import mindustry.core.UI;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.logic.LExecutor;
import mindustry.logic.LVar;
import mindustry.world.Block;
import mindustry.world.blocks.environment.StaticWall;

import static mindustry.Vars.state;
import static mindustry.Vars.ui;

public class FRLogicExecutor{

    public static class TransitionEffectI implements LExecutor.LInstruction{

        public boolean out;
        public LVar unit;

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
                for(var m : masks) m.remove();
            });
        }

        public boolean out;
        public LVar duration;

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
                    mask.actions(Actions.moveTo(0.0f, y0, t, Interp.smoother),
                        Actions.delay(t, Actions.remove()));
                }
            }
        }
    }

    public static class FetchPlusPlusI implements LExecutor.LInstruction{

        public LVar block;

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

        public FlushMessagePlusI(MessageTypePlus lifetime, LVar duration, LVar speaker, LVar x, LVar y){
            this.type = lifetime;
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
                if(Core.bundle.has(substr)){
                    text = Core.bundle.get(substr);
                }
            }

            switch(type){
                case ANNOUNCE -> FRUI.announce(text, duration.numf());
                case BUTTON -> FRUI.bottom(text, duration.numf());
                case FREE -> FRUI.freeShow(text, duration.numf(), x.numf(), y.numf());
                case SMOOTH -> FRUI.smoothShow(text, duration.numf(), x.numf(), y.numf());
                case DIALOGBOXSHOW -> FRUI.tableShow(text, duration.numf());
            }

            exec.textBuffer.setLength(0);
        }
    }
}
