package fire.world.consumers;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.StatUnit;

/** @implSpec Implement {@link CustomPowerConsumer} in building class. */
public class ConsumePowerCustom extends mindustry.world.consumers.ConsumePower{

    public ConsumePowerCustom(float usage, float capacity, boolean buffered, Block block){
        super(usage, capacity, buffered);
        Events.on(EventType.ContentInitEvent.class, e -> { //prevent adding this bar prior to others
            block.addBar("powerscale", build -> {
                assert build instanceof CustomPowerConsumer;
                return new Bar(
                    () -> Core.bundle.format("bar.powerscale", Math.round(((CustomPowerConsumer)build).consPowerScale() * 100) + StatUnit.percent.localized()),
                    () -> Pal.accent,
                    () -> Mathf.num(((CustomPowerConsumer)build).consPowerScale() > 0.0f)
                );
            });
        });
    }

    @Override
    public float requestedPower(Building build){
        assert build instanceof CustomPowerConsumer;
        return buffered
            ? (1.0f - build.power.status) * capacity
            : usage * ((CustomPowerConsumer)build).consPowerScale() * Mathf.num(build.shouldConsume());
    }

    public interface CustomPowerConsumer{
        float consPowerScale();
    }
}
