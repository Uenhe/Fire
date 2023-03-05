const lib = require('misc/lib')
const FireStatuses = require('statuses')

const yd = extend(Liquid, 'yd', Color.valueOf('f0ffff'), {})
yd.heatCapacity = 2.5
yd.temperature = -2.1
yd.viscosity = 0.4
yd.effect = FireStatuses.ds
yd.barColor = Color.valueOf('f0ffff')
yd.gasColor = Color.valueOf('c1e8f5')
yd.lightColor = Color.valueOf('0097f532')
yd.boilPoint = 0.55
exports.yd = yd
lib.addToResearch(yd, {parent: 'cryofluid'})
