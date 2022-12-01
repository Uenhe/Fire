const lib = require('misc/lib')

const bl = extend(Item, 'bl', Color.valueOf("FFFFFF"), {})
exports.bl = bl
lib.addToResearch(bl, {parent: 'sand',})

const dt = extend(Item, 'dt', Color.valueOf("C78872"), {})
dt.charge = 1.2
dt.frames = 5
dt.transitionFrames = 1
dt.frameTime = 5
exports.dt = dt
lib.addToResearch(dt, {parent: 'surge-alloy',})

const zzhhhj = extend(Item, 'zzhhhj', Color.valueOf("B60C13"), {})
zzhhhj.explosiveness = 0.8
zzhhhj.flammability = 1.4
exports.zzhhhj = zzhhhj
lib.addToResearch(zzhhhj, {parent: 'blast-compound',})

const hhhj = extend(Item, 'hhhj', Color.valueOf("EC1C24"), {})
hhhj.explosiveness = 0.1
hhhj.flammability = 2.8
exports.hhhj = hhhj
lib.addToResearch(hhhj, {parent: 'zzhhhj',})

const jmbl = extend(Item, 'jmbl', Color.valueOf("FFFFFF"), {})
exports.jmbl = jmbl
lib.addToResearch(jmbl, {parent: 'metaglass',})

const mc = extend(Item, 'mc', Color.valueOf("A14B08"), {})
mc.flammability = 0.85
exports.mc = mc
lib.addToResearch(mc, {parent: 'spore-pod',})

const xrt = extend(Item, 'xrt', Color.valueOf("72001B"), {})
xrt.flammability = 0.4
xrt.frames = 13
xrt.transitionFrames = 1
xrt.frameTime = 3
exports.xrt = xrt
lib.addToResearch(xrt, {parent: 'neoplasm',})

const yjdk = extend(Item, 'yjdk', Color.valueOf("FFF200"), {})
exports.yjdk = yjdk
lib.addToResearch(yjdk, {parent: 'blast-compound',})

const yjbld = extend(Item, 'yjbld', Color.valueOf("EC1C24"), {})
yjbld.explosiveness = 0.2
yjbld.flammability = 1.8
exports.yjbld = yjbld
lib.addToResearch(yjbld, {parent: 'yjdk',})
