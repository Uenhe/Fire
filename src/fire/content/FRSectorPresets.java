package fire.content;

import mindustry.type.SectorPreset;

public class FRSectorPresets{

    public static final SectorPreset
        landingBase, darksandPeakforest,
        frozenGround, beachLanding, darkWorkshop, desolateFortification, glaciatedPeaks,
        sporeFiord, scorchingVolcano, desertWastes, lavaStronghold,
        eteriverStronghold, chillyMountains, stormyCoast, branchedRivers, rubbleRidge, taintedEstuary;

    static{
        landingBase = create("jljd", 0, 6.0f);
        darksandPeakforest = create("hspy", 94, 7.0f, 40);

        frozenGround = create("lhyj", 15, 6.0f, 35);
        beachLanding = create("htdl", 183, 6.0f);
        darkWorkshop = create("hacj", 186, 8.0f);
        desolateFortification = create("urgent-support", 119, 9.0f, 60);
        glaciatedPeaks = create("glaciated-peaks", 118, 10.0f);

        sporeFiord = create("bzxw", 199, 8.0f, 40);
        scorchingVolcano = create("zrhs", 180, 8.0f, 50);
        desertWastes = create("desert-wastes", 205, 8.0f, 28);
        lavaStronghold = create("lava-stronghold", 232, 9.0f);

        eteriverStronghold = create("hhys", 34, 8.0f);
        chillyMountains = create("lfsm", 168, 9.0f, 17);
        stormyCoast = create("stormy-coast", 81, 10.0f, 60);
        stormyCoast.noLighting = true;
        branchedRivers = create("branched-rivers", 158, 10.0f, 14);
        rubbleRidge = create("rubble-ridge", 172, 10.0f, 65);
        taintedEstuary = create("tainted-estuary", 116, 10.0f);
    }
    
    public static void load(){}

    private static SectorPreset create(String name, int sector, float difficulty, int captureWave){
        var s = new SectorPreset(name, FRPlanets.lysetta, sector);
        s.difficulty = difficulty;
        s.captureWave = captureWave;
        return s;
    }

    private static SectorPreset create(String name, int sector, float difficulty){
        var s = new SectorPreset(name, FRPlanets.lysetta, sector);
        s.difficulty = difficulty;
        return s;
    }
}
