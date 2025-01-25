package fire.world.consumers;

import arc.Core;
import arc.struct.ObjectMap;
import fire.world.blocks.defense.Campfire;
import mindustry.gen.Building;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

import static mindustry.Vars.tilesize;

/**
 * Accepts every item, but only consume those which has flammability.<p>
 * Only collocates with {@link Campfire}..?
 */
public class ConsumeItemFlammableEach extends mindustry.world.consumers.ConsumeItemFilter{

    public final Campfire block;
    public static final ObjectMap<Building, Float> efficiencyMap = new ObjectMap<>();

    public ConsumeItemFlammableEach(Campfire block){
        this.block = block;
        filter = i -> true; //accepts every item
        optional(true, true);
    }

    /** Only support consume 1 item each time, currently. Multiple items need extra judgment. */
    @Override
    public void trigger(Building build){
        float[] sum = new float[1];
        build.items.each((item, amount) -> {
            if(item.flammability > 0.0f){
                sum[0] += item.flammability;
                build.items.remove(item, 1);
            }
        });

        efficiencyMap.put(build, sum[0]);
    }

    @Override
    public void update(Building build){
        //efficiencyMap.put(build.id, build.items.sum(((item, amount) -> item.flammability)));
    }

    @Override
    public float efficiencyMultiplier(Building build){
        return efficiencyMap.get(build, 1.0f);
    }

    @Override
    public void display(Stats stats){
        stats.remove(Stat.booster);
        stats.add(Stat.booster, table ->
            table.row().table(Styles.grayPanel, t ->
                t.row().left().add(Core.bundle.format("stat.consumeitemflammableeach", block.speedBoostPhase * 100, block.phaseRangeBoost / tilesize)).growX().pad(5.0f)
            )
        );
    }
}
