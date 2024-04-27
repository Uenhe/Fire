package fire.ai.types;

import mindustry.gen.Groups;
import mindustry.world.Build;
import mindustry.world.blocks.ConstructBlock;

import static mindustry.Vars.content;
import static mindustry.Vars.world;

/** Basically a copy of BuilderAI. */
public class BuilderDashAI extends mindustry.ai.types.BuilderAI{

    private boolean found;
    private float retreatTimer;

    @Override
    public void updateMovement(){
        if(target != null && shouldShoot()){
            unit.lookAt(target);
        }
        unit.updateBuilding = true;
        if(assistFollowing != null && assistFollowing.activelyBuilding()){
            following = assistFollowing;
        }
        if(following != null){
            retreatTimer = 0f;
            // try to follow and mimic someone
            // validate follower
            if(!following.isValid() || !following.activelyBuilding()){
                following = null;
                unit.plans.clear();
                return;
            }
            // set to follower's first build plan, whatever that is
            unit.plans.clear();
            unit.plans.addFirst(following.buildPlan());
            lastPlan = null;
        }else if(unit.buildPlan() == null || alwaysFlee){
            // not following anyone or building
            if(timer.get(timerTarget4, 40)){
                enemy = target(unit.x, unit.y, fleeRange, true, true);
            }
            // fly away from enemies when not doing anything, but only after a delay
            if((retreatTimer += arc.util.Time.delta) >= retreatDelay || alwaysFlee){
                if(enemy != null){
                    unit.clearBuilding();
                    final var core = unit.closestCore();
                    if(core != null && !unit.within(core, retreatDst)){
                        moveTo(core, retreatDst);
                        dash(core);

                    }
                }
            }
        }
        if(unit.buildPlan() != null){
            if(!alwaysFlee) retreatTimer = 0f;
            // approach plan if building
            final var req = unit.buildPlan();
            // clear break plan if another player breaking something
            if(!req.breaking && timer.get(timerTarget2, 40f)){
                for(final var player : Groups.player){
                    if(player.isBuilder() && player.unit().activelyBuilding() && player.unit().buildPlan().samePos(req) && player.unit().buildPlan().breaking){
                        unit.plans.removeFirst();
                        // remove from the list of plans
                        unit.team.data().plans.remove(p -> p.x == req.x && p.y == req.y);
                        return;
                    }
                }
            }
            final boolean valid =
                !(lastPlan != null && lastPlan.removed) &&
                    ((req.tile() != null && req.tile().build instanceof ConstructBlock.ConstructBuild cons && cons.current == req.block) ||
                        (req.breaking ?
                            Build.validBreak(unit.team(), req.x, req.y) :
                            Build.validPlace(req.block, unit.team(), req.x, req.y, req.rotation)));
            if(valid){
                // move toward the plan
                moveTo(req.tile(), unit.type.buildRange - 20f);
                dash(req.tile());

            }else{
                // discard invalid plan
                unit.plans.removeFirst();
                lastPlan = null;
            }
        }else{
            if(assistFollowing != null){
                moveTo(assistFollowing, assistFollowing.type.hitSize + unit.type.hitSize / 2f + 60f);
                dash(assistFollowing);

            }
            // follow someone and help them build
            if(timer.get(timerTarget2, 20f)){
                found = false;
                mindustry.entities.Units.nearby(unit.team, unit.x, unit.y, buildRadius, u -> {
                    if(found) return;
                    if(u.canBuild() && u != unit && u.activelyBuilding()){
                        final var plan = u.buildPlan();
                        final var build = world.build(plan.x, plan.y);
                        if(build instanceof ConstructBlock.ConstructBuild cons){
                            float dist = Math.min(cons.dst(unit) - unit.type.buildRange, 0f);
                            // make sure it can reach the plan in time
                            if(dist / unit.speed() < cons.buildCost * 0.9f){
                                following = u;
                                found = true;
                            }
                        }
                    }
                });
                if(onlyAssist){
                    float minDst = Float.MAX_VALUE;
                    mindustry.gen.Player closest = null;
                    for(final var player : Groups.player){
                        if(player.unit().canBuild() && !player.dead() && player.team() == unit.team){
                            final float dst = player.dst2(unit);
                            if(dst < minDst){
                                closest = player;
                                minDst = dst;
                            }
                        }
                    }
                    assistFollowing = closest == null ? null : closest.unit();
                }
            }
            // find a new plan
            if(!onlyAssist && !unit.team.data().plans.isEmpty() && following == null && timer.get(timerTarget3, rebuildPeriod)){
                final var blocks = unit.team.data().plans;
                final var block = blocks.first();
                // check if it's already been placed
                if(world.tile(block.x, block.y) != null && world.tile(block.x, block.y).block().id == block.block){
                    blocks.removeFirst();
                }else if(Build.validPlace(content.block(block.block), unit.team(), block.x, block.y, block.rotation) && (!alwaysFlee || !nearEnemy(block.x, block.y))){ //it's valid
                    lastPlan = block;
                    // add build plan
                    unit.addBuild(new mindustry.entities.units.BuildPlan(block.x, block.y, block.rotation, content.block(block.block), block.config));
                    // shift build plan to tail so next unit builds something else
                    blocks.addLast(blocks.removeFirst());
                }else{
                    // shift head of queue to tail, try something else next time
                    blocks.addLast(blocks.removeFirst());
                }
            }
        }
    }

    private void dash(arc.math.geom.Position pos){
        for(final var a : unit.abilities) if(a instanceof fire.entities.abilities.DashAbility da){
            da.dash(unit, pos);
            break;
        }
    }
}
