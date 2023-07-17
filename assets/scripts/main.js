//special thanks to Creators mod
function getClz(name){
    var p = Packages.rhino.NativeJavaPackage(name, Vars.mods.mainLoader());
    Packages.rhino.ScriptRuntime.setObjectProtoAndParent(p, Vars.mods.scripts.scope);
    return p
};
var fire = getClz("fire");
importPackage(fire);
importPackage(fire.entities.abilities);
importPackage(fire.type);
importPackage(fire.world.blocks.defense);
importPackage(fire.world.blocks.distribution);
importPackage(fire.world.blocks.power);
importPackage(fire.world.blocks.production);
importPackage(fire.world.blocks.sandbox);
importPackage(fire.world.blocks.storage);
importPackage(fire.world.blocks.units);
importPackage(fire.world.meta);

const
    blocks = require("blocks"),
    liquids = require("liquids"),
    statues = require("statues"),
    units = require("units"),
    lib = require("misc/lib"),
    {tree, grass} = require("misc/meta");



/*========Block Environment Region========*/
Blocks.sandWater.itemDrop = Items.sand;
Blocks.darksandWater.itemDrop = Items.sand;
Blocks.darksandTaintedWater.itemDrop = Items.sand;
Blocks.sporePine.attributes.set(tree, 1.5);
Blocks.snowPine.attributes.set(tree, 1.5);
Blocks.pine.attributes.set(tree, 1.5);
Blocks.whiteTreeDead.attributes.set(tree, 1);
Blocks.whiteTree.attributes.set(tree, 1);
Blocks.grass.attributes.set(grass, 0.25);



/*========Block Turret Region========*/
Blocks.wave.liquidCapacity = 10 + 10;
Blocks.tsunami.liquidCapacity = 40 + 20;
Blocks.wave.ammoTypes.put(liquids.liquidNitrogen, Object.assign(new LiquidBulletType(liquids.liquidNitrogen), {
    knockback: 0.7,
    drag: 0.001,
    damage: 4.55
}));
Blocks.tsunami.ammoTypes.put(liquids.liquidNitrogen, Object.assign(new LiquidBulletType(liquids.liquidNitrogen), {
    lifetime: 49,
    speed: 4,
    knockback: 1.3,
    puddleSize: 8,
    orbSize: 4,
    drag: 0.001,
    ammoMultiplier: 0.4,
    statusDuration: 240,
    damage: 6.25
}));



/*========Block Production Region========*/
Blocks.laserDrill.drillTime = 280 - 10;
Blocks.laserDrill.hardnessDrillMultiplier = 50 - 5;
Blocks.blastDrill.drillTime = 280 - 25;
Blocks.blastDrill.hardnessDrillMultiplier = 50 - 5;



/*========Block Distribution Region========*/
var buildType = Blocks.distributor.buildType.get().class;
Blocks.phaseConveyor.itemCapacity = 10 + 5;
Blocks.phaseConveyor.transportTime = 2 - 1;
Blocks.distributor.buildType = () => extend(buildType, Blocks.distributor, {
    canControl(){
        return true
    }
});
Blocks.massDriver.rotateSpeed = 5 + 5;
Blocks.massDriver.bulletSpeed = 5.5 + 9.5;



/*========Block Liquid Region========*/
Blocks.mechanicalPump.pumpAmount = 7 / 60 + 0.2 / 60;
Blocks.impulsePump.pumpAmount = 0.22 + 1.2 / 9 / 60;
Blocks.phaseConduit.liquidCapacity = 10 + 14;



/*========Block Units Region========*/
Blocks.groundFactory.plans.add(
    new UnitFactory.UnitPlan(units.guarding, 1500, ItemStack.with(
        Items.lead, 20,
        Items.titanium, 25,
        Items.silicon, 30
    ))
);
Blocks.airFactory.plans.add(
    new UnitFactory.UnitPlan(UnitTypes.alpha, 2400, ItemStack.with(
        Items.copper, 30,
        Items.lead, 40,
        Items.silicon, 30
    )),
    new UnitFactory.UnitPlan(units.firefly, 2400, ItemStack.with(
        Items.lead, 20,
        Items.metaglass, 10,
        Items.coal, 10,
        Items.silicon, 15
    ))
);
function adu(unitFrom, unitTo){
    var a = java.util.Arrays.copyOf(Blocks.tetrativeReconstructor.upgrades.get(0), 2);
    a[0] = unitFrom;
    a[1] = unitTo;
    return a
};
Blocks.additiveReconstructor.upgrades.add(
    adu(UnitTypes.alpha, UnitTypes.beta),
    adu(units.guarding, units.resisting),
    adu(units.blade, units.hatchet),
    adu(units.firefly, units.candlelight)
);
Blocks.multiplicativeReconstructor.upgrades.add(
    adu(UnitTypes.beta, units.omicron),
    adu(units.resisting, units.garrison),
    adu(units.hatchet, units.castle)
);
Blocks.exponentialReconstructor.upgrades.add(
    adu(units.omicron, units.pioneer),
    adu(units.garrison, units.shelter)
);
/*Blocks.tetrativeReconstructor.upgrades.add(
    adu(units.shelter, units.blessing)
);*/



/*========Block Effect Region========*/
Blocks.illuminator.brightness = 0.75 + 0.25;
Blocks.illuminator.radius = 140 + 60;



/*========Unit Ground Region========*/
function mutate(unitFrom, unitTo){
    var type = unitFrom.constructor.get().class;
    unitFrom.constructor = () => extend(type, {
        destroy(){
            this.super$destroy();
            if(this.lastDrownFloor == blocks.neoplasm){
                var chance = unitFrom == UnitTypes.dagger ? 0.95 : 1;
                var team = Mathf.chance(chance) ? Team.crux : this.team;
                unitTo.spawn(team, this.x, this.y)
            }
        }
    })
};
mutate(UnitTypes.dagger, units.blade);
mutate(UnitTypes.mace, units.hatchet);
mutate(UnitTypes.fortress, units.castle);



/*========Unit Air Region========*/
UnitTypes.alpha.coreUnitDock = true;
UnitTypes.beta.coreUnitDock = true;
UnitTypes.gamma.coreUnitDock = true;
UnitTypes.alpha.defaultCommand = UnitCommand.mineCommand;
UnitTypes.beta.defaultCommand = UnitCommand.mineCommand;
lib.addToResearch(UnitTypes.alpha, {parent: "air-factory"});
lib.addToResearch(UnitTypes.beta, {parent: "alpha"});



/*========Liquid Region========*/
Liquids.neoplasm.effect = statues.overgrown;



/*====Content Load Region====*/
require("blocks");
require("items");
require("liquids");
require("planets");
require("statues");
require("units");
require("techtree");



const
    mod = Vars.mods.getMod("fire"),
    name = Core.bundle.get("modname"),
    ver = mod.meta.version;

mod.meta.displayName = name;
Events.on(EventType.ClientLoadEvent, e => {

    /*========Setting Region========*/
    Vars.ui.settings.addCategory(Core.bundle.get("setting.fire"), "fire-setting", t => {

        t.checkPref("allowSandMining", false, a => {
            Blocks.sand.playerUnmineable = !a;
            Blocks.darksand.playerUnmineable = !a;
            Blocks.sandWater.playerUnmineable = !a;
            Blocks.darksandWater.playerUnmineable = !a;
            Blocks.darksandTaintedWater.playerUnmineable = !a
        });

        t.checkPref("showBlockRange", true)

    });

    /*========Menu Region========*/
    var historyDialog = new BaseDialog(Core.bundle.format("historyTitle", name));
    historyDialog.addCloseListener();

    historyDialog.buttons.button("@close", () => {
        historyDialog.hide()
    }).size(210, 64);

    historyDialog.cont.pane((() => {

        var table = new Table();

        table.add("@history").left().growX().wrap().width(800).maxWidth(1024).pad(4).labelAlign(Align.left);

        return table

    })()).grow().center().maxWidth(1024);



    var mainDialog = new BaseDialog(Core.bundle.format("mainTitle", name, ver));
    mainDialog.addCloseListener();

    mainDialog.buttons.button("@close", () => {
        mainDialog.hide()
    }).size(210, 64);

    mainDialog.buttons.button(Core.bundle.format("historyTitle", ""), () => {
        historyDialog.show()
    }).size(210, 64);

    mainDialog.cont.pane((() => {

        var table = new Table();
        function adc(content, firsts){
            if(firsts) table.button("?", Styles.grayPanel, () => Vars.ui.content.show(content)).size(40).pad(10).right();
            else table.button("?", Styles.grayPanel, () => Vars.ui.content.show(content)).size(40).pad(10)
        };

        table.image(Core.atlas.find("fire-logo", Core.atlas.find("clear"))).height(107).width(359).pad(3);
        table.row();

        table.add(Core.bundle.format("contentMain", ver)).left().growX().wrap().width(800).maxWidth(1024).pad(4).labelAlign(Align.left);
        table.row();

        table.row();
        adc(blocks.adaptiveSource, 1);
        adc(blocks.blossom, 0);
        adc(blocks.fleshReconstructor, 0);
        adc(blocks.hardenedAlloyCrucible, 0);
        adc(units.hatchet, 0);
        adc(statues.overgrown, 0);
        adc(statues.disintegrated, 0);
        table.row();

        table.add("@contentSecondary").left().growX().wrap().width(800).maxWidth(1024).pad(4).labelAlign(Align.left);
        table.row();

        if(Blocks.router.localizedName == "路由器"){

            const urlRaindance = "https://space.bilibili.com/516898377";
            const urlUenhe     = "https://space.bilibili.com/327502129";

            table.button(("@linkRaindance"), () => {
                if(!Core.app.openURI(urlRaindance)){
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(urlRaindance)
                }
            }).size(256, 64);
            table.row();

            table.button(("@linkUenhe"), () => {
                if(!Core.app.openURI(urlUenhe)){
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(urlUenhe)
                }
            }).size(256, 64);
            table.row()

        }else{

            const urlGithub = "https://github.com/Uenhe/Fire";

            table.button(("@linkGithub"), () => {
                if(!Core.app.openURI(urlGithub)){
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(urlGithub)
                }
            }).size(256, 64);
            table.row()

        };

        return table

    })()).grow().center().maxWidth(1024);
    mainDialog.show()

})
