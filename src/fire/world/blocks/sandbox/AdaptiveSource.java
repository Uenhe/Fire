package fire.world.blocks.sandbox;

import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.meta.Stat;

import static mindustry.Vars.content;

public class AdaptiveSource extends mindustry.world.blocks.sandbox.PowerSource{
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
        protected float counter;

        @Override
        public void updateTile(){
            counter += edelta();
            float limit = 60f / itemPerSec;
            while(counter >= limit){
                for(Item item : content.items()){
                    items.set(item, 1);
                    dump(item);
                    items.set(item, 0);
                    counter -= limit;
                }
            }
            liquids.clear();
            for(Liquid liquid: content.liquids()){
                liquids.add(liquid, liquidCapacity);
                dumpLiquid(liquid);
            }
        }
    }
}
