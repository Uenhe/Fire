const lib = require('misc/lib')

const bl = extend(Item, 'bl', Color.valueOf('ffffff'), {})
exports.bl = bl
lib.addToResearch(bl, {parent: 'sand',})

const dt = extend(Item, 'dt', Color.valueOf('c78872'), {})
dt.charge = 1.2
dt.frames = 5
dt.transitionFrames = 1
dt.frameTime = 5
exports.dt = dt
lib.addToResearch(dt, {parent: 'surge-alloy',})

const zzhhhj = extend(Item, 'zzhhhj', Color.valueOf('b60c13'), {})
zzhhhj.explosiveness = 0.6
zzhhhj.flammability = 1.15
exports.zzhhhj = zzhhhj
lib.addToResearch(zzhhhj, {parent: 'blast-compound',})

const hhhj = extend(Item, 'hhhj', Color.valueOf('ec1c24'), {})
hhhj.explosiveness = 0.1
hhhj.flammability = 2.8
exports.hhhj = hhhj
lib.addToResearch(hhhj, {parent: 'zzhhhj',})

const lhjj = extend(Item, 'lhjj', Color.valueOf('ec1c24'), {})
lhjj.explosiveness = 0.25
lhjj.flammability = 1.2
exports.lhjj = lhjj
lib.addToResearch(lhjj, {parent: 'slag',})

const jmbl = extend(Item, 'jmbl', Color.valueOf('ffffff'), {})
exports.jmbl = jmbl
lib.addToResearch(jmbl, {parent: 'metaglass',})

const mc = extend(Item, 'mc', Color.valueOf('a14b08'), {})
mc.flammability = 0.85
exports.mc = mc
lib.addToResearch(mc, {parent: 'spore-pod',})

const xrt = extend(Item, 'xrt', Color.valueOf('72001b'), {})
xrt.flammability = 0.3
xrt.frames = 13
xrt.transitionFrames = 1
xrt.frameTime = 3
exports.xrt = xrt
lib.addToResearch(xrt, {parent: 'neoplasm',})

const yjdk = extend(Item, 'yjdk', Color.valueOf('fff200'), {})
exports.yjdk = yjdk
lib.addToResearch(yjdk, {parent: 'blast-compound',})

const yjbld = extend(Item, 'yjbld', Color.valueOf('ec1c24'), {})
yjbld.explosiveness = 1.6
yjbld.flammability = 0.5
exports.yjbld = yjbld
lib.addToResearch(yjbld, {parent: 'yjdk',})

const ybd = extend(Item, 'ybd', Color.valueOf('fff220'), {})
ybd.explosiveness = 1.4
ybd.flammability = 1.1
ybd.transitionFrames = 1
ybd.frameTime = 4
ybd.frames = 11
exports.ybd = ybd
lib.addToResearch(ybd, {parent: 'yjdk',})
