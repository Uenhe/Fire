package fire;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.graphics.Pal;

import static mindustry.Vars.net;

public final class FRVars{

    public static final Seq<Color> colorPool = new Seq<>(45); //hardcoded initial capacity

    /** Temporary colors. */
    public static final Color
        lancer_a04 = Pal.lancerLaser.cpy().a(0.4f);

    /** Setting. */
    public static boolean
        mineSand = false, displayRange = true, specialContent = true, showLog = true, noMultiMods = true;

    static{
        Events.run(EventType.Trigger.update, () -> {
            if(Core.graphics.getFrameId() % 60 == 0){
                getSettings();
                Blocks.sand.playerUnmineable = Blocks.darksand.playerUnmineable =
                    Blocks.sandWater.playerUnmineable = Blocks.darksandWater.playerUnmineable =
                        Blocks.darksandTaintedWater.playerUnmineable = !mineSand;
            }
        });
    }

    public static void getSettings(){
        mineSand = Core.settings.getBool("minesand");
        displayRange = Core.settings.getBool("displayrange");
        specialContent = net.server() || Core.settings.getBool("specialcontent");
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
