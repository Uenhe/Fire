const lib = require('misc/lib')
const FireItems = require('items')
const FireLiquids = require('liquids')
const FireStatuses = require('statuses')
const FireUnits = require('units')
const tree = Attribute.add('tree')
exports.tree = tree
const grass = Attribute.add('grass')
exports.grass = grass



const fireCompany = Object.assign(new LightBlock('hzgs'), {
	buildVisibility: BuildVisibility.hidden,
	alwaysUnlocked: true,
	health: 2147483647,
	armor: 2147483647,
	size: 2
})
exports.fireCompany = fireCompany



/*========炮塔========*/



//炮塔-击碎
const smasher = Object.assign(new ItemTurret('js'), {
	buildVisibility: BuildVisibility.shown,
	researchCost: ItemStack.with(
		Items.copper, 150,
		Items.lead, 120,
		Items.titanium, 50
	)
})
exports.smasher = smasher



//炮塔-魇光
const nightmare = Object.assign(new ItemTurret('yg'), {
	buildVisibility: BuildVisibility.shown,
	researchCost: ItemStack.with(
		Items.titanium, 400,
		Items.thorium, 250,
		Items.silicon, 275,
		FireItems.mirrorglass, 165
	)
})
exports.nightmare = nightmare



//炮塔-点燃
const ignite = Object.assign(new ContinuousTurret('dr'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.copper, 350,
		Items.graphite, 240,
		Items.silicon, 220,
		Items.plastanium, 180
	)
})
exports.ignite = ignite



//炮塔-千里
const distance = Object.assign(new ItemTurret('ql'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.copper, 650,
		Items.thorium, 375,
		Items.silicon, 425,
		Items.plastanium, 250,
		FireItems.hardenedAlloy, 225
	)
})
exports.distance = distance



//炮塔-倒海
const seaquake = Object.assign(new LiquidTurret('dh'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.lead, 420,
		Items.metaglass, 175,
		Items.thorium, 150,
		Items.plastanium, 135,
		Items.surgeAlloy, 65
	)
})
exports.seaquake = seaquake



//炮塔-天眼
const monitor = Object.assign(new ItemTurret('ty'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.copper, 800,
		Items.metaglass, 275,
		Items.titanium, 325,
		Items.silicon, 450,
		FireItems.hardenedAlloy, 225
	)
})
exports.monitor = monitor



//炮塔-潮怨
const grudge = Object.assign(new ItemTurret('grudge'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.copper, 1350,
		Items.graphite, 475,
		Items.phaseFabric, 325,
		Items.surgeAlloy, 275,
		FireItems.hardenedAlloy, 550
	)
})
exports.grudge = grudge



/*========生产========*/



//生产-伐木机
const chopper = Object.assign(new WallCrafter('fmj'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.production,
	requirements: ItemStack.with(
		Items.copper, 20,
		Items.lead, 15,
		Items.titanium, 10,
		Items.silicon, 15
	),
	researchCost: ItemStack.with(
		Items.copper, 50,
		Items.lead, 35,
		Items.titanium, 25,
		Items.silicon, 30
	),
	health: 65,
	size: 1,
	ambientSound: Sounds.drill,
	ambientSoundVolume: 0.01,
	attribute: tree,
	drillTime: 120,
	output: FireItems.timber
})
chopper.consumePower(24 / 60)
exports.chopper = chopper



//生产-树场
const treeFarm = Object.assign(new AttributeCrafter('sc'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.copper, 50,
		Items.lead, 30,
		Items.metaglass, 20,
		Items.titanium, 25
	)
})
exports.treeFarm = treeFarm



//生产-水汽冷凝器
const vapourCondenser = Object.assign(new GenericCrafter('sqlnq'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.lead, 50,
		Items.graphite, 30,
		Items.metaglass, 30,
		Items.titanium, 20
	)
})
exports.vapourCondenser = vapourCondenser



//生产-生物质增生机
const biomassCultivator = Object.assign(new AttributeCrafter('swzzsj'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.copper, 60,
		Items.lead, 80,
		Items.titanium, 45,
		Items.silicon, 45
	),
	researchCostMultiplier: 0.6
})
exports.biomassCultivator = biomassCultivator



//生产-裂变钻头
const fissionDrill = Object.assign(new BurstDrill('lbzt'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.copper, 160,
		Items.metaglass, 80,
		Items.thorium, 375,
		Items.silicon, 145
	)
})
exports.fissionDrill = fissionDrill



/*========运输========*/



//运输-复合传送带
const compositeConveyor = Object.assign(new Conveyor('fhcsd'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.metaglass, 1,
		Items.titanium, 1,
		Items.thorium, 1
	)
})
exports.compositeConveyor = compositeConveyor



//运输-复合传送带桥
const compositeBridgeConveyor = Object.assign(new ItemBridge('composite-bridge-conveyor'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.metaglass, 4,
		Items.titanium, 6,
		Items.thorium, 4,
		Items.plastanium, 4
	)
})
exports.compositeBridgeConveyor = compositeBridgeConveyor



/*========液体========*/



//液体-复合液体路由器
const compositeLiquidRouter = Object.assign(new LiquidRouter('composite-liquid-router'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.metaglass, 8,
		Items.titanium, 12,
		Items.thorium, 6,
		Items.plastanium, 8
	)
})
exports.compositeLiquidRouter = compositeLiquidRouter



//液体-复合流体桥
const compositeBridgeConduit = Object.assign(new LiquidBridge('composite-bridge-conduit'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.metaglass, 6,
		Items.titanium, 8,
		Items.thorium, 4,
		Items.plastanium, 4
	)
})
exports.compositeBridgeConduit = compositeBridgeConduit



/*========电力========*/



//电力-装甲节点
const conductorPowerNode = Object.assign(new PowerNode('zjjd'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.lead, 10,
		FireItems.glass, 10,
		FireItems.conductor, 5
	)
})
exports.conductorPowerNode = conductorPowerNode



//电力-焰燃发电机
const flameGenerator = Object.assign(new ConsumeGenerator('yrfdj'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.lead, 150,
		Items.thorium, 90,
		Items.silicon, 225,
		Items.plastanium, 115,
		FireItems.conductor, 60
	)
})
exports.flameGenerator = flameGenerator



/*========防御========*/



//防御-水坝墙
const damWall = Object.assign(new Wall('sbq'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.defense,
	requirements: ItemStack.with(
		Items.metaglass, 3,
		Items.titanium, 4
	),
	health: 720,
	size: 1,
	requiresWater: true
})
exports.damWall = damWall



//防御-大型水坝墙
const damWallLarge = Object.assign(new Wall('sbqdx'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.defense,
	requirements: ItemStack.with(
		Items.metaglass, 12,
		Items.titanium, 16
	),
	health: 2880,
	size: 2,
	requiresWater: true
})
exports.damWallLarge = damWallLarge



//防御-硬化合金墙
const hardenedWall = Object.assign(new Wall('hardened-wall'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.defense,
	requirements: ItemStack.with(
		Items.metaglass, 3,
		FireItems.hardenedAlloy, 6
	),
	health: 1280,
	armor: 15,
	size: 1,
	placeableLiquid: true,
	insulated: true,
	absorbLasers: true
})
exports.hardenedWall = hardenedWall



//防御-大型硬化合金墙
const hardenedWallLarge = Object.assign(new Wall('hardened-wall-large'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.defense,
	requirements: ItemStack.with(
		Items.metaglass, 12,
		FireItems.hardenedAlloy, 24
	),
	health: 5120,
	armor: 15,
	size: 2,
	placeableLiquid: true,
	insulated: true,
	absorbLasers: true
})
exports.hardenedWallLarge = hardenedWallLarge



//防御-血肉墙
const fleshWall = Object.assign(new RegenProjector('xrq'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.defense,
	group: BlockGroup.walls,
	priority: TargetPriority.wall,
	requirements: ItemStack.with(
		FireItems.flesh, 24
	),
	health: 4800,
	armor: 2,
	size: 2,
	hasPower: false,
	hasItems: false,
	canOverdrive: false,
	buildCostMultiplier: 6,
	crushDamageMultiplier: 5,
	range: 1,
	healPercent: 0.024,
	optionalMultiplier: 2
})
fleshWall.consumeLiquid(Liquids.water, 1.8 / 60).boost()
exports.fleshWall = fleshWall



/*========工厂========*/



//工厂-热能窑炉
const thermalKiln = Object.assign(new AttributeCrafter('rnyl'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.copper, 60,
		Items.lead, 45,
		Items.graphite, 30
	),
	researchCost: ItemStack.with(
		Items.copper, 120,
		Items.lead, 80,
		Items.graphite, 40
	),
	size: 2,
	hasPower: true,
	hasLiquids: false,
	itemCapacity: 20,
	craftEffect: Fx.smeltsmoke,
	drawer: new DrawMulti(
		new DrawDefault(),
		new DrawFlame(Color.valueOf('ffe099'))
	),
	craftTime: 60,
	outputItem: new ItemStack(
		FireItems.glass, 6
	),
	attribute: Attribute.heat,
	boostScale: 1 / 3,
	maxBoost: 1
})
thermalKiln.consumeItems(ItemStack.with(
	Items.sand, 6,
	Items.coal, 1
))
thermalKiln.consumePower(30 / 60)
exports.thermalKiln = thermalKiln



//工厂-玻璃镀钢机
const metaglassPlater = Object.assign(new GenericCrafter('dgj'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.lead, 75,
		Items.titanium, 55,
		Items.silicon, 40
	),
	researchCost: ItemStack.with(
		Items.lead, 200,
		Items.titanium, 135,
		Items.silicon, 60
	),
	size: 2,
	hasPower: true,
	hasLiquids: false,
	craftEffect: Fx.smeltsmoke,
	drawer: new DrawMulti(
		new DrawDefault(),
		new DrawFade()
	),
	craftTime: 10,
	outputItem: new ItemStack(
		Items.metaglass, 2
	)
})
metaglassPlater.consumeItems(ItemStack.with(
	Items.lead, 1,
	FireItems.glass, 2
))
metaglassPlater.consumePower(120 / 60)
exports.metaglassPlater = metaglassPlater



//工厂-钢化玻璃打磨机
const mirrorglassPolisher = Object.assign(new GenericCrafter('dmj'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.graphite, 45,
		Items.titanium, 60,
		Items.silicon, 75
	),
	researchCost: ItemStack.with(
		Items.graphite, 160,
		Items.titanium, 200,
		Items.silicon, 180
	),
	size: 2,
	hasPower: true,
	hasLiquids: false,
	craftEffect: Fx.smeltsmoke,
	drawer: new DrawMulti(
		new DrawDefault(),
		new DrawFade()
	),
	craftTime: 90,
	outputItem: new ItemStack(
		FireItems.mirrorglass, 1
	)
})
mirrorglassPolisher.consumeItems(ItemStack.with(
	Items.metaglass, 2
))
mirrorglassPolisher.consumePower(120 / 60)
exports.mirrorglassPolisher = mirrorglassPolisher



//工厂-烘火合金萃取厂
const kindlingExtractor = Object.assign(new GenericCrafter('hhhjcqc'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.graphite, 90,
		Items.titanium, 80,
		Items.silicon, 60
	),
	size: 2,
	hasPower: true,
	hasLiquids: false,
	craftEffect: Fx.smeltsmoke,
	updateEffect: Fx.explosion,
	craftTime: 100,
	outputItem: new ItemStack(
		FireItems.kindlingAlloy, 1
	)
})
kindlingExtractor.consumeItems(ItemStack.with(
	Items.coal, 1,
	FireItems.impurityKindlingAlloy, 1
))
kindlingExtractor.consumePower(120 / 60)
exports.kindlingExtractor = kindlingExtractor



//工厂-烘火合金提取器
const impurityKindlingExtractor = Object.assign(new GenericCrafter('hhhjtqq'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.lead, 85,
		Items.graphite, 55,
		Items.titanium, 40
	),
	size: 2,
	hasPower: true,
	hasLiquids: true,
	drawer: new DrawMulti(
		new DrawRegion('-bottom'),
		new DrawLiquidTile(Liquids.slag),
		new DrawDefault()
	),
	craftTime: 66,
	outputItem: new ItemStack(
		FireItems.impurityKindlingAlloy, 2
	)
})
impurityKindlingExtractor.consumeItems(ItemStack.with(
	Items.coal, 3,
	Items.sporePod, 2
))
impurityKindlingExtractor.consumeLiquid(
	Liquids.slag, 0.45
)
impurityKindlingExtractor.consumePower(90 / 60)
exports.impurityKindlingExtractor = impurityKindlingExtractor



//工厂-导体构成仪
const conductorFormer = Object.assign(new GenericCrafter('dtgcy'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.lead, 80,
		Items.surgeAlloy, 25,
		FireItems.mirrorglass, 30
	),
	size: 2,
	hasPower: true,
	hasLiquids: false,
	craftEffect: new MultiEffect(
		Fx.lightning,
		Fx.smeltsmoke
	),
	drawer: new DrawMulti(
		new DrawArcSmelt(),
		new DrawDefault(),
		new DrawFade()
	),
	craftTime: 120,
	outputItem: new ItemStack(
		FireItems.conductor, 2
	)
})
conductorFormer.consumeItems(ItemStack.with(
	Items.copper, 2,
	Items.silicon, 3
))
conductorFormer.consumePower(200 / 60)
exports.conductorFormer = conductorFormer



//工厂-逻辑合金制造厂
const logicAlloyProcessor = Object.assign(new GenericCrafter('logic-alloy-processor'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.titanium, 105,
		Items.silicon, 60,
		Items.plastanium, 55
	),
	size: 2,
	hasPower: true,
	hasLiquids: false,
	drawer: new DrawMulti(
		new DrawDefault(),
		new DrawFade()
	),
	craftTime: 120,
	outputItem: new ItemStack(
		FireItems.logicAlloy, 2
	)
})
logicAlloyProcessor.consumeItems(ItemStack.with(
	Items.copper, 3,
	Items.titanium, 2,
	Items.silicon, 3
))
logicAlloyProcessor.consumePower(120 / 60)
exports.logicAlloyProcessor = logicAlloyProcessor



//工厂-震爆物混合器
const detonationMixer = Object.assign(new GenericCrafter('detonation-mixer'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.thorium, 45,
		Items.plastanium, 30,
		FireItems.logicAlloy, 55
	),
	size: 2,
	hasPower: true,
	hasLiquids: false,
	craftEffect: Fx.smeltsmoke,
	updateEffect: Fx.explosion,
	craftTime: 180,
	outputItem: new ItemStack(
		FireItems.detonationCompound, 2
	)
})
detonationMixer.consumeItems(ItemStack.with(
	Items.blastCompound, 2,
	Items.pyratite, 2,
	FireItems.logicAlloy, 1
))
detonationMixer.consumePower(90 / 60)
exports.detonationMixer = detonationMixer



//工厂-高速冷却器
const slagCooler = Object.assign(new GenericCrafter('gslqq'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.copper, 90,
		Items.graphite, 55,
		Items.titanium, 70
	),
	size: 2,
	hasPower: true,
	hasLiquids: true,
	craftEffect: Fx.blastsmoke,
	updateEffect: Fx.smeltsmoke,
	drawer: new DrawMulti(
		new DrawRegion('-bottom'),
		new DrawLiquidTile(Liquids.slag),
		new DrawDefault(),
		new DrawFade()
	),
	craftTime: 60,
	outputItem: new ItemStack(
		FireItems.flamefluidCrystal, 2
	)
})
slagCooler.consumeLiquids(LiquidStack.with(
	Liquids.slag, 0.5,
	Liquids.cryofluid, 0.05
))
slagCooler.consumePower(90 / 60)
exports.slagCooler = slagCooler



//工厂-铜铅搅碎机
const crusher = Object.assign(new GenericCrafter('tqjsj'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.copper, 45,
		Items.lead, 75,
		Items.graphite, 30
	),
	size: 2,
	hasPower: true,
	hasLiquids: false,
	updateEffect: Fx.pulverizeMedium,
	craftEffect: Fx.blastsmoke,
	drawer: new DrawMulti(
		new DrawRegion('-bottom'),
		new DrawRegion('-spinner', 10, true),
		new DrawDefault(),
		new DrawFade()
	),
	craftTime: 30,
	outputItem: new ItemStack(
		Items.scrap, 2
	)
})
crusher.consumeItems(ItemStack.with(
	Items.copper, 1,
	Items.lead, 1
))
crusher.consumePower(30 / 60)
exports.crusher = crusher



//工厂-木材焚烧厂
/*
* @author <guiY>
* 代码来自Extra Utilities mod
*/
let TBoutput = Items.coal
const timberBurner = extend(ConsumeGenerator, 'mcfsc', {
	setStats(){
		this.super$setStats()
		this.stats.add(Stat.productionTime, this.block.itemDuration / 60, StatUnit.seconds)
		this.stats.add(Stat.output, TBoutput)
	},
	outputsItems(){
		return true
	},
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.copper, 50,
		Items.lead, 25,
		Items.metaglass, 15,
		Items.graphite, 20
	),
	size: 2,
	hasLiquids: false,
	ambientSound: Sounds.steam,
	ambientSoundVolume: 0.01,
	generateEffect: Fx.generatespark,
	drawer: new DrawMulti(
		new DrawDefault(),
		new DrawWarmupRegion()
	),
	itemDuration: 30,
	powerProduction: 210 / 60
})
timberBurner.consumeItem(FireItems.timber)
timberBurner.buildType = () => {
	let p = 0
	let gp = 0
	let full = false
	return extend(ConsumeGenerator.ConsumeGeneratorBuild, timberBurner, {
		updateTile(){
			const cons = this.consValid()
			full = this.items.get(TBoutput) >= this.block.itemCapacity
			if(cons && !full){
				p += this.getProgressIncrease(this.block.itemDuration)
				gp += this.getProgressIncrease(this.block.itemDuration)
			}
			if(p > 1 && !full){
				this.items.add(TBoutput, 1)
				p %= 1
			}
			if(gp > 1 && !full){
				this.consume()
				gp %= 1
				this.block.generateEffect.at(this.x + Mathf.range(3), this.y + Mathf.range(3))
			}
			this.productionEfficiency = Mathf.num(cons) * Mathf.num(!full)
			this.dump(TBoutput)
			this.produced(TBoutput)
			this.warmup = Mathf.lerpDelta(this.warmup, cons && !full ? 1 : 0, 0.05)
		},
		consValid(){
			return this.efficiency > 0
		},
		getPowerProduction(){
			return Mathf.num(this.consValid()) * this.block.powerProduction * Mathf.num(!full)
		},
		status(){
			if(this.consValid() && !full){
				return BlockStatus.active
			}
			if(full && this.consValid()){
				return BlockStatus.noOutput
			}
			return BlockStatus.noInput
		},
		write(write){
			this.super$write(write)
			write.f(p)
			write.f(gp)
		},
		read(read, revision){
			this.super$read(read, revision)
			p = read.f()
			gp = read.f()
		}
	})
}
exports.timberBurner = timberBurner



//工厂-电热硅炉
const electrothermalSiliconFurnace = Object.assign(new GenericCrafter('drgl'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.copper, 250,
		Items.graphite, 200,
		Items.titanium, 120,
		Items.surgeAlloy, 80
	),
	size: 3,
	hasPower: true,
	hasLiquids: false,
	itemCapacity: 30,
	craftEffect: Fx.smeltsmoke,
	drawer: new DrawMulti(
		new DrawDefault(),
		new DrawFlame(Color.valueOf('ffef99'))
	),
	craftTime: 10,
	outputItem: new ItemStack(
		Items.silicon, 2
	)
})
electrothermalSiliconFurnace.consumeItems(ItemStack.with(
	Items.sand, 3
))
electrothermalSiliconFurnace.consumePower(720 / 60)
exports.electrothermalSiliconFurnace = electrothermalSiliconFurnace



//工厂-血肉合成仪
const fleshSynthesizer = Object.assign(new GenericCrafter('xrhcy'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.lead, 120,
		Items.graphite, 60,
		Items.silicon, 75,
		Items.plastanium, 50
	),
	size: 3,
	hasPower: true,
	hasLiquids: false,
	itemCapacity: 30,
	craftEffect: Fx.blastsmoke,
	drawer: new DrawMulti(
		new DrawRegion('-bottom'),
		new DrawWeave(),
		new DrawDefault()
	),
	craftTime: 45,
	outputItem: new ItemStack(
		FireItems.flesh, 1
	)
})
fleshSynthesizer.consumeItems(ItemStack.with(
	Items.plastanium, 3,
	Items.phaseFabric, 2,
	Items.sporePod, 1
))
fleshSynthesizer.consumePower(100 / 60)
exports.fleshSynthesizer = fleshSynthesizer



//工厂-液氮压缩机
const liquidNitrogenCompressor = Object.assign(new GenericCrafter('ydysj'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.lead, 220,
		Items.metaglass, 175,
		Items.silicon, 185,
		Items.plastanium, 130
	),
	size: 3,
	hasPower: true,
	hasLiquids: true,
	itemCapacity: 30,
	liquidCapacity: 75,
	baseExplosiveness: 5,
	drawer: new DrawMulti(
		new DrawRegion('-bottom'),
		new DrawLiquidTile(Liquids.cryofluid),
		new DrawLiquidTile(FireLiquids.liquidNitrogen),
		new DrawDefault()
	),
	craftTime: 120,
	outputLiquid: new LiquidStack(
		FireLiquids.liquidNitrogen, 50 / 60
	)
})
liquidNitrogenCompressor.consumeItems(ItemStack.with(
	Items.blastCompound, 5,
	FireItems.kindlingAlloy, 2
))
liquidNitrogenCompressor.consumeLiquids(LiquidStack.with(
	Liquids.cryofluid, 60 / 60
))
liquidNitrogenCompressor.consumePower(400 / 60)
exports.liquidNitrogenCompressor = liquidNitrogenCompressor



//工厂-硬化合金冶炼厂
const hardenedAlloySmelter = Object.assign(new GenericCrafter('hardened-alloy-smelter'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.graphite, 135,
		Items.thorium, 50,
		Items.silicon, 90,
		Items.plastanium, 80
	),
	size: 3,
	hasPower: true,
	hasLiquids: false,
	itemCapacity: 20,
	craftEffect: Fx.smeltsmoke,
	drawer: new DrawMulti(
		new DrawDefault(),
		new DrawFlame(Color.valueOf('ffef99'))
	),
	craftTime: 60,
	outputItem: new ItemStack(
		FireItems.hardenedAlloy, 3
	)
})
hardenedAlloySmelter.consumeItems(ItemStack.with(
	Items.thorium, 3,
	Items.plastanium, 6,
	FireItems.kindlingAlloy, 3
))
hardenedAlloySmelter.consumePower(600 / 60)
exports.hardenedAlloySmelter = hardenedAlloySmelter



//工厂-大型硬化合金冶炼厂
const hardenedAlloySmelterLarge = Object.assign(new GenericCrafter('hardened-alloy-smelter-large'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.crafting,
	requirements: ItemStack.with(
		Items.graphite, 375,
		Items.titanium, 265,
		Items.silicon, 320,
		FireItems.hardenedAlloy, 270
	),
	armor: 4,
	size: 6,
	hasPower: true,
	hasLiquids: true,
	itemCapacity: 60,
	liquidCapacity: 120,
	baseExplosiveness: 5,
	craftEffect: Fx.smeltsmoke,
	updateEffect: new Effect(50, e => {
		Draw.color(Pal.reactorPurple, 0.7)
		Lines.stroke(e.fout() * 2)
		Lines.circle(e.x, e.y, 4 + e.finpow() * 60)
	}),
	updateEffectChance: 0.01,
	drawer: new DrawMulti(
		new DrawDefault(),
		new DrawFlame(Color.valueOf('ffef99'))
	),
	craftTime: 120,
	outputItem: new ItemStack(
		FireItems.hardenedAlloy, 20
	)
})
hardenedAlloySmelterLarge.consumeItems(ItemStack.with(
	Items.thorium, 12,
	Items.plastanium, 20
))
hardenedAlloySmelterLarge.consumeLiquid(
	Liquids.water, 2
)
hardenedAlloySmelterLarge.consumePower(48)
hardenedAlloySmelterLarge.buildType = () => {
	var fraction = 1.05
	var color = Pal.reactorPurple
	var rotation = 90
	var i = 0
	return extend(GenericCrafter.GenericCrafterBuild, hardenedAlloySmelterLarge, {
		craft(){
			this.super$craft()
			var colorlist = [Pal.reactorPurple, Pal.thoriumPink, Pal.lightishOrange, Pal.surge, Pal.plastanium]
			i = i >= colorlist.length - 1 ? 0 : i + 1
			color = colorlist[i]
			rotation = Mathf.random(360)
			Sounds.release.at(this, Mathf.random(0.45, 0.55)) //craft sound
			for(var j = 0; j < 8; j += 1){
				Lightning.create(this.team, color, 80, this.x, this.y, (j - 1) * 45, this.block.size * 2)
			}
			fraction = 1.05
		},
		createExplosion(){
			Damage.damage(this.x, this.y, 16 * Vars.tilesize * this.timeScale, 2400 * this.timeScale)
			Fx.reactorExplosion.at(this)
			Sounds.explosionbig.at(this)
			Effect.shake(6 * this.timeScale, 16 * this.timeScale, this)
		},
		onDestroyed(){
			this.super$onDestroyed()
			if(Vars.state.rules.reactorExplosions && this.efficiency > 0){
				this.createExplosion()
			}
		},
		draw(){
			this.super$draw()
			if(this.efficiency > 0 && this.team != Team.derelict){
				if(!Vars.state.isPaused()){
					fraction = Mathf.lerpDelta(fraction, 0, 0.03 * this.timeScale)
				}
				Draw.z(Layer.effect)
				Lines.stroke(2.5, color)
				Draw.alpha(1 - this.progress)
				Lines.arc(this.x, this.y, this.block.size * 5, fraction, rotation)
			}
		}
	})
}
exports.hardenedAlloySmelterLarge = hardenedAlloySmelterLarge



/*========效果========*/



//效果-建筑治疗仪
const buildingHealer = Object.assign(new RegenProjector('buildingHealer'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.titanium, 30,
		Items.silicon, 25,
		FireItems.logicAlloy, 10
	)
})
exports.buildingHealer = buildingHealer



//效果-篝火
const campfire = Object.assign(new OverdriveProjector('gh'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.copper, 300,
		Items.metaglass, 220,
		Items.plastanium, 175,
		FireItems.timber, 200
	),
	size: 5,
	hasPower: false,
	hasLiquids: true,
	hasBoost: false,
	itemCapacity: 30,
	liquidCapacity: 30,
	lightRadius: 512,
	reload: 60,
	range: 512,
	useTime: 240,
	speedBoost: 3.2
})
campfire.buildType = () => extend(OverdriveProjector.OverdriveBuild, campfire, {
	updateTile(){
		this.super$updateTile()
		if(this.efficiency > 0){
			Units.nearby(this.team, this.x, this.y, this.block.range, u => {
				u.apply(FireStatuses.inspired, 60)
			})
			Units.nearbyEnemies(this.team, this.x - this.block.range, this.y - this.block.range, this.block.range * 2, this.block.range * 2, u => {
				if(u.within(this.x, this.y, this.block.range)){
					u.apply(StatusEffects.sapped, 60)
				}
			})
			if(Mathf.chanceDelta(0.02)){
				Fx.blastsmoke.at(this.x + Mathf.range(this.block.size * 4), this.y + Mathf.range(this.block.size * 4))
			}
			if(Mathf.chanceDelta(0.02)){
				Fx.generatespark.at(this.x + Mathf.range(this.block.size * 4), this.y + Mathf.range(this.block.size * 4))
			}
		}
	},
	draw(){
		this.super$draw()
		if(this.efficiency > 0 & Core.settings.getBool('showBlockRange')){
			Draw.color(Color.valueOf('feb380'), 1)
			Lines.stroke(1.5)
			Lines.circle(this.x, this.y, this.block.range)
			Draw.alpha(0.2)
			Fill.circle(this.x, this.y, this.block.range)
		}
	}
})
exports.campfire = campfire



//效果-天穹
const skyDome = Object.assign(new ForceProjector('sky-dome'), {
	buildVisibility: BuildVisibility.shown,
	category: Category.effect,
	requirements: ItemStack.with(
		Items.lead, 300,
		Items.phaseFabric, 120,
		FireItems.logicAlloy, 225,
		FireItems.hardenedAlloy, 180
	),
	armor: 4,
	size: 5,
	liquidCapacity: 20,
	shieldHealth: 3000,
	radius: 201.7,
	phaseRadiusBoost: 80,
	phaseShieldBoost: 1000,
	cooldownNormal: 5,
	cooldownLiquid: 1.2,
	cooldownBrokenBase: 3,
	coolantConsumption: 0.2
})
skyDome.itemConsumer = skyDome.consumeItem(Items.phaseFabric).boost()
skyDome.consumePower(20)
exports.skyDome = skyDome



//效果-装甲核心
/*
* @author <Uenhe>
* 实际上就是把原版的力墙抄了过来, 再根据js作相应调整, 不过还是花了我一天时间...我好蔡, 悲
*/
let CAradius = 96
let CAshieldHealth = 600
let CAcooldown = 1.2
let CAcooldownBrokenBase = 1.5
const coreArmored = extend(CoreBlock, 'zjhx', {
	canBreak(tile){
		return tile.team() == Team.derelict || Vars.state.teams.cores(tile.team()).size > 1 || Vars.state.isEditor()
	},
	canPlaceOn(tile, team){
		return true
	}, //可被放置
	init(){
		this.updateClipRadius(CAradius + 3)
		this.super$init()
	},
	setBars(){
		this.super$setBars()
		this.addBar('shield', func(e => new Bar(
			prov(() => Core.bundle.format('stat.shieldhealth') + ' ' + Math.floor(CAshieldHealth - e.buildup()) + ' / ' + CAshieldHealth),
			prov(() => Pal.accent),
			floatp(() => e.broken() ? 0 : 1 - e.buildup() / CAshieldHealth)
		).blink(Color.white)))
	}, //设置方块力墙状态栏
	setStats(){
		this.super$setStats()
		this.stats.add(Stat.shieldHealth, CAshieldHealth, StatUnit.none)
		this.stats.add(Stat.cooldownTime, Math.floor(CAshieldHealth / CAcooldownBrokenBase / 60), StatUnit.seconds)
		this.stats.add(Stat.range, CAradius / Vars.tilesize, StatUnit.blocks)
	}, //设置方块详细界面力墙属性
	drawPlace(x, y, rotation, vaild){
		this.super$drawPlace(x, y, rotation, vaild)
		Draw.color(Pal.gray)
		Lines.stroke(3)
		Lines.poly(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, 6, CAradius)
		Draw.color(Vars.player.team().color)
		Lines.stroke(1)
		Lines.poly(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, 6, CAradius)
		Draw.color()
	}, //方块放置预览力墙绘制
	requirements: ItemStack.with(
		Items.copper, 9000,
		Items.lead, 8000,
		Items.metaglass, 2500,
		Items.thorium, 3500,
		Items.silicon, 6000,
		Items.plastanium, 1750
	),
	buildVisibility: BuildVisibility.shown,
	researchCostMultiplier: 0.4
})
coreArmored.buildType = () => {
	var broken = true
	var buildup = 0
	var radscl = 0
	var warmup = 0
	var hit = 1 //初始化值, 其中buildup是当前力墙累计受到的伤害, 超过盾容(buildup>=shieldHealth)力墙就会破碎(broken=true)
	return extend(CoreBlock.CoreBuild, coreArmored, {
		onRemoved(){
			this.super$onRemoved()
			if(!broken && CAradius * radscl > 1){
				Fx.forceShrink.at(this.x, this.y, CAradius * radscl, this.team.color)
			}
		}, 
		inFogTo(viewer){
			return false
		},
		updateTile(){
			this.super$updateTile()
			if(this.team == Team.derelict){
				return
			}
			radscl = Mathf.lerpDelta(radscl, broken ? 0 : warmup, 0.05)
			if(Mathf.chanceDelta(buildup / CAshieldHealth * 0.1)){
				Fx.reactorsmoke.at(this.x + Mathf.range(Vars.tilesize / 2), this.y + Mathf.range(Vars.tilesize / 2))
			}
			warmup = Mathf.lerpDelta(warmup, 1, 0.1)
			if(buildup > 0){
				var scale = !broken ? CAcooldown : CAcooldownBrokenBase
				buildup -= this.delta() * scale
			}
			if(broken && buildup <= 0){
				broken = false
			}
			if(buildup >= CAshieldHealth && !broken){
				broken = true
				buildup = CAshieldHealth
				Fx.shieldBreak.at(this.x, this.y, CAradius * radscl, this.team.color)
			}
			if(hit > 0){
				hit -= 1 / 5 * Time.delta
			}
			if(CAradius * radscl > 0 && !broken){
				Groups.bullet.intersect(this.x - CAradius * radscl, this.y - CAradius * radscl, CAradius * radscl * 2, CAradius * radscl * 2, cons(bullet => {
					if(bullet.team != this.team && bullet.type.absorbable && Intersector.isInsideHexagon(this.x, this.y, CAradius * radscl * 2, bullet.x, bullet.y)){
						bullet.absorb()
						Fx.absorb.at(bullet)
						hit = 1
						buildup += bullet.damage
					}
				}))
			}
		},
		broken() {return broken},
		buildup() {return buildup},
		sense(sensor){
			if(sensor == LAccess.heat) return buildup
			return this.super$sense(sensor)
		}, //当逻辑sensor这个块的heat属性时, 返回buildup的值
		draw(){
			this.super$draw()
			if(this.team == Team.derelict){
				return
			}
			if(buildup > 0){
				Draw.alpha(buildup / CAshieldHealth * 0.75)
				Draw.z(Layer.blockAdditive)
				Draw.blend(Blending.additive)
				Draw.blend() //去掉这个有惊喜
				Draw.z(Layer.block)
				Draw.reset()
			}
			if(!broken){
				Draw.z(Layer.shields)
				Draw.color(this.team.color, Color.white, Mathf.clamp(hit))
				if(Core.settings.getBool('animatedshields')){
					Fill.poly(this.x, this.y, 6, CAradius * radscl)
				}else{
					Lines.stroke(1.5)
					Draw.alpha(0.09 + Mathf.clamp(0.08 * hit))
					Fill.poly(this.x, this.y, 6, CAradius * radscl)
					Draw.alpha(1)
					Lines.poly(this.x, this.y, 6, CAradius * radscl)
					Draw.reset()
				}
			}
			Draw.reset()
		},
		write(write){
			this.super$write(write)
			write.bool(broken)
			write.f(buildup)
			write.f(radscl)
			write.f(warmup)
		},
		read(read, revision){
			this.super$read(read, revision)
			broken = read.bool()
			buildup = read.f()
			radscl = read.f()
			warmup = read.f()
		} //write和read部分可以让值被存档&读取
	})
}
exports.coreArmored = coreArmored



//效果-Javelin机甲平台
//移除核心不清物品代码部分来自创世神mod
const javelinPad = extend(CoreBlock, 'javelinPad', {
	canBreak(tile){
		return tile.team() == Team.derelict || Vars.state.teams.cores(tile.team()).size > 1 || Vars.state.isEditor()
	},
	canPlaceOn(tile, team, rotation){
		return true
	},
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.lead, 350,
		Items.titanium, 500,
		Items.silicon, 450,
		Items.plastanium, 400,
		Items.phaseFabric, 200
	),
})
javelinPad.buildType = () => {
	return extend(CoreBlock.CoreBuild, javelinPad, {
		onRemoved(){
			Vars.state.teams.unregisterCore(this)
		}
	})
}	
exports.javelinPad = javelinPad



//效果-复合装卸器
//低帧装卸代码部分来自创世神mod
const compositeUnloader = extend(DirectionalUnloader, 'composite-unloader', {
	setStats(){
		this.super$setStats()
		this.stats.remove(Stat.speed)
		this.stats.add(Stat.speed, this.block.speed, StatUnit.itemsSecond)
	},
	buildVisibility: BuildVisibility.shown,
	category: Category.effect,
	requirements: ItemStack.with(
		Items.metaglass, 15,
		Items.titanium, 30,
		Items.thorium, 15,
		Items.silicon, 20
	),
	health: 85,
	size: 1,
	underBullets: true,
	speed: 25,
	allowCoreUnload: true
})
compositeUnloader.buildType = () => {
	var counter = 0
	return extend(DirectionalUnloader.DirectionalUnloaderBuild, compositeUnloader, {
		updateTile(){
			counter += this.edelta()
			while(counter >= 30 / this.block.speed){
				this.unloadTimer = this.block.speed
				this.super$updateTile()
				counter -= 30 / this.block.speed
			}
		}
	})
}
exports.compositeUnloader = compositeUnloader



//效果-建造指示器
const buildIndicator = Object.assign(new BuildTurret('jzzsq'), {
	buildVisibility: BuildVisibility.shown,
	requirements: ItemStack.with(
		Items.lead, 120,
		Items.thorium, 50,
		FireItems.logicAlloy, 30
	)
})
exports.buildIndicator = buildIndicator
