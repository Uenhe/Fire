package fire.world.blocks.sandbox;

import arc.struct.IntIntMap;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;

import static mindustry.Vars.content;

public class AdaptiveSource extends mindustry.world.blocks.sandbox.PowerSource{

    public static final IntIntMap turretItemMap = new IntIntMap(); //TurretBlock ID -> Item ID

    protected AdaptiveSource(String name){
        super(name);
        hasItems  = true;
        hasLiquids = outputsLiquid = true;
        update = true;
        displayFlow = false;
        canOverdrive = true;
        swapDiagonalPlacement = false;
        autolink = drawRange = false;
        group = BlockGroup.transportation;
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
        removeBar("liquid");
        removeBar("connections");
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    public class AdaptiveSourceBuild extends PowerSourceBuild implements mindustry.world.blocks.heat.HeatBlock{

        @Override
        public void updateTile(){
            for(var other : proximity){
                if(other instanceof ItemTurret.ItemTurretBuild){
                    var item = content.item(turretItemMap.get(other.block.id));
                    if(other.acceptItem(this, item)) other.handleItem(this, item);

                }else{
                    var oit = other.items;
                    for(var item : content.items()){
                        if(other.acceptItem(this, item)){
                            other.handleItem(this, item);
                            if(other.items != null)
                                oit.set(item, other.getMaximumAccepted(item));
                        }
                    }
                }

                var oli = other.liquids;
                for(var liquid : content.liquids()){
                    if(other.acceptLiquid(this, liquid)){
                        oli.set(liquid, other.block.liquidCapacity);
                    }
                }
            }
        }

        @Override
        public float heat(){
            return Float.MAX_VALUE;
        }

        @Override
        public float heatFrac(){
            return Float.MAX_VALUE;
        }
    }
}
