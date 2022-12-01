const lib = require('misc/lib')

const yd = extend(Liquid, 'yd', Color.valueOf("F0FFFF"), {})
yd.heatCapacity = 2.5
yd.temperature = -2.1
yd.viscosity = 0.4
yd.effect = StatusEffects.freezing
yd.barColor = Color.valueOf("F0FFFF")
//yd.lightColor = Color.valueOf("0097F5").a(0.2)
yd.boilPoint = 0.55

exports.yd = yd
lib.addToResearch(yd, {parent: 'cryofluid',})
