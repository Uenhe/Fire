const FireFx = require('fx')

const ds = new JavaAdapter(StatusEffect, {}, 'ds')
ds.color = Color.valueOf('ff0000')
ds.damage = 8 / 60
ds.speedMultiplier = 0.55
ds.healthMultiplier = 0.75
ds.effect = FireFx.FXds
ds.transitionDamage = 22
ds.init(run(() => {
	ds.opposite(StatusEffects.melting, StatusEffects.burning)
	ds.affinity(StatusEffects.blasted, (unit, result, time) => {})
}))
exports.ds = ds
