const FireAbilities = require('misc/abilities')

//陆军-爬辅

//爬辅-守护
const sh = Object.assign(new UnitType('sh'), {
	constructor: () => extend(UnitTypes.atrax.constructor.get().class,{}),
	hovering: true,
	health: 140,
	armor: 3,
	speed: 0.6,
	drag: 0.1,
	hitSize: 8,
	rotateSpeed: 4,
	buildSpeed: 1,
	itemCapacity: 20,
	stepShake: 1,
	shadowElevation: 0.1,
	groundLayer: Layer.legUnit - 1,
	allowLegStep: true,
	lockLegBase: true,
	legContinuousMove: true,
	canAttack: false,
	targetable: false,
	legCount: 4,
	legLength: 8,
	legExtension: 0,
	legBaseOffset: 2,
	legPairOffset: 3,
	legMinLength: 0.2,
	legMaxLength: 1.1,
	legLengthScl: 1.6,
	legStraightness: 0.3,
	legMoveSpace: 1,
	legSpeed: 0.2,
	legGroupSize: 3,
	rippleScale: 0.2
})
sh.abilities.add(
	new ForceFieldAbility(44, 0.25, 200, 400)
)
exports.sh = sh

//爬辅-抗御
const ky = Object.assign(new UnitType('ky'), {
	constructor: () => extend(UnitTypes.atrax.constructor.get().class,{}),
	hovering: true,
	health: 420,
	armor: 5,
	speed: 0.54,
	drag: 0.1,
	hitSize: 12,
	rotateSpeed: 3.6,
	buildSpeed: 1.2,
	itemCapacity: 40,
	stepShake: 1,
	shadowElevation: 0.1,
	groundLayer: Layer.legUnit - 1,
	allowLegStep: true,
	lockLegBase: true,
	legContinuousMove: true,
	canAttack: false,
	targetable: false,
	legCount: 4,
	legLength: 10,
	legExtension: 0,
	legBaseOffset: 2,
	legPairOffset: 3,
	legMinLength: 0.2,
	legMaxLength: 1.1,
	legLengthScl: 1.6,
	legMoveSpace: 1,
	legSpeed: 0.2,
	rippleScale: 0.3
})
ky.abilities.add(
	new ForceFieldAbility(54, 0.35, 280, 340),
	FireAbilities.RegenFieldAbility(1, 40, Color.valueOf('8cfffb'), 120, 3)
)
exports.ky = ky

//爬辅-卫戍
const ws = Object.assign(new UnitType('ws'), {
	constructor: () => extend(UnitTypes.atrax.constructor.get().class,{}),
	hovering: true,
	health: 930,
	armor: 6,
	speed: 0.45,
	drag: 0.3,
	hitSize: 16,
	rotateSpeed: 2.7,
	buildSpeed: 2,
	itemCapacity: 50,
	stepShake: 1,
	shadowElevation: 0.1,
	groundLayer: Layer.legUnit - 1,
	allowLegStep: true,
	lockLegBase: true,
	legContinuousMove: true,
	canAttack: false,
	targetable: false,
	legCount: 6,
	legLength: 22,
	legExtension: 2,
	legBaseOffset: 8,
	legPairOffset: 4,
	legMinLength: 0.2,
	legMaxLength: 1.1,
	legLengthScl: 1.6,
	legMoveSpace: 1,
	legSpeed: 0.2,
	legGroupSize: 3,
	rippleScale: 0.4
})
ws.abilities.add(
	new ForceFieldAbility(72, 0.6, 400, 300, 4, 45),
	new StatusFieldAbility(StatusEffects.overclock, 360, 360, 80)
)
ws.weapons.add(Object.assign(new PointDefenseWeapon('fire-point-defense-weapon'), {
	reload: 10,
	x: 5,
	y: 2,
	targetInterval: 10,
	targetSwitchInterval: 15,
	bullet: Object.assign(new BulletType(), {
		damage: 10,
		maxRange: 125,
		shootEffect: Fx.sparkShoot,
		hitEffect: Fx.pointHit
	})
}))
exports.ws = ws

//爬辅-庇护
const bh = Object.assign(new UnitType('bh'), {
	constructor: () => extend(UnitTypes.atrax.constructor.get().class,{}),
	hovering: true,
	health: 7200,
	armor: 10,
	speed: 0.4,
	drag: 0.3,
	hitSize: 24,
	rotateSpeed: 2.4,
	buildSpeed: 3.5,
	itemCapacity: 100,
	drownTimeMultiplier: 2.4,
	stepShake: 1,
	shadowElevation: 0.25,
	groundLayer: Layer.legUnit,
	allowLegStep: true,
	lockLegBase: true,
	legContinuousMove: true,
	canAttack: false,
	targetable: true,
	legCount: 6,
	legLength: 28,
	legExtension: -15,
	legBaseOffset: 6,
	legPairOffset: 2,
	legMinLength: 0.3,
	legMaxLength: 1.2,
	legLengthScl: 1,
	legMoveSpace: 1,
	legSpeed: 0.3,
	legGroupSize: 3,
	rippleScale: 1
})
bh.abilities.add(
	FireAbilities.EnergyForceFieldAbility(80, 1, 750, 270, 6, 0, 10, 10, 20, 15),
	FireAbilities.RegenFieldAbility(150 / 60, 120, Color.valueOf('8cfffb'), 120, 6)
)
exports.bh = bh

/* TODO 仍未完工的爬辅线
const sy = Object.assign(new UnitType('sy'), {
	constructor: () => extend(UnitTypes.atrax.constructor.get().class,{}),
	hovering: true,
	health: 
	armor: 
	speed: 
	drag: 
	hitSize: 
	rotateSpeed: 
	buildSpeed: 
	itemCapacity: 
})
exports.sy = sy
*/

//空军-常规

//奥密克戎
const gnj = Object.assign(new UnitType('gnj'), {
	constructor: () => extend(UnitTypes.poly.constructor.get().class,{}),
	defaultCommand: UnitCommand.rebuildCommand,
	flying: true,
	health: 580,
	armor: 4,
	speed: 3.75,
	drag: 0.08,
	accel: 0.15,
	rotateSpeed: 22,
	hitSize: 12,
	isEnemy: false,
	lowAltitude: true,
	faceTarget: true,
	createWreck: false, //禁用坠落伤害
	coreUnitDock: true,
	engineOffset: 6,
	itemCapacity: 90,
	mineRange: 120,
	mineTier: 4,
	mineSpeed: 8.5,
	buildRange: 240,
	buildSpeed: 3
})
gnj.abilities.add(
	new RepairFieldAbility(20, 300, 40),
	new ShieldRegenFieldAbility(20, 80, 300, 40),
	new StatusFieldAbility(StatusEffects.overclock, 300, 300, 40),
	new StatusFieldAbility(StatusEffects.overdrive, 300, 300, 40)
)
gnj.weapons.add(Object.assign(new Weapon('fire-gnj-weapon'), {
	reload: 12,
	x: 4,
	y: 0.6,
	inaccuracy: 1.1,
	alternate: true,
	top: false,
	rotate: true,
	shootSound: Sounds.lasershoot,
	bullet: Object.assign(new LaserBoltBulletType(), {
		damage: 18,
		speed: 10,
		lifetime: 25,
		healPercent: 4,
		width: 2.4,
		height: 5.4,
		collidesTeam: true,
		backColor: Color.valueOf('8cfffb'),
		frontColor: Color.white,
		status: StatusEffects.electrified,
		statusDuration: 150
	})
}))
exports.gnj = gnj

//Javelin机甲
const javelin = Object.assign(new UnitType('javelin'), {
	constructor: () => extend(UnitTypes.zenith.constructor.get().class,{}),
	flying: true,
	health: 340,
	armor: 1,
	speed: 9,
	drag: 0.01,
	accel: 0.015,
	rotateSpeed: 22,
	hitSize: 12,
	lowAltitude: true,
	faceTarget: true,
	engineOffset: 6,
	itemCapacity: 30,
	buildSpeed: 1
})
javelin.abilities.add(
	new MoveLightningAbility(10, 16, 0.2, 16, 3.6, 8, Color.valueOf('a9d8ff'), 'fire-javelin-heat')
)
javelin.weapons.add(Object.assign(new Weapon('fire-javelin-weapon'), {
	reload: 35,
	x: 3,
	y: 1,
	inaccuracy: 3,
	velocityRnd: 0.2,
	alternate: true,
	top: false,
	rotate: false,
	shootSound: Sounds.missile,
	shoot: Object.assign(new ShootPattern(), {
		shots: 4
	}),
	bullet: Object.assign(new MissileBulletType(), {
		damage: 21,
		speed: 5,
		lifetime: 36,
		width: 8,
		height: 8,
		splashDamage: 2,
		splashDamageRadius: 20,
		weaveScale: 8,
		weaveMag: 2,
		trailColor: Color.valueOf('b6c6fd'),
		hitEffect: Fx.blastExplosion,
		despawnEffect: Fx.blastExplosion,
		backColor: Pal.bulletYellowBack,
		frontColor: Pal.bulletYellow
	})
}))
exports.javelin = javelin

//空军-空风
//TODO 仍未完工

//空风-萤火
const firefly = Object.assign(new UnitType('firefly'), {
	constructor: () => extend(UnitTypes.horizon.constructor.get().class,{}),
	flying: true,
	health: 150,
	armor: 3,
	speed: 1.6,
	drag: 0.04,
	accel: 0.08,
	rotateSpeed: 6,
	hitSize: 10,
	circleTarget: true,
	faceTarget: true,
	lowAltitude: false,
	engineOffset: 5.6,
	itemCapacity: 15
})
firefly.abilities.add(
	new MoveLightningAbility(2, 8, 0.1, 0, 1.2, 1.6, Color.valueOf('a9d8ff'))
)
firefly.weapons.add(Object.assign(new Weapon(''), {
	shoot: ShootSpread(3, 60),
	reload: 600,
	x: 3,
	y: 1,
	shootCone: 180,
	mirror: false,
	top: false,
	shootOnDeath: true,
	shootSound: Sounds.explosion,
	bullet: Object.assign(new ShrapnelBulletType(), {
		damage: 25,
		length: 70,
		width: 17,
		killShooter: true,
		hitEffect: Fx.pulverize,
		hitSound: Sounds.explosion
	})
}))
exports.firefly = firefly

//空风-烛光
const candlelight = Object.assign(new UnitType('candlelight'), {
	constructor: () => extend(UnitTypes.horizon.constructor.get().class,{}),
	flying: true,
	health: 280,
	armor: 4,
	speed: 2.7,
	drag: 0.04,
	accel: 0.08,
	rotateSpeed: 6,
	hitSize: 14,
	circleTarget: true,
	faceTarget: true,
	lowAltitude: false,
	engineOffset: 5.6,
	itemCapacity: 25
})
candlelight.abilities.add(
	new MoveLightningAbility(2, 13, 0.4, 0, 1.2, 1.6, Color.valueOf('a9d8ff'))
)
candlelight.weapons.add(Object.assign(new Weapon('fire-candlelight-weapon'), {
	shoot: ShootSpread(5, 36),
	reload: 600,
	x: 0,
	y: 0,
	shootCone: 180,
	mirror: false,
	top: true,
	shootOnDeath: true,
	shootSound: Sounds.explosion,
	bullet: Object.assign(new ShrapnelBulletType(), {
		damage: 65,
		length: 65,
		width: 18,
		killShooter: true,
		hitEffect: Fx.pulverize,
		hitSound: Sounds.explosion
	})
}))
exports.candlelight = candlelight
