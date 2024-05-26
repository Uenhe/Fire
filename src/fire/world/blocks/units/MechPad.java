package fire.world.blocks.units;

import arc.math.Mathf;
import fire.annotations.FAnnotations;
import fire.gen.FCall;
import mindustry.content.Fx;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Tile;

import static mindustry.Vars.*;

public class MechPad extends mindustry.world.Block{

    private final UnitType unitType;
    /** Power consumed per use. */
    protected float consumesPower = -1f;

    protected MechPad(String name, UnitType type){
        super(name);
        solid = true;
        destructible = true;
        hasPower = true;
        canOverdrive = false;
        unitType = type;
        buildType = MechPadBuild::new;
    }

    @Override
    public void setStats(){
        super.setStats();

        if(consValid()) stats.add(mindustry.world.meta.Stat.powerUse, consumesPower);
    }

    private boolean consValid(){
        return consumesPower > 0f;
    }

    @FAnnotations.Remote(called = FAnnotations.Loc.server)
    public static void playerSpawn(Tile tile, Player playee){
        if(playee == null || tile == null || !(tile.build instanceof MechPadBuild pad)) return;

        final var spawnType = ((MechPad)pad.block).unitType;
        if(pad.wasVisible)
            Fx.spawn.at(pad);

        //consumes power
        if(((MechPad)pad.block).consValid())
            pad.power.graph.useBatteries(((MechPad)pad.block).consumesPower);

        playee.set(pad);

        if(!net.client()){
            Unit unit = spawnType.create(tile.team());
            unit.set(pad);
            unit.rotation(90f);
            unit.impulse(0f, 3f);
            unit.spawnedByCore(true);
            unit.controller(playee);
            unit.add();
        }

        if(state.isCampaign() && playee == player){
            spawnType.unlock();
        }
    }

    public class MechPadBuild extends mindustry.gen.Building{

        /** Active only when the player is enough close and power is enough. */
        @Override
        public boolean canControlSelect(Unit player){
            return player.isPlayer() && Mathf.dst(player.x, player.y, x, y) <= 80f && power.graph.getBatteryStored() >= consumesPower;
        }

        @Override
        public void onControlSelect(Unit unit){
            if(!unit.isPlayer()) return;
            final var playee = unit.getPlayer();

            Fx.spawn.at(playee);
            if(net.client() && playee == player)
                control.input.controlledType = null;

            playee.clearUnit();
            playee.deathTimer = Player.deathDelay + 1f;

            // do not try to respawn in unsupported environments at all
            if(!unitType.supportsEnv(state.rules.env)) return;
            FCall.playerSpawn(tile, playee);
        }
    }
}
