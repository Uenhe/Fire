package fire;

import arc.Core;
import arc.Events;
import arc.util.Interval;
import mindustry.game.EventType;

public final class FRVars{

    /** Setting. */
    public static boolean
        mineSand, displayRange, showLogs, noMultiMods;

    private static final Interval timer = new Interval(1);

    static{{
        Events.run(EventType.Trigger.update, () -> {
            if(timer.get(0, 60.0f)){
                mineSand = Core.settings.getBool("minesand");
                displayRange = Core.settings.getBool("displayrange");
                showLogs = Core.settings.getBool("showlogs");
                noMultiMods = Core.settings.getBool("nomultimods");
            }
        });
    }}
}
