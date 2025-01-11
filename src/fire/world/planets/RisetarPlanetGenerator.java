package mindustry.maps.planet;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.ai.BaseRegistry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.game.FWaves;
import mindustry.graphics.g3d.PlanetGrid.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;

public class RisetarPlanetGenerator extends SerpuloPlanetGenerator{
    //alternate, less direct generation (wip)

    @Override
    protected void generate(){
        super.generate();
        //spawn air only when spawn is blocked
        state.rules.spawns = FWaves.generate(sector.threat, new Rand(sector.id), state.rules.attackMode, state.rules.attackMode && spawner.countGroundSpawns() == 0);
    }
}
