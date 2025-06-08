package fire.ai.types;

import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Structs;
import fire.entities.abilities.DashAbility;

import static mindustry.Vars.state;

public class DashFlyingAI extends mindustry.ai.types.FlyingAI{

    private DashAbility dash;

    @Override
    public void init(){
        dash = (DashAbility)Structs.find(unit.abilities, ab -> ab instanceof DashAbility);
    }

    @Override
    public void circleAttack(float circleLength){
        super.circleAttack(circleLength);
        if(dash != null && !(unit.team == state.rules.waveTeam && !unit.damaged())) dash.dash(unit, target);
    }

    @Override
    public void moveTo(Position target, float circleLength, float smooth, boolean keepDistance, Vec2 offset, boolean arrive){
        super.moveTo(target, circleLength, smooth, keepDistance, offset, arrive);
        if(dash != null && !(unit.team == state.rules.waveTeam && !unit.damaged())) dash.dash(unit, target);
    }
}
