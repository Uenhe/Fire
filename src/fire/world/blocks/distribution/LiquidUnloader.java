package fire.world.blocks.distribution;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.liquid.ArmoredConduit;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.content;


public class LiquidUnloader extends mindustry.world.Block{

    private String centerRegion;

    public LiquidUnloader(String name){
        super(name);
        solid = false;
        underBullets = true;
        update = true;
        group = BlockGroup.liquids;
        unloadable = false;
        logicConfigurable = true;
        configurable = true;
        outputsLiquid = true;
        saveConfig = true;
        canOverdrive = false;
        buildType = LiquidUnloader.LiquidUnloaderBuild::new;

        config(Liquid.class, (LiquidUnloaderBuild tile, Liquid l) -> tile.sortLiquid = l);
        configClear((LiquidUnloaderBuild tile) -> tile.sortLiquid = null);
    }

    @Override
    public void load() {
        super.load();
        centerRegion = name + "-center";
    }

    public void drawNearbyBuilding(int x, int y, @Nullable Liquid liquid){
        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};
        for(int i = 0; i < 4; i++){
            int tx = x + dx[i], ty = y + dy[i];
            var b = Vars.world.build(tx, ty);
            if(b == null)
                continue;
            if(b.block.liquidCapacity <= 0 || !b.block.hasLiquids)
                continue;
            int size = b.block.size * 4;
            if(liquid != null)
                Draw.color(liquid.color);
            else
                Draw.color(b.team.color);
            Lines.stroke(Math.min(4, 2 + size * 0.05f));
            if(b.block instanceof ArmoredConduit)
                Lines.stroke(Math.min(4, 2 + size * 0.05f) * 0.1f);
            Lines.quad(b.x - size, b.y + size, b.x + size, b.y + size, b.x + size, b.y - size, b.x - size, b.y - size);
            Draw.color();
        }
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list){
        drawPlanConfigCenter(plan, plan.config, centerRegion, true);
        drawNearbyBuilding(plan.x,plan.y,(Liquid)plan.config);
        //drawPlanConfigCenter(plan, plan.config, edgeRegion, true);
    }

    public class LiquidUnloaderBuild extends Building {
        public Liquid sortLiquid;

        @Override
        public void updateTile(){
            super.updateTile();
            if(sortLiquid == null) return;

            for(int i = 0, n = proximity.size; i < n; i++){
                for(int j = 0; j < i; j++){
                    Building b1 = proximity.get(i), b2 = proximity.get(j);
                    if(b1.liquids == null || b2.liquids == null)
                        continue;

                    float percentage = (b1.liquids.get(sortLiquid) + b2.liquids.get(sortLiquid)) / (b1.block.liquidCapacity + b2.block.liquidCapacity);
                    float percentage1 = b1.liquids.get(sortLiquid) / b1.block.liquidCapacity - percentage;
                    float percentage2 = b2.liquids.get(sortLiquid) / b2.block.liquidCapacity - percentage;
                    if(percentage1 > 0.0001f){
                        b1.transferLiquid(b2, b1.block.liquidCapacity * percentage1, sortLiquid);
                    }else if(percentage2 > 0.0001f){
                        b2.transferLiquid(b1, b2.block.liquidCapacity * percentage2, sortLiquid);
                    }
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.color(sortLiquid == null ? Color.clear : sortLiquid.color);
            Draw.rect(centerRegion, x, y);
            Draw.color();
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            drawItemSelection(sortLiquid);
            drawNearbyBuilding((int)this.x,(int)this.y,sortLiquid);
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(LiquidUnloader.this, table, content.liquids(), () -> sortLiquid, this::configure);
        }

        @Override
        public Liquid config() {
            return this.sortLiquid;
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.s(sortLiquid == null ? -1 : sortLiquid.id);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            int id = revision == 1 ? read.s() : read.b();
            sortLiquid = id == -1 ? null : content.liquid(id);
        }
    }
}
