const glass = new Item('bl', Color.valueOf('ffffff'))
exports.glass = glass

const conductor = new Item('dt', Color.valueOf('c78872'))
Object.assign(conductor, {
	charge: 1.2,
	frames: 5,
	transitionFrames: 1,
	frameTime: 5
})
exports.conductor = conductor

const impurityKindlingAlloy = new Item('zzhhhj', Color.valueOf('b60c13'))
Object.assign(impurityKindlingAlloy, {
	explosiveness: 0.6,
	flammability: 1.15
})
exports.impurityKindlingAlloy = impurityKindlingAlloy

const kindlingAlloy = new Item('hhhj', Color.valueOf('ec1c24'))
Object.assign(kindlingAlloy, {
	explosiveness: 0.1,
	flammability: 2.8
})
exports.kindlingAlloy = kindlingAlloy

const flamefluidCrystal = new Item('lhjj', Color.valueOf('ec1c24'))
Object.assign(flamefluidCrystal, {
	explosiveness: 0.25,
	flammability: 1.2
})
exports.flamefluidCrystal = flamefluidCrystal

const mirrorglass = new Item('jmbl', Color.valueOf('ffffff'))
exports.mirrorglass = mirrorglass

const timber = new Item('mc', Color.valueOf('a14b08'))
Object.assign(timber, {
	flammability: 0.85
})
exports.timber = timber

const flesh = new Item('flesh', Color.valueOf('72001b'))
Object.assign(flesh, {
	flammability: 0.3,
	frames: 13,
	transitionFrames: 1,
	frameTime: 3
})
exports.flesh = flesh

const logicAlloy = new Item('logic-alloy', Color.valueOf('814e25'))
Object.assign(logicAlloy, {
	charge: 0.3
})
exports.logicAlloy = logicAlloy

const detonationCompound = new Item('detonation-compound', Color.valueOf('fff220'))
Object.assign(detonationCompound, {
	explosiveness: 1.6,
	flammability: 1.1
})
exports.detonationCompound = detonationCompound

const hardenedAlloy = new Item('hardened-alloy', Color.valueOf('48427f'))
Object.assign(hardenedAlloy, {
	healthScaling: 1.35
})
exports.hardenedAlloy = hardenedAlloy
