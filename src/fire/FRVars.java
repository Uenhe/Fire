package fire;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.graphics.Pal;

import static arc.graphics.Color.valueOf;
import static mindustry.Vars.net;

public final class FRVars{

    /** Temporary colors. */
    public static final Color
        _ff9900 = valueOf("ff9900"), _ff9900_a04 = _ff9900.cpy().a(0.4f),
        _ccccff = valueOf("ccccff"), _ccccff_a04 = _ccccff.cpy().a(0.4f),
        _33ccff = valueOf("33ccff"), _33ccff_a04 = _33ccff.cpy().a(0.4f),
        _404040 = valueOf("404040"), _404040_a04 = _404040.cpy().a(0.4f),
        _ea8878 = valueOf("ea8878"), _ea8878_a07 = _ea8878.cpy().a(0.7f),
        _ec7458 = valueOf("ec7458"), _ec7458_a07 = _ec7458.cpy().a(0.7f),
        _92f3fd = valueOf("92f3fd"), _6aa85e = valueOf("6aa85e"), _9e78dc = valueOf("9e78dc"),
        _ffe099 = valueOf("ffe099"), _ffcf99 = valueOf("ffcf99"), _e3ae6f = valueOf("e3ae6f"),
        _feb380 = valueOf("feb380"), _6e7080 = valueOf("6e7080"), _8cfffb = valueOf("8cfffb"),
        _f6efa1 = valueOf("f6efa1"), _f57946 = valueOf("f57946"), _f9a27a = valueOf("f9a27a"),
        _ffd8e8 = valueOf("ffd8e8"), _ff3300 = valueOf("ff3300"), _990003 = valueOf("990003"),
        _b6c6fd = valueOf("b6c6fd"), _fffac6 = valueOf("fffac6"), _d8d97faa = valueOf("d8d97faa"),
        _989aa4 = valueOf("989aa4"), _67474b = valueOf("67474b"), _ffef99 = valueOf("ffef99"),
        _ffa166 = valueOf("ffa166"), _ffa16670 = valueOf("ffa16670"),
        _444444 = valueOf("444444"), _44444488 = valueOf("44444488"),

        _lancer_a04 = Pal.lancerLaser.cpy().a(0.4f);

    /** Setting. */
    public static boolean
        mineSand = false, displayRange = true, specialContent = true, showLog = true, noMultiMods = true;

    static{
        Events.run(EventType.Trigger.update, () -> {
            if(Core.graphics.getFrameId() % 60 == 0){
                getSettings();
                Blocks.sand.playerUnmineable =
                    Blocks.darksand.playerUnmineable =
                        Blocks.sandWater.playerUnmineable =
                            Blocks.darksandWater.playerUnmineable =
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
}
