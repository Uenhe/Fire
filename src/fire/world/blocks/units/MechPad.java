package fire.world.blocks.units;

import fire.world.blocks.storage.BuildableCoreBlock;

import static mindustry.Vars.state;

public class MechPad extends BuildableCoreBlock{
    public MechPad(String name){
        super(name);
        itemCapacity = 0;
        unloadable = false;
    }

    public class MechPadBuild extends CoreBuild{
        @Override
        public void onRemoved(){
            state.teams.unregisterCore(this);
        }
    }
}
