package fire.world.blocks.power;

import arc.Core;
import arc.util.Scaling;
import fire.world.meta.FStat;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.game.Team;
import mindustry.ui.Styles;
import mindustry.world.Tile;

import static mindustry.Vars.world;

/**
 * ...Doesn't play well with blocks size odd.
 *
 * @author fy, ue
 */
public class HydroelectricGenerator extends mindustry.world.blocks.power.PowerGenerator{

    public HydroelectricGenerator(String name){
        super(name);
        noUpdateDisabled = true;
        buildType = HydroelectricGeneratorBuild::new;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FStat.specialIncrease, table -> {
            table.row();

            for(var b : Vars.content.blocks())
                if(b.isFloor() && b.asFloor().liquidDrop == Liquids.water)
                    table.table(Styles.grayPanel, t -> {
                        t.left().image(b.uiIcon).size(40.0f).pad(10.0f).scaling(Scaling.fit);
                        t.left().table(info -> {
                            info.left().add(b.localizedName).left().row();
                            info.left().add("[accent]" + FStat.floorMultiplier.localized() + b.asFloor().liquidMultiplier);
                        });
                    }).growX().pad(5.0f).row();
        });
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return getEfficiency(tile) > 0.0f;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        drawPlaceText(Core.bundle.formatFloat("bar.efficiency", getEfficiency(world.tile(x, y)) * 100, 1), x, y, valid);
    }

    public float getEfficiency(Tile tile){
        if(tile == null) return 0.0f;

        float efficiencyCountingDoubled = 0.0f;
        float efficiencyCounting = 0.0f;
        for(var other : tile.getLinkedTilesAs(this, tempTiles)){
            float efficiencyCurrent = other.floor().liquidMultiplier;
            if(other.floor().liquidDrop == null) efficiencyCurrent = 0.0f;
            efficiencyCounting += efficiencyCurrent;
            efficiencyCountingDoubled += efficiencyCurrent * efficiencyCurrent;
        }

        return (efficiencyCountingDoubled / sqr() - efficiencyCounting * efficiencyCounting / (sqr() * sqr())) * 4;
    }

    private short sqr(){
        return (short) (size * size);
    }

    public class HydroelectricGeneratorBuild extends GeneratorBuild{

        public float sum;

        @Override
        public void updateTile(){
            efficiency = productionEfficiency = sum;
        }

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();
            sum = getEfficiency(tile);
        }
    }
}
