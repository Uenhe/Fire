package fire.world.blocks.sandbox;

import mindustry.type.CellLiquid;
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
        powerProduction = Float.POSITIVE_INFINITY;
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

        private float dumpTimer;

        @Override
        public void updateTile(){
            float limit = 60.0f / itemPerSec;
            var items = this.items;
            var liquids = this.liquids;

            dumpTimer += delta();

            while(dumpTimer >= limit){
                var itemz = content.items();
                for(var item : itemz){
                    items.set(item, 1);
                    dump(item);
                    items.set(item, 0);
                    dumpTimer -= limit;
                }
            }

            liquids.clear();
            var liquidz = content.liquids();
            for(var liquid: liquidz){
                if(liquid instanceof CellLiquid) continue;
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
