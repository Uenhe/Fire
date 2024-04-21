package fire.content;

import mindustry.type.SectorPreset;

import static fire.content.FirePlanets.risetar;

public class FireSectorPresets{
    public static SectorPreset

        landingBase, darksandPlain, cornerOfZero, beachLanding,
        darkWorkshop, sporeFiord, scorchingVolcano, lavaStronghold,
        eternityRiverStronghold, chillyMountains, urgentSupport;

    public static void load(){

        landingBase = new SectorPreset("jljd", risetar, 0){{
            difficulty = 6;
        }};

        darksandPlain = new SectorPreset("hspy", risetar, 94){{
            difficulty = 7;
            captureWave = 15;
            addStartingItems = true;
        }};

        cornerOfZero = new SectorPreset("lhyj", risetar, 15){{
            difficulty = 6;
            captureWave = 30;
            addStartingItems = true;
        }};

        beachLanding = new SectorPreset("htdl", risetar, 183){{
            difficulty = 6;
            addStartingItems = true;
        }};

        darkWorkshop = new SectorPreset("hacj", risetar, 186){{
            difficulty = 8;
            addStartingItems = true;
        }};

        sporeFiord = new SectorPreset("bzxw", risetar, 199){{
            difficulty = 8;
            captureWave = 40;
            addStartingItems = true;
        }};

        scorchingVolcano = new SectorPreset("zrhs", risetar, 180){{
            difficulty = 8;
            captureWave = 50;
            addStartingItems = true;
        }};

        lavaStronghold = new SectorPreset("lava-stronghold", risetar, 232){{
            difficulty = 9;
            addStartingItems = true;
        }};

        eternityRiverStronghold = new SectorPreset("hhys", risetar, 34){{
            difficulty = 8;
            addStartingItems = true;
        }};

        chillyMountains = new SectorPreset("lfsm", risetar, 168){{
            difficulty = 9;
            captureWave = 17;
            addStartingItems = true;
        }};

        urgentSupport = new SectorPreset("urgent-support", risetar, 119){{
            difficulty = 9;
            captureWave = 60;
            addStartingItems = true;
        }};
    }
}
