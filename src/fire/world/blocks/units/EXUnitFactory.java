package fire.world.blocks.units;

import arc.Core;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import fire.content.FRItems;
import fire.world.meta.FRStat;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.world.Block;

import java.util.HashMap;
import java.util.Map;

import static mindustry.Vars.indexer;
//TODO
/*
public class EXUnitFactory extends Block {
    public Map<Item,ItemValue> itemValues = new HashMap<>();

    public static class ItemValue{
        Item it;
        float armor,Marmor;
        float power,Mpower;
        float logic,Mlogic;
    }
    
    public void addItemValue(Item item, float armor, float Armor, float power, float Power, float logic, float Logic) {
        ItemValue itemValue = new ItemValue();
        itemValue.it = item;
        itemValue.armor = armor;
        itemValue.Marmor = Armor;
        itemValue.power = power;
        itemValue.Mpower = Power;
        itemValue.logic = logic;
        itemValue.Mlogic = Logic;
        itemValues.put(item,itemValue);
    }

    public EXUnitFactory(String name){
        super(name);
        this.update = true;
        this.hasPower = true;
        this.hasItems = true;
        this.solid = true;
        this.configurable = true;
        this.clearOnDoubleTap = true;
        this.outputsPayload = true;
        this.rotate = true;
        this.regionRotated1 = 1;
        this.commandable = true;
        this.ambientSound = Sounds.respawning;

    }


    @Override
    public void setBars(){
        super.setBars();
        addBar("armor", (EXUnitFactoryBuild b) -> new Bar(Core.bundle.format(FRStat.armorLevel.name), Pal.powerBar, () -> b.armorLevel));
        addBar("power", (EXUnitFactoryBuild b) -> new Bar(Core.bundle.format(FRStat.powerLevel.name), Pal.powerBar, () -> b.powerLevel));
        addBar("logic", (EXUnitFactoryBuild b) -> new Bar(Core.bundle.format(FRStat.logicLevel.name), Pal.powerBar, () -> b.logicLevel));
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    public class EXUnitFactoryBuild extends Building {
        public float armorLevel,powerLevel,logicLevel;

        public void acceptItem(){
            float[] sum = {0.0f};
            Vars.content.items().each(it -> {
                if(this.items.get(it) > 0){
                    if()
                }
                    sum[0] += it.flammability;
            });
        }

        @Override
        public void updateTile(){
            super.updateTile();
                if(efficiency == 0.0f) return ;

                float[] sum = {0.0f};
                items.each((item, n) -> {
                    if(item.flammability > cons.value)
                        sum[0] += item.flammability;
                });
        }
    }
    public void init(){
        super.init();
        addItemValue(Items.copper,0.08f,1.5f,0.1f,0,0,0);
        addItemValue(Items.lead, 0.04f, 1.7f, 0.15f, 1f,0.05f, 0.6f);
        addItemValue(Items.metaglass, 0.06f, 3f, 0.15f, 2f, 0.08f, 2.5f);
        addItemValue(Items.graphite, 0, 0, 0.05f, 2f, 0.4f, 1.5f);
        addItemValue(Items.scrap, 0.01f, 0.8f, 0, 0, 0, 0);
        addItemValue(Items.coal,0.08f,1.5f,0.1f,0,0,0);
        addItemValue(Items.titanium, 0.04f, 1.7f, 0.15f, 1f,0.05f, 0.6f);
        addItemValue(Items.thorium, 0.01f, 0.8f, 0, 0, 0, 0);
        addItemValue(Items.silicon,0.08f,1.5f,0.1f,0,0,0);
        addItemValue(Items.plastanium, 0.04f, 1.7f, 0.15f, 1f,0.05f, 0.6f);
        addItemValue(Items.phaseFabric, 0.01f, 0.8f, 0, 0, 0, 0);
        addItemValue(Items.surgeAlloy,0.08f,1.5f,0.1f,0,0,0);
        addItemValue(Items.sporePod, 0.04f, 1.7f, 0.15f, 1f,0.05f, 0.6f);
        addItemValue(Items.sand, 0.01f, 0.8f, 0, 0, 0, 0);
        addItemValue(Items.blastCompound,0.08f,1.5f,0.1f,0,0,0);
        addItemValue(Items.pyratite, 0.04f, 1.7f, 0.15f, 1f,0.05f, 0.6f);
        addItemValue(Items.beryllium,0.08f,1.5f,0.1f,0,0,0);
        addItemValue(Items.tungsten, 0.04f, 1.7f, 0.15f, 1f,0.05f, 0.6f);
        addItemValue(Items.oxide, 0.01f, 0.8f, 0, 0, 0, 0);
        addItemValue(Items.carbide,0.08f,1.5f,0.1f,0,0,0);
        addItemValue(Items.fissileMatter, 0.04f, 1.7f, 0.15f, 1f,0.05f, 0.6f);
        addItemValue(Items.dormantCyst, 0.04f, 1.7f, 0.15f, 1f,0.05f, 0.6f);
        addItemValue(FRItems.glass, 0.01f, 0.3f, 0.05f, 1.2f,0, 0);
        addItemValue(FRItems.mirrorglass, 0.12f, 4f, 0.05f, 3.5f,0.1f, 2.5f);
        addItemValue(FRItems.sulflameAlloy, 0, 0, 2f, 5.5f,0, 0);
        addItemValue(FRItems.kindlingAlloy, 0, 0, 2.5f, 5.6f,0, 0);
        addItemValue(FRItems.conductor, 0, 0, 0.15f, 4.8f,0.8f, 4.4f);
        addItemValue(FRItems.detonationCompound, 0.01f, 0.3f, 0.05f, 1.2f,0, 0);
        addItemValue(FRItems.flamefluidCrystal, 0.01f, 0.3f, 0.05f, 1.2f,0, 0);
        addItemValue(FRItems.timber, 0.01f, 0.3f, 0.05f, 1.2f,0, 0);
        addItemValue(FRItems.flesh, 0.01f, 0.3f, 0.05f, 1.2f,0, 0);
        addItemValue(FRItems.hardenedAlloy, 9f, 6.3f, 4f, 5.8f,0, 0);
        addItemValue(FRItems.magneticAlloy, 9f, 6.3f, 14f, 6.4f,2f, 5f);
        addItemValue(FRItems.logicAlloy, 0.01f, 0.3f, 0.05f, 1.2f,0, 0);
    }
}
*/