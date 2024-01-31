//special thanks to Creators mod

package fire.world.blocks.storage;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class AdaptDirectionalUnloader extends mindustry.world.blocks.distribution.DirectionalUnloader{
    public AdaptDirectionalUnloader(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.speed);
        stats.add(Stat.speed, speed, StatUnit.itemsSecond);
    }

    public class AdaptDirectionalUnloaderBuild extends DirectionalUnloaderBuild{
        protected float counter;

        @Override
        public void updateTile(){
            counter += edelta();
            float limit = 60f / speed;
            while(counter >= limit){
                unloadTimer = speed;
                super.updateTile();
                counter -= limit;
            }
        }
    }
}
