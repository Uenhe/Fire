package fire.world.consumers;

import arc.Core;
import arc.math.Mathf;
import arc.struct.IntFloatMap;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.StatUnit;

public class ConsumePowerCustom extends mindustry.world.consumers.ConsumePower{

    public static final IntFloatMap scaleMap = new IntFloatMap();

    public ConsumePowerCustom(float usage, float capacity, boolean buffered, Block block){
        super(usage, capacity, buffered);

        //prevent adding this bar prior to others, TODO may look bad
        Time.runTask(600.0f, () ->
            block.addBar("powerz", build -> new Bar(
                () -> Core.bundle.format("bar.powerscale", Math.round(scaleMap.get(build.id) * 100) + StatUnit.percent.localized()),
                () -> Pal.accent,
                () -> 1.0f
            ))
        );
    }

    @Override
    public float requestedPower(Building build){
        return buffered
            ? (1.0f - build.power.status) * capacity
            : usage * scaleMap.get(build.id) * Mathf.num(build.shouldConsume());
    }
}
