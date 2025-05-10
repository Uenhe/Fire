package fire.ai.types;

import arc.math.geom.Position;
import arc.math.geom.Vec2;
import fire.entities.abilities.DashAbility;

public class DashBuilderAI extends mindustry.ai.types.BuilderAI{

    public DashBuilderAI(){

    }

    public DashBuilderAI(int e){
        onlyAssist = true;
    }

    @Override
    public void circleAttack(float circleLength){
        super.circleAttack(circleLength);
        if(!(unit.abilities[0] instanceof DashAbility dab)) return;
        dab.dash(unit, target);
    }

    @Override
    public void circle(Position target, float circleLength, float speed){
        super.circle(target, circleLength, speed);
        if(!(unit.abilities[0] instanceof DashAbility dab)) return;
        dab.dash(unit, target);
    }

    @Override
    public void moveTo(Position target, float circleLength, float smooth, boolean keepDistance, Vec2 offset, boolean arrive){
        super.moveTo(target, circleLength, smooth, keepDistance, offset, arrive);
        if(!(unit.abilities[0] instanceof DashAbility dab)) return;
        dab.dash(unit, target);
    }
}
