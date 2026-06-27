package fire;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import fire.content.FRUnitTypes;
import mindustry.content.Blocks;
import mindustry.content.StatusEffects;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.ui.dialogs.BaseDialog;

import java.awt.*;

import static fire.FireMod.CheatStatusCode.OK;
import static fire.FireMod.cheatBlocks;
import static fire.FireMod.checkCheating;
import static mindustry.Vars.headless;
import static mindustry.Vars.player;

public final class FRVars{

    public static final Seq<Color> colorPool = new Seq<>(48); //hardcoded initial capacity
    public static final Seq<Unit> spawnedUnits = new Seq<>();

    /** Temporary colors. */
    public static final Color
        lancer_a04 = Pal.lancerLaser.cpy().a(0.4f);

    /** Setting. */
    public static boolean
        mineSand = false, displayRange = true, showLog = true, noMultiMods = true;

    public static short equivalentWidth;

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
            if(Core.graphics.getFrameId() % 60 == 0){
                getSettings();
                Blocks.sand.playerUnmineable = Blocks.darksand.playerUnmineable =
                    Blocks.sandWater.playerUnmineable = Blocks.darksandWater.playerUnmineable =
                        Blocks.darksandTaintedWater.playerUnmineable = !mineSand;

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
                            StringBuilder sb = new StringBuilder();
                            for(var block : cheatBlocks){
                                var builds = Team.get(player.team().id).data().buildingTypes.get(block);
                                if(builds == null) continue;
                                if(builds.size > 0)
                                    sb.append("\n").append(block.localizedName);
                            }
                            table.add(Core.bundle.format("fire.err1", sb.toString())).center().row();
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

            for(var u : spawnedUnits){
                if(u.hasEffect(StatusEffects.invincible))
                    u.vel.clamp(0.5f, 0.5f); //prevent enemy ejecting when spawned
                else
                    spawnedUnits.remove(u);
            }
        });

        if(!headless)
            Events.run(EventType.Trigger.draw, () -> {
                if(Core.graphics.getFrameId() % 60 == 0)
                    equivalentWidth = (short)(100.0f * Core.graphics.getWidth() / Core.settings.getInt("uiscale", 100) / (toolkit != null ? toolkit.getScreenResolution() / 96.0f : 1.0f));
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
    }

    public static Color find(String hex){
        var result = colorPool.find(c -> (hex.length() == 6 ? hex + "ff" : hex).equals(c.toString()));
        if(result == null){
            var color = Color.valueOf(hex);
            colorPool.add(color);
            return color;
        }
        return result;
    }
}
