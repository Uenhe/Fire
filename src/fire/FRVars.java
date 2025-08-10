package fire;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.StatusEffects;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

import static mindustry.Vars.net;

public final class FRVars{

    public static final Seq<Color> colorPool = new Seq<>(48); //hardcoded initial capacity
    public static final Seq<Unit> spawnedUnits = new Seq<>();

    /** Temporary colors. */
    public static final Color
        lancer_a04 = Pal.lancerLaser.cpy().a(0.4f);

    /** Setting. */
    public static boolean
        mineSand = false, displayRange = true, specialContent = true, showLog = true, noMultiMods = true;

    static{
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

        Events.on(EventType.UnitSpawnEvent.class, e -> {
            if(e.unit.type.flying) units.add(e.unit);
        });
    }

    public static void getSettings(){
        mineSand = Core.settings.getBool("minesand");
        displayRange = Core.settings.getBool("displayrange");
        specialContent = net.server() || Core.settings.getBool("specialcontent");
        showLog = Core.settings.getBool("showlog");
        //noMultiMods = Core.settings.getBool("nomultimods");
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
