package fire.ai.types;

import fire.entities.abilities.DashAbility;

public class FlyingDashAI extends mindustry.ai.types.FlyingAI{
    @Override
    public void updateMovement(){
        super.updateMovement();
        for(var a : unit.abilities){
            if(a instanceof DashAbility) ((DashAbility)a).dash(unit);
        }
    }
}
