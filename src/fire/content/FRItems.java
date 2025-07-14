package fire.content;

import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.type.Item;

import static fire.FRVars.find;

public class FRItems{
    
    public static final Item
        glass, mirrorglass, sulflameAlloy, kindlingAlloy,
        conductor, logicAlloy, detonationCompound, flamefluidCrystal,
        timber, flesh, hardenedAlloy, magneticAlloy;

    static{
        glass = new FRItem("bl", Color.white);

        mirrorglass = new FRItem("jmbl", Color.white);

        sulflameAlloy = new FRItem("zzhhhj", "b60c13")
            .explosiveness(0.6f)
            .flammability(1.15f);

        kindlingAlloy = new FRItem("hhhj", "ec1c24")
            .explosiveness(0.1f)
            .flammability(2.8f);

        conductor = new FRItem("dt", "c78872")
            .charge(1.2f)
            .setupAnimation(5, 5.0f);

        logicAlloy = new FRItem("logic-alloy", "814e25")
            .charge(0.3f);

        detonationCompound = new FRItem("detonation-compound", "fff220")
            .explosiveness(1.6f)
            .flammability(1.1f);

        flamefluidCrystal = new FRItem("lhjj", "ec1c24")
            .explosiveness(0.25f)
            .flammability(1.2f);

        timber = new FRItem("mc", "a14b08")
            .flammability(0.85f);

        flesh = new FRItem("flesh", "b32e1b")
            .flammability(0.3f)
            .setupAnimation(13, 3.0f);

        hardenedAlloy = new FRItem("hardened-alloy", "48427f")
            .healthScaling(1.35f);

        magneticAlloy = new FRItem("magnetic-alloy", "bfba95")
            .charge(2.1f)
            .setupAnimation(22, 2.0f);
    }

    public static void load(){}

    private static class FRItem extends Item{

        public FRItem(String name, Color color){
            super(name, color);
            Items.serpuloItems.add(this);
        }

        public FRItem(String name, String hex){
            super(name, find(hex));
            Items.serpuloItems.add(this);
        }

        private FRItem explosiveness(float v){
            explosiveness = v;
            return this;
        }

        private FRItem flammability(float v){
            flammability = v;
            return this;
        }

        private FRItem healthScaling(float v){
            healthScaling = v;
            return this;
        }

        private FRItem charge(float v){
            charge = v;
            return this;
        }

        private FRItem setupAnimation(int n, float v){
            frames = n;
            transitionFrames = 1;
            frameTime = v;
            return this;
        }
    }
}
