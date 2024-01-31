package fire.content;

import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Planet;

public class FirePlanets{
    public static Planet

        risetar;

    public static void load(){
        risetar = new Planet("lst", Planets.sun, 1f, 3){{
            meshLoader = () -> new HexMesh(this, 8);
            cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, Color.valueOf("5279f0bb"), 2, 0.45f, 0.9f, 0.38f),
                new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(Color.valueOf("5279f0bb"), 0.55f), 2, 0.45f, 1f, 0.41f)
            );
            generator = new SerpuloPlanetGenerator();
            bloom = false;
            accessible = true;
            rotateTime = 12000f;
            visible = true;
            alwaysUnlocked = true;
            clearSectorOnLose = true;
            enemyCoreSpawnReplace = false;
            allowLaunchSchematics = false;
            allowLaunchLoadout = false;
            allowSectorInvasion = false;
            allowWaveSimulation = true;
            prebuildBase = false;
            orbitRadius = 64f;
            startSector = 0;
            sectorSeed = 3;
            defaultCore = Blocks.coreShard;
            atmosphereColor = Color.valueOf("1a3db1");
            atmosphereRadIn = 0.05f;
            atmosphereRadOut = 0.5f;
            iconColor = Color.valueOf("5b6fff");
            hiddenItems.addAll(Items.erekirItems).removeAll(Items.serpuloItems);
        }};
    }
}
