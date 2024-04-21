package fire.world.meta;

import mindustry.world.meta.Attribute;

public class FireAttribute{

    public static Attribute
        tree, grass, flesh;

    public static void load(){
        tree = Attribute.add("tree");
        grass = Attribute.add("grass");
        flesh = Attribute.add("flesh");
    }
}
