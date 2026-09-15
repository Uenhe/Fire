package fire;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Time;
import fire.content.FRStatusEffects;
import fire.content.FRUnitTypes;
import fire.logic.FRLogicExecutor;
import fire.ui.dialogs.FRContentInfoDialog;
import fire.world.CheatMode;
import mindustry.content.Blocks;
import mindustry.content.StatusEffects;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.dialogs.BaseDialog;

import java.awt.*;
import java.util.HashMap;

import static fire.FireMod.CheatStatusCode.OK;
import static fire.FireMod.cheatBlocks;
import static fire.FireMod.checkCheating;
import static mindustry.Vars.*;

public final class FRVars{

    public static final HashMap<String, Color> colorPool = new HashMap<>(64);
    public static final Seq<Unit> spawnedUnits = new Seq<>();

    /** Temporary colors. */
    public static final Color
        lancer_a04 = Pal.lancerLaser.cpy().a(0.4f);

    /** Setting. */
    public static boolean
        mineSand = false, displayRange = true, showLog = true, noMultiMods = true, cheatMode = false;

    public static short equivalentWidth;
    public static FRContentInfoDialog moddedContent;
    public static final float layerFlyingUnitAbove = Layer.flyingUnit + 1;
    public static final Bloom bloomFlyingUnitAbove = new Bloom(true);

    private static float contentDialogTimer;
    private static BaseDialog cheatDialog;
    private static final Toolkit toolkit;

    static{
        Toolkit tk;
        try{ //java.awt is not available on JRE but JDK
            tk = Toolkit.getDefaultToolkit();
        }catch(Throwable e){
            tk = null;
        }
        toolkit = tk;

        Events.run(EventType.Trigger.update, () -> {
            if(!headless && moddedContent != null && (contentDialogTimer += Time.delta) >= 2.0f){
                contentDialogTimer -= 2.0f;
                //a weird way to update the dialog
                //isShown() is unavailable here
                if(moddedContent.shown && moddedContent.current == FRStatusEffects.informationalProjection)
                    ui.content.show(FRStatusEffects.informationalProjection);
            }

            long frame = Core.graphics.getFrameId();
            if(frame % 10 == 0){
                for(var u : spawnedUnits){
                    if(u.hasEffect(StatusEffects.invincible))
                        u.vel.clamp(0.5f, 0.5f); //prevent enemy ejecting when spawned
                    else
                        spawnedUnits.remove(u);
                }

                final float ratio = FRLogicExecutor.MaskCutsceneI.ratio, height = Core.graphics.getHeight();
                var mask = FRLogicExecutor.MaskCutsceneI.masks[0];
                if(mask.hasActions() && mask.y >= -height * ratio * 0.5f){
                    ui.hudfrag.shown = false;
                }else if(mask.y <= -height * ratio * 0.5f && mask.y > -height * ratio && FRLogicExecutor.MaskCutsceneI.maskOut && FRLogicExecutor.MaskCutsceneI.previousShown){
                    ui.hudfrag.shown = true;
                }

                if(frame % 60 == 0){
                    getSettings();
                    Blocks.sand.playerUnmineable = Blocks.darksand.playerUnmineable =
                        Blocks.sandWater.playerUnmineable = Blocks.darksandWater.playerUnmineable =
                            Blocks.darksandTaintedWater.playerUnmineable = !mineSand;

                    CheatMode.update();

                    if(cheatDialog == null){
                        var code = checkCheating();
                        Table table = null;
                        if(code != OK){
                            cheatDialog = new BaseDialog("Warning");
                            table = cheatDialog.cont.pane(t -> {}).getTable();
                            table.row();
                        }

                        switch(code){
                            case CHEAT_BLOCK:
                                var sb = new StringBuilder();
                                var buildingTypes = Team.get(player.team().id).data().buildingTypes;

                                for(var block : cheatBlocks){
                                    var builds = buildingTypes.get(block);
                                    if(builds == null || builds.isEmpty()) continue;
                                    sb.append("\n").append(block.localizedName);
                                }
                                table.add(Core.bundle.format("fire.err1", sb)).center().row();
                                break;

                            case CHEAT_RULE:
                                table.add("@fire.err2").center();
                        }
                        if(code != OK){
                            table.row().add("@fire.err9").center();
                            cheatDialog.show();
                        }
                    }
                }
            }
        });

        if(!headless) Events.run(EventType.Trigger.draw, () -> {
            if(Core.graphics.getFrameId() % 60 == 0)
                equivalentWidth = (short)(100.0f * Core.graphics.getWidth() / Core.settings.getInt("uiscale", 100) / (toolkit != null ? toolkit.getScreenResolution() / 96.0f : 1.0f));

            final float epsilon = 0.0001f;
            bloomFlyingUnitAbove.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            Draw.draw(layerFlyingUnitAbove - epsilon, bloomFlyingUnitAbove::capture);
            Draw.draw(layerFlyingUnitAbove + epsilon, bloomFlyingUnitAbove::render);
        });

        Events.on(EventType.UnitSpawnEvent.class, e -> {
            if(e.unit.type.flying && e.unit.type != FRUnitTypes.pioneer)
                spawnedUnits.add(e.unit);
        });
    }

    public static void getSettings(){
        mineSand = Core.settings.getBool("minesand");
        displayRange = Core.settings.getBool("displayrange");
        showLog = Core.settings.getBool("showlog");
        noMultiMods = Core.settings.getBool("nomultimods");
        cheatMode = Core.settings.getBool("cheatmode");
    }

    public static Color find(String hex){
        if(hex.length() == 6) hex += "ff";
        var color = colorPool.get(hex);
        if(color != null) return color;

        color = Color.valueOf(hex);
        colorPool.put(hex, color);

//        int size = colorPool.size();
//        if(DEBUG.isDeveloper() && size > 64)
//            Log.info("Resized Color Pool Length: ", size);

        return color;
    }
}
