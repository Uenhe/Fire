package fire.world.blocks.sandbox;

import mindustry.world.meta.Stat;

import static mindustry.Vars.content;

public class AdaptiveSource extends mindustry.world.blocks.sandbox.PowerSource{

    protected short itemPerSec;

    protected AdaptiveSource(String name){
        super(name);
        hasItems = true;
        hasLiquids = true;
        update = true;
        displayFlow = false;
        canOverdrive = true;
        powerProduction = Float.MAX_VALUE;
        buildType = AdaptiveSourceBuild::new;
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
        removeBar("liquids");
        removeBar("connections");
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    public class AdaptiveSourceBuild extends PowerSourceBuild implements mindustry.world.blocks.heat.HeatBlock{

        private float counter;

        @Override
        public void updateTile(){
            if(proximity.size == 0) return;

            counter += edelta();
            final float limit = 60f / itemPerSec;

            while(counter >= limit){
                for(var item : content.items()){
                    items.set(item, 1);
                    dump(item);
                    items.set(item, 0);
                    counter -= limit;
                }
            }

            liquids.clear();
            for(var liquid: content.liquids()){
                liquids.add(liquid, liquidCapacity);
                dumpLiquid(liquid);
            }
        }

        @Override
        public float heat(){
            return Float.POSITIVE_INFINITY;
        }

        @Override
        public float heatFrac(){
            return Float.POSITIVE_INFINITY;
        }
    }
}
