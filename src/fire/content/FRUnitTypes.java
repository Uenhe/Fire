package fire.content;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import fire.FRUtils;
import fire.entities.abilities.*;
import fire.type.FleshUnitType;
import mindustry.ai.UnitCommand;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.PointDefenseWeapon;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;

import static fire.FRUtils.colors;
import static fire.FRVars.*;
import static mindustry.Vars.tilePayload;

public class FRUnitTypes{

    public static UnitType

        //legs support
        guarding, resisting, garrison, shelter, blessing,

        //mech mutated
        blade, hatchet, castle,

        //ground
        error,

        //air support
        omicron, pioneer,

        //air kamikaze
        firefly, candlight, lampryo, lumiflame,

        //air
        javelin, apollo,

        //naval
        mechanicalTide;

    public static void load(){

        //region legs support

        guarding = new UnitType("sh"){{
            constructor = LegsUnit::create;
            hovering = true;
            health = 140;
            armor = 3;
            hitSize = 8;
            speed = 0.72f;
            drag = 0.1f;
            rotateSpeed = 4f;
            buildSpeed = 1f;
            itemCapacity = 20;
            canAttack = false;

            stepShake = 1f;
            shadowElevation = 0.1f;
            groundLayer = Layer.legUnit;
            legCount = 4;
            legLength = 8f;
            legSpeed = 0.2f;
            legExtension = 0.0f;
            legBaseOffset = 2f;
            legPairOffset = 3f;
            legLengthScl = 1.6f;
            legMoveSpace = 1.4f;
            legGroupSize = 2;
            rippleScale = 0.2f;
            lockLegBase = true;
            legContinuousMove = true;
            allowLegStep = true;

            abilities.add(
                new ForceFieldAbility(44.0f, 0.5f, 200.0f, 400.0f)
            );
        }};

        resisting = new UnitType("ky"){{
            constructor = LegsUnit::create;
            hovering = true;
            health = 420;
            armor = 5;
            hitSize = 12;
            speed = 0.65f;
            drag = 0.1f;
            rotateSpeed = 3.6f;
            buildSpeed = 1.2f;
            itemCapacity = 40;
            canAttack = false;

            stepShake = 1f;
            shadowElevation = 0.1f;
            groundLayer = Layer.legUnit;
            legCount = 4;
            legLength = 10f;
            legSpeed = 0.2f;
            legExtension = 0.0f;
            legBaseOffset = 2f;
            legPairOffset = 3f;
            legLengthScl = 1.6f;
            legMoveSpace = 1.4f;
            legGroupSize = 2;
            rippleScale = 0.3f;
            lockLegBase = true;
            legContinuousMove = true;
            allowLegStep = true;

            abilities.add(
                new ForceFieldAbility(54f, 0.6f, 300.0f, 360.0f),
                new RegenFieldAbility(1.5f, 40.0f, _8cfffb)
            );
        }};

        garrison = new UnitType("ws"){{
            constructor = LegsUnit::create;
            hovering = true;
            health = 930;
            armor = 6;
            hitSize = 16;
            speed = 0.54f;
            drag = 0.3f;
            rotateSpeed = 2.7f;
            buildSpeed = 2f;
            itemCapacity = 50;
            canAttack = false;

            stepShake = 1f;
            shadowElevation = 0.1f;
            groundLayer = Layer.legUnit;
            legCount = 6;
            legLength = 22f;
            legSpeed = 0.2f;
            legExtension = 2f;
            legBaseOffset = 8f;
            legPairOffset = 4f;
            legLengthScl = 1.6f;
            legMoveSpace = 1.1f;
            legGroupSize = 3;
            rippleScale = 0.4f;
            lockLegBase = true;
            legContinuousMove = true;
            allowLegStep = true;

            abilities.add(
                new ForceFieldAbility(72f, 1.5f, 400f, 300f, 4, 45f),
                new StatusFieldAbility(StatusEffects.overclock, 360f, 360f, 80f)
            );

            weapons.add(
                new PointDefenseWeapon("fire-point-defense-weapon"){{
                    reload = 10f;
                    x = 5f;
                    y = 2f;
                    targetInterval = 10f;
                    targetSwitchInterval = 15f;
                    bullet = new BulletType(1f, 10f){{
                        maxRange = 125f;
                        shootEffect = Fx.sparkShoot;
                        hitEffect = Fx.pointHit;
                    }};
                }}
            );
        }};

        shelter = new UnitType("bh"){{
            constructor = LegsUnit::create;
            hovering = true;
            health = 7200;
            armor = 10;
            hitSize = 24;
            speed = 0.4f;
            drag = 0.3f;
            rotateSpeed = 2.4f;
            buildSpeed = 3.5f;
            itemCapacity = 100;
            drownTimeMultiplier = 2.4f;
            canAttack = false;

            stepShake = 1f;
            shadowElevation = 0.25f;
            groundLayer = Layer.legUnit;
            legCount = 6;
            legLength = 28f;
            legSpeed = 0.3f;
            legExtension = -15f;
            legBaseOffset = 6f;
            legPairOffset = 2f;
            legLengthScl = 1f;
            legMoveSpace = 1f;
            legGroupSize = 3;
            rippleScale = 1f;
            lockLegBase = true;
            legContinuousMove = true;
            allowLegStep = true;

            abilities.add(
                new EnergyForceFieldAbility(80f, 2.5f, 1080f, 270f, 20, 16, 10, 10.0f),
                new RegenFieldAbility(2.5f, 120f, _8cfffb)
            );
        }};

        blessing = new UnitType("blessing"){{
            constructor = LegsUnit::create;
            hovering = true;
            health = 26400;
            armor = 18;
            hitSize = 27f;
            speed = 0.5f;
            drag = 0.1f;
            rotateSpeed = 2.1f;
            buildSpeed = 4f;
            itemCapacity = 160;
            drownTimeMultiplier = 3.2f;
            canAttack = false;

            stepShake = 1f;
            shadowElevation = 0.8f;
            groundLayer = Layer.legUnit;
            legCount = 8;
            legLength = 60f;
            legSpeed = 0.2f;
            legExtension = -15f;
            legBaseOffset = -8f;
            legPairOffset = 3f;
            legLengthScl = 1f;
            legMoveSpace = 0.8f;
            legGroupSize = 4;
            rippleScale = 2.4f;
            lockLegBase = true;
            legContinuousMove = true;
            allowLegStep = true;

            abilities.add(
                new EnergyForceFieldAbility(160.0f, 4.0f, 7200.0f, 600.0f, 32, 32, 30, 40.0f){{
                    sides = 24;
                    lightningColor.set(Pal.surge);

                    extended = true;
                    ext_bearingFactor = 0.4f;
                    ext_counterBulletSpeedFactor = 1.2f;
                    ext_counterBulletDamageFactor = 0.8f;
                    ext_counterBulletHomingChancePercentage = 45;
                    ext_node = new FRUtils.TimeNode(140, 180, 240, 300);
                }},

                new RegenFieldAbility(3.0f, 120.0f, _8cfffb),

                new ExtinguishFieldAbility(120.0f, _8cfffb),

                new DebuffRemoveFieldAbility(120.0f, 120.0f, new Effect(30.0f, e -> {
                    Draw.color(_8cfffb, Color.lightGray, e.fin());
                    Angles.randLenVectors(e.id, 3, 6.0f + e.finpow() * 20.0f, (x, y) ->
                        Fill.square(e.x + x, e.y + y, e.fout() * 4.0f + 0.5f, 45.0f));
                }))
            );
        }};

        //endregion
        //region mech mutated

        blade = new FleshUnitType("byjd"){{
            constructor = MechUnit::create;
            health = 630.0f;
            armor = 5.0f;
            hitSize = 10.0f;
            speed = 0.6f;
            drownTimeMultiplier = 3.0f;

            mechSideSway = 0.6f;
            mechFrontSway = 0.2f;

            weapons.add(
                new Weapon("fire-byjd-weapon"){{
                    reload = 30.0f;
                    x = 0.0f;
                    shootX = 6.6f;
                    shootY = 4.2f;
                    recoil = 1.0f;
                    top = false;
                    ejectEffect = Fx.casing1;

                    shoot.shots = 3;
                    shoot.shotDelay = 4.0f;
                    bullet = new BasicBulletType(5.0f, 30.0f){{
                        lifetime = 40.0f;
                        width = 5.4f;
                        height = 10.8f;

                        backColor = _f9a27a;
                        frontColor = _ffd8e8;
                    }};
                }}
            );
        }};

        hatchet = new FleshUnitType("hatchet"){{
            constructor = MechUnit::create;
            health = 960.0f;
            armor = 8.0f;
            hitSize = 13.0f;
            speed = 0.6f;
            drownTimeMultiplier = 4.0f;

            mechSideSway = 0.6f;
            mechFrontSway = 0.45f;

            weapons.add(
                new Weapon("fire-hatchet-weapon"){{
                    reload = 10.0f;
                    recoil = 2.0f;
                    x = 0.0f;
                    shootX = 9.6f;
                    shootY = 4.0f;
                    top = false;
                    shootSound = Sounds.flame;

                    bullet = new BulletType(6.0f, 60.0f){{
                        lifetime = 20.0f;
                        hitSize = 8.0f;
                        pierceCap = 5;
                        pierce = true;
                        pierceBuilding = true;

                        hittable = reflectable = absorbable = false;
                        keepVelocity = false;
                        status = FRStatusEffects.overgrown;
                        statusDuration = 240.0f;

                        hitEffect = despawnEffect = Fx.none;
                        shootEffect = new Effect(27.0f, 80.0f, e -> {
                            Draw.color(Pal.neoplasm2, Pal.neoplasmMid, Pal.neoplasm1, e.fin());
                            Angles.randLenVectors(e.id, 32, 12.0f + e.finpow() * speed * lifetime, e.rotation, 10.0f, (x, y) ->
                                Fill.circle(e.x + x, e.y + y, 0.75f + e.fout() * 4.0f));
                        });
                    }};
                }}
            );
        }};

        castle = new FleshUnitType("bybl"){{
            constructor = MechUnit::create;
            health = 8200.0f;
            armor = 9.0f;
            hitSize = 18.0f;
            speed = 0.53f;
            rotateSpeed = 4.8f;
            drownTimeMultiplier = 5.0f;

            mechSideSway = 0.6f;
            mechFrontSway = 0.7f;

            abilities.add(
                new EnergyFieldAbility(15.0f, 30.0f, 128.0f){{
                    maxTargets = 40;
                    healPercent = 0.1f;
                    statusDuration = 40.0f;
                    status = StatusEffects.burning;

                    color = _ff3300;
                }}
            );

            weapons.add(
                new Weapon("fire-bybl-weapon"){{
                    reload = 40.0f;
                    x = 0.0f;
                    shootX = 16.0f;
                    shootY = 5.4f;
                    recoil = 5.0f;
                    shake = 3.0f;
                    top = false;
                    ejectEffect = Fx.casing2;
                    shootSound = Sounds.artillery;
                    targetAir = false;

                    shoot.shots = 2;
                    shoot.shotDelay = 8.0f;
                    bullet = new ArtilleryBulletType(2.0f, 90.0f, "shell"){{
                        lifetime = 150.0f;
                        width = 16.0f;
                        height = 16.0f;
                        knockback = 1.2f;
                        splashDamage = 200.0f;
                        splashDamageRadius = 40.0f;

                        hitEffect = Fx.blastExplosion;
                        backColor = _f9a27a;
                        frontColor = _ffd8e8;
                    }};
                }}
            );
        }};

        //endregion
        //region ground

        error = new FleshUnitType("error"){{
            constructor = LegsUnit::create;
            hovering = true;
            health = 128000;
            armor = 30;
            hitSize = 23;
            speed = 1f;
            drag = 0.1f;
            rotateSpeed = 2.7f;

            stepShake = 1f;
            shadowElevation = 0.65f;
            groundLayer = Layer.legUnit;
            legCount = 8;
            legLength = 300f;
            legSpeed = 0.2f;
            legExtension = -15f;
            legBaseOffset = 10f;
            legPairOffset = 3f;
            legLengthScl = 1f;
            legMoveSpace = 1f;
            legGroupSize = 3;
            rippleScale = 5f;
            legSplashDamage = 32f;
            legSplashRange = 30f;
            lockLegBase = false;
            legContinuousMove = false;
            allowLegStep = true;

            weapons.add(

                new Weapon("error-sapper"){{
                    reload = 12.0f;
                    x = 8.0f;
                    y = -5.0f;
                    rotateSpeed = 6.0f;
                    rotate = true;
                    shootSound = Sounds.sap;
                    bullet = new SapBulletType(){{
                        damage = 165.0f;
                        length = 128.0f;
                        width = 2.0f;
                        lifetime = 30.0f;
                        knockback = -1.0f;
                        sapStrength = 0.6f;

                        color = hitColor = _990003;
                        shootEffect = Fx.shootSmall;
                    }};
                }},

                new Weapon("error-laser"){{
                    reload = 15f;
                    x = 9f;
                    y = -7f;
                    shake = 3f;
                    recoil = 3f;
                    shadow = 8f;
                    shootY = 7f;
                    rotateSpeed = 2f;
                    rotate = true;
                    top = false;
                    shootSound = Sounds.shootBig;
                    shoot.shots = 3;
                    shoot.shotDelay = 8f;
                    bullet = new ShrapnelBulletType(){{
                        damage = 525f;
                        length = 320f;
                        width = 20f;
                        toColor = _990003;
                    }};
                }}
            );
        }};

        //endregion
        //region air support

        omicron = new UnitType("gnj"){{
            constructor = UnitEntity::create;
            defaultCommand = UnitCommand.rebuildCommand;
            flying = true;
            health = 580;
            armor = 4;
            hitSize = 12;
            speed = 3.8f;
            drag = 0.08f;
            accel = 0.15f;
            rotateSpeed = 22f;
            isEnemy = false;
            lowAltitude = true;
            faceTarget = true;
            createWreck = false; //disable damage upon death
            coreUnitDock = true;
            engineOffset = 6f;
            itemCapacity = 90;
            mineTier = 4;
            mineRange += 40f;
            mineSpeed = 8.5f;
            buildRange += 40f;
            buildSpeed = 3f;

            abilities.add(
                new RepairFieldAbility(20f, 300f, 40f),
                new ShieldRegenFieldAbility(20f, 80f, 300f, 40f),
                new StatusFieldAbility(StatusEffects.overclock, 300f, 300f, 40f)
            );

            weapons.add(
                new Weapon("heal-weapon-amount"){{
                    reload = 12.0f;
                    x = 4.0f;
                    y = 0.6f;
                    inaccuracy = 1.1f;
                    top = false;
                    rotate = true;
                    shootSound = Sounds.lasershoot;
                    bullet = new LaserBoltBulletType(10.0f, 22.0f){{
                        lifetime = 25.0f;
                        healPercent = 4.0f;
                        width = 2.4f;
                        height = 5.4f;
                        buildingDamageMultiplier = 0.2f;
                        collidesTeam = true;
                        backColor = _8cfffb;
                        frontColor = Color.white;
                        status = StatusEffects.electrified;
                        statusDuration = 150.0f;
                    }};
                }}
            );
        }};

        pioneer = new UnitType("pioneer"){{
            constructor = PayloadUnit::create;
            defaultCommand = UnitCommand.repairCommand;
            flying = true;
            health = 5600.0f;
            armor = 7.0f;
            hitSize = 30.0f;
            speed = 2.7f;
            drag = 0.05f;
            accel = 0.1f;
            rotateSpeed = 3.0f;
            payloadCapacity = 3.5f * 3.5f * tilePayload;
            engineOffset = 13.0f;
            engineSize = 7.0f;
            itemCapacity = 120;
            buildSpeed = 3.4f;
            buildRange += 80.0f;
            lowAltitude = true;

            abilities.add(
                new DashAbility(6.7f, 15, 120, 6),
                new FirstAidAbility(2400, 75, 500, 5, FRStatusEffects.sanctuaryGuard, 120, 120, new MultiEffect(
                    new Effect(45.0f, e -> {
                        Draw.color(Pal.heal);
                        Lines.stroke(e.fout() * 4.0f);
                        Lines.circle(e.x, e.y, 4.0f + e.finpow() * 32.0f);
                    }),
                    FRFx.squareEffect(60.0f, 10, 4.0f, 0.0f, Pal.heal),
                    FRFx.crossEffect(50.0f, 3.0f, 45.0f, true, Pal.heal)
                ))
            );

            weapons.add(

                new Weapon("emp-cannon-mount"){{
                    reload = 120.0f;
                    x = 0.0f;
                    y = 0.0f;
                    shake = 1.0f;
                    recoil = 3.0f;
                    rotateSpeed = 2.5f;
                    mirror = false;
                    rotate = true;
                    shootSound = Sounds.laser;

                    bullet = new EmpBulletType(){{
                        final float rad = 60.0f;

                        sprite = "circle-bullet";
                        speed = 6.0f;
                        damage = 80.0f;
                        lifetime = 35.0f;
                        width = 10.0f;
                        height = 10.0f;
                        splashDamage = 120.0f;
                        splashDamageRadius = rad;
                        healPercent = 10.0f;
                        hitShake = 3.0f;
                        trailLength = 18;
                        trailWidth = 5;
                        trailInterval = 3.0f;
                        lightRadius = 60f;
                        lightOpacity = 0.6f;
                        scaleLife = true;
                        trailRotation = true;
                        statusDuration = 180.0f;
                        status = StatusEffects.electrified;

                        radius = rad;
                        timeIncrease = 0.0f;
                        powerDamageScl = 2.0f;
                        powerSclDecrease = 0.5f;
                        unitDamageScl = 1.0f;

                        hitSound = Sounds.plasmaboom;
                        hitColor = Pal.heal;
                        lightColor = Pal.heal;
                        backColor = Pal.heal;
                        frontColor = Color.white;
                        trailColor = Pal.heal;
                        shootEffect = Fx.hitEmpSpark;
                        smokeEffect = Fx.shootBigSmoke2;

                        trailEffect = new Effect(16f, e -> {
                            Draw.color(Pal.heal);
                            for(int s : Mathf.signs)
                                Drawf.tri(e.x, e.y, 4.0f, 30f * e.fslope(), e.rotation + s * 90f);
                        });

                        hitEffect = new Effect(50f, 100f, e -> {
                            e.scaled(7f, b -> {
                                Draw.color(Pal.heal, b.fout());
                                Fill.circle(e.x, e.y, rad);
                            });
                            Draw.color(Pal.heal);
                            Lines.stroke(e.fout() * 3f);
                            Lines.circle(e.x, e.y, rad);

                            float offset = Mathf.randomSeed(e.id, 360f);
                            for(byte i = 0, points = 8; i < points; i++){
                                float angle = i * 360f / points + offset;
                                Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 4.0f, 30f * e.fout(), angle);
                            }

                            Fill.circle(e.x, e.y, 12f * e.fout());
                            Draw.color();
                            Fill.circle(e.x, e.y, 6f * e.fout());
                            Drawf.light(e.x, e.y, rad * 1.6f, Pal.heal, e.fout());
                        });
                    }};
                }},

                new Weapon("heal-weapon"){{
                    reload = 15f;
                    x = 6f;
                    y = 10f;
                    rotateSpeed = 4f;
                    rotate = true;
                    shootSound = Sounds.lasershoot;
                    bullet = new LaserBoltBulletType(8f, 10f){{
                        lifetime = 20f;
                        width = 5f;
                        height = 8f;
                        healPercent = 3f;
                        homingPower = 0.15f;
                        homingDelay = 10f;
                        homingRange = 120f;
                        trailLength = 4;
                        trailWidth = 3.5f;
                        trailInterval = 3f;
                        collidesTeam = true;
                        trailRotation = true;

                        backColor = Pal.heal;
                        frontColor = Color.white;
                        trailColor = Pal.heal;
                        trailEffect = Fx.colorSpark;

                        fragSpread = 0.0f;
                        fragRandomSpread = 15f;
                        fragBullets = 2;
                        fragBullet = new LaserBulletType(20f){{
                            length = 140f;
                            width = 8f;
                            lifetime = 15f;
                            pierceCap = 2;
                            pierceBuilding = true;
                            colors(colors, Pal.heal, Pal.heal, Color.white);
                        }};
                    }};
                }}
            );
        }};

        //endregion
        //region air kamikaze

        firefly = new UnitType("firefly"){{
            constructor = UnitEntity::create;
            flying = true;
            health = 150;
            armor = 3;
            hitSize = 10f;
            speed = 1.6f;
            drag = 0.04f;
            accel = 0.08f;
            range = maxRange = 40.0f;
            engineOffset = 5.6f;
            trailLength = 3;
            itemCapacity = 15;
            circleTarget = true;
            lowAltitude = false;

            abilities.add(
                new MoveLightningAbility(2f, 8, 0.1f, 0.0f, 1.2f, 1.6f, Pal.lancerLaser)
            );

            weapons.add(
                new Weapon(){{
                    shootCone = 180.0f;
                    mirror = false;
                    top = false;
                    shootOnDeath = true;
                    shootSound = Sounds.explosion;
                    shoot = new ShootSpread(3, 60f);
                    bullet = new ShrapnelBulletType(){{
                        damage = 25f;
                        length = 70f;
                        width = 17f;
                        killShooter = true;
                        hitEffect = Fx.pulverize;
                        hitSound = Sounds.explosion;
                    }};
                }}
            );
        }};

        candlight = new UnitType("candlelight"){{
            constructor = UnitEntity::create;
            flying = true;
            health = 280;
            armor = 4;
            hitSize = 14f;
            speed = 2.7f;
            drag = 0.02f;
            accel = 0.05f;
            range = maxRange = 40.0f;
            engineOffset = 5.6f;
            trailLength = 4;
            itemCapacity = 25;
            circleTarget = true;
            lowAltitude = false;

            abilities.add(
                new MoveLightningAbility(2.0f, 12, 0.4f, 8.0f, 1.2f, 2.4f, Pal.lancerLaser)
            );

            weapons.add(
                new Weapon("fire-candlelight-weapon"){{
                    x = 0.0f;
                    y = 2.0f;
                    shootCone = 180f;
                    mirror = false;
                    top = false;
                    shootOnDeath = true;
                    shootSound = Sounds.explosion;
                    shoot = new ShootSpread(5, 36f);
                    bullet = new ShrapnelBulletType(){{
                        damage = 65.0f;
                        length = 63.0f;
                        width = 18.0f;
                        killShooter = true;
                        hitEffect = Fx.pulverize;
                        hitSound = Sounds.explosion;
                    }};
                }}
            );
        }};

        lampryo = new UnitType("lampflame"){{
            constructor = UnitEntity::create;
            flying = true;
            health = 620.0f;
            armor = 9.0f;
            hitSize = 20.0f;
            speed = 2.2f;
            drag = 0.03f;
            accel = 0.04f;
            range = maxRange = 40.0f;
            engineSize = 3.0f;
            engineOffset = 8.0f;
            trailLength = 12;
            itemCapacity = 60;
            circleTarget = true;

            abilities.add(
                new MoveLightningAbility(3.0f, 12, 0.3f, 12.0f, 0.8f, 1.8f, Pal.lancerLaser)
            );

            weapons.add(
                new Weapon(){{
                    shootCone = 180.0f;
                    rotateSpeed = 360.0f;
                    mirror = false;
                    top = false;
                    shootOnDeath = true;
                    shootSound = Sounds.explosion;

                    bullet = new BasicBulletType(1.4f, 320.0f){{
                        killShooter = true;
                        rangeOverride = 40.0f;

                        lifetime = 120.0f;
                        width = 10.0f;
                        height = 10.0f;
                        weaveMag = 2.0f;
                        weaveScale = 10.0f;
                        homingPower = 2.0f;
                        homingRange = 60.0f;
                        buildingDamageMultiplier = 1.4f;

                        trailLength = 24;
                        trailWidth = 5f;
                        trailColor = Pal.lancerLaser;
                        backColor = Pal.lancerLaser;
                        frontColor = Pal.lancerLaser;

                        hitEffect = Fx.pulverize;
                        hitSound = Sounds.explosion;

                        bulletInterval = 5.0f;
                        intervalRandomSpread = 30.0f;
                        intervalBullets = 2;
                        intervalBullet = new ShrapnelBulletType(){{
                            damage = 25.0f;
                            length = 48.0f;
                            toColor = Pal.lancerLaser;
                        }};

                        fragBullets = 1;
                        fragVelocityMin = fragVelocityMax = 1.0f;
                        fragRandomSpread = 0.0f;
                        fragBullet = new BasicBulletType(0.0f, 250.0f){{
                            lifetime = 10.0f;
                            splashDamage = 80.0f;
                            splashDamageRadius = 60.0f;
                            buildingDamageMultiplier = 4.0f;

                            hitEffect = new MultiEffect(
                                new WaveEffect(){{
                                    lifetime = 50.0f;
                                    strokeFrom = 4.0f;
                                    interp = Interp.pow3Out;
                                }},
                                new ParticleEffect(){{
                                    lifetime = 90.0f;
                                    particles = 8;
                                    length = 80.0f;
                                    interp = Interp.pow10Out;
                                    colorFrom.set(colorTo.set(_lancer_a04));
                                    sizeFrom = 16.0f;
                                    sizeTo = 0.0f;
                                    sizeInterp = Interp.pow5In;
                                }},
                                new ParticleEffect(){{
                                    lifetime = 120.0f;
                                    particles = 6;
                                    length = 60.0f;
                                    interp = Interp.pow10Out;
                                    colorFrom.set(colorTo.set(_lancer_a04));
                                    sizeFrom = 20.0f;
                                    sizeTo = 0.0f;
                                    sizeInterp = Interp.pow5In;
                                }}
                            );
                        }};
                    }};
                }}
            );
        }};

        lumiflame = new UnitType("lumiflame"){{
            constructor = UnitEntity::create;
            flying = true;
            health = 4750.0f;
            armor = 12.0f;
            hitSize = 24.0f;
            speed = 1.65f;
            drag = 0.03f;
            accel = 0.04f;
            range = maxRange = 40.0f;
            engineSize = 4.0f;
            engineOffset = 12.0f;
            trailLength = 12;
            itemCapacity = 90;
            circleTarget = true;

            immunities.addAll(
                StatusEffects.burning, StatusEffects.melting
            );

            abilities.add(
                new DashAbility(4.8f, 12, 150, 6)
            );

            weapons.add(
                new Weapon(){{
                    alwaysShooting = true;
                    x = 8.0f;
                    reload = 10.0f / 3.0f;
                    baseRotation = 180.0f;
                    minShootVelocity = speed * 0.6f;
                    shootSound = Sounds.flame;

                    bullet = new LiquidBulletType(Liquids.slag){{
                        speed = 2.0f;
                        lifetime = 40.0f;
                        damage = 25.0f;
                        drag = 0.03f;
                        orbSize = 2.4f;
                        collidesAir = false;
                        statusDuration = 480.0f;
                        status = StatusEffects.melting;
                    }};
                }},

                new Weapon(){{
                    alwaysShooting = true;
                    x = 5.0f;
                    reload = 4.0f / 3.0f;
                    baseRotation = 180.0f;
                    minShootVelocity = speed * 3.0f;
                    shootSound = Sounds.flame;

                    bullet = new LiquidBulletType(Liquids.slag){{
                        speed = 4.0f;
                        lifetime = 30.0f;
                        damage = 40.0f;
                        drag = 0.04f;
                        orbSize = 3.0f;
                        collidesAir = false;
                        statusDuration = 600.0f;
                        status = StatusEffects.melting;
                    }};
                }},

                new Weapon(){{
                    shootOnDeath = true;
                    x = 0.0f;
                    shootCone = 180.0f;
                    rotateSpeed = 360.0f;
                    mirror = false;
                    ejectEffect = Fx.casing1;
                    shootSound = Sounds.explosionbig;

                    bullet = new BulletType(8.0f, 600.0f){{
                        killShooter = true;
                        instantDisappear = true;
                        rangeOverride = 40.0f;

                        splashDamage = 1650.0f;
                        splashDamageRadius = 120.0f;
                        buildingDamageMultiplier = 2.0f;
                        status = StatusEffects.melting;
                        statusDuration = 360.0f;

                        hitEffect = new MultiEffect(
                            new WaveEffect(){{
                                lifetime = 15.0f;
                                sizeFrom = 0.0f;
                                sizeTo = 80.0f;
                                strokeFrom = 8.0f;
                                strokeTo = 4.0f;
                                colorFrom.set(colorTo.set(_ffa166));
                            }},
                            new WaveEffect(){{
                                lifetime = 15.0f;
                                sizeFrom = 80.0f;
                                sizeTo = 120.0f;
                                strokeFrom = 4.0f;
                                strokeTo = 3.0f;
                                colorFrom.set(colorTo.set(_ffa166));
                            }}.startDelay(15.0f),
                            new WaveEffect(){{
                                lifetime = 15.0f;
                                sizeFrom = 120.0f;
                                sizeTo = 140.0f;
                                strokeFrom = 3.0f;
                                strokeTo = 2.0f;
                                colorFrom.set(colorTo.set(_ffa166));
                            }}.startDelay(30.0f),
                            new WaveEffect(){{
                                lifetime = 15.0f;
                                sizeFrom = 140.0f;
                                sizeTo = 150.0f;
                                strokeFrom = 2.0f;
                                strokeTo = 0.0f;
                                colorFrom.set(colorTo.set(_ffa166));
                            }}.startDelay(45.0f),
                            new ParticleEffect(){{
                                particles = 10;
                                lifetime = 120.0f;
                                length = 120.0f;
                                sizeFrom = 25.0f;
                                sizeTo = 0.0f;
                                interp = Interp.pow10Out;
                                sizeInterp = Interp.pow5In;
                                colorFrom = _ffa166;
                                colorFrom.set(_ffa166);
                                colorTo.set(_ffa16670);
                            }},
                            new ParticleEffect(){{
                                particles = 6;
                                lifetime = 170.0f;
                                length = 80.0f;
                                sizeFrom = 30.0f;
                                sizeTo = 0.0f;
                                interp = Interp.pow10Out;
                                sizeInterp = Interp.pow5In;
                                colorFrom.set(_ffa166);
                                colorTo.set(_ffa16670);
                            }},
                            new ParticleEffect(){{
                                particles = 4;
                                lifetime = 220.0f;
                                length = 50.0f;
                                sizeFrom = 35.0f;
                                sizeTo = 0.0f;
                                interp = Interp.pow10Out;
                                sizeInterp = Interp.pow5In;
                                colorFrom.set(_ffa166);
                                colorTo.set(_ffa16670);
                            }}
                        );

                        fragBullets = 8;
                        fragBullet = new BasicBulletType(8.0f, 35.0f){{
                            lifetime = 90.0f;
                            width = height = 16.0f;
                            drag = 0.04f;
                            splashDamageRadius = 60.0f;
                            splashDamage = 85.0f;
                            buildingDamageMultiplier = 2.5f;

                            frontColor = backColor = _ffa166;
                            status = StatusEffects.burning;
                            statusDuration = 600.0f;

                            trailInterval = 3.0f;
                            trailEffect = new ParticleEffect(){{
                                particles = 3;
                                lifetime = 80.0f;
                                length = 5.0f;
                                baseLength = 2.0f;
                                sizeFrom = 6.0f;
                                sizeTo = 0.0f;
                                interp = Interp.pow3Out;
                                sizeInterp = Interp.pow3In;
                                colorFrom.set(_444444);
                                colorTo.set(_44444488);
                            }};

                            hitEffect = despawnEffect = new MultiEffect(
                                new WaveEffect(){{
                                    lifetime = 40.0f;
                                    sizeFrom = 0.0f;
                                    sizeTo = 60.0f;
                                    strokeFrom = 8.0f;
                                    strokeTo = 0.0f;
                                    interp = Interp.pow3Out;
                                    colorFrom.set(_ffa166);
                                }},
                                new ParticleEffect(){{
                                    particles = 6;
                                    lifetime = 120.0f;
                                    length = 50.0f;
                                    sizeFrom = 16.0f;
                                    sizeTo = 0.0f;
                                    interp = Interp.pow5Out;
                                    sizeInterp = Interp.pow5In;
                                    colorFrom.set(_ffa166);
                                    colorTo.set(_ffa16670);
                                }},
                                new ParticleEffect(){{
                                    particles = 3;
                                    lifetime = 150.0f;
                                    length = 40.0f;
                                    sizeFrom = 20.0f;
                                    sizeTo = 0.0f;
                                    interp = Interp.pow5Out;
                                    sizeInterp = Interp.pow5In;
                                    colorFrom.set(_ffa166);
                                    colorTo.set(_ffa16670);
                                }}
                            );
                        }};
                    }};
                }}
            );
        }};

        //endregion
        //region air

        javelin = new UnitType("javelin"){{
            constructor = UnitEntity::create;
            flying = true;
            health = 340;
            armor = 1;
            hitSize = 12f;
            speed = 9f;
            drag = 0.01f;
            accel = 0.015f;
            rotateSpeed = 22f;
            engineOffset = 6f;
            trailLength = 5;
            itemCapacity = 30;
            buildSpeed = 1f;
            lowAltitude = true;

            abilities.add(
                new MoveLightningAbility(10f, 16, 0.2f, 16f, 3.6f, 8f, Pal.lancerLaser, "fire-javelin-heat")
            );

            weapons.add(
                new Weapon("fire-javelin-weapon"){{
                    reload = 35f;
                    x = 3f;
                    y = 1f;
                    inaccuracy = 3f;
                    velocityRnd = 0.2f;
                    top = false;
                    shootSound = Sounds.missile;
                    shoot.shots = 4;
                    bullet = new MissileBulletType(5f, 21f){{
                        lifetime = 36f;
                        width = 8f;
                        height = 8f;
                        splashDamage = 2f;
                        splashDamageRadius = 20f;
                        weaveScale = 8f;
                        weaveMag = 2f;
                        trailColor = _b6c6fd;
                        hitEffect = Fx.blastExplosion;
                        despawnEffect = Fx.blastExplosion;
                        backColor = Pal.bulletYellowBack;
                        frontColor = Pal.bulletYellow;
                    }};
                }}
            );
        }};

        apollo = new UnitType("dk"){{
            constructor = UnitEntity::create;
            flying = true;
            health = 76000.0f;
            armor = 18.0f;
            hitSize = 60.0f;
            speed = 0.36f;
            drag = 0.08f;
            accel = 0.15f;
            rotateSpeed = 2.0f;
            buildSpeed = 1.0f;
            itemCapacity = 280;
            engineOffset = 6.0f;
            lowAltitude = true;
            faceTarget = false;

            abilities.add(new ForceFieldAbility(120.0f, 4.0f, 2000.0f, 480.0f){
                @Override
                public void update(Unit unit){
                    // doubles regen when health below the half
                    if(unit.shield < max && unit.health < unit.maxHealth * 0.5f)
                        unit.shield += Time.delta * regen;

                    super.update(unit);
                }
            });

            weapons.add(

                new Weapon("omura-cannon"){{
                    reload = 495.0f;
                    x = 0.0f;
                    y = -13.0f;
                    shootY = 10.0f;
                    rotateSpeed = 1.6f;
                    recoil = 7.0f;
                    recoilTime = 360.0f;
                    rotate = true;
                    mirror = false;
                    shootSound = Sounds.release;

                    bullet = new BasicBulletType(5.1f, 720.0f){{
                        lifetime = 65.0f;
                        width = 16.0f;
                        height = 20.0f;
                        splashDamageRadius = 74.0f;
                        splashDamage = 500.0f;
                        drag = -0.02f;
                        trailLength = 20;
                        trailWidth = 3.9f;
                        pierceCap = 2;
                        pierceBuilding = true;
                        hitEffect = despawnEffect = new MultiEffect(
                            FRFx.crossEffect(30.0f, 4.5f, 0.0f, true, Pal.missileYellowBack),
                            FRFx.crossEffect(30.0f, 3.0f, 45.0f, true, Pal.missileYellowBack),
                            new Effect(30.0f, e -> {
                                Draw.color(Pal.missileYellowBack);
                                Lines.stroke(e.fout() * 2.0f);
                                Lines.circle(e.x, e.y, 4.0f + e.finpow() * 100.0f);
                            })
                        );

                        bulletInterval = 1.0f;
                        intervalRandomSpread = 0.0f;
                        intervalSpread = 359.0f;
                        intervalBullets = 1;
                        intervalBullet = new LightningBulletType(){{
                            damage = 5.0f;
                            lightningColor = Pal.missileYellowBack;
                            lightningLength = 5;
                        }};

                        fragBullets = 12;
                        fragBullet = new BasicBulletType(12.4f, 0.0f){{
                            lifetime = 32.0f;
                            splashDamageRadius = 60.0f;
                            splashDamage = 80.0f;
                            drag = 0.015f;
                            trailLength = 1;
                            trailWidth = 2.0f;
                            trailInterval = 10.0f;
                            trailEffect = Fx.artilleryTrail.wrap(Pal.missileYellowBack);
                            hitEffect = despawnEffect = Fx.none;

                            fragBullets = 3;
                            fragBullet = new LaserBulletType(90.0f){{
                                length = 230.0f;

                                fragBullets = 3;
                                fragBullet = new LightningBulletType(){{
                                    damage = 8.0f;
                                    lightningLength = 24;
                                }};
                            }};
                        }};
                    }};
                }},

                new Weapon("large-laser-mount"){{
                    reload = 20.0f;
                    x = 21.0f;
                    y = -22.0f;
                    shootY = 12.0f;
                    rotateSpeed = 3.0f;
                    recoil = 2.0f;
                    rotate = true;
                    shootSound = Sounds.laser;
                    bullet = new LaserBulletType(288.0f){{
                        length = 288.0f;
                        width = 12.0f;
                        colors(colors, _f6efa1, _f6efa1, Color.white);
                        lightningSpacing = 20.0f;
                        lightningLength = 2;
                        lightningLengthRand = 2;
                        lightningDamage = 20.0f;
                        lightningAngleRand = 25.0f;
                        status = StatusEffects.shocked;
                    }};
                }},

                new Weapon("large-artillery"){{
                    reload = 8.0f;
                    x = 18.0f;
                    y = 18.0f;
                    inaccuracy = 2.2f;
                    shootY = 6.0f;
                    rotateSpeed = 4.0f;
                    rotate = true;
                    ejectEffect = Fx.casing1;
                    shootSound = Sounds.shoot;
                    bullet = new FlakBulletType(14.2f, 60.0f){{
                        lifetime = 30.0f;
                        width = 12.0f;
                        height = 18.0f;
                        splashDamageRadius = 27.0f;
                        splashDamage = 85.0f;
                        drag = 0.012f;
                        trailLength = 2;
                        trailWidth = 4.0f;
                        pierceCap = 3;
                        pierceBuilding = true;
                        collidesGround = true;
                        status = StatusEffects.blasted;
                    }};
                }},

                new PointDefenseWeapon("fire-dk-point-defense-mount"){{
                    reload = 12.0f;
                    x = 23.0f;
                    y = -2.0f;
                    targetInterval = 1.0f;
                    targetSwitchInterval = 1.0f;
                    color = Pal.surge;
                    beamEffect = FRFx.chainLightningThin;

                    bullet = new BulletType(1.0f, 145.0f){{
                        maxRange = 225.0f;
                        shootEffect = Fx.sparkShoot;
                        hitEffect = Fx.pointHit;
                        shootSound = Sounds.spark;
                    }};
                }}
            );
        }};

        //endregion
        //region naval

        mechanicalTide = new UnitType("mechanical-tide"){{
            BulletType
            laser = new LaserBulletType(200.0f){{
                lifetime = 30.0f;
                hitSize = 4.0f;
                length = 6.00f;
                drawSize = 150.0f;
                colors(colors, Pal.heal, Pal.heal, Color.white);
                collidesAir = true;
            }},

            type1 = new BasicBulletType(0f, 200f){{
                width = height = 0.0f;
                hitEffect = despawnEffect = Fx.none;
                collides = false;
                fragAngle = 90.0f;
                lifetime = fragVelocityMin = 1.0f;
                fragRandomSpread = 0;
                fragBullets = 1;
                fragBullet = laser;
            }},

            type2 = new BasicBulletType(0f, 200f){{
                width = height = 0.0f;
                hitEffect = despawnEffect = Fx.none;
                collides = false;
                fragAngle = -90.0f;
                lifetime = fragVelocityMin = 1.0f;
                fragRandomSpread = 0;
                fragBullets = 1;
                fragBullet = laser;
            }};

            constructor = UnitWaterMove::create;
            flying = false;
            health = 122500.0f;
            armor = 37.0f;
            hitSize = 80.0f;
            speed = 0.45f;
            rotateSpeed = 0.7f;
            accel = 0.3f;
            waveTrailX = 30.0f;
            waveTrailY = -45.0f;
            trailScl = 6.0f;
            trailLength = 12;
            itemCapacity = 800;
            buildSpeed = 15.0f;
            rotateToBuilding = false;
            faceTarget = false;

            immunities.addAll(
                StatusEffects.burning, StatusEffects.melting,
                StatusEffects.electrified, StatusEffects.sapped,
                StatusEffects.shocked, StatusEffects.freezing
            );

            abilities.add(
                new UnitSpawnAbility(){{
                    unit = UnitTypes.cyerce;
                    spawnX = 0.0f;
                    spawnY = 30.0f;
                    spawnTime = 540.0f;
                }},

                new EnergyFieldAbility(250.0f, 120.0f, 300.0f){{
                    healPercent = 2.0f;
                    sameTypeHealMult = 0.5f;
                    maxTargets = 40;
                }},

                new RepairFieldAbility(3000.0f, 5 * 60.0f, 150.0f){{
                    activeEffect = new MultiEffect(
                        new WaveEffect(){{
                            lifetime = 30.0f;
                            colorFrom = colorTo = healColor;
                            sizeFrom = 60.0f;
                            sizeTo = 0.0f;
                            strokeFrom = 0.0f;
                            strokeTo = 6.0f;
                            interp = Interp.pow3In;
                        }},
                        new WaveEffect(){{
                            startDelay = 30.0f;
                            lifetime = 10.0f;
                            colorFrom = colorTo = healColor;
                            sizeFrom = 0.0f;
                            sizeTo = 200.0f;
                            strokeFrom = 6.0f;
                            strokeTo = 4.0f;
                        }},
                        new WaveEffect(){{
                            startDelay = 40f;
                            lifetime = 10f;
                            colorFrom = colorTo = healColor;
                            sizeFrom = 200f;
                            sizeTo = 300f;
                            strokeFrom = 4f;
                            strokeTo = 3f;
                        }},
                        new WaveEffect(){{
                            startDelay = 50f;
                            lifetime = 10f;
                            colorFrom = colorTo = healColor;
                            sizeFrom = 300f;
                            sizeTo = 350f;
                            strokeFrom = 3f;
                            strokeTo = 2f;
                        }},
                        new WaveEffect(){{
                            startDelay = 60f;
                            lifetime = 20f;
                            colorFrom = colorTo = healColor;
                            sizeFrom = 350f;
                            sizeTo = 375f;
                            strokeFrom = 2f;
                            strokeTo = 0f;
                        }}
                    );
                }}
            );

            weapons.add(
                new Weapon("fire-mechanical-tide-laser-large"){{
                    reload = 300.0f;
                    x = 0.0f;
                    y = 1.0f;
                    recoil = 1.5f;
                    rotate = true;
                    mirror = false;
                    rotateSpeed = 1.2f;
                    inaccuracy = 0.1f;
                    shootSound = Sounds.laser;

                    bullet = new BasicBulletType(10f, 3240){{
                        width = height = 0.0f;
                        hitEffect = despawnEffect = Fx.none;
                        buildingDamageMultiplier = 0.8f;
                        lifetime = 60f;
                        knockback = 16;
                        pierceCap = 4;
                        pierce = true;
                        pierceBuilding = true;

                        spawnBullets.add(new LaserBulletType(6400.0f){{
                            buildingDamageMultiplier = 0.8f;
                            knockback = 64;
                            pierceCap = 24;
                            pierce = true;
                            pierceBuilding = true;
                            length = 700;
                            width = 45;
                            sideAngle = 160;
                            sideWidth = 1;
                            sideLength = 30;
                            colors(colors, Pal.heal, Pal.heal, Color.white);
                        }});

                        for(byte j = 0; j < 12; j++){
                            byte s = j;
                            spawnBullets.add(new FlakBulletType(20.0f - s * 1.2f, 100.0f + s * 6.0f){{
                                sprite = "missile-large";
                                collidesGround = collidesAir = true;
                                explodeRange = 20f + s;
                                width = height = 12f + s * 0.5f;
                                shrinkY = 0f;
                                drag = 0.01f + s * 0.001f;
                                homingRange = 240f;
                                keepVelocity = false;
                                lightRadius = 60f;
                                lightOpacity = 0.7f;
                                lightColor = Pal.heal;

                                buildingDamageMultiplier = 2f;
                                splashDamageRadius = 40f + s * 2;
                                splashDamage = damage * 2;
                                pierceCap = 4;
                                pierce = true;
                                pierceBuilding = false;

                                lifetime = 60f + s * 5f;
                                backColor = Pal.heal;
                                frontColor = Color.white;

                                hitEffect = new ExplosionEffect(){{
                                    lifetime = 24f + s * 0.4f;
                                    waveStroke = 6f;
                                    waveLife = 10f + s * 0.2f;
                                    waveRadBase = 7f;
                                    waveColor = Pal.heal;
                                    waveRad = 30f;
                                    smokes = 6;
                                    smokeColor = Color.white;
                                    sparkColor = Pal.heal;
                                    sparks = 6;
                                    sparkRad = 35f;
                                    sparkStroke = 1.5f + s * 0.05f;
                                    sparkLen = 4f;
                                }};

                                weaveScale = 6f + s * 0.25f;
                                weaveMag = 3f - s * 0.14f;

                                trailColor = Pal.heal;
                                trailWidth = 4.5f - s * 0.1f;
                                trailLength = 29 - s;
                            }});
                        }
                    }};
                }},

                new Weapon("fire-mechanical-tide-laser-small"){{
                    reload = 60f;
                    x = 30f;
                    y = -18f;
                    recoil = 1.5f;
                    rotate = true;
                    mirror = true;
                    rotateSpeed = 4f;
                    inaccuracy = 0.2f;
                    shootSound = Sounds.laser;

                    bullet = new BasicBulletType(40f, 980f){{
                        width = height = 0.0f;
                        hitEffect = despawnEffect = Fx.none;
                        lifetime = 10;
                        knockback = 60;
                        pierce = true;
                        pierceBuilding = true;
                        pierceCap = 6;
                        bulletInterval = 1;
                        intervalBullets = 2;
                        intervalRandomSpread = 360;
                        intervalSpread = 0;
                        intervalBullet = new BasicBulletType(0, 200f){{
                            width = height = 0.0f;
                            hitEffect = despawnEffect = Fx.none;
                            lifetime = 4f;
                            fragBullets = 1;
                            fragRandomSpread = 0;
                            fragSpread = 0.0f;
                            weaveMag = 15;
                            weaveScale = 990;
                            fragBullet = type1;
                            intervalBullets = 1;
                            intervalRandomSpread = 0;
                            bulletInterval = 6f;
                            intervalBullet = type2;
                        }};
                    }};
                }},

                new Weapon(){{
                    shoot = new ShootSpread(9, 360f / 9);
                    mirror = false;
                    autoTarget = true;
                    controllable = false;
                    outlineRegion = Core.atlas.white();
                    region = Core.atlas.white();
                    cellRegion = Core.atlas.white();
                    heatRegion = Core.atlas.white();
                    reload = 600f;
                    shootX = 0;
                    shootY = 0;
                    shootSound = Sounds.plasmadrop;
                    inaccuracy = 360;
                    shootCone = 360;
                    bullet = new MissileBulletType(3f, 400f){{
                        splashDamage = 600f;
                        splashDamageRadius = 30f;
                        region = outlineRegion = heatRegion = cellRegion = frontRegion = backRegion = Core.atlas.white();
                        lifetime = 150f;
                        shootEffect = new WaveEffect(){{
                            lifetime = 20f;
                            colorFrom = healColor;
                            colorTo = healColor;
                            sizeFrom = 0;
                            sizeTo = 300f;
                            strokeFrom = 7f;
                            strokeTo = 2f;
                        }};
                        trailColor = Pal.heal;
                        trailLength = 30;
                        trailWidth = 12f;
                        width = 8;
                        height = 8;
                        weaveScale = 20f;
                        weaveMag = 5f;
                        ejectEffect = Fx.none;
                        ignoreRotation = true;

                        healPercent = 5f;
                        hitSound = Sounds.plasmaboom;
                        despawnEffect = Fx.greenBomb;
                        hitEffect = Fx.massiveExplosion;
                    }};
                }}
            );
        }};

        //endregion

        // put these there instead of FROverride or automatic unlocking

        ((UnitFactory)Blocks.groundFactory).plans.add(
            new UnitFactory.UnitPlan(FRUnitTypes.guarding, 1500.0f, ItemStack.with(
                Items.lead, 20,
                Items.titanium, 25,
                Items.silicon, 30
            ))
        );

        ((UnitFactory)Blocks.airFactory).plans.add(
            new UnitFactory.UnitPlan(UnitTypes.alpha, 2400.0f, ItemStack.with(
                Items.copper, 30,
                Items.lead, 40,
                Items.silicon, 30
            )),
            new UnitFactory.UnitPlan(FRUnitTypes.firefly, 2400.0f, ItemStack.with(
                Items.lead, 20,
                Items.metaglass, 10,
                Items.coal, 10,
                Items.silicon, 15
            ))
        );

        ((Reconstructor)Blocks.additiveReconstructor).upgrades.addAll(
            new UnitType[]{UnitTypes.alpha, UnitTypes.beta},
            new UnitType[]{FRUnitTypes.guarding, FRUnitTypes.resisting},
            new UnitType[]{FRUnitTypes.blade, FRUnitTypes.hatchet},
            new UnitType[]{FRUnitTypes.firefly, FRUnitTypes.candlight}
        );

        ((Reconstructor)Blocks.multiplicativeReconstructor).upgrades.addAll(
            new UnitType[]{UnitTypes.beta, FRUnitTypes.omicron},
            new UnitType[]{FRUnitTypes.resisting, FRUnitTypes.garrison},
            new UnitType[]{FRUnitTypes.hatchet, FRUnitTypes.castle},
            new UnitType[]{FRUnitTypes.candlight, FRUnitTypes.lampryo}
        );

        ((Reconstructor)Blocks.exponentialReconstructor).upgrades.addAll(
            new UnitType[]{FRUnitTypes.omicron, FRUnitTypes.pioneer},
            new UnitType[]{FRUnitTypes.garrison, FRUnitTypes.shelter},
            new UnitType[]{FRUnitTypes.lampryo, FRUnitTypes.lumiflame}
        );

        ((Reconstructor)Blocks.tetrativeReconstructor).upgrades.addAll(
            new UnitType[]{FRUnitTypes.shelter, FRUnitTypes.blessing}
        );
    }
}
