package fire.ai.types;

import mindustry.gen.Building;

/** Basically a copy of RepairAI. */
public class RepairDashAI extends mindustry.ai.types.RepairAI{

    private mindustry.gen.Teamc avoid;
    private float retreatTimer;

    @Override
    public void updateMovement(){
        if(target instanceof Building){
            boolean shoot = false;
            if(target.within(unit, unit.type.range)){
                unit.aim(target);
                shoot = true;
            }
            unit.controlWeapons(shoot);
        }else if(target == null){
            unit.controlWeapons(false);
        }
        if(target != null && target instanceof Building b && b.team == unit.team){
            if(unit.type.circleTarget){
                circleAttack(120f);
                dash(vec);

            }else if(!target.within(unit, unit.type.range * 0.65f)){
                moveTo(target, unit.type.range * 0.65f);
                dash(target);

            }
            if(!unit.type.circleTarget){
                unit.lookAt(target);
            }
        }
        //not repairing
        if(!(target instanceof Building)){
            if(timer.get(timerTarget4, 40f)){
                avoid = target(unit.x, unit.y, fleeRange, true, true);
            }
            if((retreatTimer += arc.util.Time.delta) >= retreatDelay){
                //fly away from enemies when not doing anything
                if(avoid != null){
                    var core = unit.closestCore();
                    if(core != null && !unit.within(core, retreatDst)){
                        moveTo(core, retreatDst);
                        dash(core);

                    }
                }
            }
        }else{
            retreatTimer = 0f;
        }
    }

    private void dash(arc.math.geom.Position pos){
        for(var a : unit.abilities) if(a instanceof fire.entities.abilities.DashAbility da){
            da.dash(unit, pos);
            break;
        }
    }
}
