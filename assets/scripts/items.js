const glass = new Item("bl", Color.valueOf("ffffff"));

const mirrorglass = new Item("jmbl", Color.valueOf("ffffff"));

const impurityKindlingAlloy = Object.assign(new Item("zzhhhj", Color.valueOf("b60c13")), {
    explosiveness: 0.6,
    flammability: 1.15
});

const kindlingAlloy = Object.assign(new Item("hhhj", Color.valueOf("ec1c24")), {
    explosiveness: 0.1,
    flammability: 2.8
});

const conductor = Object.assign(new Item("dt", Color.valueOf("c78872")), {
    charge: 1.2,
    frames: 5,
    transitionFrames: 1,
    frameTime: 5
});

const logicAlloy = Object.assign(new Item("logic-alloy", Color.valueOf("814e25")), {
    charge: 0.3
});

const detonationCompound = Object.assign(new Item("detonation-compound", Color.valueOf("fff220")), {
    explosiveness: 1.6,
    flammability: 1.1
});

const flamefluidCrystal = Object.assign(new Item("lhjj", Color.valueOf("ec1c24")), {
    explosiveness: 0.25,
    flammability: 1.2
});

const timber = Object.assign(new Item("mc", Color.valueOf("a14b08")), {
    flammability: 0.85
});

const flesh = Object.assign(new Item("flesh", Color.valueOf("72001b")), {
    flammability: 0.3,
    frames: 13,
    transitionFrames: 1,
    frameTime: 3
});

const hardenedAlloy = Object.assign(new Item("hardened-alloy", Color.valueOf("48427f")), {
    healthScaling: 1.35
});

const magneticAlloy = Object.assign(new Item("magnetic-alloy", Color.valueOf("bfba95")), {
    charge: 2.1,
    frames: 22,
    transitionFrames: 1,
    frameTime: 2
});

module.exports = {
    glass: glass,
    conductor: conductor,
    impurityKindlingAlloy: impurityKindlingAlloy,
    kindlingAlloy: kindlingAlloy,
    flamefluidCrystal: flamefluidCrystal,
    mirrorglass: mirrorglass,
    timber: timber,
    flesh: flesh,
    logicAlloy: logicAlloy,
    detonationCompound: detonationCompound,
    hardenedAlloy: hardenedAlloy,
    magneticAlloy: magneticAlloy
}