package fire.world.blocks.power;

import arc.Core;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import fire.world.meta.FRStat;
import mindustry.game.Team;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.Stat;

import static mindustry.Vars.world;

public class HydroelectricGenerator extends mindustry.world.blocks.power.PowerGenerator{

    public static Seq<Floor> liquidFloors = new Seq<>();

    public HydroelectricGenerator(String name){
        super(name);
        noUpdateDisabled = true;
        buildType = HydroelectricGeneratorBuild::new;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.tiles, table -> {
            table.row();

            for(var floor : liquidFloors){
                table.table(Styles.grayPanel, t -> {
                    t.left().image(floor.uiIcon).size(40.0f).pad(10.0f).scaling(Scaling.fit);
                    t.left().table(info -> {
                        info.left().add(floor.localizedName).left().row();
                        info.left().add("[accent]" + FRStat.floorMultiplier.localized() + Strings.fixed(floor.liquidMultiplier * (1.0f - floor.liquidDrop.viscosity) * floor.liquidDrop.temperature * 4.0f, 2));
                    });
                }).growX().pad(5.0f).row();
            }
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

        float efficiencyCountingDoubled = 0.0f, efficiencyCounting = 0.0f;
        var tiles = tile.getLinkedTilesAs(this, tempTiles);
        for(var other : tiles){
            float efficiencyCurrent = other.floor().liquidDrop != null
                ? other.floor().liquidMultiplier * (1.0f - other.floor().liquidDrop.viscosity) * other.floor().liquidDrop.temperature * 4.0f
                : 0.0f;
            efficiencyCounting += efficiencyCurrent;
            efficiencyCountingDoubled += efficiencyCurrent * efficiencyCurrent;
        }

        return (efficiencyCountingDoubled / sqr() - efficiencyCounting * efficiencyCounting / (sqr() * sqr())) * 4.0f;
    }

    private short sqr(){
        return (short)(size * size);
    }

    public class HydroelectricGeneratorBuild extends GeneratorBuild{

        private float sum;

        @Override
        public void updateTile(){
            efficiency = productionEfficiency = sum;
        }

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();
            sum = getEfficiency(tile);
        }

        @Override
        public float totalProgress(){
            return super.totalProgress() * efficiency; //for drawer
        }
    }
}
