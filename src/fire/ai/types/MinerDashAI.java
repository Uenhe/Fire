package fire.ai.types;

import mindustry.content.Blocks;

import static mindustry.Vars.indexer;

/** Basically a copy of MinerAI. */
public class MinerDashAI extends mindustry.ai.types.MinerAI{

    @Override
    public void updateMovement(){
        final var core = unit.closestCore();
        if(!(unit.canMine()) || core == null) return;
        if(unit.mineTile != null && !unit.mineTile.within(unit, unit.type.mineRange)){
            unit.mineTile(null);
        }
        if(ore != null && !unit.validMine(ore)){
            ore = null;
            unit.mineTile = null;
        }
        if(mining){
            if(timer.get(timerTarget2, 60 * 4) || targetItem == null){
                targetItem = unit.type.mineItems.min(i -> indexer.hasOre(i) && unit.canMine(i), i -> core.items.get(i));
            }
            // if core full of the target item, do nothing
            if(targetItem != null && core.acceptStack(targetItem, 1, unit) == 0){
                unit.clearItem();
                unit.mineTile = null;
                return;
            }
            // if inventory fulls, drop it off
            if(unit.stack.amount >= unit.type.itemCapacity || (targetItem != null && !unit.acceptsItem(targetItem))){
                mining = false;
            }else{
                if(timer.get(timerTarget3, 60) && targetItem != null){
                    ore = indexer.findClosestOre(unit, targetItem);
                }
                if(ore != null){
                    moveTo(ore, unit.type.mineRange / 2f, 20f);
                    dash(ore);

                    if(ore.block() == Blocks.air && unit.within(ore, unit.type.mineRange)){
                        unit.mineTile = ore;
                    }
                    if(ore.block() != Blocks.air){
                        mining = false;
                    }
                }
            }
        }else{
            unit.mineTile = null;
            if(unit.stack.amount == 0){
                mining = true;
                return;
            }
            if(unit.within(core, unit.type.range)){
                if(core.acceptStack(unit.stack.item, unit.stack.amount, unit) > 0){
                    mindustry.gen.Call.transferItemTo(unit, unit.stack.item, unit.stack.amount, unit.x, unit.y, core);
                }
                unit.clearItem();
                mining = true;
            }
            circle(core, unit.type.range / 1.8f);
            dash(core);

        }
    }

    private void dash(arc.math.geom.Position pos){
        for(final var a : unit.abilities) if(a instanceof fire.entities.abilities.DashAbility da){
            da.dash(unit, pos);
            break;
        }
    }
}
