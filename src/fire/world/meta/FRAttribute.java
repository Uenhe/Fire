package fire.world.meta;

import arc.Events;
import mindustry.content.Liquids;
import mindustry.game.EventType;
import mindustry.world.meta.Attribute;

import static mindustry.Vars.content;

public class FRAttribute{

    public static Attribute
        tree, grass, flesh, sporesWater;

    public static void load(){
        tree = Attribute.add("tree");
        grass = Attribute.add("grass");
        flesh = Attribute.add("flesh");
        sporesWater = Attribute.add("sporeswater");

        Events.on(EventType.ContentInitEvent.class, e -> {
            for(var b : content.blocks()){
                float a = b.attributes.get(Attribute.spores);
                if(a == 0.0f || !b.isFloor() || b.asFloor().liquidDrop != Liquids.water)
                    continue;

                b.attributes.set(sporesWater, a);
            }
        });
    }
}
