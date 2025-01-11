package fire.world.consumers;

import fire.world.blocks.defense.Campfire;
import mindustry.gen.Building;
import mindustry.world.meta.Stat;

import static mindustry.Vars.tilesize;

/**
 * Accepts every item, but only consume those which has flammability.
 * <p>
 * Collocates with {@link Campfire}..?
 */
public class ConsumeItemEachFlammable extends mindustry.world.consumers.ConsumeItemFilter{

    public final Campfire block;
    public float efficiencyMultiplier;

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
        efficiencyMultiplier = 0.0f;
        build.items.each((item, amount) ->
            efficiencyMultiplier += item.flammability);
    }

    @Override
    public float efficiencyMultiplier(Building build){
        return efficiencyMultiplier;
    }

    /** TODO: Hardcode */
    @Override
    public void display(mindustry.world.meta.Stats stats){
        stats.remove(Stat.booster);
        stats.add(Stat.booster, table ->
            table.table(mindustry.ui.Styles.grayPanel, t -> {
                t.row();
                t.left().add(arc.util.Strings.format("Accepts every item, but only consume those which has flammability\nEvery 100% flammability provides extra @% speed boost and @ range boost", block.speedBoostPhase * 100, block.phaseRangeBoost / tilesize)).growX().pad(5.0f);
            })
        );
    }
}
