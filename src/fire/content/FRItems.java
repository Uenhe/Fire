package fire.content;

import arc.graphics.Color;
import fire.world.blocks.units.ElementUnitFactory;
import mindustry.content.Items;
import mindustry.type.Item;

import static fire.FRVars.find;
import static mindustry.content.Items.*;

public class FRItems{
    
    public static final Item
        glass, mirrorglass, sulflameAlloy, kindlingAlloy,
        conductor, logicAlloy, detonationCompound, flamefluidCrystal,
        timber, flesh, hardenedAlloy, magneticAlloy;

    static{
        glass = new FRItem("bl", Color.white)
            .hardness(2);

        mirrorglass = new FRItem("jmbl", Color.white);

        sulflameAlloy = new FRItem("zzhhhj", "b60c13")
            .explosiveness(0.6f)
            .flammability(1.15f);

        kindlingAlloy = new FRItem("hhhj", "ec1c24")
            .explosiveness(0.1f)
            .flammability(2.8f);

        conductor = new FRItem("dt", "c78872")
            .charge(1.2f);

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

    public static void load(){
        ElementUnitFactory.putAllValues(
            copper,             0.08f, 1.5f,  0.1f,  0.9f,  0.0f,  0.0f, 0,
            lead,               0.06f, 1.55f, 0.15f, 1.0f,  0.05f, 0.6f, 0,
            metaglass,          0.06f, 3.0f,  0.15f, 2.0f,  0.08f, 2.5f, 2,
            graphite,           0.0f,  0.0f,  0.1f,  3.0f,  0.4f,  1.5f, 3,
            scrap,              0.01f, 0.8f,  0.0f,  0.0f,  0.0f,  0.0f, 0,
            coal,               0.0f,  0.0f,  0.3f,  2.0f,  0.0f,  0.0f, 1,
            titanium,           0.25f, 3.2f,  0.3f,  2.6f,  0.1f,  1.5f, 4,
            thorium,            0.45f, 3.75f, 0.55f, 3.1f,  0.0f,  0.0f, 5,
            silicon,            0.0f,  0.0f,  0.3f,  2.5f,  0.9f,  5.0f, 1,
            plastanium,         0.85f, 4.85f, 0.75f, 4.55f, 0.0f,  0.0f, 4,
            phaseFabric,        1.25f, 5.3f,  0.85f, 2.4f,  0.85f, 5.4f, 6,
            surgeAlloy,         1.25f, 5.5f,  1.35f, 5.95f, 0.0f,  0.0f, 4,
            sporePod,           0.0f,  0.0f,  0.4f,  2.25f, 0.0f,  0.0f, 2,
            sand,               0.01f, 0.5f,  0.0f,  0.0f,  0.0f,  0.0f, 0,
            blastCompound,      0.0f,  0.0f,  1.2f,  5.3f,  0.0f,  0.0f, 4,
            pyratite,           0.0f,  0.0f,  0.8f,  4.2f,  0.0f,  0.0f, 3,

            glass,              0.01f, 0.3f,  0.05f, 1.2f,  0.1f,  2.5f, 0,
            mirrorglass,        0.85f, 4.0f,  0.95f, 3.5f,  0.1f,  2.5f, 6,
            sulflameAlloy,      0.0f,  0.0f,  1.30f, 5.5f,  0.0f,  0.0f, 4,
            kindlingAlloy,      0.0f,  0.0f,  1.25f, 5.6f,  0.0f,  0.0f, 4,
            conductor,          0.0f,  0.0f,  0.75f, 3.85f, 0.35f, 3.4f, 3,
            detonationCompound, 0.05f, 1.3f,  1.35f, 6.25f, 0.3f,  2.9f, 9,
            flamefluidCrystal,  0.0f,  0.0f,  0.55f, 4.95f, 0.0f,  0.0f, 4,
            timber,             0.05f, 0.95f, 0.25f, 1.55f, 0.0f,  0.0f, 5,
            flesh,              1.55f, 6.1f,  0.05f, 1.2f,  2.65f, 6.4f, 12,
            hardenedAlloy,      2.0f,  6.3f,  1.25f, 5.8f,  0.0f,  0.0f, 12,
            magneticAlloy,      2.2f,  6.3f,  14.0f, 6.6f,  0.75f, 5.7f, 18,
            logicAlloy,         0.4f,  3.5f,  0.3f,  3.1f,  1.25f, 5.2f, 8
        );
    }

    private static class FRItem extends Item{

        public FRItem(String name, Color color){
            super(name, color);
            Items.serpuloItems.add(this);
        }

        public FRItem(String name, String hex){
            super(name, find(hex));
            Items.serpuloItems.add(this);
        }

        private FRItem hardness(int v){
            hardness = v;
            return this;
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
