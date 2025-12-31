package fire.world.blocks.units;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.scene.utils.Elem;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import fire.FRUtils;
import fire.world.meta.FRStat;
import mindustry.ai.UnitCommand;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static fire.FRVars.equivalentWidth;
import static fire.content.FRItems.*;
import static fire.content.FRUnitTypes.*;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.*;
import static mindustry.content.Items.sand;
import static mindustry.content.UnitTypes.arkyid;

/** @see mindustry.world.blocks.units.UnitFactory */
public class ElementUnitFactory extends mindustry.world.blocks.units.UnitBlock{

    protected final byte tier;
    protected float base;
    protected float timeScl;
    protected final Seq<UnitType> plans = new Seq<>();
    private boolean pioneered;

    public static final Block[] factories = {additiveReconstructor, multiplicativeReconstructor, exponentialReconstructor, tetrativeReconstructor};
    private static final ObjectMap<Item, ItemValue> itemValues = new ObjectMap<>(content.items().size - 6); //6 Erekir items
    private static final ObjectMap<UnitType, UnitValue> unitValues = new ObjectMap<>();

    static{
        var factories = ElementUnitFactory.factories;
        var unitValues = ElementUnitFactory.unitValues;
        var rand = Mathf.rand;

        putAllValues(
            copper,           0.08f, 1.5f,  0.1f,  0.9f, 0.0f,  0.0f,
            lead,             0.06f, 1.55f,  0.15f, 1.0f, 0.05f, 0.6f,
            metaglass,        0.06f, 3.0f,  0.15f, 2.0f, 0.08f, 2.5f,
            graphite,         0.0f,  0.0f,  0.1f,  3.0f, 0.4f,  1.5f,
            scrap,            0.01f, 0.8f,  0.0f,  0.0f, 0.0f,  0.0f,
            coal,             0.0f,  0.0f,  0.3f,  2.0f, 0.0f,  0.0f,
            titanium,         0.25f,  3.2f,  0.3f,  2.6f, 0.1f,  1.5f,
            thorium,          0.45f, 3.75f, 0.55f,  3.1f, 0.0f,  0.0f,
            silicon,          0.0f,  0.0f,  0.3f,  2.5f, 0.9f,  5.0f,
            plastanium,       0.85f,  4.85f, 0.75f,  4.55f, 0.0f, 0.0f,
            phaseFabric,      1.25f,  5.3f,  0.85f,  2.4f, 0.85f,  5.4f,
            surgeAlloy,       1.25f,  5.5f,  1.35f,  5.95f, 0.0f, 0.0f,
            sporePod,         0.0f,  0.0f,  0.4f,  2.25f, 0.0f, 0.0f,
            sand,             0.01f, 0.5f,  0.0f,  0.0f, 0.0f,  0.0f,
            blastCompound,    0.0f,  0.0f,  1.2f,  5.3f, 0.0f,  0.0f,
            pyratite,         0.0f,  0.0f,  0.8f,  4.2f, 0.0f,  0.0f,

            glass,            0.01f, 0.3f,  0.05f, 1.2f, 0.1f,  2.5f,
            mirrorglass,      0.85f,  4.0f,  0.95f,  3.5f, 0.1f,  2.5f,
            sulflameAlloy,    0.0f,  0.0f,  1.30f, 5.5f, 0.0f,  0.0f,
            kindlingAlloy,    0.0f,  0.0f,  1.25f,  5.6f, 0.0f,  0.0f,
            conductor,        0.0f,  0.0f,  0.75f,  3.85f, 0.35f, 3.4f,
            detonationCompound,0.05f, 1.3f,  1.35f, 6.25f, 0.3f, 2.9f,
            flamefluidCrystal, 0.0f, 0.0f,  0.55f, 4.95f, 0.0f,  0.0f,
            timber,           0.05f, 0.95f, 0.25f, 1.55f, 0.0f, 0.0f,
            flesh,            1.55f, 6.1f,  0.05f, 1.2f, 2.15f,  6.3f,
            hardenedAlloy,    2.0f,  6.3f,  1.25f,  5.8f, 0.0f,  0.0f,
            magneticAlloy,    2.2f,  6.3f,  14.0f, 6.4f, 0.75f,  5.7f,
            logicAlloy,       0.4f,  3.5f,  0.3f,  3.1f, 1.25f,  5.2f
        );

        for(int i = 0, n = factories.length; i < n; i++)
            for(var upgrade : ((Reconstructor)factories[i]).upgrades)
                for(int j = 0; j < 2; j++){
                    if(i != 0 && j == 0) continue; //for T3 and above, j must be 1

                    var unit = upgrade[j];
                    float u, v, w;
                         if(unit==pioneer)  {u=3.8f; v=4.4f ;w=4.6f;}
                    else if(unit==firefly)  {u=1.2f; v=1.4f ;w=0.5f;}
                    else if(unit==candlight){u=2.3f; v=2.5f ;w=1.8f;}
                    else if(unit==lampryo)  {u=3.4f; v=3.85f;w=3.2f;}
                    else if(unit==lumiflame){u=4.7f; v=4.9f ;w=4.45f;}
                    else if(unit==radiance) {u=5.6f; v=5.9f ;w=5.9f;}
                    else if(unit==guarding) {u=1.2f; v=0.5f ;w=1.3f;}
                    else if(unit==resisting){u=2.6f; v=2.8f ;w=2.9f;}
                    else if(unit==garrison) {u=3.3f; v=3.4f ;w=2.95f;}
                    else if(unit==shelter)  {u=4.5f; v=4.9f;w=4.75f;}
                    else if(unit==blessing) {u=5.9f;v=5.9f;w=5.9f;}
                    else{
                        rand.setSeed(unit.id * 1000L);
                        u = Mathf.round(i + j + 0.6f + rand.random(0.0f, 0.4f), 0.05f);
                        v = Mathf.round(i + j + 0.6f + rand.random(0.0f, 0.4f), 0.05f);
                        w = Mathf.round(i + j + 0.6f + rand.random(0.0f, 0.4f), 0.05f);
                        if(unit.naval) u += 0.5f;
                        if(unit.flying || unit.speed >= arkyid.speed) v += 0.5f;
                        if(unit.buildSpeed > 0.0f || unit.mineTier > 0) w += 0.5f;
                    }
                    unitValues.put(unit, new UnitValue(u, v, w));
                }

        //omicron is removed from T3 recipe so put it here
        unitValues.put(omicron, new UnitValue(2.5f, 3.9f, 3.2f));
    }

    private static void putAllValues(Object... values){
        var itemValues = ElementUnitFactory.itemValues;
        for(int i = 0; i < values.length; i += 7)
            itemValues.put((Item)values[i], new ItemValue((float)values[i + 1], (float)values[i + 2], (float)values[i + 3], (float)values[i + 4], (float)values[i + 5], (float)values[i + 6]));
    }

    public ElementUnitFactory(String name, int t){
        super(name);
        tier = (byte)t;
        hasPower = true;
        configurable = true;
        clearOnDoubleTap = true;
        itemCapacity = 0;
        regionRotated1 = 1;
        commandable = true;
        ambientSound = Sounds.loopUnitBuilding;
        buildType = ElementUnitFactoryBuild::new;

        var plans = this.plans;
        var factories = ElementUnitFactory.factories;

        config(Byte.class, (ElementUnitFactoryBuild build, Byte i) -> {
            if(build.currentPlan == i) return;
            build.currentPlan = i < 0 || i >= plans.size ? -1 : i;
            build.progress = 0.0f;
            if(build.command != null && (build.unit() == null || !build.unit().commands.contains(build.command)))
                build.command = null;
        });

        config(UnitType.class, (ElementUnitFactoryBuild build, UnitType val) -> {
            byte next = (byte)plans.indexOf(p -> p == val);
            if(build.currentPlan == next) return;
            build.currentPlan = next;
            build.progress = 0.0f;
            if(build.command != null && !val.commands.contains(build.command))
                build.command = null;
        });

        config(UnitCommand.class, (ElementUnitFactoryBuild build, UnitCommand command) -> build.command = command);

        configClear((ElementUnitFactoryBuild build) -> {
            build.currentPlan = -1;
            build.command = null;
        });

        for(int i = 0, n = t != 0 ? t - 1 : factories.length; i < n; i++)
            for(var upgrade : ((Reconstructor)factories[i]).upgrades)
                for(int j = 0; j < 2; j++){
                    if(i != 0 && j == 0) continue; //for T3 and above, j must be 1

                    var unit = upgrade[j];
                    if(unit == blade || unit == hatchet || unit == castle) continue;
                    if(unit == pioneer && !pioneered){
                        unit = omicron;
                        pioneered = true;
                    }
                    plans.add(unit);
                }
    }

    @Override
    public void init(){
        super.init();
        plans.sort(u -> u.id);
    }

    @Override
    public void setStats(){
        super.setStats();
        var itemValues = ElementUnitFactory.itemValues;
        var unitValues = ElementUnitFactory.unitValues;
        var plans = this.plans;
        stats.add(FRStat.elementLevel, tier);

        stats.add(Stat.input, table -> {
            table.row().table(Styles.grayPanel, t -> t.left().add("@stat.consumeelement").growX().pad(8.0f));

            var itemTable = table.row().table(Styles.grayPanel, t -> t.defaults().minWidth(32.0f).maxWidth(32.0f).pad(8.0f).row()).left().get();

            int i = 0;
            for(var item : content.items()){
                if(!item.unlockedNowHost()) continue;
                var value = itemValues.get(item);
                if(value == null) continue;

                itemTable.button(new TextureRegionDrawable(item.fullIcon), Styles.emptyi, 32.0f, () -> ui.content.show(item)).size(32.0f).pad(8.0f).scaling(Scaling.fit)
                    .tooltip(Core.bundle.format("tooltip.element", value.armorXp, value.energyXp, value.logicXp, value.armorMaxLv, value.energyMaxLv, value.logicMaxLv));

                if(++i % 10 == 0) itemTable.row();
            }

            table.row();
        });

        stats.add(Stat.output, table -> {
            int r = equivalentWidth > 2560.0f ? 8 : 4;

            table.row();
            for(int i = 0, n = plans.size; i < n;){
                var unit = plans.get(i);
                var value = unitValues.get(unit);
                boolean banned = unit.isBanned(), unlocked = unit.unlockedNowHost();

                table.table(Styles.grayPanel, t -> {
                    t.left().top().defaults().padRight(3.0f).left();

                    ImageButton bt;
                    t.stack(
                        bt = Elem.newImageButton(Styles.clearNonei, unlocked ? new TextureRegionDrawable(unit.fullIcon) : Icon.lock, 60.0f, !banned && unlocked ? () -> ui.content.show(unit) : null),
                        new Table(e -> e.image(Icon.cancel).color(Pal.remove).size(60.0f).visible(() -> banned))
                    ).size(60.0f).scaling(Scaling.bounded).left().top();
                    if(!unlocked) bt.getImageCell().tooltip("@tooltip.locked");

                    t.row();
                    t.add("[accent]" + (unlocked ? unit.localizedName : "???") + "[], [lightgray]" + (unlocked ? time(value) / 60 : "?") + " " + StatUnit.seconds.localized()).left();
                    t.row();
                    t.add(Core.bundle.format("stat.armorlv", unlocked ? Strings.fixed(value.armorLv, 2) : "???")).left();
                    t.row();
                    t.add(Core.bundle.format("stat.energylv", unlocked ? Strings.fixed(value.energyLv, 2) : "???")).left();
                    t.row();
                    t.add(Core.bundle.format("stat.logiclv", unlocked ? Strings.fixed(value.logicLv, 2) : "???")).left();
                }).growX().pad(5.0f).margin(10.0f);
                if(++i % r == 0) table.row();
            }
        });
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("progress", (ElementUnitFactoryBuild e) -> new Bar("bar.progress", Pal.ammo, e::fraction));

        addBar("units", (ElementUnitFactoryBuild e) -> new Bar(
            () -> e.unit() == null ? "[lightgray]" + Iconc.cancel :
                Core.bundle.format("bar.unitcap",
                    Fonts.getUnicodeStr(e.unit().name),
                    e.team.data().countType(e.unit()),
                    e.unit() == null ? Units.getStringCap(e.team) : (e.unit().useUnitCap ? Units.getStringCap(e.team) : "∞")
                ),
            () -> Pal.power,
            () -> e.unit() == null ? 0.0f : (e.unit().useUnitCap ? (float)e.team.data().countType(e.unit()) / Units.getCap(e.team) : 1.0f)
        ));

        addBar("armor", (ElementUnitFactoryBuild b) -> new Bar(
            () -> Core.bundle.format("bar.armorlv", Strings.fixed(b.armorXpToLv(), 1) + (b.currentPlan == -1 ? "" : " / " + Strings.fixed(unitValues.get(b.unit()).armorLv, 1))),
            () -> Pal.powerBar,
            () -> b.currentPlan == -1 ? 0.0f : b.armorXpToLv() / unitValues.get(b.unit()).armorLv));
        addBar("energy", (ElementUnitFactoryBuild b) -> new Bar(
            () -> Core.bundle.format("bar.energylv", Strings.fixed(b.energyXpToLv(), 1) + (b.currentPlan == -1 ? "" : " / " + Strings.fixed(unitValues.get(b.unit()).energyLv, 1))),
            () -> Pal.reactorPurple,
            () -> b.currentPlan == -1 ? 0.0f : b.energyXpToLv() / unitValues.get(b.unit()).energyLv));
        addBar("logic", (ElementUnitFactoryBuild b) -> new Bar(
            () -> Core.bundle.format("bar.logiclv", Strings.fixed(b.logicXpToLv(), 1) + (b.currentPlan == -1 ? "" : " / " + Strings.fixed(unitValues.get(b.unit()).logicLv, 1))),
            () -> Pal.logicControl,
            () -> b.currentPlan == -1 ? 0.0f : b.logicXpToLv() / unitValues.get(b.unit()).logicLv)
        );
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, outRegion, topRegion};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90.0f);
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
    }

    @Override
    public void getPlanConfigs(Seq<UnlockableContent> options){
        for(var plan : plans)
            if(!plan.isBanned())
                options.add(plan);
    }

    private int time(UnitValue value){
        return FRUtils.round((Mathf.sqr(value.armorLv) + Mathf.sqr(value.energyLv) + Mathf.sqr(value.logicLv)) * timeScl, 300);
    }

    private float lvToXp(float lv){
        return Mathf.pow(base, lv) - 1.0f;
    }

    public class ElementUnitFactoryBuild extends UnitBuild{

        private @Nullable Vec2 commandPos;
        private @Nullable UnitCommand command;
        private byte currentPlan = -1;
        private float armorXp, energyXp, logicXp;

        private float armorXpToLv(){
            return Mathf.log(base, armorXp + 1.0f);
        }

        private float energyXpToLv(){
            return Mathf.log(base, energyXp + 1.0f);
        }

        private float logicXpToLv(){
            return Mathf.log(base, logicXp + 1.0f);
        }

        public float fraction(){
            if(currentPlan == -1) return 0.0f;
            return progress / time(unitValues.get(plans.get(currentPlan)));
        }

        public boolean canSetCommand(){
            var output = unit();
            return output != null && output.commands.size > 1 && output.allowChangeCommands &&
                !(output.commands.size == 2 && output.commands.get(1) == UnitCommand.enterPayloadCommand);
        }

        @Override
        public void created(){
            if(currentPlan == -1)
                currentPlan = (byte)plans.indexOf(UnlockableContent::unlockedNow);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            if(plans.size > 1 && currentPlan != -1 && currentPlan < plans.size)
                drawItemSelection(plans.get(currentPlan));
        }

        @Override
        public Vec2 getCommandPosition(){
            return commandPos;
        }

        @Override
        public void onCommand(Vec2 target){
            commandPos = target;
        }

        @Override
        public Object senseObject(LAccess sensor){
            if(sensor == LAccess.config) return unit();
            return super.senseObject(sensor);
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress) return Mathf.clamp(fraction());
            return super.sense(sensor);
        }

        @Override
        public void buildConfiguration(Table table){
            var units = Seq.with(plans).map(u -> u).retainAll(u -> u.unlockedNow() && !u.isBanned());
            if(units.any()){
                ItemSelection.buildTable(ElementUnitFactory.this, table, units, this::unit, unit -> configure((byte)plans.indexOf(u -> u == unit)), selectionRows, selectionColumns);

                table.row();

                var commands = new Table();
                commands.top().left();
                commands.clear();
                commands.background(null);

                var unit = unit();
                if(unit != null && canSetCommand()){
                    commands.background(Styles.black6);
                    var group = new ButtonGroup<ImageButton>();
                    group.setMinCheckCount(0);
                    int i = 0, columns = Mathf.clamp(units.size, 2, selectionColumns);
                    var list = unit.commands;

                    commands.image(Tex.whiteui, Pal.gray).height(4.0f).growX().colspan(columns).row();

                    for(var item : list){
                        var button = commands.button(item.getIcon(), Styles.clearNoneTogglei, 40.0f, () -> configure(item)).tooltip(item.localized()).group(group).get();
                        button.update(() -> button.setChecked(command == item || (command == null && unit.defaultCommand == item)));

                        if(++i % columns == 0) commands.row();
                    }

                    if(list.size < columns)
                        for(int j = 0, n = columns - list.size; j < n; j++)
                            commands.add().size(40.0f);
                }

                table.row().add(commands).fillX().left();

            }else{
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return false;
        }

        @Override
        public void display(Table table){
            super.display(table);
            var region = new TextureRegionDrawable();
            table.row();
            table.table(t -> {
                t.left().image().update(i -> {
                    i.setDrawable(currentPlan == -1 ? Icon.cancel : region.set(plans.get(currentPlan).uiIcon));
                    i.setScaling(Scaling.fit);
                    i.setColor(currentPlan == -1 ? Color.lightGray : Color.white);
                }).size(32.0f).padBottom(-4.0f).padRight(2.0f);
                t.label(() -> currentPlan == -1 ? "@none" : plans.get(currentPlan).localizedName).wrap().width(230.0f).color(Color.lightGray);
            }).left();
        }

        @Override
        public Object config(){
            return currentPlan;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            if(currentPlan != -1){
                var plan = plans.get(currentPlan);
                Draw.draw(Layer.blockOver, () -> Drawf.construct(this, plan, rotdeg() - 90.0f, progress / time(unitValues.get(plan)), speedScl, time));
            }

            Draw.z(Layer.blockOver);

            payRotation = rotdeg();
            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion, x, y);
        }

        @Override
        public void updateTile(){
            if(currentPlan < 0 || currentPlan >= plans.size)
                currentPlan = -1;

            moveOutPayload();

            if(currentPlan != -1 && payload == null){
                var plan = plans.get(currentPlan);
                var value = unitValues.get(plan);

                if(currentPlan != -1 && armorXp >= lvToXp(value.armorLv) && energyXp >= lvToXp(value.energyLv) && logicXp >= lvToXp(value.logicLv)){
                    time += edelta() * speedScl * state.rules.unitBuildSpeed(team);
                    progress += edelta() * state.rules.unitBuildSpeed(team);
                    speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);

                }else{
                    speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
                }

                if(plan.isBanned()){
                    currentPlan = -1;
                    return;
                }

                float time = time(value);
                if(progress >= time){
                    progress -= 1.0f;

                    var unit = plan.create(team);
                    if(unit.isCommandable()){
                        if(commandPos != null) unit.command().commandPosition(commandPos);
                        unit.command().command(command == null && unit.type.defaultCommand != null ? unit.type.defaultCommand : command);
                    }

                    payload = new UnitPayload(unit);
                    payVector.setZero();

                    armorXp -= lvToXp(value.armorLv);
                    energyXp -= lvToXp(value.energyLv);
                    logicXp -= lvToXp(value.logicLv);

                    Events.fire(new EventType.UnitCreateEvent(payload.unit, this));
                }

                progress = Mathf.clamp(progress, 0.0f, time);

            }else{
                progress = 0.0f;
            }
        }

        @Override
        public boolean shouldConsume(){
            if(currentPlan == -1) return false;
            return enabled && payload == null;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            var value = itemValues.get(item);
            float v = lvToXp(tier != 0 ? tier + 0.9f : Float.MAX_VALUE);
            return value != null && (armorXp < Math.min(lvToXp(value.armorMaxLv), v) || energyXp < Math.min(lvToXp(value.energyMaxLv), v) || logicXp < Math.min(lvToXp(value.logicMaxLv), v));
        }

        @Override
        public void handleItem(Building source, Item item){
            var value = itemValues.get(item);
            float v = lvToXp(tier != 0 ? tier + 0.9f : Float.MAX_VALUE);
            armorXp = Mathf.clamp(armorXp + value.armorXp, armorXp, Math.min(lvToXp(value.armorMaxLv), v));
            energyXp = Mathf.clamp(energyXp + value.energyXp, energyXp, Math.min(lvToXp(value.energyMaxLv), v));
            logicXp = Mathf.clamp(logicXp + value.logicXp, logicXp, Math.min(lvToXp(value.logicMaxLv), v));
        }

        public @Nullable UnitType unit(){
            return currentPlan == -1 ? null : plans.get(currentPlan);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.b(currentPlan);
            TypeIO.writeVecNullable(write, commandPos);
            TypeIO.writeCommand(write, command);
            write.f(armorXp);
            write.f(energyXp);
            write.f(logicXp);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            currentPlan = read.b();
            commandPos = TypeIO.readVecNullable(read);
            command = TypeIO.readCommand(read);
            armorXp = read.f();
            energyXp = read.f();
            logicXp = read.f();
        }
    }

    private static class ItemValue{
        private final float armorXp, armorMaxLv, energyXp, energyMaxLv, logicXp, logicMaxLv;
        private ItemValue(float armorXp, float armorMaxLv, float energyXp, float energyMaxLv, float logicXp, float logicMaxLv){
            this.armorXp = armorXp;
            this.armorMaxLv = armorMaxLv;
            this.energyXp = energyXp;
            this.energyMaxLv = energyMaxLv;
            this.logicXp = logicXp;
            this.logicMaxLv = logicMaxLv;
        }
    }

    private static class UnitValue{
        private final float armorLv, energyLv, logicLv;
        private UnitValue(float armorLv, float energyLv, float logicLv){
            this.armorLv = armorLv;
            this.energyLv = energyLv;
            this.logicLv = logicLv;
        }
    }
}
