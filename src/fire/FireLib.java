package fire;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;

public class FireLib{

    public static Color color(String hex){
        return Color.valueOf(hex);
    }

    public static String format(String key, Object... args){
        return Core.bundle.format(key, args);
    }

    public static TextureRegion atlas(String name){
        return Core.atlas.find(name);
    }

    public static boolean getSetting(String name){
        return Core.settings.getBool(name);
    }

    public static boolean desktop(){
        return Core.app.isDesktop();
    }

    public static boolean consValid(Building build){
        return build.efficiency > 0f;
    }

    public static void debug(Building build, String text){
        build.block.drawPlaceText(text, build.tileX(), build.tileY(), true);
    }
}
