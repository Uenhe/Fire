package fire.gen;

import fire.content.FireStatusEffects;
import mindustry.game.Team;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;

public class MutableMechUnit extends mindustry.gen.MechUnit{
    public UnitType toRespawn;
    private static final StatusEffect statusEffect = FireStatusEffects.overgrown;

    public MutableMechUnit(UnitType toRespawn){
        this.toRespawn = toRespawn;
    }

    @Override
    public void destroy(){
        super.destroy();

        for(var status : statuses){
            if(status.effect == statusEffect){
                var unit = toRespawn.spawn(Team.crux, x, y);
                for(var s : statuses){
                    unit.apply(s.effect, s.time);
                }
            }
        }
    }
}
