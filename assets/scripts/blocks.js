const
    items = require("items"),
    liquids = require("liquids"),
    statues = require("statues"),
    units = require("units"),
    fx = require("misc/fx"),
    {tree, grass, flesh} = require("misc/meta"),
    turr = Category.turret,       prod = Category.production,
    dist = Category.distribution, liqu = Category.liquid,
    powe = Category.power,        defe = Category.defense,
    craf = Category.crafting,     unit = Category.units,
    effe = Category.effect;

function setup(b, cat, req, size, health){
    b.buildVisibility = BuildVisibility.shown;
    b.category = cat;
    b.requirements = req;
    b.size = size;
    if(health) b.health = health
};



const fireCompany = Object.assign(new LightBlock("hzgs"), {
    buildVisibility: BuildVisibility.hidden,
    alwaysUnlocked: true,
    hasShadow: false,
    health: 2147483647,
    armor: 2147483647,
    size: 2
});



const adaptiveSource = Object.assign(new AdaptiveSource("adaptive-source"), {
    buildVisibility: BuildVisibility.sandboxOnly,
    category: dist,
    health: 250,
    size: 1,
    liquidCapacity: 300,
    laserRange: 25,
    maxNodes: 500,
    powerProduction: 10000000/60,
    itemPerSec: 3600
});



/* ==== Environment Region 环境 ==== */



//环境-瘤液池
const neoplasm = Object.assign(new Floor("pooled-neoplasm"), {
    variants: 0,
    drownTime: 230,
    speedMultiplier: 0.5,
    statusDuration: 240,
    status: statues.overgrown,
    isLiquid: true,
    liquidDrop: Liquids.neoplasm
});



//环境-血土
const bloodyDirt = Object.assign(new Floor("bloody-dirt"), {
    variants: 8
});
bloodyDirt.attributes.set(flesh, 1/9);



/* ==== Turret Region 炮塔 ==== */



//炮塔-击碎
const smasher = Object.assign(new ItemTurret("js"), {
    researchCost: ItemStack.with(
        Items.copper, 150,
        Items.lead, 120,
        Items.titanium, 50
    ),
    reload: 60/1.4,
    range: 250,
    shootCone: 30,
    inaccuracy: 2,
    rotateSpeed: 10,
    targetAir: false,
    shootSound: Sounds.bang
});
setup(smasher, turr, ItemStack.with(
    Items.copper, 80,
    Items.lead, 45,
    Items.titanium, 25
), 2, 800);
smasher.consumeCoolant(12/60);
smasher.ammo(
    Items.copper, Object.assign(new ArtilleryBulletType(6, 20), {
        lifetime: 130,
        knockback: 1.6,
        width: 12, height: 12,
        splashDamageRadius: 13.75,
        splashDamage: 24,
        reloadMultiplier: 0.9,
        pierceArmor: true
    }),
    Items.lead, Object.assign(new ArtilleryBulletType(12, 10), {
        lifetime: 78,
        knockback: 1.6,
        width: 8, height: 8,
        splashDamageRadius: 15.25,
        splashDamage: 18,
        reloadMultiplier: 1.8,
        pierceArmor: true
    }),
    Items.metaglass, Object.assign(new ArtilleryBulletType(6, 26), {
        lifetime: 130,
        knockback: 1.6,
        width: 12, height: 12,
        splashDamageRadius: 19.25,
        splashDamage: 40,
        ammoMultiplier: 3,
        reloadMultiplier: 1.2,
        pierceArmor: true,
        fragBullets: 12,
        fragBullet: Object.assign(new BasicBulletType(4, 10), {
            lifetime: 24,
            width: 10, height: 12,
            shrinkY: 1,
            collidesAir: false,
            pierceArmor: true
        })
    }),
    Items.graphite, Object.assign(new ArtilleryBulletType(5, 40), {
        lifetime: 160,
        knockback: 1.6,
        width: 12, height: 12,
        splashDamageRadius: 19.25,
        splashDamage: 45,
        ammoMultiplier: 4,
        pierceArmor: true
    }),
    items.impurityKindlingAlloy, Object.assign(new ArtilleryBulletType(6, 105), {
        lifetime: 130,
        knockback: 1.6,
        width: 12, height: 12,
        splashDamageRadius: 22.25,
        splashDamage: 80,
        ammoMultiplier: 4,
        reloadMultiplier: 0.7,
        pierceArmor: true,
        status: StatusEffects.blasted,
        fragBullets: 8,
        fragBullet: Object.assign(new BasicBulletType(2, 55), {
            lifetime: 40,
            width: 10, height: 12,
            shrinkY: 1,
            status: StatusEffects.melting,
            statusDuration: 240,
            pierceArmor: true
        })
    }),
    items.detonationCompound, Object.assign(new ArtilleryBulletType(4, 65), {
        lifetime: 196,
        knockback: 2,
        width: 15, height: 15,
        splashDamageRadius: 32,
        splashDamage: 220,
        ammoMultiplier: 5,
        pierceArmor: true,
        status: StatusEffects.melting,
        statusDuration: 300
    })
);



//炮塔-魇光
const nightmare = Object.assign(new ItemTurret("yg"), {
    researchCost: ItemStack.with(
        Items.titanium, 400,
        Items.thorium, 250,
        Items.silicon, 275,
        items.mirrorglass, 165
    ),
    reload: 60/2.5,
    range: 220,
    shootCone: 30,
    inaccuracy: 2,
    rotateSpeed: 10,
    shootSound: Sounds.shootBig,
    coolantMultiplier: 0.95
});
setup(nightmare, turr, ItemStack.with(
    Items.titanium, 140,
    Items.thorium, 110,
    Items.silicon, 85,
    items.mirrorglass, 55
), 2, 1080);
nightmare.consumeCoolant(12/60);
nightmare.ammo(
    Items.copper, Object.assign(new LaserBulletType(50), {
        length: 230,
        colors: [Color.valueOf("ff9900").mul(1, 1, 1, 0.4), Color.valueOf("ff9900"), Color.white],
        reloadMultiplier: 0.8,
        buildingDamageMultiplier: 0.5
    }),
    Items.lead, Object.assign(new LaserBulletType(45), {
        length: 230,
        colors: [Color.valueOf("ccccff").mul(1, 1, 1, 0.4), Color.valueOf("ccccff"), Color.white],
        reloadMultiplier: 1.1,
        buildingDamageMultiplier: 0.5
    }),
    Items.metaglass, Object.assign(new LaserBulletType(55), {
        length: 275, width: 12.5,
        hitSize: 3.5,
        colors: [Color.valueOf("ccccff").mul(1, 1, 1, 0.4), Color.valueOf("ccccff"), Color.white],
        rangeChange: 45,
        ammoMultiplier: 3,
        reloadMultiplier: 0.8,
        buildingDamageMultiplier: 0.5
    }),
    Items.graphite, Object.assign(new LaserBulletType(40), {
        length: 230,
        colors: [Color.valueOf("33ccff").mul(1, 1, 1, 0.4), Color.valueOf("33ccff"), Color.white],
        status: StatusEffects.freezing,
        statusDuration: 2.5 * 60,
        ammoMultiplier: 3,
        reloadMultiplier: 1.6,
        buildingDamageMultiplier: 0.5
    }),
    Items.sand, Object.assign(new LaserBulletType(15), {
        length: 380, width: 10,
        hitSize: 3,
        colors: [Color.valueOf("ccccff").mul(1, 1, 1, 0.4), Color.valueOf("ccccff"), Color.white],
        rangeChange: 150,
        ammoMultiplier: 1,
        reloadMultiplier: 0.5,
        buildingDamageMultiplier: 0.5
    }),
    Items.titanium, Object.assign(new LaserBulletType(75), {
        length: 230,
        colors: [Color.valueOf("ccccff").mul(1, 1, 1, 0.4), Color.valueOf("ccccff"), Color.white],
        status: StatusEffects.corroded,
        statusDuration: 2.5 * 60,
        reloadMultiplier: 1.2,
        buildingDamageMultiplier: 0.5
    }),
    Items.thorium, Object.assign(new LaserBulletType(90), {
        length: 230, width: 20,
        hitSize: 5,
        colors: [Pal.thoriumPink.mul(1, 1, 1, 0.4), Pal.thoriumPink, Color.white],
        pierceArmor: true,
        status: StatusEffects.melting,
        statusDuration: 2.5 * 60,
        reloadMultiplier: 0.6,
        buildingDamageMultiplier: 0.5
    }),
    Items.scrap, Object.assign(new LaserBulletType(25), {
        length: 400, width: 10,
        hitSize: 3,
        colors: [Color.valueOf("ccccff").mul(1, 1, 1, 0.4), Color.valueOf("ccccff"), Color.white],
        rangeChange: 170,
        ammoMultiplier: 1,
        reloadMultiplier: 1 / 3,
        buildingDamageMultiplier: 0.5
    }),
    Items.silicon, Object.assign(new LaserBulletType(65), {
        length: 230, width: 16,
        hitSize: 4.5,
        colors: [Color.valueOf("404040").mul(1, 1, 1, 0.4), Color.valueOf("404040"), Color.white],
        ammoMultiplier: 3,
        buildingDamageMultiplier: 0.5
    }),
    Items.surgeAlloy, Object.assign(new LaserBulletType(105), {
        length: 265, width: 16,
        hitSize: 4.5,
        colors: [Pal.surge.mul(1, 1, 1, 0.4), Pal.surge, Color.white],
        rangeChange: 35,
        pierceArmor: true,
        status: StatusEffects.shocked,
        lightningSpacing: 30,
        lightningLength: 0.8,
        lightningDelay: 1.2,
        lightningLengthRand: 10,
        lightningDamage: 24,
        lightningAngleRand: 30,
        ammoMultiplier: 4,
        reloadMultiplier: 0.85,
        buildingDamageMultiplier: 0.5,
        hitSound: Sounds.spark
    }),
    items.glass, Object.assign(new LaserBulletType(25), {
        length: 315, width: 10,
        hitSize: 3,
        colors: [Color.valueOf("ccccff").mul(1, 1, 1, 0.4), Color.valueOf("ccccff"), Color.white],
        rangeChange: 85,
        ammoMultiplier: 3,
        reloadMultiplier: 0.8,
        buildingDamageMultiplier: 0.5
    }),
    items.mirrorglass, Object.assign(new LaserBulletType(75), {
        length: 390, width: 10,
        hitSize: 3,
        colors: [Color.valueOf("ccccff").mul(1, 1, 1, 0.4), Color.valueOf("ccccff"), Color.white],
        rangeChange: 160,
        ammoMultiplier: 4,
        reloadMultiplier: 0.6,
        buildingDamageMultiplier: 0.5
    })
);



//炮塔-点燃
const ignite = Object.assign(new ContinuousTurret("dr"), {
    hasPower: true,
    hasItems: false,
    liquidCapacity: 60,
    range: 225,
    shootCone: 360,
    rotateSpeed: 1.2,
    shootWarmupSpeed: 0.16,
    aimChangeSpeed: 1.8,
    shootY: 4,
    shootSound: Sounds.tractorbeam,
    loopSoundVolume: 1,
    loopSound: Sounds.flux,
    shootType: Object.assign(new PointLaserBulletType(), {
        damage: 160,
        status: StatusEffects.melting,
        statusDuration: 150,
        hitColor: Pal.slagOrange
    })
});
setup(ignite, turr, ItemStack.with(
    Items.copper, 350,
    Items.graphite, 240,
    Items.silicon, 220,
    Items.plastanium, 180
), 3, 1760);
ignite.consumeLiquid(Liquids.slag, 20/60);
ignite.consumePower(400/60);



//炮塔-盛放
const Bcolor = StatusEffects.blasted.color;
const blossom = Object.assign(new PowerTurret("blossom"), {
    liquidCapacity: 30,
    reload: 60/1.5,
    range: 250,
    shootCone: 30,
    inaccuracy: 2,
    rotateSpeed: 3.5,
    recoil: 1,
    shake: 1,
    velocityRnd: 0.1,
    coolantMultiplier: 0.95,
    shootSound: Sounds.missile,
    shoot: Object.assign(new ShootAlternate(2.4), {
        shots: 2
    }),
    shootType: Object.assign(new FlakBulletType(4, 40), {
        sprite: "missile-large",
        lifetime: 62,
        width: 12, height: 12,
        shrinkY: 0,
        drag: -0.003,
        homingRange: 80,
        explodeRange: 40,
        splashDamageRadius: 36,
        splashDamage: 65,
        weaveScale: 10,
        weaveMag: 2,
        trailLength: 24, trailWidth: 4,
        lightRadius: 100,
        lightOpacity: 1.2,
        ammoMultiplier: 1,
        buildingDamageMultiplier: 0.25,
        collidesGround: true,
        makeFire: true,
        statusDuration: 150,
        status: StatusEffects.burning,
        trailColor: Bcolor,
        lightColor: Bcolor,
        backColor: Bcolor,
        frontColor: Color.white,
        hitSound: Sounds.explosion,
        hitEffect: Object.assign(new ExplosionEffect(), {
            lifetime: 27,
            waveStroke: 4, waveLife: 8,
            waveRadBase: 8, waveRad: 24,
            sparks: 4, sparkRad: 27,
            sparkStroke: 1.5, sparkLen: 3,
            smokes: 4,
            waveColor: Bcolor,
            sparkColor: Bcolor,
            smokeColor: Color.white
        }),
        fragBullets: 6,
        fragBullet: Object.assign(new BasicBulletType(4, 25), {
            lifetime: 16,
            width: 3, height: 5,
            splashDamageRadius: 32,
            splashDamage: 45,
            homingPower: 0.15,
            homingRange: 80,
            homingDelay: 8,
            trailLength: 3, trailWidth: 3,
            buildingDamageMultiplier: 0.25,
            collidesGround: true,
            status: StatusEffects.blasted,
            backColor: Bcolor,
            frontColor: Color.white,
            trailColor: Bcolor
        })
    })
});
setup(blossom, turr, ItemStack.with(
    Items.lead, 140,
    Items.graphite, 90,
    Items.silicon, 105,
    Items.plastanium, 45
), 3, 1650);
blossom.consumePower(480/60);
blossom.consumeCoolant(18/60);



//炮塔-千里
const distance = Object.assign(new ItemTurret("ql"), {
    armor: 5,
    liquidCapacity: 45,
    reload: 60/0.1,
    range: 500,
    shootCone: 15,
    inaccuracy: 0,
    rotateSpeed: 1,
    recoil: 2,
    shake: 4,
    ammoPerShot: 6,
    shootSound: Sounds.missileLaunch
});
setup(distance, turr, ItemStack.with(
    Items.copper, 650,
    Items.thorium, 375,
    Items.silicon, 425,
    Items.plastanium, 250,
    items.hardenedAlloy, 225
), 3, 2160);
distance.consumeCoolant(30/60);
distance.ammo(
    items.logicAlloy, Object.assign(new MissileBulletType(3, 285), {
        lifetime: 167,
        width: 25, height: 28,
        splashDamageRadius: 56,
        splashDamage: 305,
        homingPower: 0.16,
        despawnShake: 3,
        ammoMultiplier: 1,
        buildingDamageMultiplier: 0.2,
        shootEffect: Fx.shootBig,
        smokeEffect: Fx.shootSmokeMissile,
        hitEffect: Fx.massiveExplosion,
        hitSound: Sounds.mediumCannon
    }),
    items.detonationCompound, Object.assign(new MissileBulletType(4, 320), {
        lifetime: 193,
        width: 25, height: 28,
        splashDamageRadius: 72,
        splashDamage: 320,
        rangeChange: 270, 
        homingPower: 0.16,
        despawnShake: 5,
        makeFire: true,
        ammoMultiplier: 1,
        buildingDamageMultiplier: 0.2,
        shootEffect: Fx.shootBig,
        smokeEffect: Fx.shootSmokeMissile,
        hitEffect: Fx.massiveExplosion,
        hitSound: Sounds.mediumCannon,
        fragBullets: 3,
        fragBullet: Object.assign(new MissileBulletType(2, 220), {
            lifetime: 144,
            width: 20, height: 24,
            splashDamage: 780,
            splashDamageRadius: 120,
            homingPower: 0.04,
            despawnShake: 2,
            shrinkY: 1,
            buildingDamageMultiplier: 0.2,
            status: StatusEffects.blasted,
            hitEffect: Fx.blastExplosion
        })
    })
);



//炮塔-倒海
const seaquake = Object.assign(new LiquidTurret("dh"), {
    liquidCapacity: 75,
    reload: 60/25,
    range: 312,
    shootCone: 60,
    inaccuracy: 2.7,
    recoil: 2,
    velocityRnd: 0.1,
    shootEffect: Fx.shootLiquid,
    shoot: Object.assign(new ShootPattern(), {
        shots: 3
    })
});
setup(seaquake, turr, ItemStack.with(
    Items.lead, 420,
    Items.metaglass, 175,
    Items.thorium, 150,
    Items.plastanium, 135,
    Items.surgeAlloy, 65
), 3, 1980);
seaquake.ammo(
    Liquids.water, Object.assign(new LiquidBulletType(Liquids.water), {
        speed: 6,
        damage: 0.4,
        lifetime: 54,
        knockback: 2.2,
        puddleSize: 11,
        orbSize: 6,
        drag: 0.001,
        layer: Layer.bullet - 2,
        ammoMultiplier: 0.6,
        statusDuration: 270
    }),
    Liquids.slag, Object.assign(new LiquidBulletType(Liquids.slag), {
        speed: 6,
        damage: 5.75,
        lifetime: 54,
        knockback: 1.5,
        puddleSize: 11,
        orbSize: 6,
        drag: 0.001,
        ammoMultiplier: 0.6,
        statusDuration: 270
    }),
    Liquids.cryofluid, Object.assign(new LiquidBulletType(Liquids.cryofluid), {
        speed: 6,
        damage: 0.4,
        lifetime: 54,
        knockback: 1.5,
        puddleSize: 11,
        orbSize: 6,
        drag: 0.001,
        ammoMultiplier: 0.6,
        statusDuration: 270
    }),
    Liquids.oil, Object.assign(new LiquidBulletType(Liquids.oil), {
        speed: 6,
        damage: 0.4,
        lifetime: 54,
        knockback: 1.5,
        puddleSize: 11,
        orbSize: 6,
        drag: 0.001,
        layer: Layer.bullet - 2,
        ammoMultiplier: 0.6,
        statusDuration: 270
    }),
    liquids.liquidNitrogen, Object.assign(new LiquidBulletType(liquids.liquidNitrogen), {
        speed: 6,
        damage: 5.5,
        lifetime: 54,
        knockback: 2,
        puddleSize: 11,
        orbSize: 6,
        drag: 0.001,
        boilTime: 120,
        ammoMultiplier: 0.6,
        statusDuration: 270
    })
);
seaquake.consumePower(200/60);



//炮塔-潮怨
const grudge = Object.assign(new ItemTurret("grudge"), {
    armor: 10,
    liquidCapacity: 75,
    canOverdrive: false,
    reload: 60/4,
    range: 360,
    shootCone: 24,
    inaccuracy: 2,
    maxAmmo: 60,
    recoil: 4,
    recoilTime: 10,
    shake: 2.2,
    rotateSpeed: 6.5,
    shootSound: Sounds.shootBig,
    ammoUseEffect: Fx.casing3,
    coolantMultiplier: 0.825,
    shoot: Object.assign(new ShootAlternate(6.3), {
        shots: 2,
        barrels: 3
    })
});
setup(grudge, turr, ItemStack.with(
    Items.copper, 1350,
    Items.graphite, 475,
    Items.phaseFabric, 325,
    Items.surgeAlloy, 275,
    items.hardenedAlloy, 550
), 4, 4320);
grudge.consumeCoolant(72/60);
grudge.ammo(
    Items.thorium, Object.assign(new BasicBulletType(8, 90), {
        lifetime: 46,
        knockback: 0.8,
        width: 20, height: 27,
        hitSize: 6,
        pierceCap: 4,
        pierceBuilding: true,
        pierceArmor: true,
        ammoMultiplier: 4,
        status: StatusEffects.corroded,
        statusDuration: 120,
        shootEffect: Fx.shootBig,
        fragBullets: 4,
        fragBullet: Object.assign(new BasicBulletType(4, 35), {
            lifetime: 10,
            width: 8, height: 11,
            pierceArmor: true
        })
    }),
    items.detonationCompound, Object.assign(new BasicBulletType(8, 105), {
        lifetime: 46,
        knockback: 0.7,
        width: 18, height: 25,
        hitSize: 6,
        pierceCap: 2,
        pierceBuilding: true,
        makeFire: true,
        ammoMultiplier: 8,
        status: StatusEffects.burning,
        statusDuration: 240,
        shootEffect: Fx.shootBig,
        frontColor: Pal.lightishOrange,
        backColor: Pal.lightOrange,
        fragBullets: 2,
        fragBullet: Object.assign(new BulletType(4, 0), {
            lifetime: 1,
            splashDamageRadius: 65,
            splashDamage: 115,
            collides: false,
            collidesTiles: false,
            hittable: false,
            status: StatusEffects.blasted,
            hitSoundVolume: 0.5,
            hitSound: Sounds.mediumCannon,
            hitEffect: Fx.pulverize,
            despawnEffect: Object.assign(new WaveEffect(), {
                lifetime: 24,
                sizeFrom: 0, sizeTo: 65,
                strokeFrom: 4, strokeTo: 0,
                colorFrom: Pal.lightishOrange, colorTo: Color.white
            })
        })
    }),
    items.hardenedAlloy, Object.assign(new BasicBulletType(6, 375), {
        lifetime: 82,
        knockback: 2,
        width: 22, height: 36,
        hitSize: 7.2,
        rangeChange: 120,
        pierceCap: 2,
        pierceBuilding: true,
        pierceArmor: true,
        reloadMultiplier: 0.3,
        buildingDamageMultiplier: 0.1,
        status: StatusEffects.melting,
        statusDuration: 240,
        shootEffect: Fx.shootBig,
        frontColor: Pal.sapBullet,
        backColor: Pal.sapBulletBack,
        fragBullets: 4,
        fragBullet: Object.assign(new BulletType(48, 0), {
            lifetime: 1,
            splashDamageRadius: 57,
            splashDamage: 140,
            collides: false,
            collidesTiles: false,
            hittable: false,
            hitSoundVolume: 0.5,
            hitSound: Sounds.mediumCannon,
            hitEffect: Fx.pulverizeMedium,
            despawnEffect: Object.assign(new WaveEffect(), {
                lifetime: 15,
                sizeFrom: 0, sizeTo: 57,
                strokeFrom: 4, strokeTo: 0,
                colorFrom: Pal.sapBullet, colorTo: Color.white
            })
        })
    })
);



//炮塔-磁轨
const MRcolor = Color.valueOf("ec7458");
const MRchargeTime = 150;
const magneticRail = Object.assign(new ItemTurret("magnetic-rail"), {
    liquidCapacity: 1800,
    canOverdrive: false,
    reload: 60/0.2,
    range: 135 * Vars.tilesize,
    shootCone: 0.1,
    inaccuracy: 0,
    recoil: 5,
    rotateSpeed: 1,
    coolantMultiplier: 0.075,
    moveWhileCharging: false,
    shootSound: Sounds.laser,
    shoot: Object.assign(new ShootPattern(), {
        firstShotDelay: MRchargeTime
    }),
    drawer: (() => {
        const e = new DrawTurret();
        e.parts.addAll(
            Object.assign(new ShapePart(), {
                progress: DrawPart.PartProgress.smoothReload,
                layer: Layer.effect,
                x: 0, y: 0,
                radius: 57, radiusTo: 120,
                stroke: 8, strokeTo: 0,
                circle: true,
                hollow: true,
                color: MRcolor
            }),
            Object.assign(new ShapePart(), {
                progress: DrawPart.PartProgress.smoothReload,
                layer: Layer.effect,
                x: 0, y: 0,
                radius: 58, radiusTo: 32,
                stroke: 4, strokeTo: 0,
                circle: true,
                hollow: true,
                color: MRcolor
            }),
            Object.assign(new ShapePart(), {
                progress: DrawPart.PartProgress.smoothReload,
                layer: Layer.effect,
                x: 0, y: 0,
                radius: 32, radiusTo: 0,
                stroke: 3, strokeTo: 0,
                circle: true,
                hollow: true,
                color: MRcolor
            }),
            Object.assign(new ShapePart(), {
                progress: DrawPart.PartProgress.smoothReload,
                layer: Layer.effect,
                x: 0, y: 0,
                radius: 4, radiusTo: 0,
                stroke: 2, strokeTo: 0,
                circle: true,
                hollow: false,
                color: MRcolor
            }),
            Object.assign(new HaloPart(), {
                progress: DrawPart.PartProgress.smoothReload.delay(1),
                layer: Layer.effect,
                x: 0, y: 0,
                radius: 4, radiusTo: 0,
                stroke: 4, strokeTo: 0,
                haloRadius: 8,
                haloRotation: 0,
                haloRotateSpeed: 0.6,
                rotateSpeed: 0,
                sides: 5,
                hollow: false,
                tri: false,
                color: MRcolor
            }),
            Object.assign(new HaloPart(), {
                progress: DrawPart.PartProgress.smoothReload.delay(1),
                layer: Layer.effect,
                x: 0, y: 0,
                radius: 8, radiusTo: 0,
                stroke: 4, strokeTo: 0,
                haloRadius: 24,
                haloRotation: 0,
                haloRotateSpeed: -1.8,
                rotateSpeed: 0,
                sides: 5,
                hollow: false,
                tri: false,
                color: MRcolor
            }),
            Object.assign(new HaloPart(), {
                progress: DrawPart.PartProgress.smoothReload.delay(1),
                layer: Layer.effect,
                x: 0, y: 0,
                radius: 12, radiusTo: 0,
                stroke: 4, strokeTo: 0,
                haloRadius: 43,
                haloRotation: 0,
                haloRotateSpeed: 2.2,
                rotateSpeed: 0,
                sides: 5,
                hollow: false,
                tri: false,
                color: MRcolor
            }),
            Object.assign(new HaloPart(), {
                progress: DrawPart.PartProgress.smoothReload.delay(1),
                layer: Layer.effect,
                x: 0, y: 0,
                radius: 12, radiusTo: 0,
                stroke: 4, strokeTo: 0,
                haloRadius: 43,
                haloRotation: 0,
                haloRotateSpeed: 2.2,
                rotateSpeed: 0,
                sides: 8,
                hollow: false,
                tri: false,
                color: MRcolor
            }),
            Object.assign(new HaloPart(), {
                progress: DrawPart.PartProgress.smoothReload.delay(1),
                layer: Layer.effect,
                x: 0, y: 0,
                radius: 18, radiusTo: 0,
                stroke: 4, strokeTo: 0,
                haloRadius: 63,
                haloRotation: 0,
                haloRotateSpeed: -1.2,
                rotateSpeed: 0,
                sides: 3,
                shapes: 8,
                hollow: false,
                tri: false,
                color: MRcolor
            }),
            Object.assign(new HaloPart(), {
                progress: DrawPart.PartProgress.smoothReload.delay(1),
                layer: Layer.effect,
                x: 0, y: 0,
                radius: 10, radiusTo: 0,
                stroke: 4, strokeTo: 0,
                haloRadius: 63,
                haloRotation: 0,
                haloRotateSpeed: 2.8,
                rotateSpeed: 0,
                sides: 3,
                shapes: 6,
                hollow: false,
                tri: false,
                color: MRcolor
            })
        );
        return e
    })()
});
setup(magneticRail, turr, ItemStack.with(
    Items.silicon, 2750,
    Items.thorium, 3200,
    items.conductor, 34500,
    items.hardenedAlloy, 1800
), 12, 138240);
magneticRail.consumePower(300000/60);
magneticRail.consumeCoolant(432/60);
magneticRail.ammo(
    items.conductor, Object.assign(new BasicBulletType(60, 2400), {
        lifetime: 36,
        width: 24, height: 30,
        trailLength: 12, trailWidth: 6,
        trailChance: 1,
        splashDamageRadius: 210,
        splashDamage: 1200,
        pierceCap: 30,
        pierceBuilding: true,
        collidesGround: true,
        backColor: MRcolor,
        frontColor: Color.white,
        trailColor: MRcolor,
        chargeEffect: new MultiEffect(
            //TODO add one more.
            fx.railChargeEffect(MRchargeTime, MRcolor, 2, 135 * Vars.tilesize, 80)
        ),
        despawnEffect: new MultiEffect(

            /* wave */
            Object.assign(new WaveEffect(), {
                lifetime: 75,
                interp: Interp.circleOut,
                sizeFrom: 192, sizeTo: 8,
                strokeFrom: 8, strokeTo: 0,
                colorFrom: MRcolor, colorTo: MRcolor
            }),

            /* circles */
            Object.assign(new ParticleEffect(), {
                lifetime: 60,
                particles: 12,
                baseLength: 72,
                sizeFrom: 32, sizeTo: 0,
                colorFrom: MRcolor, colorTo: MRcolor
            }),

            /* lines */
            Object.assign(new ParticleEffect(), {
                lifetime: 60,
                particles: 30,
                length: 72,
                baseLength: 48,
                line: true,
                strokeFrom: 8, strokeTo: 0,
                lenFrom: 60, lenTo: 0,
                colorFrom: MRcolor, colorTo: MRcolor
            })
        ),

        /* lightning bullet - the lightning when bullet despawns */
        lightningColor: MRcolor,
        lightning: 16,
        lightningLength: 24,
        lightningLengthRand: 4,
        lightningDamage: 40,

        /* interval bullet - the lightning while bullet flying */
        bulletInterval: 0.5,
        intervalRandomSpread: 0,
        intervalSpread: 359,
        intervalBullets: 2,
        intervalBullet: Object.assign(new LightningBulletType(), {
            damage: 5,
            lightningColor: MRcolor,
            lightningLength: 16
        }),

        /* frag bullet */
        fragRandomSpread: 30,
        fragSpread: 60,
        fragBullets: 6,
        fragBullet: Object.assign(new BasicBulletType(10, 800), {
            lifetime: 45,
            width: 24, height: 30,
            trailLength: 8, trailWidth: 6,
            splashDamageRadius: 220,
            splashDamage: 300,
            pierceCap: 1,
            pierceBuilding: true,
            collidesGround: true,
            backColor: MRcolor,
            frontColor: Color.white,
            trailColor: MRcolor,
            despawnEffect: new MultiEffect(

                /* wave */
                Object.assign(new WaveEffect(), {
                    lifetime: 36,
                    interp: Interp.circleOut,
                    sizeFrom: 108, sizeTo: 4,
                    strokeFrom: 4, strokeTo: 0,
                    colorFrom: MRcolor, colorTo: MRcolor
                }),

                /* circles */
                Object.assign(new ParticleEffect(), {
                    lifetime: 36,
                    particles: 6,
                    baseLength: 40,
                    sizeFrom: 20, sizeTo: 0,
                    colorFrom: MRcolor, colorTo: MRcolor
                }),

                /* lines */
                Object.assign(new ParticleEffect(), {
                    lifetime: 30,
                    particles: 12,
                    length: 40,
                    baseLength: 24,
                    line: true,
                    strokeFrom: 4, strokeTo: 0,
                    lenFrom: 40, lenTo: 0,
                    colorFrom: MRcolor, colorTo: MRcolor
                })
            ),

            /* lightning bullet - the lightning when bullet despawns */
            lightningColor: MRcolor,
            lightning: 8,
            lightningLength: 16,
            lightningLengthRand: 2,
            lightningDamage: 15,

            /* interval bullet - the lightning while bullet flying */
            bulletInterval: 0.5,
            intervalRandomSpread: 0,
            intervalSpread: 359,
            intervalBullets: 1,
            intervalBullet: Object.assign(new LightningBulletType(), {
                damage: 5,
                lightningColor: MRcolor,
                lightningLength: 12
            }),

            /* frag bullet */
            fragRandomSpread: 30,
            fragSpread: 72,
            fragBullets: 5,
            fragBullet: Object.assign(new BasicBulletType(20, 300), {
                lifetime: 30,
                width: 24, height: 30,
                trailLength: 6, trailWidth: 6.3,
                splashDamageRadius: 120,
                splashDamage: 180,
                collidesGround: true,
                backColor: MRcolor,
                frontColor: Color.white,
                trailColor: MRcolor
            })
        })
    }),
    items.hardenedAlloy, Object.assign(new BasicBulletType(60, 15000), {
        lifetime: 24,
        width: 0, height: 0,
        hitSize: 12,
        rangeChange: 45 * Vars.tilesize,
        splashDamageRadius: 180,
        splashDamage: 11500,
        backColor: MRcolor,
        frontColor: Color.white,
        trailColor: MRcolor,
        chargeEffect: fx.railChargeEffect(MRchargeTime, MRcolor, 2, (135 + 45) * Vars.tilesize, 80),
        parts: Seq.with(
            Object.assign(new HaloPart(), {
                tri: true,
                layer: Layer.effect,
                shapes: 2,
                haloRotation: 0,
                haloRotateSpeed: 9,
                radius: 40,
                triLength: 60,
                color: MRcolor
            }),
            Object.assign(new HaloPart(), {
                tri: true,
                layer: Layer.effect,
                shapes: 2,
                haloRotation: 90,
                haloRotateSpeed: 9,
                radius: 40,
                triLength: 60,
                color: MRcolor
            })
        ),
        fragBullets: 3,
        fragBullet: Object.assign(new BasicBulletType(40, 4320), {
            lifetime: 60,
            width: 16, height: 20,
            trailLength: 11, trailWidth: 2,
            splashDamageRadius: 180,
            splashDamage: 4560,
            collidesGround: true,
            backColor: MRcolor,
            frontColor: Color.white,
            trailColor: MRcolor
        })
    })
);



/* ==== Production Region 生产 ==== */



//生产-伐木机
const chopper = Object.assign(new WallCrafter("fmj"), {
    researchCost: ItemStack.with(
        Items.copper, 50,
        Items.lead, 35,
        Items.titanium, 25,
        Items.silicon, 30
    ),
    ambientSound: Sounds.drill,
    ambientSoundVolume: 0.01,
    attribute: tree,
    drillTime: 120,
    output: items.timber
});
setup(chopper, prod, ItemStack.with(
    Items.copper, 20,
    Items.lead, 15,
    Items.titanium, 10,
    Items.silicon, 15
), 1, 65);
chopper.consumePower(24/60);



//生产-树场
const treeFarm = Object.assign(new AttributeCrafter("sc"), {
    hasPower: true,
    hasLiquids: true,
    updateEffect: Object.assign(new Effect(60, e => {
        Draw.color(Color.valueOf("6aa85e"));
        Draw.alpha(e.fslope());
        Fx.rand.setSeed(e.id);
        for(var i = 0; i < 2; i += 1){
            Fx.v.trns(Fx.rand.random(360), Fx.rand.random(e.finpow() * 9)).add(e.x, e.y);
            Fill.circle(Fx.v.x, Fx.v.y, Fx.rand.random(1.4, 2.4))
        }
    }), {
        layer: Layer.bullet - 1
    }),
    drawer: new DrawMulti(
        new DrawRegion("-bottom"),
        new DrawLiquidTile(Liquids.water),
        new DrawDefault()
    ),
    attribute: grass,
    maxBoost: 1,
    craftTime: 240,
    outputItem: new ItemStack(
        items.timber, 4
    )
});
setup(treeFarm, prod, ItemStack.with(
    Items.copper, 50,
    Items.lead, 30,
    Items.metaglass, 20,
    Items.titanium, 25
), 2);
treeFarm.consumeLiquid(Liquids.water, 12/60);
treeFarm.consumePower(150/60);



//生产-水汽冷凝器
const vapourCondenser = Object.assign(new GenericCrafter("sqlnq"), {
    hasPower: true,
    hasItems: false,
    hasLiquids: true,
    drawer: new DrawMulti(
        new DrawRegion("-bottom"),
        new DrawLiquidTile(Liquids.water),
        new DrawDefault()
    ),
    craftTime: 120,
    outputLiquid: new LiquidStack(
        Liquids.water, 24/60
    )
});
setup(vapourCondenser, prod, ItemStack.with(
    Items.lead, 50,
    Items.graphite, 30,
    Items.metaglass, 30,
    Items.titanium, 20
), 2);
vapourCondenser.consumePower(210/60);



//生产-生物质增生机
const biomassCultivator = Object.assign(new AttributeCrafter("swzzsj"), {
    researchCostMultiplier: 0.6,
    hasPower: true,
    hasLiquids: true,
    itemCapacity: 20,
    liquidCapacity: 30,
    updateEffect: Object.assign(new Effect(60, e => {
        Draw.color(Color.valueOf("9e78dc"));
        Draw.alpha(e.fslope());
        Fx.rand.setSeed(e.id);
        for(var i = 0; i < 2; i += 1){
            Fx.v.trns(Fx.rand.random(360), Fx.rand.random(e.finpow() * 12)).add(e.x, e.y);
            Fill.circle(Fx.v.x, Fx.v.y, Fx.rand.random(1.8, 2.8))
        }
    }), {
        layer: Layer.bullet - 1
    }),
    drawer: new DrawMulti(
        new DrawRegion("-bottom"),
        new DrawLiquidTile(Liquids.water),
        new DrawDefault(),
        new DrawCultivator(),
        new DrawRegion("-top")
    ),
    attribute: Attribute.spores,
    maxBoost: 3.5,
    craftTime: 60,
    outputItem: new ItemStack(
        Items.sporePod, 3
    )
});
setup(biomassCultivator, prod, ItemStack.with(
    Items.copper, 80,
    Items.lead, 105,
    Items.titanium, 50,
    Items.silicon, 75
), 3);
biomassCultivator.consumeLiquid(Liquids.water, 30/60);
biomassCultivator.consumePower(180/60);



//生产-裂变钻头
const fissionDrill = Object.assign(new BurstDrill("lbzt"), {
    itemCapacity: 75,
    liquidCapacity: 30,
    drillEffect: new MultiEffect(
        Fx.mineImpact,
        Fx.drillSteam
    ),
    baseExplosiveness: 5,
    tier: 8,
    drillTime: 45,
    shake: 4,
    baseArrowColor: Color.valueOf("989aa4")
});
setup(fissionDrill, prod, ItemStack.with(
    Items.copper, 160,
    Items.metaglass, 80,
    Items.thorium, 375,
    Items.silicon, 145
), 5, 1120);
fissionDrill.consumeLiquid(Liquids.water, 12/60);



/* ==== Distribution Region 运输 ==== */



//运输-复合传送带
const compositeConveyor = Object.assign(new Conveyor("fhcsd"), {
    speed: 0.21,
    displayedSpeed: 25,
    junctionReplacement: Blocks.invertedSorter
});
setup(compositeConveyor, dist, ItemStack.with(
    Items.metaglass, 1,
    Items.titanium, 1,
    Items.thorium, 1
), 1, 85);



//运输-复合传送带桥
const compositeBridgeConveyor = Object.assign(new ItemBridge("composite-bridge-conveyor"), {
    hasPower: false,
    itemCapacity: 12,
    range: 8,
    pulse: true
});
setup(compositeBridgeConveyor, dist, ItemStack.with(
    Items.metaglass, 4,
    Items.titanium, 6,
    Items.thorium, 4,
    Items.plastanium, 4
), 1, 85);
compositeConveyor.bridgeReplacement = compositeBridgeConveyor;



/* ==== Liquid Region 液体 ==== */



//液体-复合液体路由器
const compositeLiquidRouter = Object.assign(new LiquidRouter("composite-liquid-router"), {
    liquidCapacity: 120,
    solid: false,
    underBullets: true
});
setup(compositeLiquidRouter, liqu, ItemStack.with(
    Items.metaglass, 8,
    Items.titanium, 12,
    Items.thorium, 6,
    Items.plastanium, 8
), 1, 85);



//液体-复合流体桥
const compositeBridgeConduit = Object.assign(new LiquidBridge("composite-bridge-conduit"), {
    hasPower: false,
    liquidCapacity: 16,
    range: 8,
    pulse: true
});
setup(compositeBridgeConduit, liqu, ItemStack.with(
    Items.metaglass, 6,
    Items.titanium, 8,
    Items.thorium, 4,
    Items.plastanium, 4
), 1, 85);



/* ==== Power Region 电力 ==== */



//电力-导体节点
const conductorPowerNode = Object.assign(new BatteryNode("zjjd"), {
    maxNodes: 8,
    laserRange: 25
});
setup(conductorPowerNode, powe, ItemStack.with(
    Items.lead, 10,
    items.glass, 10,
    items.conductor, 5
), 2, 225);
conductorPowerNode.consumePowerBuffered(20000);



//电力-焰燃发电机
const flameGenerator = Object.assign(new ConsumeGenerator("yrfdj"), {
    hasLiquids: true,
    itemCapacity: 20,
    liquidCapacity: 30,
    baseExplosiveness: 5,
    generateEffect: new MultiEffect(
        Fx.explosion,
        Fx.fuelburn,
        Fx.generatespark,
        Fx.smeltsmoke
    ),
    ambientSound: Sounds.steam,
    ambientSoundVolume: 0.02,
    drawer: new DrawMulti(
        new DrawDefault(),
        new DrawWarmupRegion(),
        new DrawLiquidRegion(Liquids.cryofluid)
    ),
    itemDuration: 120,
    powerProduction: 2400/60
});
setup(flameGenerator, powe, ItemStack.with(
    Items.lead, 120,
    Items.thorium, 75,
    Items.silicon, 160,
    Items.plastanium, 50,
    items.conductor, 30
), 3, 840);
flameGenerator.consume(new ConsumeItemFlammable(1.15));
flameGenerator.consumeLiquid(Liquids.cryofluid, 9/60);



//电力-焰爆反应堆
const burstReactor = Object.assign(new ImpactReactor("burst-reactor"), {
    itemCapacity: 40,
    liquidCapacity: 150,
    baseExplosiveness: 10,
    ambientSoundVolume: 0.06,
    ambientSound: Sounds.pulse,
    itemDuration: 240,
    powerProduction: 108000/60,
    warmupSpeed: 0.0008,
    explosionRadius: 120,
    explosionDamage: 11600,
    explosionShake: 8,
    explosionShakeDuration: 30,
    explodeEffect: new MultiEffect(
        //TODO complete.
        Fx.impactReactorExplosion
    )
});
setup(burstReactor, powe, ItemStack.with(
    items.magneticAlloy, 500
), 6, 9600);
burstReactor.consumeItem(items.detonationCompound, 8);
burstReactor.consumeLiquid(liquids.liquidNitrogen, 50/60);
burstReactor.consumePower(3600/60);



/* ==== Defense Region 防御 ==== */



//防御-水坝墙
const damWall = Object.assign(new Wall("sbq"), {
    requiresWater: true
});
setup(damWall, defe, ItemStack.with(
    Items.metaglass, 3,
    Items.titanium, 4
), 1, 720);



//防御-大型水坝墙
const damWallLarge = Object.assign(new Wall("sbqdx"), {
    requiresWater: true
});
setup(damWallLarge, defe, ItemStack.with(
    Items.metaglass, 12,
    Items.titanium, 16
), 2, damWall.health * 4);



//防御-硬化合金墙
const hardenedWall = Object.assign(new ArmorWall("hardened-wall"), {
    armor: 12,
    placeableLiquid: true,
    insulated: true,
    absorbLasers: true,
    armorIncrease: 0.2
});
setup(hardenedWall, defe, ItemStack.with(
    Items.metaglass, 3,
    items.hardenedAlloy, 6
), 1, 1440);



//防御-大型硬化合金墙
const hardenedWallLarge = Object.assign(new ArmorWall("hardened-wall-large"), {
    armor: 12,
    placeableLiquid: true,
    insulated: true,
    absorbLasers: true,
    armorIncrease: 0.2
});
setup(hardenedWallLarge, defe, ItemStack.with(
    Items.metaglass, 12,
    items.hardenedAlloy, 24
), 2, hardenedWall.health * 4);



//防御-血肉墙
const fleshWall = Object.assign(new RegenWall("xrq"), {
    healPercent: 5/60,
    optionalMultiplier: 2,
    chanceHeal: 0.15,
    chanceDeflect: 10,
    regenPercent: 0.5,
    flashHit: true,
    flashColor: Pal.health,
    frames: 6,
    frameTime: 5
});
setup(fleshWall, defe, ItemStack.with(
    items.flesh, 24,
    items.logicAlloy, 12
), 2, 8000);
fleshWall.consumeLiquid(Liquids.water, 1.8/60).boost();



/* ==== Crafting Region 工厂 ==== */



//工厂-热能窑炉
const thermalKiln = Object.assign(new AttributeCrafter("rnyl"), {
    researchCost: ItemStack.with(
        Items.copper, 120,
        Items.lead, 80,
        Items.graphite, 40
    ),
    hasPower: true,
    hasLiquids: false,
    itemCapacity: 20,
    craftEffect: Fx.smeltsmoke,
    drawer: new DrawMulti(
        new DrawDefault(),
        new DrawFlame(Color.valueOf("ffe099"))
    ),
    craftTime: 60,
    outputItem: new ItemStack(
        items.glass, 6
    ),
    attribute: Attribute.heat,
    boostScale: 1/3
});
setup(thermalKiln, craf, ItemStack.with(
    Items.copper, 60,
    Items.lead, 45,
    Items.graphite, 30
), 2);
thermalKiln.consumeItems(ItemStack.with(
    Items.sand, 6,
    Items.coal, 1
));
thermalKiln.consumePower(30/60);



//工厂-玻璃镀钢机
const metaglassPlater = Object.assign(new GenericCrafter("dgj"), {
    researchCost: ItemStack.with(
        Items.lead, 200,
        Items.titanium, 135,
        Items.silicon, 60
    ),
    hasPower: true,
    hasLiquids: false,
    craftEffect: Fx.smeltsmoke,
    drawer: new DrawMulti(
        new DrawDefault(),
        new DrawFade()
    ),
    craftTime: 10,
    outputItem: new ItemStack(
        Items.metaglass, 2
    )
});
setup(metaglassPlater, craf, ItemStack.with(
    Items.lead, 75,
    Items.titanium, 55,
    Items.silicon, 40
), 2);
metaglassPlater.consumeItems(ItemStack.with(
    Items.lead, 1,
    items.glass, 2
));
metaglassPlater.consumePower(120/60);



//工厂-钢化玻璃打磨机
const mirrorglassPolisher = Object.assign(new GenericCrafter("dmj"), {
    researchCost: ItemStack.with(
        Items.graphite, 160,
        Items.titanium, 200,
        Items.silicon, 180
    ),
    hasPower: true,
    hasLiquids: false,
    craftEffect: Fx.smeltsmoke,
    drawer: new DrawMulti(
        new DrawDefault(),
        new DrawFade()
    ),
    craftTime: 90,
    outputItem: new ItemStack(
        items.mirrorglass, 1
    )
});
setup(mirrorglassPolisher, craf, ItemStack.with(
    Items.graphite, 45,
    Items.titanium, 60,
    Items.silicon, 75
), 2);
mirrorglassPolisher.consumeItems(ItemStack.with(
    Items.metaglass, 2
));
mirrorglassPolisher.consumePower(120/60);



//工厂-烘火合金提取器
const impurityKindlingExtractor = Object.assign(new GenericCrafter("hhhjtqq"), {
    hasPower: true,
    hasLiquids: true,
    drawer: new DrawMulti(
        new DrawRegion("-bottom"),
        new DrawLiquidTile(Liquids.slag),
        new DrawDefault()
    ),
    craftTime: 60,
    outputItem: new ItemStack(
        items.impurityKindlingAlloy, 2
    )
});
setup(impurityKindlingExtractor, craf, ItemStack.with(
    Items.lead, 85,
    Items.graphite, 55,
    Items.titanium, 40
), 2);
impurityKindlingExtractor.consumeItems(ItemStack.with(
    Items.coal, 3,
    Items.sporePod, 2
));
impurityKindlingExtractor.consumeLiquid(Liquids.slag, 24/60);
impurityKindlingExtractor.consumePower(90/60);



//工厂-烘火合金萃取厂
const kindlingExtractor = Object.assign(new GenericCrafter("hhhjcqc"), {
    hasPower: true,
    hasLiquids: false,
    craftEffect: Fx.smeltsmoke,
    updateEffect: Fx.explosion,
    craftTime: 60,
    outputItem: new ItemStack(
        items.kindlingAlloy, 1
    )
});
setup(kindlingExtractor, craf, ItemStack.with(
    Items.graphite, 90,
    Items.titanium, 80,
    Items.silicon, 60
), 2);
kindlingExtractor.consumeItems(ItemStack.with(
    Items.coal, 1,
    items.impurityKindlingAlloy, 1
));
kindlingExtractor.consumePower(120/60);



//工厂-导体构成仪
const conductorFormer = Object.assign(new GenericCrafter("dtgcy"), {
    hasPower: true,
    hasLiquids: false,
    craftEffect: new MultiEffect(
        Fx.lightning,
        Fx.smeltsmoke
    ),
    drawer: new DrawMulti(
        new DrawArcSmelt(),
        new DrawDefault(),
        new DrawFade()
    ),
    craftTime: 120,
    outputItem: new ItemStack(
        items.conductor, 2
    )
});
setup(conductorFormer, craf, ItemStack.with(
    Items.lead, 80,
    Items.surgeAlloy, 25,
    items.mirrorglass, 30
), 2);
conductorFormer.consumeItems(ItemStack.with(
    Items.copper, 2,
    Items.silicon, 3
));
conductorFormer.consumePower(200/60);



//工厂-逻辑合金制造厂
const logicAlloyProcessor = Object.assign(new GenericCrafter("logic-alloy-processor"), {
    hasPower: true,
    hasLiquids: false,
    drawer: new DrawMulti(
        new DrawDefault(),
        new DrawFade()
    ),
    craftTime: 120,
    outputItem: new ItemStack(
        items.logicAlloy, 2
    )
});
setup(logicAlloyProcessor, craf, ItemStack.with(
    Items.titanium, 105,
    Items.silicon, 60,
    Items.plastanium, 55
), 2);
logicAlloyProcessor.consumeItems(ItemStack.with(
    Items.copper, 3,
    Items.titanium, 2,
    Items.silicon, 3
));
logicAlloyProcessor.consumePower(120/60);



//工厂-震爆物混合器
const detonationMixer = Object.assign(new GenericCrafter("detonation-mixer"), {
    hasPower: true,
    hasLiquids: false,
    craftEffect: Fx.smeltsmoke,
    updateEffect: Fx.explosion,
    craftTime: 120,
    outputItem: new ItemStack(
        items.detonationCompound, 2
    )
});
setup(detonationMixer, craf, ItemStack.with(
    Items.thorium, 45,
    Items.plastanium, 30,
    items.logicAlloy, 55
), 2);
detonationMixer.consumeItems(ItemStack.with(
    Items.blastCompound, 2,
    Items.pyratite, 2,
    items.logicAlloy, 1
));
detonationMixer.consumePower(90/60);



//工厂-高速冷却器
const slagCooler = Object.assign(new GenericCrafter("gslqq"), {
    hasPower: true,
    hasLiquids: true,
    liquidCapacity: 30,
    craftEffect: Fx.blastsmoke,
    updateEffect: Fx.smeltsmoke,
    drawer: new DrawMulti(
        new DrawRegion("-bottom"),
        new DrawLiquidTile(Liquids.slag),
        new DrawDefault(),
        new DrawFade()
    ),
    craftTime: 60,
    outputItem: new ItemStack(
        items.flamefluidCrystal, 2
    )
});
setup(slagCooler, craf, ItemStack.with(
    Items.copper, 90,
    Items.graphite, 55,
    Items.titanium, 70
), 2);
slagCooler.consumeLiquids(LiquidStack.with(
    Liquids.slag, 24/60,
    Liquids.cryofluid, 3/60
));
slagCooler.consumePower(90/60);



//工厂-铜铅搅碎机
const crusher = Object.assign(new GenericCrafter("tqjsj"), {
    hasPower: true,
    hasLiquids: false,
    updateEffect: Fx.pulverizeMedium,
    craftEffect: Fx.blastsmoke,
    drawer: new DrawMulti(
        new DrawRegion("-bottom"),
        new DrawRegion("-spinner", 10, true),
        new DrawDefault(),
        new DrawFade()
    ),
    craftTime: 30,
    outputItem: new ItemStack(
        Items.scrap, 2
    )
});
setup(crusher, craf, ItemStack.with(
    Items.copper, 45,
    Items.lead, 75,
    Items.graphite, 30
), 2);
crusher.consumeItems(ItemStack.with(
    Items.copper, 1,
    Items.lead, 1
));
crusher.consumePower(30/60);



//工厂-木材焚烧厂
const timberBurner = Object.assign(new CrafterGenerator("mcfsc"), {
    ambientSound: Sounds.steam,
    ambientSoundVolume: 0.01,
    generateEffect: Fx.generatespark,
    drawer: new DrawMulti(
        new DrawDefault(),
        new DrawWarmupRegion()
    ),
    itemDuration: 30,
    powerProduction: 210/60,
    outputItem: Items.coal
});
setup(timberBurner, craf, ItemStack.with(
    Items.copper, 50,
    Items.lead, 25,
    Items.metaglass, 15,
    Items.graphite, 20
), 2);
timberBurner.consumeItem(items.timber);



//工厂-电热硅炉
const electrothermalSiliconFurnace = Object.assign(new GenericCrafter("drgl"), {
    hasPower: true,
    hasLiquids: false,
    itemCapacity: 30,
    craftEffect: Fx.smeltsmoke,
    drawer: new DrawMulti(
        new DrawDefault(),
        new DrawFlame(Color.valueOf("ffef99"))
    ),
    craftTime: 10,
    outputItem: new ItemStack(
        Items.silicon, 2
    )
});
setup(electrothermalSiliconFurnace, craf, ItemStack.with(
    Items.copper, 250,
    Items.graphite, 200,
    Items.titanium, 120,
    Items.surgeAlloy, 80
), 3)
electrothermalSiliconFurnace.consumeItems(ItemStack.with(
    Items.sand, 3
));
electrothermalSiliconFurnace.consumePower(720/60);



//工厂-血肉合成仪
const fleshSynthesizer = Object.assign(new AttributeCrafter("flesh-synthesizer"), {
    hasPower: true,
    hasLiquids: false,
    itemCapacity: 30,
    liquidCapacity: 20,
    craftEffect: Fx.blastsmoke,
    drawer: new DrawMulti(
        new DrawRegion("-bottom"),
        new DrawLiquidTile(Liquids.neoplasm),
        new DrawWeave(),
        new DrawDefault()
    ),
    attribute: flesh,
    craftTime: 45,
    outputItem: new ItemStack(
        items.flesh, 1
    )
});
setup(fleshSynthesizer, craf, ItemStack.with(
    Items.lead, 120,
    Items.graphite, 60,
    Items.silicon, 75,
    Items.plastanium, 50
), 3)
fleshSynthesizer.consumeItems(ItemStack.with(
    Items.plastanium, 2,
    Items.phaseFabric, 1,
    Items.sporePod, 1
));
fleshSynthesizer.consumeLiquid(Liquids.neoplasm, 12/60);
fleshSynthesizer.consumePower(100/60);



//工厂-液氮压缩机
const liquidNitrogenCompressor = Object.assign(new GenericCrafter("ydysj"), {
    hasPower: true,
    hasLiquids: true,
    itemCapacity: 30,
    liquidCapacity: 75,
    baseExplosiveness: 5,
    drawer: new DrawMulti(
        new DrawRegion("-bottom"),
        new DrawLiquidTile(Liquids.cryofluid),
        new DrawLiquidTile(liquids.liquidNitrogen),
        new DrawDefault()
    ),
    craftTime: 120,
    outputLiquid: new LiquidStack(
        liquids.liquidNitrogen, 50/60
    )
});
setup(liquidNitrogenCompressor, craf, ItemStack.with(
    Items.lead, 220,
    Items.metaglass, 175,
    Items.silicon, 185,
    Items.plastanium, 130
), 3);
liquidNitrogenCompressor.consumeItems(ItemStack.with(
    Items.blastCompound, 5,
    items.kindlingAlloy, 2
));
liquidNitrogenCompressor.consumeLiquid(Liquids.cryofluid, 60/60);
liquidNitrogenCompressor.consumePower(400/60);



//工厂-硬化合金冶炼厂
const hardenedAlloySmelter = Object.assign(new GenericCrafter("hardened-alloy-smelter"), {
    hasPower: true,
    hasLiquids: false,
    itemCapacity: 20,
    craftEffect: Fx.smeltsmoke,
    drawer: new DrawMulti(
        new DrawDefault(),
        new DrawFlame(Color.valueOf("ffef99"))
    ),
    craftTime: 60,
    outputItem: new ItemStack(
        items.hardenedAlloy, 3
    )
});
setup(hardenedAlloySmelter, craf, ItemStack.with(
    Items.graphite, 135,
    Items.thorium, 50,
    Items.silicon, 90,
    Items.plastanium, 80
), 3);
hardenedAlloySmelter.consumeItems(ItemStack.with(
    Items.thorium, 3,
    Items.plastanium, 6,
    items.kindlingAlloy, 2
));
hardenedAlloySmelter.consumePower(600/60);



//工厂-磁能合金构成仪
const magneticAlloyFormer = Object.assign(new GenericCrafter("magnetic-alloy-former"), {
    hasPower: true,
    hasLiquids: false,
    itemCapacity: 20,
    drawer: new DrawMulti(
        new DrawRegion("-bottom"),
        new DrawArcSmelt(),
        new DrawDefault()
    ),
    craftTime: 90,
    outputItem: new ItemStack(
        items.magneticAlloy, 2
    )
});
setup(magneticAlloyFormer, craf, ItemStack.with(
    items.hardenedAlloy, 75
), 3);
magneticAlloyFormer.consumeItems(ItemStack.with(
    Items.surgeAlloy, 1,
    items.conductor, 3,
    items.hardenedAlloy, 2
));
magneticAlloyFormer.consumePower(1440/60);



//工厂-硬化合金坩埚
const hardenedAlloyCrucible = Object.assign(new EnergyCrafter("hardened-alloy-crucible"), {
    armor: 4,
    hasPower: true,
    hasLiquids: true,
    itemCapacity: 60,
    liquidCapacity: 120,
    craftEffect: Fx.smeltsmoke,
    updateEffect: new Effect(50, e => {
        Draw.color(Pal.reactorPurple, 0.7);
        Lines.stroke(e.fout() * 2);
        Lines.circle(e.x, e.y, 4 + e.finpow() * 60)
    }),
    updateEffectChance: 0.01,
    drawer: new DrawMulti(
        new DrawDefault(),
        new DrawFlame(Color.valueOf("ffef99"))
    ),
    craftTime: 120,
    outputItem: new ItemStack(
        items.hardenedAlloy, 20
    ),
    explosionRadius: 240,
    explosionDamage: 2880,
    explosionShake: 6,
    explosionShakeDuration: 30,
    explodeEffect: Fx.reactorExplosion,
    explodeSound: Sounds.explosionbig,
    maxInstability: 360,
    lightningDamage: 80,
    lightningAmount: 8,
    baseColor: Color.valueOf("67474b"),
    circleColor: [Pal.reactorPurple, Pal.thoriumPink, Pal.lightishOrange, Pal.surge, Pal.plastanium]
});
setup(hardenedAlloyCrucible, craf, ItemStack.with(
    Items.surgeAlloy, 1200,
    items.hardenedAlloy, 450
), 6);
hardenedAlloyCrucible.consumeItems(ItemStack.with(
    Items.thorium, 12,
    Items.plastanium, 20
));
hardenedAlloyCrucible.consumeLiquid(Liquids.water, 120/60);
hardenedAlloyCrucible.consumePower(18000/60);



/* ==== Units Region 单位 ==== */



const fleshReconstructor = extend(UnitFactory, "flesh-reconstructor", {
    canPlaceOn(tile, team, rotation){
        return Vars.state.rules.infiniteResources || tile.getLinkedTilesAs(this, this.tempTiles).sumf(o => o.floor().attributes.get(flesh)) >= 1
    },
    hasLiquids: true,
    liquidCapacity: 30,
    plans: Seq.with(
        new UnitFactory.UnitPlan(units.blade, 2700, ItemStack.with(
            items.flesh, 55,
            items.logicAlloy, 65
        ))
    )
});
setup(fleshReconstructor, unit, ItemStack.with(
    items.flesh, 100
), 3);
fleshReconstructor.consumeLiquid(Liquids.neoplasm, 48/60);
fleshReconstructor.consumePower(270/60);



/* ==== Effect Region 效果 ==== */



//效果-建筑治疗仪
const buildingHealer = Object.assign(new FixedRegenProjector("buildingHealer"), {
    hasLiquids: false,
    baseColor: Pal.heal,
    drawer: new DrawMulti(
        new DrawDefault(),
        Object.assign(new DrawPulseShape(false), {
            color: Pal.heal
        }),
        Object.assign(new DrawShape(), {
            layer: Layer.effect,
            color: Pal.heal,
            radius: 2.4,
            timeScl: 2,
            useWarmupRadius: true
        })
    ),
    range: 32,
    healPercent: 0.04,
    optionalMultiplier: 1.5,
    optionalUseTime: 200
});
setup(buildingHealer, effe, ItemStack.with(
    Items.titanium, 30,
    Items.silicon, 25,
    items.logicAlloy, 10
), 2, 320);
buildingHealer.consumeItem(Items.silicon).boost();
buildingHealer.consumePower(100/60);



//效果-篝火
const campfire = Object.assign(new UnitOverdriveProjector("gh"), {
    hasPower: false,
    hasLiquids: true,
    hasBoost: false,
    itemCapacity: 30,
    liquidCapacity: 30,
    lightRadius: 360,
    reload: 60,
    range: 320,
    useTime: 240,
    speedBoost: 3.2,
    allyStatus: statues.inspired,
    enemyStatus: StatusEffects.sapped,
    statusDuration: 60,
    updateEffectChance: 0.03,
    updateEffect: new MultiEffect(
        Fx.blastsmoke,
        Fx.generatespark
    ),
});
setup(campfire, effe, ItemStack.with(
    Items.copper, 300,
    Items.metaglass, 220,
    Items.plastanium, 175,
    items.timber, 200
), 5);
campfire.consumeItems(ItemStack.with(
    Items.pyratite, 4,
    items.timber, 8,
    items.kindlingAlloy, 4,
    items.flamefluidCrystal, 4
));
campfire.consumeLiquid(Liquids.slag, 36/60);



//效果-天穹
const skyDome = Object.assign(new ForceProjector("sky-dome"), {
    armor: 4,
    liquidCapacity: 20,
    shieldHealth: 3000,
    radius: 201.7,
    phaseRadiusBoost: 80,
    phaseShieldBoost: 1000,
    cooldownNormal: 5,
    cooldownLiquid: 1.2,
    cooldownBrokenBase: 3,
    coolantConsumption: 0.2
});
setup(skyDome, effe, ItemStack.with(
    Items.lead, 300,
    Items.phaseFabric, 120,
    items.logicAlloy, 225,
    items.hardenedAlloy, 180
), 5);
skyDome.itemConsumer = skyDome.consumeItem(Items.phaseFabric).boost();
skyDome.consumePower(1200/60);



//效果-建造指示器
const buildIndicator = Object.assign(new BuildTurret("jzzsq"), {
    range: 285,
    buildSpeed: 0.75
});
setup(buildIndicator, effe, ItemStack.with(
    Items.lead, 120,
    Items.thorium, 50,
    items.logicAlloy, 30
), 2, 720);
buildIndicator.consumePower(120/60);



//效果-装甲核心
const coreArmored = Object.assign(new ForceCoreBlock("zjhx"), {
    researchCostMultiplier: 0.4,
    armor: 8,
    itemCapacity: 10500,
    buildCostMultiplier: 0.4,
    unitType: units.omicron,
    unitCapModifier: 12,
    radius: 96,
    shieldHealth: 650,
    cooldownNormal: 1.2,
    cooldownBroken: 1.5,
    shieldRotation: 0,
    sides: 6
});
setup(coreArmored, effe, ItemStack.with(
    Items.copper, 9000,
    Items.lead, 8000,
    Items.metaglass, 2500,
    Items.thorium, 3500,
    Items.silicon, 6000,
    Items.plastanium, 1750
), 5, 11200);



//效果-Javelin机甲平台
const javelinPad = Object.assign(new MechPad("javelinPad"), {
    unitType: units.javelin,
    unitCapModifier: 0
});
setup(javelinPad, effe, ItemStack.with(
    Items.lead, 350,
    Items.titanium, 500,
    Items.silicon, 450,
    Items.plastanium, 400,
    Items.phaseFabric, 200
), 2, 1200);



//效果-复合装卸器
const compositeUnloader = Object.assign(new CompositeUnloader("composite-unloader"), {
    underBullets: true,
    speed: 25,
    allowCoreUnload: true
});
setup(compositeUnloader, effe, ItemStack.with(
    Items.metaglass, 15,
    Items.titanium, 30,
    Items.thorium, 15,
    Items.silicon, 20
), 1, 85);



module.exports = {
    fireCompany: fireCompany,
    adaptiveSource: adaptiveSource,
    neoplasm: neoplasm,
    bloodyDirt: bloodyDirt,
    smasher: smasher,
    nightmare: nightmare,
    ignite: ignite,
    blossom: blossom,
    distance: distance,
    seaquake: seaquake,
    grudge: grudge,
    magneticRail: magneticRail,
    chopper: chopper,
    treeFarm: treeFarm,
    vapourCondenser: vapourCondenser,
    biomassCultivator: biomassCultivator,
    fissionDrill: fissionDrill,
    compositeConveyor: compositeConveyor,
    compositeBridgeConveyor: compositeBridgeConveyor,
    compositeLiquidRouter: compositeLiquidRouter,
    compositeBridgeConduit: compositeBridgeConduit,
    conductorPowerNode: conductorPowerNode,
    flameGenerator: flameGenerator,
    burstReactor: burstReactor,
    damWall: damWall,
    damWallLarge: damWallLarge,
    hardenedWall: hardenedWall,
    hardenedWallLarge: hardenedWallLarge,
    fleshWall: fleshWall,
    thermalKiln: thermalKiln,
    metaglassPlater: metaglassPlater,
    mirrorglassPolisher: mirrorglassPolisher,
    impurityKindlingExtractor: impurityKindlingExtractor,
    kindlingExtractor: kindlingExtractor,
    conductorFormer: conductorFormer,
    logicAlloyProcessor: logicAlloyProcessor,
    detonationMixer: detonationMixer,
    slagCooler: slagCooler,
    crusher: crusher,
    timberBurner: timberBurner,
    electrothermalSiliconFurnace: electrothermalSiliconFurnace,
    fleshSynthesizer: fleshSynthesizer,
    liquidNitrogenCompressor: liquidNitrogenCompressor,
    hardenedAlloySmelter: hardenedAlloySmelter,
    hardenedAlloyCrucible: hardenedAlloyCrucible,
    fleshReconstructor: fleshReconstructor,
    buildingHealer: buildingHealer,
    campfire: campfire,
    skyDome: skyDome,
    buildIndicator: buildIndicator,
    coreArmored: coreArmored,
    javelinPad: javelinPad,
    compositeUnloader: compositeUnloader
}