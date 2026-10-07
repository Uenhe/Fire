package fire.type;

import arc.Events;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.IntMap;
import arc.util.Tmp;
import fire.content.FRBlocks;
import fire.content.FRFx;
import fire.content.FRStatusEffects;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;

import static mindustry.Vars.*;

public class FleshUnitType extends UnitType{

    public static final IntMap<UnitType> fleshUnitMap = new IntMap<>(29);

    static{
        Events.on(EventType.UnitDrownEvent.class, e -> {
            if(e.unit.type.flying || e.unit.type instanceof FleshUnitType || e.unit.lastDrownFloor != FRBlocks.neoplasm) return;
            var team = state.rules.waveTeam;
            if(e.unit.team != team) return;

            float min = Float.MAX_VALUE;
            final int tr = 6;
            int utx = e.unit.tileX(), uty = e.unit.tileY(),
                mtx = utx + tr + 1, mty = uty + tr + 1,
                ttx = -1, tty = -1;

            for(int tx = utx - tr; tx < mtx; tx++){
                for(int ty = uty - tr; ty < mty; ty++){
                    var tile = world.tile(tx, ty);
                    if(tile == null || !tile.block().isAir() || tile.floor().isDeep()) continue;
                    float dst = Mathf.dst2(utx, uty, tx, ty);
                    if(min > dst){
                        min = dst;
                        ttx = tx;
                        tty = ty;
                    }
                }
            }

            Unit spawned;
            var type = fleshUnitMap.get(e.unit.type.id);
            if(min == Float.MAX_VALUE){
                spawned = type.spawn(team, e.unit.x, e.unit.y);
            }else{
                spawned = type.spawn(team, ttx * tilesize, tty * tilesize, Angles.angle(e.unit.tileX(), e.unit.tileY(), ttx, tty));
                FRFx.fleshTeleportEffect.at(e.unit.x, e.unit.y, e.unit.rotation - 90.0f, new FRFx.TpFxData(spawned, spawned.x, spawned.y));
            }

            for(var effect : content.statusEffects()){
                if(!e.unit.hasEffect(effect)) continue;
                spawned.apply(effect, e.unit.getDuration(effect));
            }
        });
    }

    public FleshUnitType(String name){
        super(name);
        healColor = Pal.neoplasm1;
    }

    public FleshUnitType(String name, UnitType origin){
        super(name);
        healColor = Pal.neoplasm1;
        fleshUnitMap.put(origin.id, this);
        fleshUnitMap.put(id, origin);
    }

    @Override
    public void update(Unit unit){
        if(Mathf.chanceDelta(0.05)){
            Tmp.v1.rnd(Mathf.range(hitSize * 0.7f));
            FRStatusEffects.overgrown.effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0.0f, hitSize * 0.8f);
        }
    }
}
