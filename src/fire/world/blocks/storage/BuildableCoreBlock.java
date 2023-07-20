package fire.world.blocks.storage;

import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.state;

public class BuildableCoreBlock extends CoreBlock{
    public BuildableCoreBlock(String name){
        super(name);
    }

    @Override
    public boolean canBreak(Tile tile){
        return tile.team() == Team.derelict || state.teams.cores(tile.team()).size > 1 || state.isEditor();
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return true;
    }
}
