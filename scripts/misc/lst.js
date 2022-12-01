const lib = require("misc/lib")
const sS = require("misc/sectorSize")

const lst = new JavaAdapter(Planet, {
    load() {
        this.meshLoader = prov(() => new HexMesh(lst, 4))
        this.super$load()
    }
}, "lst", Planets.sun, 1)
sS.planetGrid(lst, 3.3)
lst.generator = new SerpuloPlanetGenerator()
lst.bloom = false
lst.accessible = true
lst.rotateTime = 12000
lst.visible = true
lst.alwaysUnlocked = true
lst.clearSectorOnLose = true
lst.enemyCoreSpawnReplace = false
lst.allowLaunchSchematics = false
lst.allowLaunchLoadout = false
lst.allowSectorInvasion = false
lst.allowWaveSimulation = false
lst.enemyCoreSpawnReplace = false
lst.prebuildBase = false
lst.orbitRadius = 64
lst.startSector = 0
lst.defaultCore = Blocks.coreShard
lst.atmosphereColor = Color.valueOf("4d1b7e")
lst.atmosphereRadIn = 0.05
lst.atmosphereRadOut = 0.5
lst.iconColor = Color.valueOf("8e4dee")

const jljd = new SectorPreset("jljd", lst, 0)
jljd.difficulty = 7
exports.jljd = jljd
lib.addToResearch(jljd, {
	parent: 'planetaryTerminal',
	objectives: Seq.with(
		new Objectives.SectorComplete(SectorPresets.planetaryTerminal))
})

const hspy = new SectorPreset("hspy", lst, 94)
hspy.difficulty = 6
hspy.addStartingItems = true
hspy.captureWave = 30
exports.hspy = hspy
lib.addToResearch(hspy, {
	parent: 'jljd',
	objectives: Seq.with(
		new Objectives.SectorComplete(jljd))
})

const lhyj = new SectorPreset("lhyj", lst, 15)
lhyj.difficulty = 6
lhyj.addStartingItems = true;
lhyj.captureWave = 30;
exports.lhyj = lhyj;
lib.addToResearch(lhyj, {
	parent: 'hspy',
	objectives: Seq.with(
		new Objectives.SectorComplete(hspy))
})
