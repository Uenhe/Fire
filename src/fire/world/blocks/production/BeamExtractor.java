package fire.world.blocks.production;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
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
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
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
import static mindustry.Vars.tilesize;

/** @see mindustry.world.blocks.production.Drill Drill */
public class BeamExtractor extends mindustry.world.Block{

    protected byte tier;
    protected short range;
    protected short drillTime;
    protected byte hardnessDrillMultiplier;
    protected float warmupSpeed;
    protected float boostScale;
    protected byte shootY;
    protected Color baseColor = Color.clear;
    protected Color boostColor = Color.clear;
    protected Effect updateEffect = Fx.none;
    protected float updateEffectChance;
    protected Effect drillEffect = Fx.none;
    protected float drillEffectChance;

    private final int beamEffectTimer = timers++;
    private TextureRegion base, heat;

    public BeamExtractor(String name){
        super(name);
        update = true;
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        outlineIcon = true;
        outlinedIcon = 1;
        buildType = BeamExtractorBuild::new;

        config(Item.class, (BeamExtractorBuild build, Item item) -> build.selected = (byte)item.id);
        configClear((BeamExtractorBuild build) -> build.selected = -1);
    }

    @Override
    public void load(){
        base = Core.atlas.find("block-" + size);
        heat = Core.atlas.find(name + "-heat");
        super.load();
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
        stats.add(Stat.range, (float) range / tilesize, StatUnit.blocks);
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
        float wx = x * tilesize + offset, wy = y * tilesize + offset;
        Drawf.dashCircle(wx, wy, range, baseColor);
        Drawf.dashCircle(wx, wy, closeDst(), baseColor);
        checkOre(x, y, true, null, null);
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out){
        if(!(outlinedIcon > 0 && outlinedIcon < getGeneratedIcons().length && getGeneratedIcons()[outlinedIcon].equals(region)))
            out.add(region);

        resetGeneratedIcons();
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{base, region};
    }

    private void checkOre(int x, int y, boolean draw, @Nullable IntSet set, @Nullable Entry entry){
        if(set != null) set.clear();
        Tile closest = null;
        float mr = range + 1.0f;

        float wx = x * tilesize + offset, wy = y * tilesize + offset;
        for    (short tx = (short)((wx - range) / tilesize); tx <= Mathf.ceil((wx + range) / tilesize); tx++)
            for(short ty = (short)((wy - range) / tilesize); ty <= Mathf.ceil((wy + range) / tilesize); ty++){
                float dst = Mathf.dst(wx, wy, tx * tilesize, ty * tilesize);
                if(dst > range || dst <= closeDst()) continue;
                var tile = world.tile(tx, ty);
                if(tile == null || tile.block() != Blocks.air || tile.drop() == null || tile.drop().hardness > tier) continue;

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

    private float closeDst(){
        return size * tilesize * 2.4f;
    }

    public class BeamExtractorBuild extends mindustry.gen.Building{

        private byte selected = -1;
        private float drillTimer, warmup, boostWarmup, beamX, beamY, drawrot;
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
            warmup = Mathf.lerpDelta(warmup, Mathf.num(valid()), warmupSpeed);
            boostWarmup = Mathf.lerpDelta(boostWarmup, optionalEfficiency, warmupSpeed);
            if(warmup >= 0.999f) warmup = 1.0f;
            if(boostWarmup >= 0.999f) boostWarmup = 1.0f;
            if(mining != null && valid()){
                beamX = Mathf.lerpDelta(beamX, mining.worldx(), 0.25f);
                beamY = Mathf.lerpDelta(beamY, mining.worldy(), 0.25f);
            }
            if(timer(timerDump, dumpTime))
                dump(items.first());

            if(selected == -1) return;
            checkOre(tile.x, tile.y, false, null, new Entry(selected, ore -> mining = ore));
            if(blocked() || items.total() >= itemCapacity || efficiency <= 0.0f) return;

            drawrot = Angles.angle(x, y, beamX, beamY) - 90.0f;
            drillTimer += edelta() * warmup * Mathf.lerp(1.0f, boostScale, boostWarmup);

            if(wasVisible){
                if(timer(beamEffectTimer, 5.0f))
                    Fx.missileTrailShort.at(beamX, beamY);
                if(Mathf.chanceDelta(updateEffectChance * warmup))
                    updateEffect.at(x + Mathf.range(size * 2.0f), y + Mathf.range(size * 2.0f));
            }

            if(drillTimer >= getDrillTime() && items.total() < itemCapacity){
                byte amount = (byte)(drillTimer / getDrillTime());
                for(byte i = 0; i < amount; i++)
                    offload(mining.drop());

                if(wasVisible){
                    Fx.itemTransfer.at(mining.worldx(), mining.worldy(), 0.0f, mining.drop().color, this);
                    if(Mathf.chance(drillEffectChance))
                        drillEffect.at(x + Mathf.range(size * size), y + Mathf.range(size * size), mining.drop().color);
                }

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
            return drawrot;
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
            else if(!valid() || blocked()) return BlockStatus.noOutput;
            return super.status();
        }

        @Override
        public void draw(){
            Draw.rect(base, x, y);
            Draw.color();
            Draw.z(Layer.turret - 0.5f);
            Drawf.shadow(region, x, y, drawrot);
            Draw.z(Layer.turret);
            Draw.rect(region, x, y, drawrot);
            Drawf.additive(heat, Tmp.c1.set(Pal.turretHeat).a(warmup), x, y, drawrot, Layer.turretHeat);

            if(warmup == 0.0f || blocked()) return;

            final float swingScl = 12.0f, swingMag = 1.0f,
                ex = beamX + Mathf.sin(Time.time, swingScl, swingMag),
                ey = beamY + Mathf.sin(Time.time, swingScl + 2.0f, swingMag);

            Draw.z(Layer.turretHeat + 0.5f);
            Draw.blend(Blending.additive);
            Draw.color(Tmp.c1.set(baseColor).lerp(boostColor, boostWarmup), warmup * (baseColor.a * (0.5f + Mathf.absin(7.0f, 0.3f))));
            Drawf.laser(UnitTypes.mono.mineLaserRegion, UnitTypes.mono.mineLaserEndRegion, x + Angles.trnsx(drawrot, 0.0f, shootY), y + Angles.trnsy(drawrot, 0.0f, shootY), ex, ey, 0.75f);
            Draw.blend();

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
        public void write(Writes write){
            super.write(write);
            write.b(selected);
            write.f(drillTimer);
            write.f(warmup);
            write.f(boostWarmup);
            write.f(drawrot);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            selected = read.b();
            drillTimer = read.f();
            warmup = read.f();
            boostWarmup = read.f();
            drawrot = read.f();
        }

        private void init(){
            checkOre(tile.x, tile.y, false, available, null);
        }

        private boolean blocked(){
            return mining == null || mining.block() != Blocks.air;
        }

        private boolean valid(){
            return selected != -1 && efficiency > 0.0f && items.total() < itemCapacity;
        }

        private float speed(){
            return 60.0f * warmup * Mathf.lerp(1.0f, boostScale, boostWarmup) * efficiency * timeScale / getDrillTime();
        }

        private float getDrillTime(){
            if(selected == -1) return Float.POSITIVE_INFINITY;
            return drillTime + hardnessDrillMultiplier * config().hardness;
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
