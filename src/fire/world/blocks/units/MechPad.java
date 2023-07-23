package fire.world.blocks.units;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.state;

public class MechPad extends fire.world.blocks.storage.BuildableCoreBlock{
    public MechPad(String name){
        super(name);
        itemCapacity = 0;
        unloadable = false;
    }

    @Override
    public void setStats(){
        super.setStats();
        if(canBeBuilt() && requirements.length > 0) stats.add(Stat.buildTime, buildCost / 60f, StatUnit.seconds);
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("capacity");
    }

    public class MechPadBuild extends CoreBuild{
        @Override
        public void onRemoved(){
            state.teams.unregisterCore(this);
        }
    }
}
