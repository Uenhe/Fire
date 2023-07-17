//special thanks to Creators mod

package fire.world.blocks.storage;

import mindustry.world.blocks.distribution.DirectionalUnloader;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class CompositeUnloader extends DirectionalUnloader{
    public CompositeUnloader(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.speed);
        stats.add(Stat.speed, speed, StatUnit.itemsSecond);
    }

    public class compositeUnloaderBuild extends DirectionalUnloaderBuild{
        protected float counter;

        @Override
        public void updateTile(){
            counter += edelta();
            while(counter >= 30f / speed){
                unloadTimer = speed;
                super.updateTile();
                counter -= 30f / speed;
            }
        }
    }
}
