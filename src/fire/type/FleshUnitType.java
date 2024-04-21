package fire.type;

public class FleshUnitType extends mindustry.type.UnitType{

    public FleshUnitType(String name){
        super(name);
        healColor = mindustry.graphics.Pal.neoplasm1;
    }

    @Override
    public void update(mindustry.gen.Unit unit){
        super.update(unit);
        if(arc.math.Mathf.chanceDelta(0.04))
            fire.content.FireStatusEffects.overgrown.effect.at(unit.x, unit.y, 0f, unit);
    }
}
