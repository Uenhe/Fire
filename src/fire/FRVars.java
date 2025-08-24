package fire;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.struct.Seq;
import fire.content.FRUnitTypes;
import mindustry.content.Blocks;
import mindustry.content.StatusEffects;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

import java.awt.*;

import static mindustry.Vars.headless;

public final class FRVars{

    public static final Seq<Color> colorPool = new Seq<>(48); //hardcoded initial capacity
    public static final Seq<Unit> spawnedUnits = new Seq<>();

    /** Temporary colors. */
    public static final Color
        lancer_a04 = Pal.lancerLaser.cpy().a(0.4f);

    /** Setting. */
    public static boolean
        mineSand = false, displayRange = true, showLog = true, noMultiMods = true;

    public static float equivalentWidth;

    private static final Toolkit toolkit;

    static{
        Toolkit tk;
        try{ //java.awt is not available on JRE but JDK
            tk = Toolkit.getDefaultToolkit();
        }catch(Throwable e){
            tk = null;
        }
        toolkit = tk;

        var units = spawnedUnits;
        Events.run(EventType.Trigger.update, () -> {
            if(Core.graphics.getFrameId() % 60 == 0){
                getSettings();
                Blocks.sand.playerUnmineable = Blocks.darksand.playerUnmineable =
                    Blocks.sandWater.playerUnmineable = Blocks.darksandWater.playerUnmineable =
                        Blocks.darksandTaintedWater.playerUnmineable = !mineSand;
            }

            for(var u : units){
                if(u.hasEffect(StatusEffects.invincible))
                    u.vel.clamp(0.5f, 0.5f); //prevent enemy ejecting when spawned
                else
                    units.remove(u);
            }
        });

        if(!headless) Events.run(EventType.Trigger.draw, () -> {
            if(Core.graphics.getFrameId() % 60 == 0){
                float winScl = toolkit != null ? toolkit.getScreenResolution() / 96.0f : 1.0f;
                equivalentWidth = 100.0f * Core.graphics.getWidth() / Core.settings.getInt("uiscale", 100) / winScl;
            }
        });

        Events.on(EventType.UnitSpawnEvent.class, e -> {
            if(e.unit.type.flying && e.unit.type != FRUnitTypes.pioneer) units.add(e.unit);
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
