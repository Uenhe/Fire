package fire.ai.types;

import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Structs;
import fire.entities.abilities.DashAbility;

import static mindustry.Vars.pathfinder;

public class DashRepairAI extends mindustry.ai.types.RepairAI{

    private DashAbility dash;

    @Override
    public void init(){
        dash = (DashAbility)Structs.find(unit.abilities, ab -> ab instanceof DashAbility);
    }

    public void pathfind(int pathTarget, boolean stopAtTargetTile){
        var tile = unit.tileOn();
        if(tile == null) return;
        var targetTile = pathfinder.getField(unit.team, unit.type.flowfieldPathType, pathTarget).getNextTile(tile);
        if((tile == targetTile && stopAtTargetTile) || !unit.canPass(targetTile.x, targetTile.y)) return;
        unit.movePref(vec.trns(unit.angleTo(targetTile.worldx(), targetTile.worldy()), prefSpeed()));
        if(dash != null) dash.dash(unit, target);
    }

    @Override
    public void circleAttack(float circleLength){
        super.circleAttack(circleLength);
        if(dash != null) dash.dash(unit, target);
    }

    @Override
    public void moveTo(Position target, float circleLength, float smooth, boolean keepDistance, Vec2 offset, boolean arrive){
        super.moveTo(target, circleLength, smooth, keepDistance, offset, arrive);
        if(dash != null) dash.dash(unit, target);
    }
}
