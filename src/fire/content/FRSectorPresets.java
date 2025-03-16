package fire.content;

import mindustry.type.SectorPreset;

public class FRSectorPresets{

    public static SectorPreset
        landingBase, darksandPlain,
        frozenGround, beachLanding, darkWorkshop, desolateFortification, glaciatedPeaks,
        sporeFiord, scorchingVolcano, lavaStronghold,
        eteriverStronghold, chillyMountains, stormyCoast, branchedRivers, taintedEstuary;

    public static void load(){

        landingBase = create("jljd", 0, 6, 0, false);
        darksandPlain = create("hspy", 94, 7, 50);

        frozenGround = create("lhyj", 15, 6, 35);
        beachLanding = create("htdl", 183, 6);
        darkWorkshop = create("hacj", 186, 8);
        desolateFortification = create("urgent-support", 119, 9, 60);
        glaciatedPeaks = create("glaciated-peaks", 118, 10);

        sporeFiord = create("bzxw", 199, 8, 40);
        scorchingVolcano = create("zrhs", 180, 8, 50);
        lavaStronghold = create("lava-stronghold", 232, 9);

        eteriverStronghold = create("hhys", 34, 8);
        chillyMountains = create("lfsm", 168, 9, 17);
        stormyCoast = create("stormy-coast", 81, 10, 60);
        branchedRivers = create("branched-rivers", 158, 10, 14);
        taintedEstuary = create("tainted-estuary", 116, 10);
    }

    static SectorPreset create(String name, int sector, float difficulty){
        return create(name, sector, difficulty, 0, true);
    }

    static SectorPreset create(String name, int sector, float difficulty, int captureWave){
        return create(name, sector, difficulty, captureWave, true);
    }

    static SectorPreset create(String name, int sector, float difficulty, int captureWave, boolean addStartingItems){
        var s = new SectorPreset(name, FRPlanets.lysetta, sector);
        s.difficulty = difficulty;
        s.captureWave = captureWave;
        s.addStartingItems = addStartingItems;
        return s;
    }
}
