package fire.world.blocks.storage;

import mindustry.world.meta.Stat;

/** Special thanks to Creators mod. */
public class AdaptDirectionalUnloader extends mindustry.world.blocks.distribution.DirectionalUnloader{

    protected AdaptDirectionalUnloader(String name){
        super(name);
        buildType = AdaptDirectionalUnloaderBuild::new;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.speed);
        stats.add(Stat.speed, speed, mindustry.world.meta.StatUnit.itemsSecond);
    }

    public class AdaptDirectionalUnloaderBuild extends DirectionalUnloaderBuild{

        private float counter;

        @Override
        public void updateTile(){
            counter += edelta();
            float limit = 60.0f / speed;

            while(counter >= limit){
                unloadTimer = speed;
                super.updateTile();
                counter -= limit;
            }
        }
    }
}
