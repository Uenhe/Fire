package fire.world.blocks.power;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.world.meta.BlockStatus;

public class BatteryNode extends mindustry.world.blocks.power.PowerNode{
    public BatteryNode(String name){
        super(name);
        consumesPower = true;
        outputsPower = true;
    }

    public class BatteryNodeBuild extends PowerNodeBuild{
        @Override
        public float warmup(){
            return power.status;
        }

        @Override
        public void overwrote(Seq<Building> previous){
            for(Building other : previous){
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
