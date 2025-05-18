package fire.world.meta;

import arc.Events;
import mindustry.content.Liquids;
import mindustry.game.EventType;
import mindustry.world.meta.Attribute;

import static mindustry.Vars.content;

public class FRAttribute{

    public static final Attribute
        tree, grass, flesh, sporesWater;

    static{
        tree = Attribute.add("tree");
        grass = Attribute.add("grass");
        flesh = Attribute.add("flesh");
        sporesWater = Attribute.add("sporeswater");
    }

    public static void load(){
        Events.on(EventType.ContentInitEvent.class, e -> {
            for(var b : content.blocks()){
                if(!b.isFloor() || b.asFloor().liquidDrop != Liquids.water) continue;

                float a = b.attributes.get(Attribute.spores);
                if(a == 0.0f) continue;

                b.attributes.set(sporesWater, a);
            }
        });
    }
}
