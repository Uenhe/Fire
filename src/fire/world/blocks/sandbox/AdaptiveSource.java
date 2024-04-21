package fire.world.blocks.sandbox;

import mindustry.world.meta.Stat;

import static mindustry.Vars.content;

public class AdaptiveSource extends mindustry.world.blocks.sandbox.PowerSource{

    protected short itemPerSec = 100;

    protected AdaptiveSource(String name){
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
        removeBar("liquids");
        removeBar("connections");
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    public class AdaptiveSourceBuild extends PowerSourceBuild{

        private float counter;

        @Override
        public void updateTile(){
            if(proximity.size == 0) return;

            counter += edelta();
            final float limit = 60f / itemPerSec;

            while(counter >= limit){
                for(final var item : content.items()){
                    items.set(item, 1);
                    dump(item);
                    items.set(item, 0);
                    counter -= limit;
                }
            }

            liquids.clear();
            for(final var liquid: content.liquids()){
                liquids.add(liquid, liquidCapacity);
                dumpLiquid(liquid);
            }
        }
    }
}
