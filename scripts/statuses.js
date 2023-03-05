const FireFx = require('fx')

const ds = extend(StatusEffect, 'ds', {})
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

const inspired = extend(StatusEffect, 'inspired', {})
inspired.color = Pal.accent
inspired.healthMultiplier = 1.15
inspired.speedMultiplier = 1.4
inspired.reloadMultiplier = 1.2
inspired.buildSpeedMultiplier = 2
inspired.effect = Fx.overdriven
inspired.effectChance = 0.4
exports.inspired = inspired
