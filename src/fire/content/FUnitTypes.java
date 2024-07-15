package fire.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import fire.ai.FUnitCommand;
import fire.entities.abilities.*;
import fire.type.FleshUnitType;
import mindustry.ai.UnitCommand;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.PointDefenseWeapon;

import static mindustry.Vars.tilePayload;

public class FUnitTypes{

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
        firefly, candlelight,

        //air
        javelin, apollo;

    public static void load(){

        //region legs support
        var colLegSpt = Color.valueOf("8cfffb");

        guarding = new UnitType("sh"){{
            constructor = LegsUnit::create;
            hovering = true;
            health = 140;
            armor = 3;
            hitSize = 8;
            speed = 0.6f;
            drag = 0.1f;
            rotateSpeed = 4f;
            buildSpeed = 1f;
            itemCapacity = 20;
            canAttack = false;
            targetable = false;

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
                new ForceFieldAbility(44f, 20f / 60f, 200f, 400f)
            );
        }};

        resisting = new UnitType("ky"){{
            constructor = LegsUnit::create;
            hovering = true;
            health = 420;
            armor = 5;
            hitSize = 12;
            speed = 0.54f;
            drag = 0.1f;
            rotateSpeed = 3.6f;
            buildSpeed = 1.2f;
            itemCapacity = 40;
            canAttack = false;
            targetable = false;

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
                new ForceFieldAbility(54f, 0.6f, 300f, 360f),
                new RegenFieldAbility(1f, 40f, colLegSpt)
            );
        }};

        garrison = new UnitType("ws"){{
            constructor = LegsUnit::create;
            hovering = true;
            health = 930;
            armor = 6;
            hitSize = 16;
            speed = 0.45f;
            drag = 0.3f;
            rotateSpeed = 2.7f;
            buildSpeed = 2f;
            itemCapacity = 50;
            canAttack = false;
            targetable = false;

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
                new ForceFieldAbility(72f, 1.0f, 400f, 300f, 4, 45f),
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
                new RegenFieldAbility(2.5f, 120f, colLegSpt)
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
                new EnergyForceFieldAbility(160.0f, 4.0f, 7200.0f, 600.0f, 28, 24, 15, 30.0f){{
                    sides = 24;
                    lightningColor = Pal.surge;
                    unlocks = true;
                }},

                new RegenFieldAbility(3f, 120f, colLegSpt),

                new ExtinguishFieldAbility(120f, colLegSpt),

                new DebuffRemoveFieldAbility(120f, 120f, new Effect(30f, e -> {
                    Draw.color(colLegSpt, Color.lightGray, e.fin());
                    Angles.randLenVectors(e.id, 3, 6f + e.finpow() * 20f, (x, y) ->
                        Fill.square(e.x + x, e.y + y, e.fout() * 4f + 0.5f, 45f));
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

                        backColor = Color.valueOf("f9a27a");
                        frontColor = Color.valueOf("ffd8e8");
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
                        status = FStatusEffects.overgrown;
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
            rotateSpeed = 6.0f;
            drownTimeMultiplier = 5.0f;

            mechSideSway = 0.6f;
            mechFrontSway = 0.7f;

            abilities.add(
                new EnergyFieldAbility(15.0f, 30.0f, 128.0f){{
                    maxTargets = 40;
                    healPercent = 0.1f;
                    statusDuration = 40.0f;
                    status = StatusEffects.burning;
                    color = Color.valueOf("ff3300");
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
                        backColor = Color.valueOf("f9a27a");
                        frontColor = Color.valueOf("ffd8e8");
                    }};
                }}
            );
        }};

        //endregion
        //region ground

        error = new FleshUnitType("error"){{
            hidden = true;
            
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
                        color = Color.valueOf("990003");
                        hitColor = Color.valueOf("990003");
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
                        toColor = Color.valueOf("990003");
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
                    reload = 12f;
                    x = 4f;
                    y = 0.6f;
                    inaccuracy = 1.1f;
                    top = false;
                    rotate = true;
                    shootSound = Sounds.lasershoot;
                    bullet = new LaserBoltBulletType(10f, 22f){{
                        lifetime = 25f;
                        healPercent = 4f;
                        width = 2.4f;
                        height = 5.4f;
                        buildingDamageMultiplier = 0.2f;
                        collidesTeam = true;
                        backColor = Color.valueOf("8cfffb");
                        frontColor = Color.white;
                        status = StatusEffects.electrified;
                        statusDuration = 150f;
                    }};
                }}
            );
        }};

        pioneer = new UnitType("pioneer"){{
            constructor = PayloadUnit::create;
            defaultCommand = FUnitCommand.repairDashCommand;
            commands = new UnitCommand[]{UnitCommand.moveCommand, FUnitCommand.repairDashCommand, FUnitCommand.rebuildDashCommand, FUnitCommand.assistDashCommand};
            flying = true;
            health = 5600;
            armor = 7;
            hitSize = 30.0f;
            speed = 2.7f;
            drag = 0.05f;
            accel = 0.1f;
            rotateSpeed = 3.0f;
            payloadCapacity = 3.5f * 3.5f * tilePayload;
            engineOffset = 13.0f;
            engineSize = 7.0f;
            itemCapacity = 120;
            buildSpeed = 3.2f;
            lowAltitude = true;

            abilities.add(
                new DashAbility(6.0f, 15, 120, 6),
                new FirstAidAbility(2400, 80, 1200, 0, FStatusEffects.sanctuaryGuard, 360, 120)
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
                        powerDamageScl = 2.0f;
                        healPercent = 10.0f;
                        timeIncrease = 0.0f;
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
                            for(int s : Mathf.signs){
                                Drawf.tri(e.x, e.y, 4.0f, 30f * e.fslope(), e.rotation + s * 90f);
                            }
                        });

                        hitEffect = new Effect(50f, 100f, e -> {
                            final byte points = 8;
                            float offset = Mathf.randomSeed(e.id, 360f);

                            e.scaled(7f, b -> {
                                Draw.color(Pal.heal, b.fout());
                                Fill.circle(e.x, e.y, rad);
                            });
                            Draw.color(Pal.heal);
                            Lines.stroke(e.fout() * 3f);
                            Lines.circle(e.x, e.y, rad);
                            for(byte i = 0; i < points; i++){
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
                            colors = new Color[] {Pal.heal, Pal.heal, Color.white};
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
            rotateSpeed = 6f;
            engineOffset = 5.6f;
            trailLength = 3;
            itemCapacity = 15;
            circleTarget = true;
            lowAltitude = false;

            abilities.add(
                new MoveLightningAbility(2f, 8, 0.1f, 0.0f, 1.2f, 1.6f, Color.valueOf("a9d8ff"))
            );

            weapons.add(
                new Weapon(){{
                    reload = 600f;
                    shootCone = 180f;
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

        candlelight = new UnitType("candlelight"){{
            constructor = UnitEntity::create;
            flying = true;
            health = 280;
            armor = 4;
            hitSize = 14f;
            speed = 2.7f;
            drag = 0.02f;
            accel = 0.05f;
            rotateSpeed = 6f;
            engineOffset = 5.6f;
            trailLength = 4;
            itemCapacity = 25;
            circleTarget = true;
            lowAltitude = false;

            abilities.add(
                new MoveLightningAbility(2f, 12, 0.4f, 8f, 1.2f, 2.4f, Color.valueOf("a9d8ff"))
            );

            weapons.add(
                new Weapon("fire-candlelight-weapon"){{
                    x = 0.0f;
                    y = 2f;
                    reload = 600f;
                    shootCone = 180f;
                    mirror = false;
                    top = false;
                    shootOnDeath = true;
                    shootSound = Sounds.explosion;
                    shoot = new ShootSpread(5, 36f);
                    bullet = new ShrapnelBulletType(){{
                        damage = 65f;
                        length = 63f;
                        width = 18f;
                        killShooter = true;
                        hitEffect = Fx.pulverize;
                        hitSound = Sounds.explosion;
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
                new MoveLightningAbility(10f, 16, 0.2f, 16f, 3.6f, 8f, Color.valueOf("a9d8ff"), "fire-javelin-heat")
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
                        trailColor = Color.valueOf("b6c6fd");
                        hitEffect = Fx.blastExplosion;
                        despawnEffect = Fx.blastExplosion;
                        backColor = Pal.bulletYellowBack;
                        frontColor = Pal.bulletYellow;
                    }};
                }}
            );
        }};

        apollo = new UnitType("dk"){{
            final float reg = 4f;

            constructor = UnitEntity::create;
            flying = true;
            health = 76000;
            armor = 18;
            hitSize = 64f;
            speed = 0.36f;
            drag = 0.08f;
            accel = 0.15f;
            rotateSpeed = 2f;
            buildSpeed = 1f;
            itemCapacity = 280;
            engineOffset = 6f;
            lowAltitude = true;
            faceTarget = false;

            abilities.add(

                new ForceFieldAbility(120f, reg, 2000f, 480f){

                    @Override
                    public void update(Unit unit){
                        super.update(unit);
                        regen = unit.health < unit.maxHealth * 0.5f ? reg * 2f : reg;
                    }
                }
            );

            weapons.add(

                new Weapon("omura-cannon"){{
                    reload = 495f;
                    x = 0.0f;
                    y = -13f;
                    shootY = 14f;
                    rotateSpeed = 2f;
                    recoil = 5f;
                    recoilTime = 360f;
                    rotate = true;
                    mirror = false;
                    shootSound = Sounds.release;
                    bullet = new BasicBulletType(10f, 840f){{
                        lifetime = 66.7f;
                        width = 16f;
                        height = 20f;
                        splashDamageRadius = 80f;
                        splashDamage = 560f;
                        pierceCap = 2;
                        pierceBuilding = true;
                        collidesGround = true;
                        fragBullets = 12;
                        fragBullet = new BasicBulletType(10f, 0.0f){{
                            lifetime = 35f;
                            splashDamageRadius = 70f;
                            splashDamage = 320f;
                            collidesGround = true;
                            fragBullets = 2;
                            fragBullet = new LaserBulletType(160f){{
                                lifetime = 16f;
                                length = 260f;
                                colors = new Color[]{Color.valueOf("a9d8ff66"), Color.valueOf("a9d8ff66"), Color.white};
                                fragBullets = 2;
                                fragBullet = new LightningBulletType(){{
                                    damage = 8f;
                                    lightningLength = 30;
                                    collidesAir = true;
                                }};
                            }};
                        }};
                    }};
                }},

                new Weapon("large-laser-mount"){{
                    reload = 20f;
                    x = 21f;
                    y = -22f;
                    shootY = 12f;
                    rotateSpeed = 3f;
                    rotate = true;
                    shootSound = Sounds.laser;
                    bullet = new LaserBulletType(288f){{
                        lifetime = 16f;
                        length = 288f;
                        width = 22f;
                        colors = new Color[]{Color.valueOf("f6efa1"), Color.valueOf("f6efa1"), Color.white};
                        lightningSpacing = 15f;
                        lightningLength = 1;
                        lightningDelay = 1.2f;
                        lightningLengthRand = 10;
                        lightningDamage = 12f;
                        lightningAngleRand = 30f;
                        status = StatusEffects.shocked;
                    }};
                }},

                new Weapon("large-artillery"){{
                    reload = 8f;
                    x = 18f;
                    y = 18f;
                    inaccuracy = 2.2f;
                    shootY = 6f;
                    rotateSpeed = 4f;
                    rotate = true;
                    ejectEffect = Fx.casing1;
                    shootSound = Sounds.shoot;
                    bullet = new FlakBulletType(12f, 60f){{
                        lifetime = 30f;
                        width = 14f;
                        height = 20f;
                        splashDamageRadius = 27f;
                        splashDamage = 85f;
                        pierceCap = 3;
                        pierceBuilding = true;
                        collidesGround = true;
                        status = StatusEffects.blasted;
                    }};
                }},

                new PointDefenseWeapon("fire-dk-point-defense-mount"){{
                    reload = 12f;
                    x = 23f;
                    y = -2f;
                    targetInterval = 1f;
                    targetSwitchInterval = 1f;
                    bullet = new BulletType(1f, 145f){{
                        maxRange = 225f;
                        shootEffect = Fx.sparkShoot;
                        hitEffect = Fx.pointHit;
                    }};
                }}
            );
        }};
    }
}
