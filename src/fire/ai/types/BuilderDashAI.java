package fire.ai.types;

import arc.math.geom.Position;
import arc.util.Reflect;
import arc.util.Time;
import fire.entities.abilities.DashAbility;
import mindustry.ai.types.BuilderAI;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.world.Build;
import mindustry.world.blocks.ConstructBlock;

import static mindustry.Vars.world;

/** Basically a copy of BuilderAI. */
public class BuilderDashAI extends BuilderAI{

    private final DashAbility dash;

    public BuilderDashAI(DashAbility ab){
        dash = ab;
    }

    public BuilderDashAI(DashAbility ab, int e){
        this(ab);
        onlyAssist = true;
    }

    @Override
    public void updateMovement(){
        if(target != null && shouldShoot()) unit.lookAt(target);
        else if(!unit.type.flying) unit.lookAt(unit.prefRotation());
        unit.updateBuilding = true;
        if(assistFollowing != null && assistFollowing.activelyBuilding()) following = assistFollowing;
        boolean moving = false;
        if(following != null){
            retreatTimer(0.0f);
            if(!following.isValid() || !following.activelyBuilding()){
                following = null;
                unit.plans.clear();
                return;
            }
            unit.plans.clear();
            unit.plans.addFirst(following.buildPlan());
            lastPlan = null;
        }else if(unit.buildPlan() == null || alwaysFlee){
            if(timer.get(timerTarget4, 40.0f)) enemy = target(unit.x, unit.y, fleeRange, true, true);
            retreatTimer(retreatTimer() + Time.delta);
            if(retreatTimer() >= retreatDelay || alwaysFlee){
                if(enemy != null){
                    unit.clearBuilding();
                    var core = unit.closestCore();
                    if(core != null && !unit.within(core, retreatDst)){
                        moveTo(core, retreatDst);
                        dash(core);
                        moving = true;
                    }
                }
            }
        }
        if(unit.buildPlan() != null){
            if(!alwaysFlee) retreatTimer(0.0f);
            var req = unit.buildPlan();
            if(!req.breaking && timer.get(timerTarget2, 40.0f)){
                var players = Groups.player;
                for(var player : players){
                    if(player.isBuilder() && player.unit().activelyBuilding() && player.unit().buildPlan().samePos(req) && player.unit().buildPlan().breaking){
                        unit.plans.removeFirst();
                        unit.team.data().plans.remove(p -> p.x == req.x && p.y == req.y);
                        return;
                    }
                }
            }
            boolean valid = !(lastPlan != null && lastPlan.removed)
                && ((req.tile() != null && req.tile().build instanceof ConstructBlock.ConstructBuild cons && cons.current == req.block) || (req.breaking ? Build.validBreak(unit.team(), req.x, req.y) : Build.validPlace(req.block, unit.team(), req.x, req.y, req.rotation)));
            if(valid){
                moveTo(req.tile(), unit.type.buildRange - 20f, 20f);
                dash(req.tile());
                moving = !unit.within(req.tile(), unit.type.buildRange - 10f);
            }else{
                unit.plans.removeFirst();
                lastPlan = null;
            }
        }else{
            if(assistFollowing != null){
                moveTo(assistFollowing, assistFollowing.type.hitSize + unit.type.hitSize/2f + 60f);
                dash(assistFollowing);
                moving = !unit.within(assistFollowing, assistFollowing.type.hitSize + unit.type.hitSize/2f + 65f);
            }
            if(timer.get(timerTarget2, 20.0f)){
                found(false);
                Units.nearby(unit.team, unit.x, unit.y, buildRadius, u -> {
                    if(found()) return;
                    if(u.canBuild() && u != unit && u.activelyBuilding()){
                        var plan = u.buildPlan();
                        var build = world.build(plan.x, plan.y);
                        if(build instanceof ConstructBlock.ConstructBuild cons){
                            float dist = Math.min(cons.dst(unit) - unit.type.buildRange, 0);
                            if(dist / unit.speed() < cons.buildCost * 0.9f){
                                following = u;
                                found(true);
                            }
                        }
                    }
                });
                if(onlyAssist){
                    float minDst = Float.MAX_VALUE;
                    Player closest = null;
                    var players = Groups.player;
                    for(var player : players){
                        if(!player.dead() && player.isBuilder() && player.team() == unit.team){
                            float dst = player.dst2(unit);
                            if(dst < minDst){
                                closest = player;
                                minDst = dst;
                            }
                        }
                    }
                    assistFollowing = closest == null ? null : closest.unit();
                }
            }
            if(!onlyAssist && !unit.team.data().plans.isEmpty() && following == null && timer.get(timerTarget3, rebuildPeriod)){
                var blocks = unit.team.data().plans;
                var block = blocks.first();
                if(world.tile(block.x, block.y) != null && world.tile(block.x, block.y).block() == block.block) blocks.removeFirst();
                else if(Build.validPlace(block.block, unit.team(), block.x, block.y, block.rotation) && (!alwaysFlee || !nearEnemy(block.x, block.y))){
                    lastPlan = block;
                    unit.addBuild(new BuildPlan(block.x, block.y, block.rotation, block.block, block.config));
                    blocks.addLast(blocks.removeFirst());
                }else blocks.addLast(blocks.removeFirst());
            }
        }
        if(!unit.type.flying) unit.updateBoosting(unit.type.boostWhenBuilding || moving || unit.floorOn().isDuct || unit.floorOn().damageTaken > 0f || unit.floorOn().isDeep());
    }

    private void dash(Position pos){
        if(dash == null) return;
        dash.dash(unit, pos);
    }

    private float retreatTimer(){
        return Reflect.get(BuilderAI.class, this, "retreatTimer");
    }

    private void retreatTimer(float retreatTimer){
        Reflect.set(BuilderAI.class, this, "retreatTimer", retreatTimer);
    }

    private boolean found(){
        return Reflect.get(BuilderAI.class, this, "found");
    }

    private void found(boolean found){
        Reflect.set(BuilderAI.class, this, "found", found);
    }
}
