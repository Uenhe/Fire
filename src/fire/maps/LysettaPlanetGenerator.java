package fire.maps;

import arc.math.Rand;
import arc.util.Reflect;
import fire.game.FRWaves;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.ItemStack;
import mindustry.type.Sector;
import mindustry.world.Block;

import static mindustry.Vars.*;

public class LysettaPlanetGenerator extends SerpuloPlanetGenerator{

    public LysettaPlanetGenerator(){
        Reflect.set(SerpuloPlanetGenerator.class, this, "arr", new Block[][]{
            {Blocks.water, Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.darksandTaintedWater, Blocks.stone, Blocks.stone},
            {Blocks.water, Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.sand, Blocks.sand, Blocks.grass, Blocks.sand, Blocks.sand, Blocks.darksandTaintedWater, Blocks.stone, Blocks.stone, Blocks.stone},
            {Blocks.water, Blocks.darksandWater, Blocks.darksand, Blocks.sand, Blocks.salt, Blocks.grass, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.darksandTaintedWater, Blocks.stone, Blocks.stone, Blocks.stone},
            {Blocks.water, Blocks.sandWater, Blocks.sand, Blocks.salt, Blocks.grass, Blocks.sand, Blocks.grass, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.snow, Blocks.iceSnow, Blocks.ice},
            {Blocks.deepwater, Blocks.water, Blocks.sandWater, Blocks.sand, Blocks.salt, Blocks.sand, Blocks.grass, Blocks.basalt, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.ice},
            {Blocks.deepwater, Blocks.water, Blocks.sandWater, Blocks.sand, Blocks.grass, Blocks.sand, Blocks.grass, Blocks.iceSnow, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.snow, Blocks.ice},
            {Blocks.deepwater, Blocks.sandWater, Blocks.sand, Blocks.sand, Blocks.moss, Blocks.moss, Blocks.snow, Blocks.basalt, Blocks.basalt, Blocks.basalt, Blocks.ice, Blocks.snow, Blocks.ice},
            {Blocks.deepTaintedWater, Blocks.darksandTaintedWater, Blocks.darksand, Blocks.darksand, Blocks.basalt, Blocks.moss, Blocks.basalt, Blocks.hotrock, Blocks.basalt, Blocks.ice, Blocks.snow, Blocks.ice, Blocks.ice},
            {Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.moss, Blocks.sporeMoss, Blocks.snow, Blocks.basalt, Blocks.basalt, Blocks.ice, Blocks.snow, Blocks.ice, Blocks.ice},
            {Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.sporeMoss, Blocks.ice, Blocks.ice, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice},
            {Blocks.deepTaintedWater, Blocks.darksandTaintedWater, Blocks.darksand, Blocks.sporeMoss, Blocks.sporeMoss, Blocks.ice, Blocks.ice, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice},
            {Blocks.taintedWater, Blocks.darksandTaintedWater, Blocks.darksand, Blocks.sporeMoss, Blocks.moss, Blocks.sporeMoss, Blocks.iceSnow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice},
            {Blocks.darksandWater, Blocks.darksand, Blocks.snow, Blocks.ice, Blocks.iceSnow, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice}
        });
    }

    @Override
    protected void generate(){
        super.generate();
        state.rules.spawns = FRWaves.generate(sector.threat, new Rand(sector.id), state.rules.attackMode && spawner.countGroundSpawns() == 0);
        state.rules.loadout.addAll(ItemStack.with(
            Items.copper, 4000,
            Items.lead, 4000,
            Items.graphite, 2000,
            Items.silicon, 2500,
            Items.metaglass, 2000,
            Items.titanium, 2000,
            Items.thorium, 1000,
            Items.plastanium, 500
        )); //TODO this doesn't require relevant items when launching
    }

    @Override
    public boolean allowAcceleratorLanding(Sector sector){
        return false;
    }
}
