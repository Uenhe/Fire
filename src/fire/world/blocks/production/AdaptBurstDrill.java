package fire.world.blocks.production;

import arc.math.Mathf;

public class AdaptBurstDrill extends mindustry.world.blocks.production.BurstDrill{
    public AdaptBurstDrill(String name){
        super(name);
    }

    public class AdaptBurstDrillBuild extends BurstDrillBuild{
        @Override
        public void updateTile(){
            if(dominantItem == null) return;
            if(timer(timerDump, Mathf.floor(drillTime / Mathf.sqr(size)))){
                dump(dominantItem);
            }
            super.updateTile();
        }
    }
}
