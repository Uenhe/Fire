const
    statues = require("statues"),
    flying  = () => new UnitEntity.create(),
    payload = () => new PayloadUnit.create(),
    mech    = () => new MechUnit.create(),
    legs    = () => new LegsUnit.create();

function setup(u, cons, health, armor, hitsize, spd, rotspd){
    if(cons == flying || cons == payload) u.flying = true;
    if(cons == legs) u.hovering = true;
    u.constructor = cons;
    u.health = health;
    u.armor = armor;
    u.hitSize = hitsize;
    u.speed = spd;
    if(rotspd) u.rotateSpeed = rotspd
};
function setupLegs(u, amount, leng, spd, extens, baseoffset, pairoffset,
lengscl, movespace, groupsize, ripscl, lockbase, continuousmove){
    u.groundLayer = Layer.legUnit;
    u.legCount = amount;
    u.legLength = leng;
    u.legSpeed = spd;
    u.legExtension = extens;
    u.legBaseOffset = baseoffset;
    u.legPairOffset = pairoffset;
    u.legLengthScl = lengscl;
    u.legMoveSpace = movespace;
    u.legGroupSize = groupsize;
    u.rippleScale = ripscl;
    u.lockLegBase = lockbase;
    u.legContinuousMove = continuousmove;
    u.allowLegStep = true
};



/*========陆========*/



//爬辅-守护
const guarding = Object.assign(new UnitType("sh"), {
    drag: 0.1,
    buildSpeed: 1,
    itemCapacity: 20,
    stepShake: 1,
    shadowElevation: 0.1,
    canAttack: false,
    targetable: false
});
setup(guarding, legs, 140, 3, 8, 0.6, 4);
setupLegs(guarding, 4, 8, 0.2, 0, 2, 3, 1.6, 1.4, 2, 0.2, true, false);
guarding.abilities.add(
    new ForceFieldAbility(44, 0.25, 200, 400)
);



//爬辅-抗御
const resisting = Object.assign(new UnitType("ky"), {
    drag: 0.1,
    buildSpeed: 1.2,
    itemCapacity: 40,
    stepShake: 1,
    shadowElevation: 0.1,
    canAttack: false,
    targetable: false,
});
setup(resisting, legs, 420, 5, 12, 0.54, 3.6);
setupLegs(resisting, 4, 10, 0.2, 0, 2, 3, 1.6, 1.4, 2, 0.3, true, false);
resisting.abilities.add(
    new ForceFieldAbility(54, 0.35, 280, 340),
    new RegenFieldAbility(60/60, 40, Color.valueOf("8cfffb"), 120, 3)
);



//爬辅-卫戍
const garrison = Object.assign(new UnitType("ws"), {
    drag: 0.3,
    buildSpeed: 2,
    itemCapacity: 50,
    stepShake: 1,
    shadowElevation: 0.1,
    canAttack: false,
    targetable: false,
});
setup(garrison, legs, 930, 6, 16, 0.45, 2.7);
setupLegs(garrison, 6, 22, 0.2, 2, 8, 4, 1.6, 1.1, 3, 0.4, true, false);
garrison.abilities.add(
    new ForceFieldAbility(72, 0.6, 400, 300, 4, 45),
    new StatusFieldAbility(StatusEffects.overclock, 360, 360, 80)
);
garrison.weapons.add(
    Object.assign(new PointDefenseWeapon("fire-point-defense-weapon"), {
        reload: 10,
        x: 5, y: 2,
        targetInterval: 10,
        targetSwitchInterval: 15,
        bullet: Object.assign(new BulletType(), {
            damage: 10,
            maxRange: 125,
            shootEffect: Fx.sparkShoot,
            hitEffect: Fx.pointHit
        })
    })
);



//爬辅-庇护
const shelter = Object.assign(new UnitType("bh"), {
    drag: 0.3,
    buildSpeed: 3.5,
    itemCapacity: 100,
    drownTimeMultiplier: 2.4,
    stepShake: 1,
    shadowElevation: 0.25,
    canAttack: false
});
setup(shelter, legs, 7200, 10, 24, 0.4, 2.4);
setupLegs(shelter, 6, 28, 0.3, -15, 6, 2, 1, 1, 3, 1, true, false);
shelter.abilities.add(
    new EnergyForceFieldAbility(80, 120/60, 1050, 270, 10, 10, 20, 15),
    new RegenFieldAbility(150/60, 120, Color.valueOf("8cfffb"), 120, 3)
);



//变异尖刀-利刃
const blade = Object.assign(new UnitType("byjd"), {
    drownTimeMultiplier: 3,
    healColor: Pal.neoplasm1
});
setup(blade, mech, 630, 5, 8, 0.6);
blade.weapons.add(
    Object.assign(new Weapon("large-weapon"), {
        reload: 15,
        x: 4, y: 2,
        recoil: 1,
        top: false,
        ejectEffect: Fx.casing1,
        bullet: Object.assign(new BasicBulletType(3, 35), {
            width: 10, height: 12,
            lifetime: 65
        })
    })
);



//变异战锤-柄斧
const hatchet = Object.assign(new UnitType("hatchet"), {
    drownTimeMultiplier: 3,
    healColor: Pal.neoplasm1
});
setup(hatchet, mech, 960, 8, 10, 0.6);
hatchet.weapons.add(
    Object.assign(new Weapon("flamethrower"), {
        reload: 10,
        recoil: 3,
        shootY: 2,
        top: false,
        ejectEffect: Fx.none,
        shootSound: Sounds.flame,
        bullet: (() => {
            const speed = 6;
            const lifetime = 20;
            const b = new BulletType(speed, 60);
            Object.assign(b, {
                lifetime: lifetime,
                hitSize: 8,
                pierceCap: 5,
                statusDuration: 240,
                pierce: true,
                pierceBuilding: true,
                hittable: false,
                keepVelocity: false,
                status: statues.overgrown,
                shootEffect: new Effect(32, 80, e => {
                    const amount = 24;
                    const size = 4;
                    Draw.color(Pal.neoplasm2, Pal.neoplasmMid, Pal.neoplasm1, e.fin());
                    Angles.randLenVectors(e.id, amount, 8 + e.finpow() * speed * lifetime, e.rotation, 10, (x, y) => {
                        Fill.circle(e.x + x, e.y + y, 0.75 + e.fout() * size)
                    })
                }),
                hitEffect: new Effect(14, e => {
                    Draw.color(Pal.neoplasm2, Pal.neoplasmMid, e.fin());
                    Lines.stroke(0.5 + e.fout());
                    Angles.randLenVectors(e.id, 6, 1 + e.fin() * 15, e.rotation, 50, (x, y) => {
                        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1 + e.fout() * 3)
                    })
                }),
                despawnEffect: Fx.none
            });
            return b
        })()
    })
);



//变异堡垒-坚城
const castle = Object.assign(new UnitType("bybl"), {
    mechFrontSway: 0.55,
    stepShake: 0.1,
    drownTimeMultiplier: 4,
    targetAir: false,
    healColor: Pal.neoplasm1
});
setup(castle, mech, 8200, 9, 13, 4/7.5, 6);
castle.abilities.add(
    Object.assign(new EnergyFieldAbility(15, 30, 128), {
        status: StatusEffects.burning,
        statusDuration: 40,
        maxTargets: 40,
        healPercent: 0.1,
        color: Color.valueOf("ff3300")
    })
),
castle.weapons.add(
    Object.assign(new Weapon("artillery"), {
        reload: 30,
        x: 9, y: 1,
        top: false,
        recoil: 6,
        shake: 3,
        ejectEffect: Fx.casing2,
        shootSound: Sounds.artillery,
        shoot: Object.assign(new ShootPattern(), {
            shots: 2,
            shotDelay: 6
        }),
        bullet: Object.assign(new ArtilleryBulletType(3, 120, "shell"), {
            width: 16, height: 16,
            lifetime: 120,
            knockback: 1.2,
            splashDamage: 220,
            splashDamageRadius: 40,
            hitEffect: Fx.blastExplosion,
            backColor: Pal.bulletYellowBack,
            frontColor: Pal.bulletYellow
        })
    })
);



//错误
const error = Object.assign(new UnitType("error"), {
    drag: 0.1,
    stepShake: 1,
    legSplashRange: 30,
    legSplashDamage: 32,
    shadowElevation: 0.65
});
setup(error, legs, 128000, 34, 23, 7.5/7.5, 2.7);
setupLegs(error, 8, 300, 0.2, -15, 10, 3, 1, 1, 3, 5, false, true);
error.weapons.add(
    Object.assign(new Weapon("error-sapper"), {
        reload: 12,
        x: 8, y: -5,
        rotateSpeed: 6,
        rotate: true,
        shootSound: Sounds.sap,
        bullet: Object.assign(new SapBulletType(), {
            damage: 165,
            length: 128, width: 2,
            lifetime: 30,
            knockback: -1,
            sapStrength: 0.6,
            color: Color.valueOf("990003"),
            hitColor: Color.valueOf("990003"),
            shootEffect: Fx.shootSmall
        })
    }),
    Object.assign(new Weapon("error-laser"), {
        reload: 15,
        x: 9, y: -7,
        shake: 3,
        recoil: 3,
        shadow: 8,
        shootY: 7,
        rotateSpeed: 2,
        rotate: true,
        top: false,
        shootSound: Sounds.shootBig,
        shoot: Object.assign(new ShootPattern(), {
            shots: 3,
            shotDelay: 8
        }),
        bullet: Object.assign(new ShrapnelBulletType(), {
            length: 320, width: 20,
            damage: 525,
            toColor: Color.valueOf("990003")
        })
    })
);



/*========空========*/



//奥密克戎
const omicron = Object.assign(new UnitType("gnj"), {
    defaultCommand: UnitCommand.rebuildCommand,
    drag: 0.08,
    accel: 0.15,
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
});
setup(omicron, flying, 580, 4, 12, 28.5/7.5, 22);
omicron.abilities.add(
    new RepairFieldAbility(20, 300, 40),
    new ShieldRegenFieldAbility(20, 80, 300, 40),
    new StatusFieldAbility(StatusEffects.overclock, 300, 300, 40),
    new StatusFieldAbility(StatusEffects.overdrive, 300, 300, 40)
);
omicron.weapons.add(
    Object.assign(new Weapon("fire-gnj-weapon"), {
        reload: 12,
        x: 4, y: 0.6,
        inaccuracy: 1.1,
        top: false,
        rotate: true,
        shootSound: Sounds.lasershoot,
        bullet: Object.assign(new LaserBoltBulletType(10, 22), {
            lifetime: 25,
            healPercent: 4,
            width: 2.4, height: 5.4,
            buildingDamageMultiplier: 0.2,
            collidesTeam: true,
            backColor: Color.valueOf("8cfffb"),
            frontColor: Color.white,
            status: StatusEffects.electrified,
            statusDuration: 150
        })
    })
);



//先锋
const pioneer = Object.assign(new UnitType("pioneer"), {
    defaultCommand: UnitCommand.repairCommand,
    drag: 0.05,
    accel: 0.1,
    payloadCapacity: 3.5 * 3.5 * Vars.tilePayload,
    engineOffset: 13,
    engineSize: 7,
    itemCapacity: 30,
    buildSpeed: 2.6,
    lowAltitude: true,
    abilities: Seq.with(
        new DashAbility(10, 24, 120, new Effect(40, e => {
            Draw.color(e.color, Color.darkGray, e.fin());
            Angles.randLenVectors(e.id, 6, 6 + e.fin() * 8, (x, y) => {
                Fill.square(e.x + x, e.y + y, e.fout() * 3 + 0.5, 45);
            })
        }))
    )
});
setup(pioneer, payload, 5600, 7, 30, 20.25/7.5, 3);
pioneer.weapons.add(
    Object.assign(new Weapon("emp-cannon-mount"), {
        reload: 60/0.5,
        x: 0, y: 0,
        shake: 1,
        recoil: 3,
        rotateSpeed: 2.5,
        mirror: false,
        rotate: true,
        shootSound: Sounds.laser,
        bullet: (() => {
            const rad = 60;
            return Object.assign(new EmpBulletType(), {
                sprite: "circle-bullet",
                speed: 6, damage: 80,
                lifetime: 35,
                width: 10, height: 10,
                splashDamage: 120,
                splashDamageRadius: rad,
                powerDamageScl: 2,
                healPercent: 10,
                timeIncrease: 0,
                shrinkY: 0,
                hitShake: 3,
                trailLength: 18, trailWidth: 5,
                trailInterval: 3,
                lightRadius: 60, lightOpacity: 0.6,
                scaleLife: true,
                trailRotation: true,
                statusDuration: 180,
                status: StatusEffects.electrified,
                hitColor: Pal.heal,
                lightColor: Pal.heal,
                backColor: Pal.heal,
                frontColor: Color.white,
                trailColor: Pal.heal,
                hitSound: Sounds.plasmaboom,
                shootEffect: Fx.hitEmpSpark,
                smokeEffect: Fx.shootBigSmoke2,
                trailEffect: new Effect(16, e => {
                    Draw.color(Pal.heal);
                    for(var s of Mathf.signs){
                        Drawf.tri(e.x, e.y, 4, 30 * e.fslope(), e.rotation + s * 90)
                    }
                }),
                hitEffect: new Effect(50, 100, e => {
                    const points = 8;
                    var offset = Mathf.randomSeed(e.id, 360);
                    e.scaled(7, b => {
                        Draw.color(Pal.heal, b.fout());
                        Fill.circle(e.x, e.y, rad)
                    });
                    Draw.color(Pal.heal);
                    Lines.stroke(e.fout() * 3);
                    Lines.circle(e.x, e.y, rad);
                    for(var i = 0; i < points; i += 1){
                        var angle = i * 360 / points + offset;
                        Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 4, 30 * e.fout(), angle)
                    };
                    Fill.circle(e.x, e.y, 12 * e.fout());
                    Draw.color();
                    Fill.circle(e.x, e.y, 6 * e.fout());
                    Drawf.light(e.x, e.y, rad * 1.6, Pal.heal, e.fout())
                })
            })
        })()
    }),
    Object.assign(new Weapon("heal-weapon"), {
        reload: 30/2,
        x: 6, y: 10,
        rotateSpeed: 4,
        rotate: true,
        shootSound: Sounds.lasershoot,
        bullet: Object.assign(new LaserBoltBulletType(8, 10), {
            lifetime: 20,
            width: 5, height: 8,
            healPercent: 3,
            homingPower: 0.15,
            homingDelay: 10,
            homingRange: 120,
            trailLength: 4, trailWidth: 3.5,
            trailInterval: 3,
            collidesTeam: true,
            trailRotation: true,
            backColor: Pal.heal,
            frontColor: Color.white,
            trailColor: Pal.heal,
            trailEffect: Fx.colorSpark,
            fragSpread: 0,
            fragRandomSpread: 15,
            fragBullets: 2,
            fragBullet: Object.assign(new LaserBulletType(20), {
                length: 140, width: 8,
                lifetime: 15,
                pierceCap: 2,
                pierceBuilding: true,
                colors: [Pal.heal, Pal.heal, Color.white]
            })
        })
    })
);



//Javelin机甲
const javelin = Object.assign(new UnitType("javelin"), {
    drag: 0.01,
    accel: 0.015,
    engineOffset: 6,
    itemCapacity: 30,
    buildSpeed: 1,
    lowAltitude: true
});
setup(javelin, flying, 340, 1, 12, 67.5/7.5, 22);
javelin.abilities.add(
    new MoveLightningAbility(10, 16, 0.2, 16, 3.6, 8, Color.valueOf("a9d8ff"), "fire-javelin-heat")
);
javelin.weapons.add(
    Object.assign(new Weapon("fire-javelin-weapon"), {
        reload: 35,
        x: 3, y: 1,
        inaccuracy: 3,
        velocityRnd: 0.2,
        top: false,
        shootSound: Sounds.missile,
        shoot: Object.assign(new ShootPattern(), {
            shots: 4
        }),
        bullet: Object.assign(new MissileBulletType(), {
            damage: 21,
            speed: 5,
            lifetime: 36,
            width: 8, height: 8,
            splashDamage: 2,
            splashDamageRadius: 20,
            weaveScale: 8,
            weaveMag: 2,
            trailColor: Color.valueOf("b6c6fd"),
            hitEffect: Fx.blastExplosion,
            despawnEffect: Fx.blastExplosion,
            backColor: Pal.bulletYellowBack,
            frontColor: Pal.bulletYellow
        })
    })
);



//空风-萤火
const firefly = Object.assign(new UnitType("firefly"), {
    drag: 0.04,
    accel: 0.08,
    engineOffset: 5.6,
    itemCapacity: 15,
    circleTarget: true,
    lowAltitude: false
});
setup(firefly, flying, 150, 3, 10, 12/7.5, 6);
firefly.abilities.add(
    new MoveLightningAbility(2, 8, 0.1, 0, 1.2, 1.6, Color.valueOf("a9d8ff"))
);
firefly.weapons.add(
    Object.assign(new Weapon(), {
        shoot: ShootSpread(3, 60),
        reload: 600,
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
    })
);



//空风-烛光
const candlelight = Object.assign(new UnitType("candlelight"), {
    drag: 0.04,
    accel: 0.08,
    engineOffset: 5.6,
    itemCapacity: 25,
    circleTarget: true,
    lowAltitude: false,
});
setup(candlelight, flying, 280, 4, 14, 20.25/7.5, 6);
candlelight.abilities.add(
    new MoveLightningAbility(2, 13, 0.4, 0, 1.2, 1.6, Color.valueOf("a9d8ff"))
);
candlelight.weapons.add(
    Object.assign(new Weapon("fire-candlelight-weapon"), {
        shoot: ShootSpread(5, 36),
        reload: 600,
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
    })
);



//灼阳
const apollo = Object.assign(new UnitType("dk"), {
    drag: 0.08,
    accel: 0.15,
    itemCapacity: 280,
    engineOffset: 6,
    lowAltitude: true,
    faceTarget: false
});
setup(apollo, flying, 76000, 18, 64, 1.5/7.5, 2);
apollo.abilities.add(
    new ForceFieldAbility(96, 3, 2000, 480)
);
apollo.weapons.add(
    Object.assign(new Weapon("fire-dk-weaponMain"), {
        reload: 495,
        x: 0, y: -10,
        inaccuracy: 0.7,
        shootY: -10,
        rotateSpeed: 2,
        rotate: true,
        mirror: false,
        shootSound: Sounds.release,
        bullet: Object.assign(new BasicBulletType(12, 840), {
            lifetime: 50,
            width: 16, height: 20,
            splashDamageRadius: 80,
            splashDamage: 560,
            pierceCap: 2,
            pierceBuilding: true,
            collidesGround: true,
            fragBullets: 12,
            fragBullet: Object.assign(new BasicBulletType(10, 0), {
                lifetime: 35,
                splashDamageRadius: 70,
                splashDamage: 320,
                collidesGround: true,
                fragBullets: 2,
                fragBullet: Object.assign(new LaserBulletType(160), {
                    lifetime: 16,
                    length: 260,
                    colors: [Color.valueOf("a9d8ff66"), Color.valueOf("a9d8ff66"), Color.white],
                    fragBullets: 2,
                    fragBullet: Object.assign(new LightningBulletType(), {
                        damage: 8,
                        lightningLength: 30,
                        collidesAir: true
                    })
                })
            })
        })
    }),
    Object.assign(new Weapon("fire-dk-weaponLaser"), {
        reload: 60/3,
        x: 8, y: -23,
        shootY: 12,
        rotateSpeed: 3,
        rotate: true,
        shootSound: Sounds.laser,
        bullet: Object.assign(new LaserBulletType(288), {
            lifetime: 16,
            length: 225, width: 22,
            colors: [Color.valueOf("f6efa1"), Color.valueOf("f6efa1"), Color.white],
            lightningSpacing: 15,
            lightningLength: 0.6,
            lightningDelay: 1.2,
            lightningLengthRand: 10,
            lightningDamage: 12,
            lightningAngleRand: 30,
            status: StatusEffects.shocked
        })
    }),
    Object.assign(new Weapon("fire-dk-weaponArtillery"), {
        reload: 60/7.5,
        x: 18, y: 22,
        inaccuracy: 2.2,
        shootY: 22,
        rotateSpeed: 4,
        rotate: true,
        ejectEffect: Fx.casing1,
        shootSound: Sounds.shoot,
        bullet: Object.assign(new FlakBulletType(14, 60), {
            lifetime: 16,
            width: 14, height: 20,
            splashDamageRadius: 27,
            splashDamage: 85,
            pierceCap: 3,
            pierceBuilding: true,
            collidesGround: true,
            status: StatusEffects.blasted
        })
    }),
    Object.assign(new PointDefenseWeapon("fire-dk-weaponDefense"), {
        reload: 60/10,
        x: 15.5, y: 12,
        targetInterval: 4,
        targetSwitchInterval: 8,
        bullet: Object.assign(new BulletType(1, 65), {
            maxRange: 168,
            shootEffect: Fx.sparkShoot,
            hitEffect: Fx.pointHit
        })
    })
);



module.exports = {
    guarding: guarding,
    resisting: resisting,
    garrison: garrison,
    shelter: shelter,
    blade: blade,
    hatchet: hatchet,
    castle: castle,
    error: error,
    omicron: omicron,
    pioneer: pioneer,
    javelin: javelin,
    firefly: firefly,
    candlelight: candlelight,
    apollo: apollo
}
