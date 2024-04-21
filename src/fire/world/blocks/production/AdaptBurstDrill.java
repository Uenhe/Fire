package fire.world.blocks.production;

public class AdaptBurstDrill extends mindustry.world.blocks.production.BurstDrill{

    protected AdaptBurstDrill(String name){
        super(name);
    }

    public class AdaptBurstDrillBuild extends BurstDrillBuild{

        @Override
        public void updateTile(){
            if(dominantItem == null) return;
            if(timer(timerDump, arc.math.Mathf.floor(drillTime / size / size))){
                dump(dominantItem);
            }
            super.updateTile();
        }
    }
}
