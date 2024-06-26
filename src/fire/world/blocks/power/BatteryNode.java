package fire.world.blocks.power;

import arc.math.Mathf;
import mindustry.world.meta.BlockStatus;

public class BatteryNode extends mindustry.world.blocks.power.PowerNode{

    protected BatteryNode(String name){
        super(name);
        consumesPower = true;
        outputsPower = true;
        buildType = BatteryNodeBuild::new;
    }

    public class BatteryNodeBuild extends PowerNodeBuild{

        @Override
        public float warmup(){
            return power.status;
        }

        @Override
        public void overwrote(arc.struct.Seq<mindustry.gen.Building> previous){
            for(var other : previous){
                if(other.power != null && other.block.consPower != null && other.block.consPower.buffered){
                    float amount = other.block.consPower.capacity * other.power.status;
                    power.status = Mathf.clamp(power.status + amount / consPower.capacity);
                }
            }
        }

        @Override
        public BlockStatus status(){
            if(Mathf.equal(power.status, 0f, 0.001f)) return BlockStatus.noInput;
            if(Mathf.equal(power.status, 1f, 0.001f)) return BlockStatus.active;
            return BlockStatus.noOutput;
        }
    }
}
