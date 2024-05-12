package fire.gen;

import fire.content.FStatusEffects;
import mindustry.type.UnitType;

public class MutableMechUnit extends mindustry.gen.MechUnit{

    private final UnitType toRespawn;

    public MutableMechUnit(UnitType type){
        toRespawn = type;
    }

    @Override
    public void destroy(){
        super.destroy();

        if(hasEffect(FStatusEffects.overgrown)){
            final var unit = toRespawn.spawn(mindustry.game.Team.crux, x, y);
            statuses.each(e -> unit.apply(e.effect, e.time));
        }
    }
}
