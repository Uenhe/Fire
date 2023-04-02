const lib = require('misc/lib')
const FireBlocks = require('blocks')
const FireItems = require('items')
const FireUnits = require('units')

const lst = new Planet('lst', Planets.sun, 1, 3)
lst.meshLoader = prov(() => new HexMesh(lst, 7.5))
lst.cloudMeshLoader = prov(() => new MultiMesh(
	new HexSkyMesh(lst, 11, 0.15, 0.13, 5, Color.valueOf("5279f0bb"), 2, 0.45, 0.9, 0.38),
	new HexSkyMesh(lst, 1, 0.6, 0.16, 5, Color.white.cpy().lerp(Color.valueOf("5279f0bb"), 0.55), 2, 0.45, 1, 0.41)
))
lst.generator = new SerpuloPlanetGenerator()
lst.bloom = false
lst.accessible = true //是否在星球选择列表显示
lst.rotateTime = 12000
lst.visible = true //是否在宇宙内可见
lst.alwaysUnlocked = true
lst.clearSectorOnLose = true //是否在区块丢失时重置区块
lst.enemyCoreSpawnReplace = false
lst.allowLaunchSchematics = false //是否允许核心蓝图
lst.allowLaunchLoadout = false //是否允许自定义物资装运
lst.allowSectorInvasion = false //是否开启敌基区块侵略
lst.allowWaveSimulation = true //是否允许后台模拟, 留意如果false, 将不能在打区块时访问其他本星球区块
lst.prebuildBase = false //是否在核心降落时, 以核心为中心生成预设方块, false可以解决建筑断连问题
lst.orbitRadius = 64
lst.startSector = 0 //起始区块编号
lst.sectorSeed = 3
lst.defaultCore = Blocks.coreShard
lst.atmosphereColor = Color.valueOf('1a3db1')
lst.atmosphereRadIn = 0.05
lst.atmosphereRadOut = 0.5
lst.iconColor = Color.valueOf('5b6fff')
lst.hiddenItems.addAll(Items.erekirItems).removeAll(Items.serpuloItems)

const jljd = new SectorPreset('jljd', lst, 0)
jljd.difficulty = 6
exports.jljd = jljd
lib.addToResearch(jljd, {
	parent: 'planetaryTerminal',
	objectives: Seq.with(
		new Objectives.SectorComplete(SectorPresets.planetaryTerminal)
	)
})

const hspy = new SectorPreset('hspy', lst, 94)
hspy.difficulty = 7
hspy.addStartingItems = true
hspy.captureWave = 15
exports.hspy = hspy
lib.addToResearch(hspy, {
	parent: 'jljd',
	objectives: Seq.with(
		new Objectives.SectorComplete(jljd),
		new Objectives.Research(FireBlocks.dmj),
		new Objectives.Research(FireBlocks.yg)
	)
})

const bzxw = new SectorPreset('bzxw', lst, 199)
bzxw.difficulty = 8
bzxw.addStartingItems = true
bzxw.captureWave = 40
exports.bzxw = bzxw
lib.addToResearch(bzxw, {
	parent: 'hspy',
	objectives: Seq.with(
		new Objectives.SectorComplete(hspy)
	)
})

const lhyj = new SectorPreset('lhyj', lst, 15)
lhyj.difficulty = 6
lhyj.addStartingItems = true
lhyj.captureWave = 30
exports.lhyj = lhyj
lib.addToResearch(lhyj, {
	parent: 'hspy',
	objectives: Seq.with(
		new Objectives.SectorComplete(hspy)
	)
})

const hhys = new SectorPreset('hhys', lst, 34)
hhys.difficulty = 8
hhys.addStartingItems = true
exports.hhys = hhys
lib.addToResearch(hhys, {
	parent: 'hspy',
	objectives: Seq.with(
		new Objectives.SectorComplete(hspy),
		new Objectives.Research(FireUnits.sh)
	)
})

const zrhs = new SectorPreset('zrhs', lst, 180)
zrhs.difficulty = 8
zrhs.addStartingItems = true
zrhs.captureWave = 50
exports.zrhs = zrhs
lib.addToResearch(zrhs, {
	parent: 'bzxw',
	objectives: Seq.with(
		new Objectives.SectorComplete(bzxw),
		new Objectives.Research(FireBlocks.fhcsd)
	)
})

const htdl = new SectorPreset('htdl', lst, 183)
htdl.difficulty = 6
htdl.addStartingItems = true
exports.htdl = htdl
lib.addToResearch(htdl, {
	parent: 'lhyj',
	objectives: Seq.with(
		new Objectives.SectorComplete(lhyj)
	)
})

const lfsm = new SectorPreset('lfsm', lst, 168)
lfsm.difficulty = 9
lfsm.addStartingItems = true
lfsm.captureWave = 17
exports.lfsm = lfsm
lib.addToResearch(lfsm, {
	parent: 'hhys',
	objectives: Seq.with(
		new Objectives.SectorComplete(hhys),
		new Objectives.Produce(FireItems.dt)
	)
})

const hacj = new SectorPreset('hacj', lst, 186)
hacj.difficulty = 8
hacj.addStartingItems = true
exports.hacj = hacj
lib.addToResearch(hacj, {
	parent: 'htdl',
	objectives: Seq.with(
		new Objectives.SectorComplete(htdl),
		new Objectives.Research(FireUnits.ws)
	)
})
