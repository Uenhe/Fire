package fire.type;

import fire.content.FStatusEffects;

public class FleshUnitType extends mindustry.type.UnitType{

    public FleshUnitType(String name){
        super(name);
        healColor = mindustry.graphics.Pal.neoplasm1;
    }

    @Override
    public void update(mindustry.gen.Unit unit){
        super.update(unit);
        if(arc.math.Mathf.chanceDelta(0.04))
            FStatusEffects.overgrown.effect.at(unit.x, unit.y, 0f, unit);
    }
}
