package fire.world.blocks.production;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.BlockStatus;

import static mindustry.Vars.*;
import static mindustry.Vars.tilesize;

public class LaserDrillTower extends Block{

    protected byte tier;
    protected short range;
    protected float speed;
    protected float warmupSpeed;
    protected float boostScale;
    protected boolean mineHardnessScaling;
    protected Color baseColor = Color.clear;
    protected Color boostColor = Color.clear;
    protected Effect updateEffect = Fx.none;
    protected float updateEffectChance;
    protected Effect drillEffect = Fx.none;
    protected float drillEffectChance;

    public LaserDrillTower(String name){
        super(name);

        update = true;
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        buildType = LaserDrillTowerBuild::new;

        config(Item.class, (LaserDrillTowerBuild build, Item item) -> build.selected = item.id);
        configClear((LaserDrillTowerBuild build) -> build.selected = -1);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        float dx = x * tilesize + offset, dy = y * tilesize + offset;
        Drawf.dashCircle(dx, dy, range, baseColor);
        Drawf.dashCircle(dx, dy, closeDst(), baseColor);
        checkOre(x, y, true, null, null);
    }

    private void checkOre(int x, int y, boolean draw, @Nullable IntSet set, @Nullable Entry entry){
        if(set != null) set.clear();
        Tile closest = null;
        float mindst = 10000.0f;

        for(int tx = x - range; tx < x + range; tx++) for(int ty = y - range; ty < y + range; ty++){
            float dst = Mathf.dst(x, y, tx, ty);
            if(dst * tilesize > range || dst <= closeDst() / tilesize) continue;
            var tile = world.tile(tx, ty);
            if(tile == null || tile.block() != Blocks.air || tile.drop() == null || tile.drop().hardness > tier) continue;

            if(draw){
                Draw.color(Tmp.c1.set(baseColor).a(Mathf.absin(4.0f, 0.4f)));
                Fill.square(tx * tilesize, ty * tilesize , tilesize * 0.5f);
                Draw.reset();
            }

            if(set != null)
                set.add(tile.drop().id);

            if(entry != null)
                if(tile.drop().id == entry.item && mindst > dst){
                    closest = tile;
                    mindst = dst;
                }
        }

        if(closest != null) entry.cons.get(closest);
    }

    private float closeDst(){
        return size * tilesize * 2.4f;
    }

    public class LaserDrillTowerBuild extends Building{

        private int selected = -1;
        private float timer, warmup, boostWarmup, laserX, laserY;
        private Tile mining;
        private final IntSet available = new IntSet();

        @Override
        public void buildConfiguration(Table table){
            init();
            if(available.isEmpty()) return;

            var items = new Seq<Item>();
            available.each(id -> items.add(content.item(id)));
            ItemSelection.buildTable(block, table, items, this::config, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public void updateTile(){
            warmup = Mathf.lerpDelta(warmup, Mathf.num(selected != -1 && efficiency > 0.0f), warmupSpeed);
            boostWarmup = Mathf.lerpDelta(boostWarmup, optionalEfficiency, warmupSpeed);
            if(mining != null){
                laserX = Mathf.lerp(laserX, mining.worldx(), 0.5f * delta());
                laserY = Mathf.lerp(laserY, mining.worldy(), 0.5f * delta());
            }
            if(timer(timerDump, dumpTime))
                dump(config() != null && items.has(config()) ? config() : null);

            if(selected == -1) return;
            checkOre(tile.x, tile.y, false, null, new Entry(selected, ore -> {
                mining = ore;
                if(laserX == 0.0f && laserY == 0.0f){
                    laserX = mining.worldx();
                    laserY = mining.worldy();
                }
            }));
            if(mining == null || items.total() >= itemCapacity || efficiency <= 0.0f) return;

            timer += edelta() * speed * warmup * Mathf.lerp(1.0f, boostScale, optionalEfficiency);

            if(wasVisible && Mathf.chanceDelta(updateEffectChance * warmup))
                updateEffect.at(x + Mathf.range(size * 2.0f), y + Mathf.range(size * 2.0f));

            if(timer >= val() && items.total() < itemCapacity){
                byte amount = (byte)(timer / val());
                for(byte i = 0; i < amount; i++)
                    offload(mining.drop());

                if(wasVisible){
                    Fx.itemTransfer.at(mining.worldx(), mining.worldy(), 0.0f, mining.drop().color, this);
                    if(Mathf.chance(drillEffectChance))
                        drillEffect.at(x + Mathf.range(size * size), y + Mathf.range(size * size), mining.drop().color);
                }
                timer -= val();
            }

            if(timer(timerDump, dumpTime / timeScale))
                dump(mining.drop());
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float progress(){
            return Mathf.clamp(timer / val());
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0.0f && items.total() < itemCapacity;
        }

        @Override
        public float ambientVolume(){
            return efficiency * size * size * 0.25f;
        }

        @Override
        public Item config(){
            return content.item(selected);
        }

        @Override
        public BlockStatus status(){
            if(efficiency > 0.0f && selected == -1 || items.total() >= itemCapacity) return BlockStatus.noOutput;
            return super.status();
        }

        @Override
        public void draw(){
            super.draw();
            if(mining == null) return;

            final float swingScl = 12f, swingMag = 1f;

            float ex = laserX + Mathf.sin(Time.time, swingScl, swingMag);
            float ey = laserY + Mathf.sin(Time.time, swingScl + 2.0f, swingMag);

            Draw.z(Layer.blockAdditive);
            Draw.blend(Blending.additive);
            Draw.color(Tmp.c1.set(baseColor).lerp(boostColor, boostWarmup), warmup * (baseColor.a * (1f - 0.3f + Mathf.absin(7.0f, 0.3f))));
            Draw.alpha(warmup);
            Drawf.laser(Core.atlas.find("minelaser"), Core.atlas.find("minelaser-end"), x, y, ex, ey, 0.75f);
            Draw.blend();

            Lines.stroke(1.0f, Pal.accent);
            Draw.alpha(warmup);
            Lines.poly(laserX, laserY, 4, tilesize * 0.5f * Mathf.sqrt2, Time.time);

            Draw.color();
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, baseColor);
            Drawf.dashCircle(x, y, closeDst(), baseColor);
            checkOre(tile.x, tile.y, true, null, null);
        }

        @Override
        public void add(){
            super.add();
            init();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(selected);
            write.f(timer);
            write.f(warmup);
            write.f(boostWarmup);
            write.s(tile.x);
            write.s(tile.y);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            selected = read.s();
            timer = read.f();
            warmup = read.f();
            boostWarmup = read.f();
            tile = world.tile(read.s(), read.s());
        }

        private void init(){
            checkOre(tile.x, tile.y, false, available, null);
        }

        /** Algorithm comes from {@link mindustry.gen.UnitEntity#update() Unit's update}. Really confusing. */
        private float val(){
            return 50.0f + 15.0f * (mineHardnessScaling ? mining.drop().hardness : 1.0f);
        }
    }

    private static class Entry{

        public final int item;
        public final Cons<Tile> cons;

        public Entry(int item, Cons<Tile> cons){
            this.item = item;
            this.cons = cons;
        }
    }
}
