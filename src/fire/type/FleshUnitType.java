package fire.type;

import arc.math.Mathf;
import fire.content.FRStatusEffects;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

public class FleshUnitType extends mindustry.type.UnitType{

    public FleshUnitType(String name){
        super(name);
        healColor = Pal.neoplasm1;
    }

    @Override
    public void update(Unit unit){
        if(Mathf.chanceDelta(0.04))
            FRStatusEffects.overgrown.effect.at(unit.x, unit.y, 0.0f, unit);
    }
}
