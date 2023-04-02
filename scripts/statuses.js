const frostbite = extend(StatusEffect, 'frostbite', {})
frostbite.color = Color.valueOf('ff0000')
frostbite.damage = 8 / 60
frostbite.speedMultiplier = 0.55
frostbite.healthMultiplier = 0.75
frostbite.transitionDamage = 22
frostbite.effect = new Effect(40, e => {
	Draw.color(Color.valueOf('f0ffff'))
	Angles.randLenVectors(e.id, 2, 1 + e.fin() * 2, (x, y) => {
		Fill.circle(e.x + x, e.y + y, e.fout() * 1.2)
	})
})
frostbite.init(run(() => {
	frostbite.opposite(StatusEffects.melting, StatusEffects.burning)
	frostbite.affinity(StatusEffects.blasted, (unit, result, time) => {
		unit.damagePierce(transitionDamage)
	})
}))
exports.frostbite = frostbite

const inspired = new StatusEffect('inspired')
inspired.color = Pal.accent
inspired.healthMultiplier = 1.15
inspired.speedMultiplier = 1.4
inspired.reloadMultiplier = 1.2
inspired.buildSpeedMultiplier = 2
inspired.effect = Fx.overdriven
exports.inspired = inspired
