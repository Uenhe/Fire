package fire;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.util.Interval;
import mindustry.game.EventType;
import mindustry.graphics.Pal;

import static fire.FRUtils.color;
import static fire.FRUtils.colora;

public final class FRVars{

    /** Temporary colors. */
    public static final Color
        _ff9900 = color(255, 153, 0), _ff9900_a04 = _ff9900.cpy().a(0.4f),
        _ccccff = color(204, 204, 255), _ccccff_a04 = _ccccff.cpy().a(0.4f),
        _33ccff = color(51, 204, 255), _33ccff_a04 = _33ccff.cpy().a(0.4f),
        _404040 = color(64, 64, 64), _404040_a04 = _404040.cpy().a(0.4f),
        _ea8878 = color(234, 136, 120), _ea8878_a07 = _ea8878.cpy().a(0.7f),
        _ec7458 = color(236, 116, 88), _ec7458_a07 = _ec7458.cpy().a(0.7f),

        _92f3fd = color(146, 243, 253), _6aa85e = color(106, 168, 94), _9e78dc = color(158, 120, 220),
        _ffe099 = color(255, 224, 153), _ffcf99 = color(255, 207, 153), _e3ae6f = color(227, 174, 111),
        _feb380 = color(254, 179, 128), _6e7080 = color(110, 112, 128), _8cfffb = color(140, 255, 251),
        _f6efa1 = color(246, 239, 161), _f57946 = color(245, 121, 70), _f9a27a = color(249, 162, 122),
        _ffd8e8 = color( 255, 216, 232), _ff3300 = color(255, 51, 0), _990003 = color(153, 0, 3),
        _b6c6fd = color(182, 198, 253), _fffac6 = color(255, 250, 198), _d8d97faa = colora(216, 217, 127, 170),
        _989aa4 = color(152, 154, 164), _67474b = color(103, 71, 75),

        _lancer_a04 = Pal.lancerLaser.cpy().a(0.4f);

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
