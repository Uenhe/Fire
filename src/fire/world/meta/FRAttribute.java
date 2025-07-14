package fire.world.meta;

import mindustry.world.meta.Attribute;

public class FRAttribute{

    public static final Attribute
        tree, grass, flesh, sporesWater;

    static{
        tree = Attribute.add("tree");
        grass = Attribute.add("grass");
        flesh = Attribute.add("flesh");
        sporesWater = Attribute.add("sporeswater");
    }

    public static void load(){}
}
