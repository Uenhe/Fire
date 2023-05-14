Events.on(EventType.ClientLoadEvent, e => {
	Vars.ui.settings.addCategory(Core.bundle.get('setting.fire'), 'fire-setting', t => {
		t.checkPref('allowSandMining', false, a => {
			Blocks.sand.playerUnmineable = !a
			Blocks.darksand.playerUnmineable = !a
			Blocks.sandWater.playerUnmineable = !a
			Blocks.darksandWater.playerUnmineable = !a
			Blocks.darksandTaintedWater.playerUnmineable = !a
		})
		t.checkPref('showBlockRange', true)
	})
})

const lib = require('misc/lib')
const FireBlocks = require('blocks')
const FireLiquids = require('liquids')
const FireStatuses = require('statuses')
const FireUnits = require('units')

Blocks.sandWater.itemDrop = Items.sand
Blocks.darksandWater.itemDrop = Items.sand
Blocks.darksandTaintedWater.itemDrop = Items.sand
Blocks.sporePine.attributes.set(FireBlocks.tree, 1.5)
Blocks.snowPine.attributes.set(FireBlocks.tree, 1.5)
Blocks.pine.attributes.set(FireBlocks.tree, 1.5)
Blocks.whiteTreeDead.attributes.set(FireBlocks.tree, 1)
Blocks.whiteTree.attributes.set(FireBlocks.tree, 1)
Blocks.grass.attributes.set(FireBlocks.grass, 0.25)

Blocks.wave.liquidCapacity = 10 + 10
Blocks.tsunami.liquidCapacity = 40 + 20
Blocks.wave.ammoTypes.put(FireLiquids.liquidNitrogen, Object.assign(new LiquidBulletType(FireLiquids.liquidNitrogen), {
	knockback: 0.7,
	drag: 0.001,
	status: FireStatuses.frostbite,
	damage: 4.55
}))
Blocks.tsunami.ammoTypes.put(FireLiquids.liquidNitrogen, Object.assign(new LiquidBulletType(FireLiquids.liquidNitrogen), {
	lifetime: 49,
	speed: 4,
	knockback: 1.3,
	puddleSize: 8,
	orbSize: 4,
	drag: 0.001,
	ammoMultiplier: 0.4,
	status: FireStatuses.frostbite,
	statusDuration: 240,
	damage: 6.25
}))

Blocks.laserDrill.drillTime = 280 - 10
Blocks.laserDrill.hardnessDrillMultiplier = 50 - 5
Blocks.blastDrill.drillTime = 280 - 25
Blocks.blastDrill.hardnessDrillMultiplier = 50 - 5

Blocks.phaseConveyor.itemCapacity = 10 + 5
Blocks.phaseConveyor.transportTime = 2 - 1
Blocks.distributor.buildType = () => extend(Router.RouterBuild, Blocks.distributor, {
	canControl(){
		return true
	}
})
Blocks.massDriver.rotateSpeed = 5 + 5
Blocks.massDriver.bulletSpeed = 5.5 + 9.5

Blocks.mechanicalPump.pumpAmount = 7 / 60 + 0.2 / 60
Blocks.impulsePump.pumpAmount = 0.22 + 1.2 / 9 / 60
Blocks.phaseConduit.liquidCapacity = 10 + 14

Blocks.groundFactory.plans.add(
	new UnitFactory.UnitPlan(FireUnits.sh, 1500, ItemStack.with(Items.lead, 20, Items.titanium, 25, Items.silicon, 30))
)
Blocks.airFactory.plans.add(
	new UnitFactory.UnitPlan(UnitTypes.alpha, 2400, ItemStack.with(Items.copper, 30, Items.lead, 40, Items.silicon, 30)),
	new UnitFactory.UnitPlan(FireUnits.firefly, 2400, ItemStack.with(Items.lead, 20, Items.metaglass, 10, Items.coal, 10, Items.silicon, 15))
)
Blocks.additiveReconstructor.addUpgrade(UnitTypes.alpha, UnitTypes.beta)
Blocks.additiveReconstructor.addUpgrade(FireUnits.sh, FireUnits.ky)
Blocks.additiveReconstructor.addUpgrade(FireUnits.firefly, FireUnits.candlelight)
Blocks.multiplicativeReconstructor.addUpgrade(UnitTypes.beta, FireUnits.gnj)
Blocks.multiplicativeReconstructor.addUpgrade(FireUnits.ky, FireUnits.ws)
Blocks.exponentialReconstructor.addUpgrade(FireUnits.ws, FireUnits.bh)
//Blocks.tetrativeReconstructor.addUpgrade(FireUnits.bh, FireUnits.sy)

Blocks.illuminator.brightness = 0.75 + 0.25
Blocks.illuminator.radius = 140 + 60

UnitTypes.alpha.coreUnitDock = true
UnitTypes.beta.coreUnitDock = true
UnitTypes.gamma.coreUnitDock = true
UnitTypes.alpha.defaultCommand = UnitCommand.mineCommand
UnitTypes.beta.defaultCommand = UnitCommand.mineCommand
lib.addToResearch(UnitTypes.alpha, {parent: 'air-factory'})
lib.addToResearch(UnitTypes.beta, {parent: 'alpha'})
