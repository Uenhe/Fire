const FireAbilities = require('abilities')

//陆军-爬辅

//爬辅-守护
const sh = extend(UnitType, 'sh', {})
sh.constructor = prov(() => extend(UnitTypes.merui.constructor.get().class, {}))
sh.hovering = true
sh.health = 140
sh.armor = 3
sh.speed = 0.6
sh.drag = 0.1
sh.hitSize = 8
sh.rotateSpeed = 4
sh.itemCapacity = 20
sh.stepShake = 1
sh.shadowElevation = 0.1
sh.groundLayer = Layer.legUnit - 1
sh.allowLegStep = true
sh.lockLegBase = true
sh.legContinuousMove = true
sh.canAttack = false
sh.targetable = false
sh.legCount = 4
sh.legLength = 8
sh.legExtension = 0
sh.legBaseOffset = 2
sh.legPairOffset = 3
sh.legMinLength = 0.2
sh.legMaxLength = 1.1
sh.legLengthScl = 1.6
sh.legStraightness = 0.3
sh.legMoveSpace = 1
sh.legSpeed = 0.2
sh.legGroupSize = 3
sh.rippleScale = 0.2
sh.abilities.add(new ForceFieldAbility(44, 0.25, 200, 400))
exports.sh = sh

//爬辅-抗御
const ky = extend(UnitType, 'ky', {})
ky.constructor = prov(() => extend(UnitTypes.cleroi.constructor.get().class, {}))
ky.hovering = true
ky.health = 420
ky.armor = 5
ky.speed = 0.54
ky.drag = 0.1
ky.hitSize = 12
ky.rotateSpeed = 3.6
ky.itemCapacity = 40
ky.stepShake = 1
ky.shadowElevation = 0.1
ky.groundLayer = Layer.legUnit - 1
ky.allowLegStep = true
ky.lockLegBase = true
ky.legContinuousMove = true
ky.canAttack = false
ky.targetable = false
ky.legCount = 4
ky.legLength = 10
ky.legExtension = 0
ky.legBaseOffset = 2
ky.legPairOffset = 3
ky.legMinLength = 0.2
ky.legMaxLength = 1.1
ky.legLengthScl = 1.6
ky.legMoveSpace = 1
ky.legSpeed = 0.2
ky.rippleScale = 0.3
ky.abilities.add(
	new ForceFieldAbility(54, 0.35, 280, 340),
	new RepairFieldAbility(40, 60, 40),
)
exports.ky = ky

//爬辅-卫戍
const ws = extend(UnitType, 'ws', {})
ws.constructor = prov(() => extend(UnitTypes.anthicus.constructor.get().class, {}))
ws.hovering = true
ws.health = 930
ws.armor = 6
ws.speed = 0.45
ws.drag = 0.3
ws.hitSize = 16
ws.rotateSpeed = 2.7
ws.itemCapacity = 50
ws.stepShake = 1
ws.shadowElevation = 0.1
ws.groundLayer = Layer.legUnit - 1
ws.allowLegStep = true
ws.lockLegBase = true
ws.legContinuousMove = true
ws.canAttack = false
ws.targetable = false
ws.legCount = 6
ws.legLength = 22
ws.legExtension = 2
ws.legBaseOffset = 8
ws.legPairOffset = 4
ws.legMinLength = 0.2
ws.legMaxLength = 1.1
ws.legLengthScl = 1.6
ws.legMoveSpace = 1
ws.legSpeed = 0.2
ws.legGroupSize = 3
ws.rippleScale = 0.4
ws.abilities.add(
	new ForceFieldAbility(72, 0.6, 400, 300, 4, 45), //此处45为旋转角度(roration), 边数(sides)为4, 此时成正方形
	new StatusFieldAbility(StatusEffects.overclock, 360, 360, 80),
)
ws.weapons.add((() => {
	const weapon = new PointDefenseWeapon('fire-point-defense-weapon')
	weapon.reload = 10
	weapon.x = 5
	weapon.y = 2
	weapon.targetInterval = 10
	weapon.targetSwitchInterval = 15
	weapon.bullet = (() => {
		const bullet = new BulletType()
		bullet.damage = 10
		bullet.maxRange = 125
		bullet.shootEffect = Fx.sparkShoot
		bullet.hitEffect = Fx.pointHit
		return bullet
	})()
	return weapon
})())
exports.ws = ws

//爬辅-庇护
const bh = extend(UnitType, 'bh', {})
bh.constructor = prov(() => extend(UnitTypes.tecta.constructor.get().class, {}))
bh.hovering = true
bh.health = 7200
bh.armor = 10
bh.speed = 0.4
bh.drag = 0.3
bh.hitSize = 24
bh.rotateSpeed = 2.4
bh.itemCapacity = 100
bh.drownTimeMultiplier = 2.4
bh.stepShake = 1
bh.shadowElevation = 0.25
bh.groundLayer = Layer.legUnit
bh.allowLegStep = true
bh.lockLegBase = true
bh.legContinuousMove = true
bh.canAttack = false
bh.targetable = false
bh.legCount = 6
bh.legLength = 28
bh.legExtension = -15
bh.legBaseOffset = 6
bh.legPairOffset = 2
bh.legMinLength = 0.3
bh.legMaxLength = 1.2
bh.legLengthScl = 1
bh.legMoveSpace = 1
bh.legSpeed = 0.3
bh.legGroupSize = 3
bh.rippleScale = 1
bh.abilities.add(
	FireAbilities.EnergyForceFieldAbility(80, 1, 750, 270, 6, 0, 10, 10, 20, 15),
	new RepairFieldAbility(80, 30, 120) //第一个回血量, 第二个冷却, 第三个范围
)
exports.bh = bh

/* TODO 仍未完工的爬辅线
const sy = extend(UnitType, 'sy', {})
sy.constructor = prov(() => extend(UnitTypes.collaris.constructor.get().class, {}))
sy.hovering = true
sy.health = 
sy.armor = 
sy.speed = 
sy.drag = 
sy.hitSize = 
sy.rotateSpeed = 
sy.itemCapacity = 
exports.sy = sy
*/

//空军-常规

//奥密克戎
const gnj = extend(UnitType, 'gnj', {})
gnj.constructor = prov(() => extend(UnitTypes.poly.constructor.get().class, {}))
gnj.defaultCommand = UnitCommand.rebuildCommand
gnj.flying = true
gnj.health = 580
gnj.armor = 4
gnj.speed = 3.75
gnj.drag = 0.08
gnj.accel = 0.15
gnj.rotateSpeed = 22
gnj.hitSize = 12
gnj.isEnemy = false
gnj.lowAltitude = true
gnj.faceTarget = true
gnj.createWreck = false //禁用坠落伤害
gnj.coreUnitDock = true
gnj.engineOffset = 6
gnj.itemCapacity = 90
gnj.mineRange = 120
gnj.mineTier = 4
gnj.mineSpeed = 8.5
gnj.buildRange = 240
gnj.buildSpeed = 3
gnj.abilities.add(
	new RepairFieldAbility(20, 300, 40),
	new ShieldRegenFieldAbility(20, 80, 300, 40),
	new StatusFieldAbility(StatusEffects.overclock, 300, 300, 40),
	new StatusFieldAbility(StatusEffects.overdrive, 300, 300, 40)
)
gnj.weapons.add((() => {
	const weapon = new Weapon('fire-gnj-weapon')
	weapon.reload = 12
	weapon.x = 4
	weapon.y = 0.6
	weapon.inaccuracy = 1.1
	weapon.alternate = true
	weapon.top = false
	weapon.rotate = true
	weapon.shootSound = Sounds.lasershoot
	weapon.bullet = (() => {
		const bullet = new LaserBoltBulletType()
		bullet.damage = 18
		bullet.speed = 10
		bullet.lifetime = 25
		bullet.healPercent = 4
		bullet.width = 2.4
		bullet.height = 5.4
		bullet.collidesTeam = true
		bullet.backColor = Pal.heal
		bullet.frontColor = Color.white
		bullet.status = StatusEffects.electrified
		bullet.statusDuration = 150
		return bullet
	})()
	return weapon
})())
exports.gnj = gnj

const javelin = extend(UnitType, 'javelin', {})
javelin.constructor = prov(() => extend(UnitTypes.zenith.constructor.get().class, {}))
javelin.flying = true
javelin.health = 340
javelin.armor = 1
javelin.speed = 9
javelin.drag = 0.01
javelin.accel = 0.015
javelin.rotateSpeed = 22
javelin.hitSize = 12
javelin.lowAltitude = true
javelin.faceTarget = true
javelin.engineOffset = 6
javelin.itemCapacity = 30
javelin.buildSpeed = 1
javelin.abilities.add(new MoveLightningAbility(10, 16, 0.2, 16, 3.6, 8, Color.valueOf('a9d8ff'), 'fire-javelin-heat'))
javelin.weapons.add((() => {
	const weapon = new Weapon('fire-javelin-weapon')
	weapon.reload = 35
	weapon.x = 3
	weapon.y = 1
	weapon.shoot.shots = 4
	weapon.inaccuracy = 3
	weapon.velocityRnd = 0.2
	weapon.alternate = true
	weapon.top = false
	weapon.rotate = false
	weapon.shootSound = Sounds.missile
	weapon.bullet = (() => {
		const bullet = new MissileBulletType()
		bullet.damage = 21
		bullet.speed = 5
		bullet.lifetime = 36
		bullet.width = 8
		bullet.height = 8
		bullet.splashDamage = 2
		bullet.splashDamageRadius = 20
		bullet.weaveScale = 8
		bullet.weaveMag = 2
		bullet.trailColor = Color.valueOf('b6c6fd')
		bullet.hitEffect = Fx.blastExplosion
		bullet.despawnEffect = Fx.blastExplosion
		bullet.backColor = Pal.bulletYellowBack
		bullet.frontColor = Pal.bulletYellow
		return bullet
	})()
	return weapon
})())
exports.javelin = javelin

//空军-空风
//TODO 仍未完工

const firefly = extend(UnitType, 'firefly', {})
firefly.constructor = prov(() => extend(UnitTypes.horizon.constructor.get().class, {}))
firefly.flying = true
firefly.health = 150
firefly.armor = 3
firefly.speed = 1.6
firefly.drag = 0.04
firefly.accel = 0.08
firefly.rotateSpeed = 6
firefly.hitSize = 10
firefly.circleTarget = true
firefly.faceTarget = true
firefly.lowAltitude = false
firefly.engineOffset = 5.6
firefly.itemCapacity = 15
firefly.abilities.add(new MoveLightningAbility(2, 8, 0.1, 0, 1.2, 1.6, Color.valueOf('a9d8ff')))
firefly.weapons.add((() => {
	const weapon = new Weapon('')
	weapon.shoot = ShootSpread(3, 60)
	weapon.reload = 600
	weapon.x = 3
	weapon.y = 1
	weapon.shootCone = 180
	weapon.mirror = false
	weapon.top = false
	weapon.shootOnDeath = true
	weapon.shootSound = Sounds.explosion
	weapon.bullet = (() => {
		const bullet = new ShrapnelBulletType()
		bullet.damage = 25
		bullet.length = 70
		bullet.width = 17
		bullet.killShooter = true
		bullet.hitEffect = Fx.pulverize
		bullet.hitSound = Sounds.explosion
		return bullet
	})()
	return weapon
})())
exports.firefly = firefly


const candlelight = extend(UnitType, 'candlelight', {})
candlelight.constructor = prov(() => extend(UnitTypes.horizon.constructor.get().class, {}))
candlelight.flying = true
candlelight.health = 280
candlelight.armor = 4
candlelight.speed = 2.7
candlelight.drag = 0.04
candlelight.accel = 0.08
candlelight.rotateSpeed = 6
candlelight.hitSize = 14
candlelight.circleTarget = true
candlelight.faceTarget = true
candlelight.lowAltitude = false
candlelight.engineOffset = 5.6
candlelight.itemCapacity = 25
candlelight.abilities.add(new MoveLightningAbility(2, 13, 0.4, 0, 1.2, 1.6, Color.valueOf('a9d8ff')))
candlelight.weapons.add((() => {
	const weapon = new Weapon('fire-candlelight-weapon')
	weapon.shoot = ShootSpread(5, 36)
	weapon.reload = 600
	weapon.x = 0
	weapon.y = 0
	weapon.shootCone = 180
	weapon.mirror = false
	weapon.top = true
	weapon.shootOnDeath = true
	weapon.shootSound = Sounds.explosion
	weapon.bullet = (() => {
		const bullet = new ShrapnelBulletType()
		bullet.damage = 65
		bullet.length = 65
		bullet.width = 18
		bullet.killShooter = true
		bullet.hitEffect = Fx.pulverize
		bullet.hitSound = Sounds.explosion
		return bullet
	})()
	return weapon
})())
exports.candlelight = candlelight
