package fire.world.consumers;

import arc.math.Mathf;
import mindustry.gen.Building;

public class ConsumePowerCustom extends mindustry.world.consumers.ConsumePower{

    public float scale;

    public ConsumePowerCustom(float usage, float capacity, boolean buffered){
        this.usage = usage;
        this.capacity = capacity;
        this.buffered = buffered;
    }

    @Override
    public float requestedPower(Building build){
        return buffered
            ? (1.0f - build.power.status) * capacity
            : usage * scale * Mathf.num(build.shouldConsume());
    }
}
