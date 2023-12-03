package fire;

import arc.Core;
import mindustry.gen.Building;

public class FireLib{
    public static String format(String key, Object... args){
        return Core.bundle.format(key, args);
    }
    public static boolean consValid(Building build){
        return build.efficiency > 0f;
    }
}
