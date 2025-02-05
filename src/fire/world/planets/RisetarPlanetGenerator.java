package fire.world.planets;

import arc.math.Rand;
import fire.content.FWaves;

import static mindustry.Vars.spawner;
import static mindustry.Vars.state;

public class RisetarPlanetGenerator extends mindustry.maps.planet.SerpuloPlanetGenerator{
    //alternate, less direct generation (wip)

    @Override
    protected void generate(){
        super.generate();
        //spawn air only when spawn is blocked
        state.rules.spawns = FWaves.generate(sector.threat, new Rand(sector.id), state.rules.attackMode, state.rules.attackMode && spawner.countGroundSpawns() == 0);
    }
}
