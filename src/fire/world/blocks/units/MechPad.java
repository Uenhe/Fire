package fire.world.blocks.units;

import arc.math.Mathf;
import fire.net.FRCall;
import mindustry.content.Fx;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;

import static mindustry.Vars.*;

public class MechPad extends mindustry.world.Block{

    protected final UnitType unitType;
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

    private boolean consValid(){
        return powerCons > 0.0f;
    }

    public static void playerSpawn(Tile tile, Player pl){
        if(pl == null || tile == null || !(tile.build instanceof MechPadBuild pad)) return;

        var spawnType = ((MechPad)pad.block).unitType;
        if(pad.wasVisible)
            Fx.spawn.at(pad);

        //consumes power
        if(((MechPad)pad.block).consValid() && pad.power.graph.getPowerBalance() < ((MechPad)pad.block).powerCons * 60.0f)
            pad.power.graph.useBatteries(((MechPad)pad.block).powerCons);

        pl.set(pad);

        if(!net.client()){
            var unit = spawnType.create(tile.team());
            unit.set(pad);
            unit.rotation(90.0f);
            unit.impulse(0.0f, 3.0f);
            unit.spawnedByCore(true);
            unit.controller(pl);
            unit.add();
        }

        if(state.isCampaign() && pl == player)
            spawnType.unlock();
    }

    public class MechPadBuild extends mindustry.gen.Building{

        /** Active only when the player is close enough and power is enough. */
        @Override
        public boolean canControlSelect(Unit unit){
            return unit.isPlayer() && Mathf.dst(unit.x, unit.y, x, y) <= 120.0f && (power.graph.getBatteryStored() >= powerCons || power.graph.getPowerBalance() * 60.0f >= powerCons);
        }

        @Override
        public void onControlSelect(Unit unit){
            if(!unit.isPlayer()) return;
            Player pl = unit.getPlayer();

            Fx.spawn.at(pl);
            if(net.client() && pl == player)
                control.input.controlledType = null;

            pl.clearUnit();
            pl.deathTimer = Player.deathDelay + 1.0f;

            // do not try to respawn in unsupported environments at all
            if(!unitType.supportsEnv(state.rules.env)) return;
            FRCall.playerSpawn(tile, pl);
        }
    }
}
