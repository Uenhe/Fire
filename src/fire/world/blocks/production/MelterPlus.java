package fire.world.blocks.production;

import mindustry.world.blocks.production.ItemIncinerator;

public class MelterPlus extends ItemIncinerator {

    protected MelterPlus(String name){
        super(name);
        buildType = MelterPlus.MelterPlusBuild::new;
        noUpdateDisabled = true;
    }

    public class MelterPlusBuild extends ItemIncinerator.ItemIncineratorBuild {

        public float sum;

        @Override
        public void updateTile(){
            efficiency = sum;
        }

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();
            //sum = returnEfficiency(tile.x,tile.y);
        }

        @Override
        public float totalProgress(){
            return enabled ? super.totalProgress() : 0f;
        }


    }
}
