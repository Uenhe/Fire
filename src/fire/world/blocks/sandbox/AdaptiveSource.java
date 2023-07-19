package fire.world.blocks.sandbox;

import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.sandbox.PowerSource;
import mindustry.world.meta.Stat;

import static mindustry.Vars.content;

public class AdaptiveSource extends PowerSource{
    public int itemPerSec = 100;

    public AdaptiveSource(String name){
        super(name);
        hasItems = true;
        hasLiquids = true;
        update = true;
        displayFlow = false;
        canOverdrive = true;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.itemCapacity);
        stats.remove(Stat.liquidCapacity);
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("items");
        removeBar("liquid");
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    public class AdaptiveSourceBuild extends PowerSourceBuild{
        protected int itemCount, liquidCount;
        protected float counter;

        @Override
        public void updateTile(){
            super.updateTile();
            counter += edelta();
            float limit = 60f / itemPerSec;
            itemCount = itemCount >= content.items().size - 1 ? 0 : itemCount + 1;
            liquidCount = liquidCount >= content.liquids().size - 1 ? 0 : liquidCount + 1;
            Item item = content.item(itemCount);
            Liquid liquid = content.liquid(liquidCount);
            while(counter >= limit){
                items.set(item, 1);
                dump(item);
                items.set(item, 0);
                counter -= limit;
            }
            liquids.clear();
            liquids.add(liquid, liquidCapacity);
            dumpLiquid(liquid);
        }
    }
}
