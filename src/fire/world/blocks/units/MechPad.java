package fire.world.blocks.units;

import arc.math.Mathf;
import fire.annotations.FireAnnotations;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;

import static mindustry.Vars.*;

public class MechPad extends mindustry.world.Block{

    public UnitType unitType;
    /** Power consumed per use. */
    public float consumesPower = -1f;

    public MechPad(String name, UnitType type){
        super(name);
        solid = true;
        destructible = true;
        hasPower = true;
        canOverdrive = false;
        unitType = type;
    }

    @Override
    public void setStats(){
        super.setStats();

        if(consValid()) stats.add(Stat.powerUse, consumesPower);
    }

    public boolean consValid(){
        return consumesPower > 0f;
    }

    @FireAnnotations.Remote(called = FireAnnotations.Loc.server)
    public static void playerSpawn(Tile tile, Player play){
        if(play == null || tile == null || !(tile.build instanceof MechPadBuild pad)) return;

        UnitType spawnType = ((MechPad)pad.block).unitType;
        if(pad.wasVisible){
            Fx.spawn.at(pad);
        }

        //consumes power
        if(((MechPad)pad.block).consValid()) pad.power.graph.useBatteries(((MechPad)pad.block).consumesPower);
        play.set(pad);

        if(!net.client()){
            Unit unit = spawnType.create(tile.team());
            unit.set(pad);
            unit.rotation(90f);
            unit.impulse(0f, 3f);
            unit.spawnedByCore(true);
            unit.controller(play);
            unit.add();
        }

        if(state.isCampaign() && play == player){
            spawnType.unlock();
        }
    }

    public class MechPadBuild extends Building{

        @Override
        public boolean canControlSelect(Unit player){
            /* active only when:
             *
             * 1) player is enough close to pad
             * 2) power is enough
             *
             */
            return player.isPlayer() && Mathf.dst(player.x, player.y, x, y) <= 80f && power.graph.getBatteryStored() >= consumesPower;
        }

        @Override
        public void onControlSelect(Unit unit){
            if(!unit.isPlayer()) return;
            Player play = unit.getPlayer();

            Fx.spawn.at(play);
            if(net.client() && play == player){
                control.input.controlledType = null;
            }

            play.clearUnit();
            play.deathTimer = Player.deathDelay + 1f;
            requestSpawn(play);
        }

        public void requestSpawn(Player player){
            //do not try to respawn in unsupported environments at all
            if(!unitType.supportsEnv(state.rules.env)) return;

            fire.gen.FireCall.playerSpawn(tile, player);
        }
    }
}
