Events.on(EventType.ClientLoadEvent, e => {
	Vars.ui.settings.game.checkPref('allowSandMining', false, a => {
		Blocks.sand.playerUnmineable = !a
		Blocks.darksand.playerUnmineable = !a
		Blocks.sandWater.playerUnmineable = !a
		Blocks.darksandWater.playerUnmineable = !a
		Blocks.darksandTaintedWater.playerUnmineable = !a
	}) //设置-游戏-手挖沙
	Vars.ui.settings.game.checkPref('showBlockRange', false) //设置-游戏-显示特殊方块范围
})

const lib = require('misc/lib')
const FireLiquids = require('liquids')
const FireStatuses = require('statuses')
const FireUnits = require('units')
const sm = Attribute.add('sm')
const cd = Attribute.add('cd')

Blocks.sandWater.itemDrop = Items.sand
Blocks.darksandWater.itemDrop = Items.sand
Blocks.darksandTaintedWater.itemDrop = Items.sand
Blocks.sporePine.attributes.set(sm, 1.5)
Blocks.snowPine.attributes.set(sm, 1.5)
Blocks.pine.attributes.set(sm, 1.5)
Blocks.whiteTreeDead.attributes.set(sm, 1)
Blocks.whiteTree.attributes.set(sm, 1)
Blocks.grass.attributes.set(cd, 0.25)

Blocks.wave.liquidCapacity = 20
Blocks.tsunami.liquidCapacity = 60
Blocks.wave.ammoTypes.put(FireLiquids.yd, (() => {
	const ammo = new LiquidBulletType(FireLiquids.yd)
	ammo.knockback = 0.7
	ammo.drag = 0.001
	ammo.status = FireStatuses.frostbite
	ammo.damage = 4.55
	return ammo
})())
Blocks.tsunami.ammoTypes.put(FireLiquids.yd, (() => {
	const ammo = new LiquidBulletType(FireLiquids.yd)
	ammo.lifetime = 49
	ammo.speed = 4
	ammo.knockback = 1.3
	ammo.puddleSize = 8
	ammo.orbSize = 4
	ammo.drag = 0.001
	ammo.ammoMultiplier = 0.4
	ammo.status = FireStatuses.frostbite
	ammo.statusDuration = 240
	ammo.damage = 6.25
	return ammo
})()) 

Blocks.laserDrill.drillTime = 270
Blocks.laserDrill.hardnessDrillMultiplier = 45
Blocks.blastDrill.drillTime = 255
Blocks.blastDrill.hardnessDrillMultiplier = 45

Blocks.phaseConveyor.itemCapacity = 15
Blocks.distributor.buildType = () => extend(Router.RouterBuild, Blocks.distributor, {
	canControl(){
		return true
	}
})

Blocks.mechanicalPump.pumpAmount = 0.12
Blocks.impulsePump.pumpAmount = 2 / 9
Blocks.phaseConduit.liquidCapacity = 24

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

UnitTypes.alpha.coreUnitDock = true
UnitTypes.beta.coreUnitDock = true
UnitTypes.gamma.coreUnitDock = true
UnitTypes.alpha.defaultCommand = UnitCommand.mineCommand
UnitTypes.beta.defaultCommand = UnitCommand.mineCommand
lib.addToResearch(UnitTypes.alpha, {parent: 'air-factory'})
lib.addToResearch(UnitTypes.beta, {parent: 'alpha'})
