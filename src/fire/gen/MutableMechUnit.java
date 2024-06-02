package fire.gen;

import mindustry.type.UnitType;

public class MutableMechUnit extends mindustry.gen.MechUnit{

    private final UnitType toRespawn;

    public MutableMechUnit(UnitType type){
        toRespawn = type;
    }

    @Override
    public void destroy(){
        super.destroy();

        if(hasEffect(fire.content.FStatusEffects.overgrown)){
            final var unit = toRespawn.spawn(mindustry.game.Team.crux, x, y);
            for(final var e : statuses)
                unit.apply(e.effect, e.time);
        }
    }
}
