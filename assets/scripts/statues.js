const frostbite = extend(StatusEffect, "frostbite", {
    color: Color.valueOf("ff0000"),
    damage: 8/60,
    speedMultiplier: 0.55,
    healthMultiplier: 0.75,
    transitionDamage: 22,
    effect: new Effect(40, e => {
        Draw.color(frostbite.color);
        Angles.randLenVectors(e.id, 2, 1 + e.fin() * 2, (x, y) => {
            Fill.circle(e.x + x, e.y + y, e.fout() * 1.2)
        })
    })
});
frostbite.init(() => {
    frostbite.opposite(StatusEffects.melting, StatusEffects.burning);
    frostbite.affinity(StatusEffects.blasted, (unit, result, time) => {
        unit.damagePierce(frostbite.transitionDamage)
    })
});

const inspired = Object.assign(new StatusEffect("inspired"), {
    color: Pal.accent,
    healthMultiplier: 1.15,
    speedMultiplier: 1.4,
    reloadMultiplier: 1.2,
    buildSpeedMultiplier: 2,
    effectChance: 0.07,
    effect: Fx.overdriven
});

const overgrown = Object.assign(new NeoplasmStatusEffect("overgrown"), {
    color: Liquids.neoplasm.color,
    damage: 36/60,
    speedMultiplier: 0.9,
    effectChance: 0.1,
    healthMultiplier: 1.1,
    unfHealthMultiplier: 0.9,
    regenPercent: 0.0004,
    maxShield: 1200,
    effect: new Effect(40, e => {
        Draw.color(overgrown.color);
        Angles.randLenVectors(e.id, 2, 1 + e.fin() * 2, (x, y) => {
            Fill.circle(e.x + x, e.y + y, e.fout() * 1.2)
        })
    })
});

const disintegrated = Object.assign(new ArmorPierceStatusEffect("disintegrated"), {
    damage: 300/60,
    speedMultiplier: 0.6,
    maxArmorReduce: 10,
    effectChance: 0.2,
    parentizeEffect: true,
    effect: Fx.unitShieldBreak
});

module.exports = {
    frostbite: frostbite,
    inspired: inspired,
    overgrown: overgrown,
    disintegrated: disintegrated
}
