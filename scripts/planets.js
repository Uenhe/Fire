const lisertar = Object.assign(new Planet('lst', Planets.sun, 1, 3), {
	meshLoader: () => new HexMesh(lisertar, 7.5),
	cloudMeshLoader: () => new MultiMesh(
		new HexSkyMesh(lisertar, 11, 0.15, 0.13, 5, Color.valueOf("5279f0bb"), 2, 0.45, 0.9, 0.38),
		new HexSkyMesh(lisertar, 1, 0.6, 0.16, 5, Color.white.cpy().lerp(Color.valueOf("5279f0bb"), 0.55), 2, 0.45, 1, 0.41)
	),
	generator: new SerpuloPlanetGenerator(),
	bloom: false,
	accessible: true, //是否在星球选择列表显示
	rotateTime: 12000,
	visible: true, //是否在宇宙内可见
	alwaysUnlocked: true,
	clearSectorOnLose: true, //是否在区块丢失时重置区块
	enemyCoreSpawnReplace: false,
	allowLaunchSchematics: false, //是否允许核心蓝图
	allowLaunchLoadout: false, //是否允许自定义物资装运
	allowSectorInvasion: false, //是否开启敌基区块侵略
	allowWaveSimulation: true, //是否允许后台模拟, 留意如果false, 将不能在打区块时访问其他本星球区块
	prebuildBase: false, //是否在核心降落时, 以核心为中心生成预设方块, false可以解决建筑断连问题
	orbitRadius: 64,
	startSector: 0, //起始区块编号
	sectorSeed: 3,
	defaultCore: Blocks.coreShard,
	atmosphereColor: Color.valueOf('1a3db1'),
	atmosphereRadIn: 0.05,
	atmosphereRadOut: 0.5,
	iconColor: Color.valueOf('5b6fff')
})
lisertar.hiddenItems.addAll(Items.erekirItems).removeAll(Items.serpuloItems)
exports.lisertar = lisertar

const landingBase = Object.assign(new SectorPreset('jljd', lisertar, 0), {
	difficulty: 6
})
exports.landingBase = landingBase

const darksandPlain = Object.assign(new SectorPreset('hspy', lisertar, 94), {
	difficulty: 7,
	captureWave: 15,
	addStartingItems: true
})
exports.darksandPlain = darksandPlain

const cornerOfZero = Object.assign(new SectorPreset('lhyj', lisertar, 15), {
	difficulty: 6,
	captureWave: 30,
	addStartingItems: true
})
exports.cornerOfZero = cornerOfZero

const beachLanding = Object.assign(new SectorPreset('htdl', lisertar, 183), {
	difficulty: 6,
	addStartingItems: true
})
exports.beachLanding = beachLanding

const darkWorkshop = Object.assign(new SectorPreset('hacj', lisertar, 186), {
	difficulty: 8,
	addStartingItems: true
})
exports.darkWorkshop = darkWorkshop

const sporeFiord = Object.assign(new SectorPreset('bzxw', lisertar, 199), {
	difficulty: 8,
	captureWave: 40,
	addStartingItems: true
})
exports.sporeFiord = sporeFiord

const scorchingVolcano = Object.assign(new SectorPreset('zrhs', lisertar, 180), {
	difficulty: 8,
	captureWave: 50,
	addStartingItems: true
})
exports.scorchingVolcano = scorchingVolcano

const eternalriverStronghold = Object.assign(new SectorPreset('hhys', lisertar, 34), {
	difficulty: 8,
	addStartingItems: true
})
exports.eternalriverStronghold = eternalriverStronghold

const chillyMountains = Object.assign(new SectorPreset('lfsm', lisertar, 168), {
	difficulty: 9,
	captureWave: 17,
	addStartingItems: true
})
exports.chillyMountains = chillyMountains
