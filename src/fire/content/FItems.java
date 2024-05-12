package fire.content;

import mindustry.type.Item;

import static arc.graphics.Color.valueOf;

public class FItems{
    
    public static Item
        glass, mirrorglass, impurityKindlingAlloy, kindlingAlloy,
        conductor, logicAlloy, detonationCompound, flamefluidCrystal,
        timber, flesh, hardenedAlloy, magneticAlloy;

    public static void load(){

        glass = new Item("bl", valueOf("ffffff"));

        mirrorglass = new Item("jmbl", valueOf("ffffff"));

        impurityKindlingAlloy = new Item("zzhhhj", valueOf("b60c13")){{
            explosiveness = 0.6f;
            flammability = 1.15f;
        }};

        kindlingAlloy = new Item("hhhj", valueOf("ec1c24")){{
            explosiveness = 0.1f;
            flammability = 2.8f;
        }};

        conductor = new Item("dt", valueOf("c78872")){{
            charge = 1.2f;
            frames = 5;
            transitionFrames = 1;
            frameTime = 5;
        }};

        logicAlloy = new Item("logic-alloy", valueOf("814e25")){{
            charge = 0.3f;
        }};

        detonationCompound = new Item("detonation-compound", valueOf("fff220")){{
            explosiveness = 1.6f;
            flammability = 1.1f;
        }};

        flamefluidCrystal = new Item("lhjj", valueOf("ec1c24")){{
            explosiveness = 0.25f;
            flammability = 1.2f;
        }};

        timber = new Item("mc", valueOf("a14b08")){{
            flammability = 0.85f;
        }};

        flesh = new Item("flesh", valueOf("b32e1b")){{
            flammability = 0.3f;
            frames = 13;
            transitionFrames = 1;
            frameTime = 3;
        }};

        hardenedAlloy = new Item("hardened-alloy", valueOf("48427f")){{
            healthScaling = 1.35f;
        }};

        magneticAlloy = new Item("magnetic-alloy", valueOf("bfba95")){{
            charge = 2.1f;
            frames = 22;
            transitionFrames = 1;
            frameTime = 2;
        }};
    }
}
