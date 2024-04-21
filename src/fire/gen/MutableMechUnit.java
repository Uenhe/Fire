package fire.gen;

import mindustry.type.UnitType;

public class MutableMechUnit extends mindustry.gen.MechUnit{

    private final UnitType toRespawn;

    public MutableMechUnit(UnitType toRespawn){
        this.toRespawn = toRespawn;
    }

    @Override
    public void destroy(){
        super.destroy();

        if(hasEffect(fire.content.FireStatusEffects.overgrown))
            statuses.each(se -> toRespawn.spawn(mindustry.game.Team.crux, x, y).apply(se.effect, se.time));
    }
}
