const lisertar = Object.assign(new Planet("lst", Planets.sun, 1, 3), {
    meshLoader: () => new HexMesh(lisertar, 8),
    cloudMeshLoader: () => new MultiMesh(
        new HexSkyMesh(lisertar, 11, 0.15, 0.13, 5, Color.valueOf("5279f0bb"), 2, 0.45, 0.9, 0.38),
        new HexSkyMesh(lisertar, 1, 0.6, 0.16, 5, Color.white.cpy().lerp(Color.valueOf("5279f0bb"), 0.55), 2, 0.45, 1, 0.41)
    ),
    generator: new SerpuloPlanetGenerator(),
    bloom: false,
    accessible: true,
    rotateTime: 12000,
    visible: true,
    alwaysUnlocked: true,
    clearSectorOnLose: true,
    enemyCoreSpawnReplace: false,
    allowLaunchSchematics: false,
    allowLaunchLoadout: false,
    allowSectorInvasion: false,
    allowWaveSimulation: true,
    prebuildBase: false,
    orbitRadius: 64,
    startSector: 0,
    sectorSeed: 3,
    defaultCore: Blocks.coreShard,
    atmosphereColor: Color.valueOf("1a3db1"),
    atmosphereRadIn: 0.05,
    atmosphereRadOut: 0.5,
    iconColor: Color.valueOf("5b6fff")
});
lisertar.hiddenItems.addAll(Items.erekirItems).removeAll(Items.serpuloItems);

const landingBase = Object.assign(new SectorPreset("jljd", lisertar, 0), {
    difficulty: 6
});

const darksandPlain = Object.assign(new SectorPreset("hspy", lisertar, 94), {
    difficulty: 7,
    captureWave: 15,
    addStartingItems: true
});

const cornerOfZero = Object.assign(new SectorPreset("lhyj", lisertar, 15), {
    difficulty: 6,
    captureWave: 30,
    addStartingItems: true
});

const beachLanding = Object.assign(new SectorPreset("htdl", lisertar, 183), {
    difficulty: 6,
    addStartingItems: true
});

const darkWorkshop = Object.assign(new SectorPreset("hacj", lisertar, 186), {
    difficulty: 8,
    addStartingItems: true
});

const sporeFiord = Object.assign(new SectorPreset("bzxw", lisertar, 199), {
    difficulty: 8,
    captureWave: 40,
    addStartingItems: true
});

const scorchingVolcano = Object.assign(new SectorPreset("zrhs", lisertar, 180), {
    difficulty: 8,
    captureWave: 50,
    addStartingItems: true
});

const eternalRiverStronghold = Object.assign(new SectorPreset("hhys", lisertar, 34), {
    difficulty: 8,
    addStartingItems: true
});

const chillyMountains = Object.assign(new SectorPreset("lfsm", lisertar, 168), {
    difficulty: 9,
    captureWave: 17,
    addStartingItems: true
});

module.exports = {
    lisertar: lisertar,
    landingBase: landingBase,
    darksandPlain: darksandPlain,
    cornerOfZero: cornerOfZero,
    beachLanding: beachLanding,
    darkWorkshop: darkWorkshop,
    sporeFiord: sporeFiord,
    scorchingVolcano: scorchingVolcano,
    eternalRiverStronghold: eternalRiverStronghold,
    chillyMountains: chillyMountains
}
