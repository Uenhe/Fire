package fire.maps;

import arc.math.Rand;
import fire.game.FRWaves;
import mindustry.type.Sector;

import static mindustry.Vars.spawner;
import static mindustry.Vars.state;

public class RisetarPlanetGenerator extends mindustry.maps.planet.SerpuloPlanetGenerator{
    //alternate, less direct generation (wip)

    @Override
    protected void generate(){
        super.generate();
        //spawn air only when spawn is blocked
        state.rules.spawns = FRWaves.generate(sector.threat, new Rand(sector.id), state.rules.attackMode, state.rules.attackMode && spawner.countGroundSpawns() == 0);
    }

    @Override
    public boolean allowAcceleratorLanding(Sector sector){
        return false;
    }
}
