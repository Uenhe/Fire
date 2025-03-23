package fire.ai.types;

import arc.math.geom.Position;
import fire.entities.abilities.DashAbility;
import mindustry.gen.Building;
import mindustry.gen.Teamc;

/** Basically a copy of RepairAI. */
public class RepairDashAI extends mindustry.ai.types.RepairAI{

    private final DashAbility dash;
    private Teamc avoid;
    private float retreatTimer;

    public RepairDashAI(DashAbility ab){
        dash = ab;
    }

    @Override
    public void updateMovement(){
        if(target instanceof Building){
            final boolean shoot;
            if(target.within(unit, unit.type.range)){
                unit.aim(target);
                shoot = true;
            }else{
                shoot = false;
            }
            unit.controlWeapons(shoot);
        }else if(target == null){
            unit.controlWeapons(false);
        }
        if(target != null && target instanceof Building b && b.team == unit.team){
            if(unit.type.circleTarget){
                circleAttack(120.0f);
                dash(vec);
            }else if(!target.within(unit, unit.type.range * 0.65f)){
                moveTo(target, unit.type.range * 0.65f);
                dash(target);
            }
            if(!unit.type.circleTarget){
                unit.lookAt(target);
            }
        }
        if(!(target instanceof Building)){
            if(timer.get(timerTarget4, 40.0f)){
                avoid = target(unit.x, unit.y, fleeRange, true, true);
            }
            if((retreatTimer += arc.util.Time.delta) >= retreatDelay){
                if(avoid != null){
                    var core = unit.closestCore();
                    if(core != null && !unit.within(core, retreatDst)){
                        moveTo(core, retreatDst);
                        dash(core);

                    }
                }
            }
        }else{
            retreatTimer = 0.0f;
        }
    }

    private void dash(Position pos){
        if(dash == null) return;
        dash.dash(unit, pos);
    }
}
