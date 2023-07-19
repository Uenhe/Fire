package fire.ai.types;

import fire.entities.abilities.DashAbility;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.abilities.Ability;

public class FlyingDashAI extends FlyingAI{
    @Override
    public void updateMovement(){
        super.updateMovement();
        for(Ability a : unit.abilities){
            if(a instanceof DashAbility) ((DashAbility) a).dash(unit);
        }
    }
}
