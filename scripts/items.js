const lib = require('misc/lib')

const bl = new Item('bl', Color.valueOf('ffffff'))
exports.bl = bl
lib.addToResearch(bl, {parent: 'sand'})

const dt = new Item('dt', Color.valueOf('c78872'))
dt.charge = 1.2
dt.frames = 5
dt.transitionFrames = 1
dt.frameTime = 5
exports.dt = dt
lib.addToResearch(dt, {parent: 'surge-alloy'})

const zzhhhj = new Item('zzhhhj', Color.valueOf('b60c13'))
zzhhhj.explosiveness = 0.6
zzhhhj.flammability = 1.15
exports.zzhhhj = zzhhhj
lib.addToResearch(zzhhhj, {parent: 'blast-compound'})

const hhhj = new Item('hhhj', Color.valueOf('ec1c24'))
hhhj.explosiveness = 0.1
hhhj.flammability = 2.8
exports.hhhj = hhhj
lib.addToResearch(hhhj, {parent: 'zzhhhj'})

const lhjj = new Item('lhjj', Color.valueOf('ec1c24'))
lhjj.explosiveness = 0.25
lhjj.flammability = 1.2
exports.lhjj = lhjj
lib.addToResearch(lhjj, {parent: 'slag'})

const jmbl = new Item('jmbl', Color.valueOf('ffffff'))
exports.jmbl = jmbl
lib.addToResearch(jmbl, {parent: 'metaglass'})

const mc = new Item('mc', Color.valueOf('a14b08'))
mc.flammability = 0.85
exports.mc = mc
lib.addToResearch(mc, {parent: 'water'})

const xrt = new Item('xrt', Color.valueOf('72001b'))
xrt.flammability = 0.3
xrt.frames = 13
xrt.transitionFrames = 1
xrt.frameTime = 3
exports.xrt = xrt
lib.addToResearch(xrt, {parent: 'neoplasm'})

const logicAlloy = new Item('logic-alloy', Color.valueOf('814e25'))
logicAlloy.charge = 0.3
exports.logicAlloy = logicAlloy
lib.addToResearch(logicAlloy, {parent: 'silicon'})

const detonationCompound = new Item('detonation-compound', Color.valueOf('fff220'))
detonationCompound.explosiveness = 1.6
detonationCompound.flammability = 1.1
exports.detonationCompound = detonationCompound
lib.addToResearch(detonationCompound, {parent: 'logic-alloy'})

const hardenedAlloy = new Item('hardened-alloy', Color.valueOf('48427f'))
exports.hardenedAlloy = hardenedAlloy
lib.addToResearch(hardenedAlloy, {parent: 'hhhj'})
