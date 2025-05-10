package fire.world.blocks.production;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.IntSet;
import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.*;

/** @see mindustry.world.blocks.production.Drill */
public class BeamExtractor extends mindustry.world.Block{

    protected byte tier;
    protected short range;
    protected short drillTime;
    protected byte hardnessDrillMultiplier;
    protected float warmupSpeed;
    protected float boostScale;
    protected final Color baseColor = new Color();
    protected final Color boostColor = new Color();
    protected Effect updateEffect = Fx.none;
    protected byte updateEffectChancePercentage; //a value of 50 -> 50%
    protected final Seq<Barrel> barrels = new Seq<>();

    private TextureRegion base = new TextureRegion();
    private TextureRegion[] barrelRegions;
    private TextureRegion[] heatRegions;

    public BeamExtractor(String name){
        super(name);
        update = true;
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        buildType = BeamExtractorBuild::new;

        config(Item.class, (BeamExtractorBuild build, Item item) -> build.selected = (byte)item.id);
        configClear((BeamExtractorBuild build) -> build.selected = -1);
    }

    @Override
    public void load(){
        super.load();
        var barrels = this.barrels;
        base = Core.atlas.find("block-" + size); //set() is unavailable when reading vanilla sprite?
        barrelRegions = new TextureRegion[barrels.size];
        heatRegions = new TextureRegion[barrels.size];

        for(byte i = 0, len = (byte)barrels.size; i < len; i++){
            var barrel = barrels.get(i);
            barrelRegions[i] = Core.atlas.find(name + barrel.name);
            heatRegions[i] = Core.atlas.find(name + barrel.name + "-heat");
        }
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("items");
        addBar("drillspeed", (BeamExtractorBuild e) -> new Bar(
            () -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.speed(), 2)),
            () -> Pal.ammo,
            () -> e.warmup)
        );
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.range, (float)range / tilesize, StatUnit.blocks);
        stats.add(Stat.drillTier, StatValues.drillables(drillTime, hardnessDrillMultiplier, 1.0f, new ObjectFloatMap<>(),
            b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null && f.itemDrop.hardness <= tier && (indexer.isBlockPresent(f) || state.isMenu()))
        );

        if(boostScale != 0.0f && findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase cons){
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(), cons.amount, boostScale, false, cons::consumes));
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(wp(x), wp(y), range, baseColor);
        Drawf.dashCircle(wp(x), wp(y), closeDst(), baseColor);
        checkOre(x, y, true, null, null);
    }

    @Override
    protected TextureRegion[] icons(){
        var regions = Seq.with(base, region);
        var barrelRegions = this.barrelRegions;

        if(barrelRegions.length > 1){
            var names = new Seq<String>(barrelRegions.length);

            for(var region : barrelRegions){
                if(names.contains(region.asAtlas().name)) continue;
                regions.add(region);
                names.add(region.asAtlas().name);
            }
        }

        return regions.toArray(TextureRegion.class);
    }

    /** Modified from super's one. */
    @Override
    public void createIcons(MultiPacker packer){
        var toDispose = new Seq<Pixmap>();

        var icons = icons();
        for(var icon : icons){
            var region = Pixmaps.outline(Core.atlas.getPixmap(icon), outlineColor, outlineRadius);
            toDispose.add(region);
            Drawf.checkBleed(region);
            packer.add(MultiPacker.PageType.main, icon.asAtlas().name, region);
        }

        var pixmap = Core.atlas.getPixmap(base).crop();
        toDispose.add(pixmap);
        pixmap.draw(Core.atlas.getPixmap(base), true);
        pixmap.draw(Core.atlas.getPixmap(region), true);

        packer.add(MultiPacker.PageType.main, "block-" + name + "-full", pixmap);
        packer.add(MultiPacker.PageType.editor, name + "-icon-editor", new PixmapRegion(pixmap));

        for(var pm : toDispose) pm.dispose();
    }

    private void checkOre(int x, int y, boolean draw, @Nullable IntSet set, @Nullable Entry entry){
        if(set != null) set.clear();
        Tile closest = null;
        float mr = Float.MAX_VALUE;

        short range = this.range;
        int mx = Mathf.ceil((wp(x) + range) / tilesize), my = Mathf.ceil((wp(y) + range) / tilesize);
        for    (short tx = (short)((wp(x) - range) / tilesize); tx <= mx; tx++)
            for(short ty = (short)((wp(y) - range) / tilesize); ty <= my; ty++){
                float dst = Mathf.dst(wp(x), wp(y), tx * tilesize, ty * tilesize);
                if(dst > range || dst <= closeDst()) continue;
                var tile = world.tile(tx, ty);
                if(tile == null || !tile.block().isAir() || tile.drop() == null || tile.drop().hardness > tier) continue;

                if(draw){
                    Draw.color(Tmp.c1.set(baseColor).lerp(tile.drop().color, 1.0f).a(Mathf.absin(4.0f, 0.4f)));
                    Fill.square(tx * tilesize, ty * tilesize , tilesize * 0.5f);
                    Draw.reset();
                }

                if(set != null)
                    set.add(tile.drop().id);

                if(entry != null)
                    if(tile.drop().id == entry.item && mr > dst){
                        closest = tile;
                        mr = dst;
                    }
        }

        if(entry != null) entry.cons.get(closest);
    }

    private float wp(int p){
        return p * tilesize + offset;
    }

    private float closeDst(){
        return size * tilesize * 2.4f;
    }

    /** A kind of mess. */
    public class BeamExtractorBuild extends mindustry.gen.Building{

        private byte selected = -1;
        private float drillTimer, warmup, boostWarmup, beamX, beamY;
        private Tile mining;
        private final IntSet available = new IntSet();
        private final float[] drawrots = new float[barrels.size];

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
            warmup = Mathf.lerpDelta(warmup, Mathf.num(valid()), warmupSpeed);
            boostWarmup = Mathf.lerpDelta(boostWarmup, optionalEfficiency, warmupSpeed);
            if(Mathf.equal(warmup, 1.0f, 0.001f)) warmup = 1.0f;
            if(Mathf.equal(boostWarmup, 1.0f, 0.001f)) boostWarmup = 1.0f;
            if(mining != null && valid()){
                beamX = Mathf.lerpDelta(beamX, mining.worldx(), 0.25f);
                beamY = Mathf.lerpDelta(beamY, mining.worldy(), 0.25f);
            }
            if(timer(timerDump, dumpTime))
                dump(items.first());

            if(selected == -1) return;
            if(mining == null || selected != mining.drop().id || blocked())
                checkOre(tile.x, tile.y, false, null, new Entry(selected, ore -> mining = ore));
            if(!valid()) return;

            for(byte i = 0, len = (byte)barrels.size; i < len; i++){
                var barrel = barrels.get(i);
                drawrots[i] = Angles.angle(x + barrel.x, y + barrel.y, beamX, beamY) - 90.0f;
            }
            drillTimer += edelta() * warmup * Mathf.lerp(1.0f, boostScale, boostWarmup);

            if(wasVisible){
                if(Mathf.chanceDelta(updateEffectChancePercentage * warmup * 0.01))
                    updateEffect.at(x + Mathf.range(size * 2.0f), y + Mathf.range(size * 2.0f));
            }

            if(drillTimer >= getDrillTime() && items.total() < itemCapacity){
                for(byte i = 0, amount = (byte)(drillTimer / getDrillTime()); i < amount; i++)
                    offload(mining.drop());

                if(wasVisible)
                    Fx.itemTransfer.at(mining.worldx(), mining.worldy(), 0.0f, mining.drop().color, this);

                drillTimer -= getDrillTime();
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
            return Mathf.clamp(drillTimer / getDrillTime());
        }

        @Override
        public float drawrot(){
            return drawrots[0];
        }

        @Override
        public boolean shouldAmbientSound(){
            return valid();
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
        public void configure(Object value){
            super.configure(value);
            if(!(value instanceof Item)) return;
            drillTimer *= 0.4f;
            warmup *= 0.6f;
        }

        @Override
        public BlockStatus status(){
            if(selected != -1 && efficiency <= 0.0f) return BlockStatus.noInput;
            else if(!valid()) return BlockStatus.noOutput;
            return super.status();
        }

        @Override
        public void draw(){
            final float swingScl = 12.0f, swingMag = 1.0f;
            Draw.rect(base, x, y);
            Draw.color();

            for(byte i = 0, len = (byte)barrels.size; i < len; i++){
                var barrel = barrels.get(i);
                float rot = drawrots[i], bx = x + barrel.x, by = y + barrel.y;

                Draw.z(Layer.turret - 1.0f);
                Drawf.shadow(barrelRegions[i], bx, by, rot);
                Draw.z(i == 0 ? Layer.turret : Layer.turret - 0.5f);
                Draw.rect(barrelRegions[i], bx, by, rot);
                if(heatRegions[i].found()) Drawf.additive(heatRegions[i], Tmp.c1.set(Pal.turretHeat).a(warmup), x, y, rot, Layer.turretHeat);

                if(warmup == 0.0f) continue;

                Draw.z(Layer.turretHeat + 0.5f);
                Draw.blend(Blending.additive);
                Draw.color(Tmp.c1.set(baseColor).lerp(boostColor, boostWarmup), warmup * barrel.laserOpacity * (baseColor.a * (0.5f + Mathf.absin(7.0f, 0.3f))));
                Drawf.laser(UnitTypes.mono.mineLaserRegion, UnitTypes.mono.mineLaserEndRegion,
                    bx + Angles.trnsx(rot, 0.0f, barrel.shootY), by + Angles.trnsy(rot, 0.0f, barrel.shootY),
                    beamX + Mathf.sin(Time.time, swingScl, swingMag), beamY + Mathf.sin(Time.time, swingScl + 2.0f, swingMag),
                    0.75f * barrel.laserStrokeScale
                );
                Draw.blend();
            }

            Lines.stroke(1.0f, Pal.accent);
            Draw.alpha(warmup);
            Lines.poly(beamX, beamY, 4, tilesize * 0.5f * Mathf.sqrt2, Time.time);

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
            beamX = x;
            beamY = y;
        }

        @Override
        public byte version(){
            return 0;
        }

        @Override
        public void write(Writes write){
            write.b(selected);
            write.f(drillTimer);
            write.f(warmup);
            write.f(boostWarmup);
            for(float v : drawrots) write.f(v);
        }

        @Override
        public void read(Reads read, byte revision){
            if(revision != version()) return;

            selected = read.b();
            drillTimer = read.f();
            warmup = read.f();
            boostWarmup = read.f();
            for(byte i = 0, len = (byte)barrels.size; i < len; i++) drawrots[i] = read.f();
        }

        private void init(){
            checkOre(tile.x, tile.y, false, available, null);
        }

        private boolean blocked(){
            return mining == null || !mining.block().isAir();
        }

        private boolean valid(){
            return selected != -1 && efficiency > 0.0f && items.total() < itemCapacity && !blocked();
        }

        private float speed(){
            return 60.0f * warmup * Mathf.lerp(1.0f, boostScale, boostWarmup) * efficiency * timeScale / getDrillTime();
        }

        private float getDrillTime(){
            if(selected == -1) return Float.POSITIVE_INFINITY;
            return drillTime + hardnessDrillMultiplier * config().hardness;
        }
    }

    public static class Barrel{
        private final String name;
        private final float x, y, shootY, laserOpacity, laserStrokeScale;
        public Barrel(String name, float x, float y, float shootY, float laserOpacity, float laserStrokeScale){
            this.name = name;
            this.x = x;
            this.y = y;
            this.shootY = shootY;
            this.laserOpacity = laserOpacity;
            this.laserStrokeScale = laserStrokeScale;
        }
    }

    private static class Entry{
        private final byte item;
        private final Cons<Tile> cons;
        private Entry(byte item, Cons<Tile> cons){
            this.item = item;
            this.cons = cons;
        }
    }
}
