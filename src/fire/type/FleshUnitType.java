package fire.type;

import arc.math.Mathf;
import fire.content.FStatusEffects;
import mindustry.gen.Unit;

public class FleshUnitType extends mindustry.type.UnitType{

    public FleshUnitType(String name){
        super(name);
        healColor = mindustry.graphics.Pal.neoplasm1;
    }

    @Override
    public void update(Unit unit){
        super.update(unit);
        if(Mathf.chanceDelta(0.04))
            FStatusEffects.overgrown.effect.at(unit.x, unit.y, 0.0f, unit);
    }
}
