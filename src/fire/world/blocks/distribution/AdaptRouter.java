package fire.world.blocks.distribution;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.content.UnitTypes;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.net;

public class AdaptRouter extends mindustry.world.Block{

    public AdaptRouter(String name){
        super(name);
        solid = false;
        underBullets = true;
        update = true;
        group = BlockGroup.transportation;
        instantTransfer = true;
        unloadable = false;
        canOverdrive = false;
        logicConfigurable = true;
        buildType = AdaptRouterBuild::new;
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    public static class AdaptRouterBuild extends Building implements mindustry.world.blocks.ControlBlock{

        private float logicTimer;
        private @Nullable Building source;
        private final BlockUnitc unit = (BlockUnitc)UnitTypes.block.create(team);

        @Override
        public Unit unit(){
            unit.tile(this);
            unit.team(team);
            return (Unit)unit;
        }

        @Override
        public boolean shouldAutoTarget(){
            return false;
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.config && !isControlled()){
                logicTimer = 5.0f;
                rotation = (int)p1;
            }
            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4){
            if(type == LAccess.config && !isControlled()){
                logicTimer = 5.0f;
                rotation = (int)p1;
            }
            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void updateTile(){
            logicTimer -= delta();
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return enabled && getTileTarget(source, item, false) != null;
        }

        @Override
        public void handleItem(Building source, Item item){
            var target = getTileTarget(source, item, true);
            if(target != null){
                this.source = source;
                target.handleItem(this, item);
            }
        }

        private @Nullable Building getTileTarget(Building src, Item item, boolean set){
            var u = unit;
            if(u != null && isControlled()){
                u.health(health);
                u.team(team);
                u.set(x, y);
                if(u.isShooting()){
                    var other = nearby(rotation = Mathf.mod((int)((angleTo(u.aimX(), u.aimY()) + 45) / 90), 4));
                    if(other != null && other != src && other != source && !(src.block.instantTransfer && other.block.instantTransfer) & other.acceptItem(this, item) && team == other.team) return other;
                }

            }else if(logicTimer >= 0.0f){
                var other = nearby(rotation);
                if(other != null && other != src && other != source && !(src.block.instantTransfer && other.block.instantTransfer) & other.acceptItem(this, item) && team == other.team) return other;

            }else{
                var proximity = this.proximity;
                if(net.server()){ //byd desync
                    for(int i = 0, n = proximity.size; i < n; i++){
                        var other = proximity.get((i + rotation) % n);
                        if(set) rotation = (rotation + 1) % n;
                        if(other == src || other == source || (src.block.instantTransfer && other.block.instantTransfer) || !other.acceptItem(this, item) || team != other.team) continue;
                        return other;
                    }

                }else{
                    return new Seq<>(proximity)
                        .removeAll(build -> build == src || build == source || (src.block.instantTransfer && build.block.instantTransfer) || !build.acceptItem(this, item) || team != build.team)
                        .random();
                }
            }

            return null;
        }
    }
}
