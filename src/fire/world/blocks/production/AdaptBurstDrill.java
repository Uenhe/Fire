package fire.world.blocks.production;

/** TODO reconstruct this if v147 is released, as {@link mindustry.world.Block#dumpTime dumpTime} is no longer {@code final}. */
public class AdaptBurstDrill extends mindustry.world.blocks.production.BurstDrill{

    protected AdaptBurstDrill(String name){
        super(name);
        buildType = AdaptBurstDrillBuild::new;
    }

    public class AdaptBurstDrillBuild extends BurstDrillBuild{

        @Override
        public void updateTile(){
            if(dominantItem == null) return;
            if(timer(timerDump, arc.math.Mathf.floor(drillTime / size / size)))
                dump(dominantItem);

            super.updateTile();
        }
    }
}
