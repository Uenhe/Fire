package fire.world.consumers;

import arc.struct.ObjectMap;
import arc.util.Strings;
import fire.world.blocks.defense.Campfire;
import mindustry.gen.Building;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

import static mindustry.Vars.tilesize;

/**
 * Accepts every item, but only consume those which has flammability.
 * <p>
 * Only collocates with {@link Campfire}..?
 */
public class ConsumeItemEachFlammable extends mindustry.world.consumers.ConsumeItemFilter{

    public final Campfire block;
    public static final ObjectMap<Integer, Float> efficiencyMap = new ObjectMap<>();

    public ConsumeItemEachFlammable(Campfire block){
        this.block = block;
        filter = i -> true; //accepts every item
        optional(true, true);
    }

    /** Only support consume 1 item each time, currently. Multiple items need extra judgment. */
    @Override
    public void trigger(Building build){
        build.items.each((item, amount) -> {
            if(item.flammability > 0.0f)
                build.items.remove(item, 1);
        });
    }

    @Override
    public void update(Building build){
        efficiencyMap.put(build.id, build.items.sum(((item, amount) -> item.flammability)));
    }

    @Override
    public float efficiencyMultiplier(Building build){
        return efficiencyMap.get(build.id, 1.0f);
    }

    /** TODO: Hardcode */
    @Override
    public void display(Stats stats){
        stats.remove(Stat.booster);
        stats.add(Stat.booster, table ->
            table.row().table(Styles.grayPanel, t ->
                t.row().left().add(Strings.format("Accepts every item, but only consume those which has flammability\nEvery 100% flammability provides extra @% speed boost and @ block range boost", block.speedBoostPhase * 100, block.phaseRangeBoost / tilesize)).growX().pad(5.0f)
            )
        );
    }
}
