package fire.content;

import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.type.Item;

public class FRItems{
    
    public static Item
        glass, mirrorglass, sulflameAlloy, kindlingAlloy,
        conductor, logicAlloy, detonationCompound, flamefluidCrystal,
        timber, flesh, hardenedAlloy, magneticAlloy;

    public static void load(){

        glass = new Item("bl", Color.white);

        mirrorglass = new Item("jmbl", Color.white);

        sulflameAlloy = new Item("zzhhhj", Color.valueOf("b60c13")){{
            explosiveness = 0.6f;
            flammability = 1.15f;
        }};

        kindlingAlloy = new Item("hhhj", Color.valueOf("ec1c24")){{
            explosiveness = 0.1f;
            flammability = 2.8f;
        }};

        conductor = new Item("dt", Color.valueOf("c78872")){{
            charge = 1.2f;
            frames = 5;
            transitionFrames = 1;
            frameTime = 5;
        }};

        logicAlloy = new Item("logic-alloy", Color.valueOf("814e25")){{
            charge = 0.3f;
        }};

        detonationCompound = new Item("detonation-compound", Color.valueOf("fff220")){{
            explosiveness = 1.6f;
            flammability = 1.1f;
        }};

        flamefluidCrystal = new Item("lhjj", Color.valueOf("ec1c24")){{
            explosiveness = 0.25f;
            flammability = 1.2f;
        }};

        timber = new Item("mc", Color.valueOf("a14b08")){{
            flammability = 0.85f;
        }};

        flesh = new Item("flesh", Color.valueOf("b32e1b")){{
            flammability = 0.3f;
            frames = 13;
            transitionFrames = 1;
            frameTime = 3;
        }};

        hardenedAlloy = new Item("hardened-alloy", Color.valueOf("48427f")){{
            healthScaling = 1.35f;
        }};

        magneticAlloy = new Item("magnetic-alloy", Color.valueOf("bfba95")){{
            charge = 2.1f;
            frames = 22;
            transitionFrames = 1;
            frameTime = 2;
        }};

        Items.serpuloItems.addAll(
            glass, mirrorglass, sulflameAlloy, kindlingAlloy,
            conductor, logicAlloy, detonationCompound, flamefluidCrystal,
            timber, flesh, hardenedAlloy, magneticAlloy
        );
    }
}
