package fire.ai.types;

import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Structs;
import fire.entities.abilities.DashAbility;
import mindustry.ai.types.GroundAI;
import mindustry.entities.units.AIController;

public class DashBuilderAI extends mindustry.ai.types.BuilderAI{

    private DashAbility dash;

    public DashBuilderAI(boolean e){
        onlyAssist = e;
    }

    @Override
    public void init(){
        dash = (DashAbility)Structs.find(unit.abilities, ab -> ab instanceof DashAbility);
    }

    @Override
    public void moveTo(Position target, float circleLength, float smooth, boolean keepDistance, Vec2 offset, boolean arrive){
        super.moveTo(target, circleLength, smooth, keepDistance, offset, arrive);
        if(dash != null) dash.dash(unit, target);
    }

    @Override
    public AIController fallback(){
        return unit.type.flying ? new DashFlyingAI() : new GroundAI();
    }
}
