package fire.content;

import mindustry.type.SectorPreset;

public class FRSectorPresets{

    public static SectorPreset
        landingBase, darksandPlain,
        frozenGround, beachLanding, darkWorkshop, desolateFortification, glaciatedPeaks,
        sporeFiord, scorchingVolcano, lavaStronghold,
        eteriverStronghold, chillyMountains, stormyCoast, branchedRivers, rubbleRidge, taintedEstuary;

    public static void load(){
        landingBase = new FRSectorPreset("jljd", 0)
            .difficulty(6.0f);
        darksandPlain = new FRSectorPreset("hspy", 94)
            .difficulty(7.0f)
            .captureWave(50);

        frozenGround = new FRSectorPreset("lhyj", 15)
            .difficulty(6.0f)
            .captureWave(35);
        beachLanding = new FRSectorPreset("htdl", 183)
            .difficulty(6.0f);
        darkWorkshop = new FRSectorPreset("hacj", 186)
            .difficulty(8.0f);
        desolateFortification = new FRSectorPreset("urgent-support", 119)
            .difficulty(9.0f)
            .captureWave(60);
        glaciatedPeaks = new FRSectorPreset("glaciated-peaks", 118)
            .difficulty(10.0f);

        sporeFiord = new FRSectorPreset("bzxw", 199)
            .difficulty(8.0f)
            .captureWave(40);
        scorchingVolcano = new FRSectorPreset("zrhs", 180)
            .difficulty(8.0f)
            .captureWave(50);
        lavaStronghold = new FRSectorPreset("lava-stronghold", 232)
            .difficulty(9.0f);

        eteriverStronghold = new FRSectorPreset("hhys", 34)
            .difficulty(8.0f);
        chillyMountains = new FRSectorPreset("lfsm", 168)
            .difficulty(9.0f)
            .captureWave(17);
        stormyCoast = new FRSectorPreset("stormy-coast", 81)
            .difficulty(10.0f)
            .captureWave(60);
        branchedRivers = new FRSectorPreset("branched-rivers", 158)
            .difficulty(10.0f)
            .captureWave(14);
        rubbleRidge = new FRSectorPreset("rubble-ridge", 172)
            .difficulty(10.0f)
            .captureWave(65);
        taintedEstuary = new FRSectorPreset("tainted-estuary", 116)
            .difficulty(10.0f);
    }

    private static class FRSectorPreset extends SectorPreset{

        private FRSectorPreset(String name, int sector){
            super(name, FRPlanets.lysetta, sector);
            addStartingItems = true;
        }

        private FRSectorPreset captureWave(int n){
            captureWave = n;
            return this;
        }

        private FRSectorPreset difficulty(float v){
            difficulty = v;
            return this;
        }
    }
}
