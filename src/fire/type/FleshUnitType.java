package fire.type;

import arc.math.Angles;
import arc.math.Mathf;
import fire.content.FRBlocks;
import fire.content.FRFx;
import fire.content.FRStatusEffects;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;

import static mindustry.Vars.*;

public class FleshUnitType extends UnitType{

    public final UnitType origin;

    public FleshUnitType(String name){
        this(name, null);
    }

    public FleshUnitType(String name, UnitType origin){
        super(name);
        healColor = Pal.neoplasm1;
        if((this.origin = origin) != null){
            origin.abilities.add(new DrownRespawnAbility(this));
        }
    }

    @Override
    public void update(Unit unit){
        if(Mathf.chanceDelta(0.04))
            FRStatusEffects.overgrown.effect.at(unit.x, unit.y, 0.0f, hitSize);
    }

    /** {@link mindustry.game.EventType.UnitDrownEvent UnitDrownEvent} is buggy (why???) so I have to use an ability. */
    private static class DrownRespawnAbility extends mindustry.entities.abilities.Ability{

        private final UnitType flesh;

        private DrownRespawnAbility(UnitType flesh){
            this.flesh = flesh;
        }

        @Override
        public void death(Unit unit){
            var team = state.rules.waveTeam;
            if(unit.lastDrownFloor != FRBlocks.neoplasm || unit.team != team) return;

            float min = Float.MAX_VALUE;
            final int tr = 6;
            int utx = unit.tileX(), uty = unit.tileY(),
                mtx = utx + tr + 1, mty = uty + tr + 1,
                ttx = -1, tty = -1;

            for(int tx = utx - tr; tx < mtx; tx++){
                for(int ty = uty - tr; ty < mty; ty++){
                    var tile = world.tile(tx, ty);
                    if(tile == null || !tile.block().isAir() || tile.floor().isDeep()) continue;
                    float dst = Mathf.dst(utx, uty, tx, ty);
                    if(min > dst){
                        min = dst;
                        ttx = tx;
                        tty = ty;
                    }
                }
            }

            Unit spawned;
            if(min == Float.MAX_VALUE){
                spawned = flesh.spawn(team, unit.x, unit.y);
            }
            else{
                spawned = flesh.spawn(team, ttx * tilesize, tty * tilesize, Angles.angle(unit.tileX(), unit.tileY(), ttx, tty));
                FRFx.fleshTeleportEffect.at(unit.x, unit.y, unit.rotation - 90.0f, new FRFx.TpFxData(spawned, spawned.x, spawned.y));
            }

            for(var effect : content.statusEffects()){
                if(!unit.hasEffect(effect)) continue;
                spawned.apply(effect, unit.getDuration(effect));
            }
        }
    }
}
