package fire.content;

import mindustry.type.SectorPreset;

public class FSectorPresets{

    public static SectorPreset

        landingBase, darksandPlain, cornerOfZero, beachLanding,
        darkWorkshop, desolateFortification, glaciatedPeaks, sporeFiord,
        scorchingVolcano, lavaStronghold, eteriverStronghold, chillyMountains;

    public static void load(){

        landingBase = create("jljd", 0, 6, 0, false);
        darksandPlain = create("hspy", 94, 7, 15);

        cornerOfZero = create("lhyj", 15, 6, 30);
        beachLanding = create("htdl", 183, 6);
        darkWorkshop = create("hacj", 186, 8);
        glaciatedPeaks = create("glaciated-peaks", 118, 10);

        sporeFiord = create("bzxw", 199, 8, 40);
        scorchingVolcano = create("zrhs", 180, 8, 50);
        lavaStronghold = create("lava-stronghold", 232, 9);

        eteriverStronghold = create("hhys", 34, 8);
        chillyMountains = create("lfsm", 168, 9, 17);
        desolateFortification = create("urgent-support", 119, 9, 60);
    }

    private static SectorPreset create(String name, int sector, float difficulty){
        return create(name, sector, difficulty, 0, true);
    }

    private static SectorPreset create(String name, int sector, float difficulty, int captureWave){
        return create(name, sector, difficulty, captureWave, true);
    }

    private static SectorPreset create(String name, int sector, float difficulty, int captureWave, boolean addStartingItems){
        SectorPreset s = new SectorPreset(name, FPlanets.risetar, sector);
        s.difficulty = difficulty;
        s.captureWave = captureWave;
        s.addStartingItems = addStartingItems;
        return s;
    }
}
