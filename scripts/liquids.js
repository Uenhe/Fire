const FireStatuses = require('statuses')

const liquidNitrogen = new Liquid('liquid-nitrogen', Color.valueOf('f0ffff'))
Object.assign(liquidNitrogen, {
	heatCapacity: 2.5,
	temperature: -2.1,
	viscosity: 0.4,
	boilPoint: -1.7,
	effect: FireStatuses.frostbite,
	barColor: Color.valueOf('f0ffff'),
	gasColor: Color.valueOf('c1e8f5'),
	lightColor: Color.valueOf('0097f532')
})
exports.liquidNitrogen = liquidNitrogen
