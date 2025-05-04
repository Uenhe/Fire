package fire.ai.types;

import arc.math.geom.Position;
import arc.util.Reflect;
import arc.util.Time;
import fire.entities.abilities.DashAbility;
import mindustry.ai.types.RepairAI;
import mindustry.gen.Building;
import mindustry.gen.Teamc;

/** Basically a copy of RepairAI. */
public class RepairDashAI extends RepairAI{

    private final DashAbility dash;

    public RepairDashAI(DashAbility ab){
        dash = ab;
    }

    @Override
    public void updateMovement(){
        if(target instanceof Building){
            boolean shoot = false;
            if(target.within(unit, unit.type.range)){
                unit.aim(target);
                shoot = true;
            }
            unit.controlWeapons(shoot);
        }else if(target == null) unit.controlWeapons(false);
        if(target != null && target instanceof Building b && b.team == unit.team){
            if(unit.type.circleTarget){
                circleAttack(120.0f);
                dash(vec);
            }else if(!target.within(unit, unit.type.range * 0.65f)){
                moveTo(target, unit.type.range * 0.65f);
                dash(target);
            }
            if(!unit.type.circleTarget) unit.lookAt(target);
        }
        if(!(target instanceof Building)){
            if(timer.get(timerTarget4, 40.0f)) avoid(target(unit.x, unit.y, fleeRange, true, true));
            retreatTimer(retreatTimer() + Time.delta);
            if(retreatTimer() >= retreatDelay){
                if(avoid() != null){
                    var core = unit.closestCore();
                    if(core != null && !unit.within(core, retreatDst)){
                        moveTo(core, retreatDst);
                        dash(core);

                    }
                }
            }
        }else retreatTimer(0.0f);
    }

    private void dash(Position pos){
        if(dash == null) return;
        dash.dash(unit, pos);
    }

    private Teamc avoid(){
        return Reflect.get(RepairAI.class, this, "avoid");
    }

    private void avoid(Teamc avoid){
        Reflect.set(RepairAI.class, this, "avoid", avoid);
    }

    private float retreatTimer(){
        return Reflect.get(RepairAI.class, this, "retreatTimer");
    }

    private void retreatTimer(float retreatTimer){
        Reflect.set(RepairAI.class, this, "retreatTimer", retreatTimer);
    }
}
