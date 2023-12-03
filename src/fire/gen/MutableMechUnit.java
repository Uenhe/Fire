package fire.gen;

import fire.content.FireStatusEffects;
import mindustry.game.Team;
import mindustry.gen.MechUnit;
import mindustry.type.UnitType;

public class MutableMechUnit extends MechUnit{
    public UnitType toRespawn;

    public MutableMechUnit(UnitType toRespawn){
        this.toRespawn = toRespawn;
    }

    @Override
    public void destroy(){
        super.destroy();

        for(var status : statuses){
            if(status.effect == FireStatusEffects.overgrown){
                var unit = toRespawn.spawn(Team.crux, x, y);
                for(var s : statuses){
                    unit.apply(s.effect, s.time);
                }
            }
        }
    }
}
