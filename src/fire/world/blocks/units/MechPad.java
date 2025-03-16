package fire.world.blocks.units;

import arc.math.Mathf;
import fire.annotations.FRAnnotations;
import fire.gen.FRCall;
import mindustry.content.Fx;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;

import static mindustry.Vars.control;
import static mindustry.Vars.net;
import static mindustry.Vars.player;
import static mindustry.Vars.state;

public class MechPad extends mindustry.world.Block{

    final UnitType unitType;
    /** Power consumed per use. */
    protected float powerCons;

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

        if(consValid()) stats.add(Stat.powerUse, powerCons);
    }

    boolean consValid(){
        return powerCons > 0.0f;
    }

    @FRAnnotations.Remote(called = FRAnnotations.Loc.server)
    public static void playerSpawn(Tile tile, Player playee){
        if(playee == null || tile == null || !(tile.build instanceof MechPadBuild pad)) return;

        var spawnType = ((MechPad)pad.block).unitType;
        if(pad.wasVisible)
            Fx.spawn.at(pad);

        //consumes power
        if(((MechPad)pad.block).consValid() && pad.power.graph.getPowerBalance() < ((MechPad)pad.block).powerCons * 60.0f)
            pad.power.graph.useBatteries(((MechPad)pad.block).powerCons);

        playee.set(pad);

        if(!net.client()){
            Unit unit = spawnType.create(tile.team());
            unit.set(pad);
            unit.rotation(90.0f);
            unit.impulse(0.0f, 3.0f);
            unit.spawnedByCore(true);
            unit.controller(playee);
            unit.add();
        }

        if(state.isCampaign() && playee == player)
            spawnType.unlock();
    }

    public class MechPadBuild extends mindustry.gen.Building{

        /** Active only when the player is close enough and power is enough. */
        @Override
        public boolean canControlSelect(Unit player){
            return player.isPlayer() && Mathf.dst(player.x, player.y, x, y) <= 120.0f && (power.graph.getBatteryStored() >= powerCons || power.graph.getPowerBalance() * 60.0f >= powerCons);
        }

        @Override
        public void onControlSelect(Unit unit){
            if(!unit.isPlayer()) return;
            Player playee = unit.getPlayer();

            Fx.spawn.at(playee);
            if(net.client() && playee == player)
                control.input.controlledType = null;

            playee.clearUnit();
            playee.deathTimer = Player.deathDelay + 1.0f;

            // do not try to respawn in unsupported environments at all
            if(!unitType.supportsEnv(state.rules.env)) return;
            FRCall.playerSpawn(tile, playee);
        }
    }
}
