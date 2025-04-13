package fire.content;

import arc.Core;
import arc.Events;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.IntIntMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import fire.entities.FRUnitSorts;
import fire.entities.bullets.FleshBulletType;
import fire.entities.bullets.FoldingBulletType;
import fire.entities.bullets.LightningPointBulletType;
import fire.entities.bullets.SpriteBulletType;
import fire.world.DEBUG;
import fire.world.blocks.campaign.AcceleratorCutscene;
import fire.world.blocks.defense.ArmorWall;
import fire.world.blocks.defense.RegenWall;
import fire.world.blocks.defense.turrets.ItemBulletStackTurret;
import fire.world.blocks.defense.turrets.JackpotTurret;
import fire.world.blocks.environment.EnvBlock;
import fire.world.blocks.power.BatteryNode;
import fire.world.blocks.power.HydroelectricGenerator;
import fire.world.blocks.production.EnergyCrafter;
import fire.world.blocks.production.GeneratorCrafter;
import fire.world.blocks.production.BeamExtractor;
import fire.world.blocks.production.SurgeCrafter;
import fire.world.blocks.sandbox.AdaptiveSource;
import fire.world.blocks.storage.AdaptDirectionalUnloader;
import fire.world.blocks.storage.ForceCoreBlock;
import fire.world.blocks.units.MechPad;
import fire.world.consumers.ConsumePowerCustom;
import fire.world.draw.DrawArrows;
import fire.world.draw.DrawWeavePlus;
import fire.world.kits.Campfire;
import fire.world.kits.EnergyField;
import fire.world.meta.FRAttribute;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.UnitSorts;
import mindustry.entities.Units;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootMulti;
import mindustry.entities.pattern.ShootSpread;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ContinuousTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OverlayFloor;
import mindustry.world.blocks.liquid.LiquidBridge;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeItemFlammable;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;

import static fire.FRUtils.*;
import static fire.FRVars.*;
import static mindustry.Vars.*;
import static mindustry.type.ItemStack.mult;
import static mindustry.type.ItemStack.with;

public class FRBlocks{

    /** Component itself -> its inferior. */
    public static final IntIntMap compositeMap = new IntIntMap(5);
    public static Block

        //environment
        neoplasm, bloodyDirt, hardenedCovering,

        //sandbox & misc
        adaptiveSource, fireCompany,

        //turret
        smasher, nightmare,
        ignition, blossom, gambler, seaquake, distance, magneticDomain,
        grudge, aerolite, magneticSphere,
        magneticRail,
        scab,

        //production
        chopper, treeFarm, vapourCondenser, biomassCultivator, stackedCultivator, fissionDrill, constraintExtractor,

        //distribution
        compositeConveyor, hardenedAlloyConveyor, compositeBridgeConveyor,

        //liquid
        magneticRingPump, compositeLiquidRouter, hardenedLiquidTank, compositeBridgeConduit,

        //power
        conductorPowerNode, flameGenerator, hydroelectricGenerator, hydroelectricGeneratorLarge, burstReactor,

        //defense
        damWall, damWallLarge, hardenedWall, hardenedWallLarge, fleshWall,

        //crafting
        thermalKiln, metaglassPlater, mirrorglassPolisher, sulflameExtractor, kindlingExtractor, conductorFormer, logicAlloyProcessor, detonationMixer, slagCooler, crusher, timberBurner,
        electrothermalSiliconFurnace, fleshSynthesizer, liquidNitrogenCompressor, hardenedAlloySmelter, magneticAlloyFormer,
        cryofluidMixerLarge,
        magnetismConcentratedRollingMill, magneticRingSynthesizer, electromagnetismDiffuser,
        hardenedAlloyCrucible,

        //units
        fleshReconstructor,

        //effect
        buildingHealer, campfire, skyDome, buildIndicator, coreBulwark, javelinPad, compositeUnloader, primaryInterplanetaryAccelerator,

        //env
        envEteriverStronghold, envStormyCoast1, envStormyCoast2, envGlaciatedPeaks,

        //DEBUG
        DEBUG_TURRET, DEBUG_MEND;

    private static BulletType destroyBullet(float dmg, float radius){
        final float time = 15.0f;

        var b = new BulletType(1.0f, 0.0f);
        b.lifetime = time;
        b.splashDamage = dmg;
        b.splashDamageRadius = radius;

        var e = new WaveEffect();
        e.lifetime = time;
        e.sizeFrom = 0.0f;
        e.sizeTo = radius;
        e.strokeFrom = 2.0f;
        e.strokeTo = 0.0f;
        e.interp = Interp.pow3Out;

        b.despawnEffect = b.hitEffect = e;
        return b;
    }

    public static void load(){

        //region environment

        neoplasm = new Floor("pooled-neoplasm", 0){{
            drownTime = 230.0f;
            speedMultiplier = 0.5f;
            statusDuration = 240.0f;
            status = FRStatusEffects.overgrown;
            isLiquid = true;
            liquidDrop = Liquids.neoplasm;
        }};

        bloodyDirt = new Floor("bloody-dirt", 8){{
            attributes.set(FRAttribute.flesh, 1.0f / 9.0f);
        }};

        hardenedCovering = new OverlayFloor("hardened-covering"){{
            variants = 8;
        }};

        //endregion
        //region sandbox & misc

        adaptiveSource = new AdaptiveSource("adaptive-source"){{
            requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
            liquidCapacity = 100.0f;
            laserRange = 200.0f;
            maxNodes = 500;
            itemPerSec = 2000;
            autolink = drawRange = false;
        }};

        fireCompany = new Block("fire-company"){{
            requirements(Category.effect, BuildVisibility.hidden, with());
            size = 2;
        }};

        //endregion
        //region turret

        smasher = new ItemTurret("js"){{
            requirements(Category.turret, with(
                Items.copper, 65,
                Items.lead, 45,
                Items.titanium, 20
            ));
            researchCost = with(
                Items.copper, 150,
                Items.lead, 105,
                Items.titanium, 50
            );
            health = 800;
            size = 2;
            reload = 42.0f;
            range = 250.0f;
            shootCone = 10.0f;
            inaccuracy = 2.0f;
            rotateSpeed = 10.0f;
            recoil = 1.6f;
            targetAir = false;
            shootSound = Sounds.bang;

            consumeCoolant(0.2f);

            ammo(

                Items.copper, new ArtilleryBulletType(6f, 20f){{
                    lifetime = 130f;
                    knockback = 1.6f;
                    width = 12f;
                    height = 12f;
                    splashDamageRadius = 13.75f;
                    splashDamage = 24f;
                    reloadMultiplier = 0.9f;
                    pierceArmor = true;
                }},

                Items.lead, new ArtilleryBulletType(12f, 10.0f){{
                    lifetime = 78f;
                    knockback = 1.6f;
                    width = 8.0f;
                    height = 8.0f;
                    splashDamageRadius = 15.25f;
                    splashDamage = 18f;
                    reloadMultiplier = 1.8f;
                    pierceArmor = true;
                }},

                Items.metaglass, new ArtilleryBulletType(6f, 25f){{
                    lifetime = 130f;
                    knockback = 1.6f;
                    width = 12f;
                    height = 12f;
                    splashDamageRadius = 20f;
                    splashDamage = 25f;
                    ammoMultiplier = 3.0f;
                    reloadMultiplier = 1.15f;
                    pierceArmor = true;
                    fragBullets = 6;
                    fragBullet = new BasicBulletType(2f, 10.0f){{
                        lifetime = 24f;
                        width = 10.0f;
                        height = 12f;
                        shrinkY = 1.0f;
                        collidesAir = false;
                    }};
                }},

                Items.graphite, new ArtilleryBulletType(5f, 40f){{
                    lifetime = 160f;
                    knockback = 1.6f;
                    width = 12f;
                    height = 12f;
                    splashDamageRadius = 19.25f;
                    splashDamage = 45f;
                    ammoMultiplier = 4;
                    pierceArmor = true;
                }},

                FRItems.sulflameAlloy, new ArtilleryBulletType(6f, 105f){{
                    lifetime = 130f;
                    knockback = 1.6f;
                    width = 12f;
                    height = 12f;
                    splashDamageRadius = 22.25f;
                    splashDamage = 80f;
                    ammoMultiplier = 4;
                    reloadMultiplier = 0.7f;
                    pierceArmor = true;
                    status = StatusEffects.blasted;
                    fragBullets = 8;
                    fragBullet = new BasicBulletType(2f, 55f){{
                        lifetime = 40f;
                        width = 10.0f;
                        height = 12f;
                        shrinkY = 1.0f;
                        status = StatusEffects.melting;
                        statusDuration = 240f;
                        pierceArmor = true;
                    }};
                }},

                FRItems.detonationCompound, new ArtilleryBulletType(4f, 65f){{
                    lifetime = 196f;
                    knockback = 2.0f;
                    width = 15f;
                    height = 15f;
                    splashDamageRadius = 32f;
                    splashDamage = 220f;
                    ammoMultiplier = 5;
                    pierceArmor = true;
                    status = StatusEffects.melting;
                    statusDuration = 300f;
                }}
            );
        }};

        nightmare = new ItemTurret("yg"){{
            requirements(Category.turret, with(
                Items.titanium, 75,
                Items.thorium, 60,
                Items.silicon, 35,
                FRItems.mirrorglass, 25
            ));
            researchCost = with(
                Items.titanium, 400,
                Items.thorium, 250,
                Items.silicon, 275,
                FRItems.mirrorglass, 165
            );
            health = 1080;
            size = 2;
            reload = 21.6f;
            range = 24 * tilesize;
            shootCone = 30.0f;
            inaccuracy = 2.0f;
            rotateSpeed = 10.0f;
            shootSound = Sounds.shootBig;
            coolantMultiplier = 2.0f;

            consumeCoolant(0.2f);

            ammo(

                Items.copper, new LaserBulletType(50.0f){{
                    length = 196.0f;
                    colors(colors, _ff9900_a04, _ff9900, Color.white);
                    reloadMultiplier = 0.8f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.lead, new LaserBulletType(45.0f){{
                    length = 196.0f;
                    colors(colors, _ccccff_a04, _ccccff, Color.white);
                    reloadMultiplier = 1.1f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.metaglass, new LaserBulletType(55.0f){{
                    length = 241.0f;
                    width = 12.5f;
                    hitSize = 3.5f;
                    colors(colors, _ccccff_a04, _ccccff, Color.white);
                    rangeChange = 45.0f;
                    ammoMultiplier = 3;
                    reloadMultiplier = 0.8f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.graphite, new LaserBulletType(40.0f){{
                    length = 196.0f;
                    colors(colors, _33ccff_a04, _33ccff, Color.white);
                    status = StatusEffects.freezing;
                    statusDuration = 150f;
                    ammoMultiplier = 3;
                    reloadMultiplier = 1.6f;
                    buildingDamageMultiplier = 0.5f;
                    pierceCap = 4;
                }},

                Items.sand, new LaserBulletType(15.0f){{
                    length = 346.0f;
                    width = 10.0f;
                    hitSize = 3.0f;
                    colors(colors, _ccccff_a04, _ccccff, Color.white);
                    rangeChange = 150f;
                    ammoMultiplier = 1.0f;
                    reloadMultiplier = 0.5f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.titanium, new LaserBulletType(50.0f){{
                    length = 196.0f;
                    colors(colors, _ccccff_a04, _ccccff, Color.white);
                    status = StatusEffects.corroded;
                    statusDuration = 240.0f;
                    reloadMultiplier = 1.15f;
                    buildingDamageMultiplier = 0.5f;
                    pierceCap = 4;
                }},

                Items.thorium, new LaserBulletType(60.0f){{
                    length = 196.0f;
                    width = 20.0f;
                    hitSize = 5.0f;
                    colors = new Color[]{Pal.thoriumPink.cpy().a(0.4f), Pal.thoriumPink, Color.white};
                    status = StatusEffects.melting;
                    statusDuration = 240.0f;
                    reloadMultiplier = 0.75f;
                    buildingDamageMultiplier = 0.5f;
                    pierceCap = 4;
                }},

                Items.scrap, new LaserBulletType(25.0f){{
                    length = 366.0f;
                    width = 10.0f;
                    hitSize = 3.0f;
                    colors(colors, _ccccff_a04, _ccccff, Color.white);
                    rangeChange = 170.0f;
                    ammoMultiplier = 1.0f;
                    reloadMultiplier = 0.3f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.silicon, new LaserBulletType(65.0f){{
                    length = 196.0f;
                    colors(colors, _404040_a04, _404040, Color.white);
                    ammoMultiplier = 3.0f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.surgeAlloy, new LaserBulletType(80.0f){{
                    length = 231.0f;
                    width = 18.0f;
                    hitSize = 4.5f;
                    colors = new Color[]{Pal.surge.cpy().a(0.4f), Pal.surge, Color.white};
                    rangeChange = 35.0f;
                    pierceArmor = true;
                    status = StatusEffects.shocked;
                    lightningSpacing = 27.0f;
                    lightningLength = 1;
                    lightningDelay = 1.2f;
                    lightningLengthRand = 6;
                    lightningDamage = 20.0f;
                    lightningAngleRand = 24.0f;
                    ammoMultiplier = 4.0f;
                    reloadMultiplier = 0.85f;
                    buildingDamageMultiplier = 0.5f;
                    pierceCap = 4;
                    hitSound = Sounds.spark;
                }},

                FRItems.glass, new LaserBulletType(25.0f){{
                    length = 281.0f;
                    width = 10.0f;
                    hitSize = 3.0f;
                    colors(colors, _ccccff_a04, _ccccff, Color.white);
                    rangeChange = 85.0f;
                    ammoMultiplier = 3.0f;
                    reloadMultiplier = 0.8f;
                    buildingDamageMultiplier = 0.5f;
                }},

                FRItems.mirrorglass, new LaserBulletType(75.0f){{
                    length = 356.0f;
                    width = 10.0f;
                    hitSize = 3.0f;
                    colors(colors, _ccccff_a04, _ccccff, Color.white);
                    rangeChange = 160.0f;
                    ammoMultiplier = 4.0f;
                    reloadMultiplier = 0.6f;
                    buildingDamageMultiplier = 0.5f;
                }}
            );
        }};

        scab = new ItemTurret("scab"){{
            requirements(Category.turret, BuildVisibility.hidden, with(
                FRItems.flesh, 50
            ));
            health = 4000;
            size = 4;
            reload = 50.0f;
            range = 40 * tilesize;
            shootCone = 15.0f;
            inaccuracy = 1.0f;
            rotateSpeed = 4.0f;
            shoot = new ShootBarrel(){{
                shots = 3;
                barrels = new float[]{
                    -4.0f, 0.0f, 335.0f,
                    0.0f, 0.0f, 0.0f,
                    4.0f, 0.0f, 25.0f
                };
            }};

            ammo(
                FRItems.flesh, new FleshBulletType(15.6f, 75.0f, 12, 6, 120.0f){{
                    lifetime = 125.0f;
                    drag = 0.05f;
                    homingDelay = 10.0f;
                    homingPower = 0.06f;
                    homingRange = 8 * tilesize;
                    statusDuration = 360.0f;

                    trailLength = 3;
                    trailWidth = 2.2f;
                    trailColor = _f57946;
                    trailEffect = FRStatusEffects.overgrown.effect;
                    trailChance = 0.15f;

                    adhereChancePercentage = 40;
                    removeAmount = 10;
                    maxSpread = 2.0f;
                    spreadIntensity = 0.03f;
                }}
            );
        }};

        ignition = new ContinuousTurret("dr"){{
            requirements(Category.turret, with(
                Items.copper, 350,
                Items.graphite, 240,
                Items.silicon, 220,
                Items.plastanium, 180
            ));
            health = 1800;
            size = 3;
            hasPower = true;
            hasItems = false;
            liquidCapacity = 60f;
            range = 225f;
            shootCone = 360f;
            rotateSpeed = 1.2f;
            recoil = 0.9f;
            shootWarmupSpeed = 0.16f;
            aimChangeSpeed = 1.8f;
            shootY = 2.0f;
            shootSound = Sounds.tractorbeam;
            loopSoundVolume = 1.0f;
            loopSound = Sounds.flux;

            drawer = new DrawTurret(){{
                var heatP = DrawPart.PartProgress.warmup.blend(p -> Mathf.absin(2.0f, 1.0f) * p.warmup, 0.2f);

                parts.add(

                    new RegionPart("-main"){{
                        progress = PartProgress.warmup;
                        heatProgress = heatP;
                        under = true;
                    }},

                    new RegionPart("-blade"){{
                        progress = PartProgress.warmup;
                        heatProgress = heatP;
                        mirror = true;
                        moves.add(new PartMove(PartProgress.warmup, 1.6f, 2.0f, -15.0f));
                    }},

                    new RegionPart("-barrel"){{
                        progress = PartProgress.warmup;
                        heatProgress = heatP;
                        mirror = true;
                        moves.add(new PartMove(PartProgress.warmup, 0.0f, -1.6f, 0.0f));
                    }}
                );
            }};

            consumePower(n(400));
            consumeLiquid(Liquids.slag, n(20));

            shootType = new PointLaserBulletType(){{
                damage = 160.0f;
                drawSize = 800.0f;
                status = StatusEffects.melting;
                statusDuration = 150.0f;
                hitColor = Pal.slagOrange;
            }};
        }};

        blossom = new PowerTurret("blossom"){{
            requirements(Category.turret, with(
                Items.lead, 140,
                Items.graphite, 90,
                Items.silicon, 105,
                Items.plastanium, 45
            ));
            health = 1650;
            size = 3;
            liquidCapacity = 30f;
            reload = 40f;
            range = 250f;
            shootCone = 30f;
            inaccuracy = 2.0f;
            rotateSpeed = 3.5f;
            recoil = 1.0f;
            shake = 1.0f;
            velocityRnd = 0.1f;
            coolantMultiplier = 1.75f;
            shootSound = Sounds.missile;
            shoot = new ShootAlternate(2.4f);
            shoot.shots = 2;

            consumePower(n(480));
            consumeCoolant(0.3f);

            shootType = new FlakBulletType(4.0f, 40.0f){{
                var color = StatusEffects.blasted.color;

                sprite = "missile-large";
                lifetime = 62.0f;
                width = 12.0f;
                height = 12.0f;
                drag = -0.003f;
                homingRange = 80.0f;
                explodeRange = 40.0f;
                splashDamageRadius = 36.0f;
                splashDamage = 60.0f;
                weaveScale = 10.0f;
                weaveMag = 2.0f;
                trailLength = 24;
                trailWidth = 2.7f;
                lightRadius = 100.0f;
                lightOpacity = 1.2f;
                ammoMultiplier = 1.0f;
                buildingDamageMultiplier = 0.25f;
                collidesGround = true;
                makeFire = true;
                statusDuration = 480.0f;
                status = StatusEffects.burning;
                trailColor = lightColor = backColor = color;
                frontColor = Color.white;
                hitSound = Sounds.explosion;
                hitEffect = new ExplosionEffect(){{
                    lifetime = 27.0f;
                    waveStroke = 4.0f;
                    waveLife = 8.0f;
                    waveRadBase = 8.0f;
                    waveRad = 24.0f;
                    sparks = 4;
                    sparkRad = 27.0f;
                    sparkStroke = 1.5f;
                    sparkLen = 3.0f;
                    smokes = 4;
                    waveColor = sparkColor = color;
                    smokeColor = Color.white;
                }};
                fragBullets = 6;
                fragBullet = new BasicBulletType(4.0f, 25.0f){{
                    lifetime = 16.0f;
                    width = 5.0f;
                    height = 12.0f;
                    splashDamageRadius = 32.0f;
                    splashDamage = 35.0f;
                    homingPower = 0.15f;
                    homingRange = 80.0f;
                    homingDelay = 8.0f;
                    trailLength = 3;
                    trailWidth = 1.0f;
                    buildingDamageMultiplier = 0.25f;
                    collidesGround = true;
                    status = StatusEffects.blasted;
                    backColor = color;
                    frontColor = Color.white;
                    trailColor = color;
                }};
            }};
        }};

        gambler = new JackpotTurret("gambler"){{
            final float chargeTime = 120.0f;
            var colors = new Color[]{Pal.lightOrange, Pal.thoriumPink, Pal.surge, Pal.sapBulletBack};
            var chargeFx = FRFx.jackpotChargeEffect(chargeTime, 0.17f, 40.0f, 4, colors);

            requirements(Category.turret, with(
                Items.thorium, 450,
                Items.plastanium, 145,
                Items.surgeAlloy, 85,
                FRItems.logicAlloy, 60
            ));
            health = 1920;
            size = 3;
            hasLiquids = false;
            canOverdrive = false;
            reload = 120.0f;
            range = 40 * tilesize;
            shootCone = 30.0f;
            shootY = 0.0f;
            recoil = 13.0f;
            rotateSpeed = 4.0f;
            ammoPerShot = 5;
            maxAmmo = 20;
            shootSound = Sounds.shotgun;
            shoot.firstShotDelay = chargeTime;

            jackpotAmmo.add(

                new JackpotAmmo(Items.copper, 45,
                    new ShootAlternate(4.0f){{
                        shots = barrels = 3;
                        firstShotDelay = chargeTime;
                    }},
                    new BasicBulletType(18.0f, 180.0f){{
                        lifetime = 30.0f;
                        width = 6.0f;
                        height = 12.0f;
                        drag = 0.04f;
                        status = FRStatusEffects.disintegrated;
                        statusDuration = 240.0f;

                        chargeEffect = chargeFx;
                        shootEffect = FRFx.squareEffect(60.0f, 4, 3.0f, 45.0f, null);
                        frontColor = Color.white;
                        backColor = hitColor = colors[0];
                    }}),

                new JackpotAmmo(Items.thorium, 30,
                    new ShootMulti(
                        new ShootAlternate(0.0f){{
                            shots = 1;
                            barrels = 7;
                            firstShotDelay = chargeTime;
                        }},
                        new ShootSpread(7, 2.0f)
                    ),
                    new BasicBulletType(10.5f, 160.0f){{
                        lifetime = 30.0f;
                        width = 8.0f;
                        height = 10.0f;
                        status = FRStatusEffects.disintegrated;
                        statusDuration = 360.0f;
                        pierce = pierceBuilding = true;
                        pierceCap = 2;

                        chargeEffect = chargeFx;
                        shootEffect = FRFx.squareEffect(60.0f, 2, 3.0f, 45.0f, null);
                        hitEffect = despawnEffect = FRFx.hitBulletSmall(colors[1]);
                        frontColor = Color.white;
                        backColor = hitColor = colors[1];
                    }}
                ),

                new JackpotAmmo(Items.surgeAlloy, 15,
                    new ShootMulti(
                        new ShootAlternate(0.0f){{
                            shots = 2;
                            barrels = 7;
                            shotDelay = 6.0f;
                            firstShotDelay = chargeTime;
                        }},
                        new ShootSpread(7, 3.0f)
                    ),
                    new BasicBulletType(10.5f, 90.0f){{
                        lifetime = 30.0f;
                        width = 8.0f;
                        height = 10.0f;
                        status = FRStatusEffects.disintegrated;
                        statusDuration = 480.0f;
                        pierce = pierceBuilding = true;
                        pierceCap = 3;
                        lightning = 2;
                        lightningDamage = 10.0f;
                        lightningLength = 3;
                        lightningLengthRand = 1;

                        chargeEffect = chargeFx;
                        shootEffect = FRFx.squareEffect(25.0f, 1, 3.0f, 45.0f, null);
                        hitEffect = despawnEffect = FRFx.hitBulletSmall(colors[2]);
                        frontColor = Color.white;
                        backColor = hitColor = colors[2];
                    }}
                ),

                new JackpotAmmo(FRItems.hardenedAlloy, 0,
                    new ShootMulti(
                        new ShootAlternate(0.0f){{
                            shots = 7;
                            barrels = 7;
                            shotDelay = 4.0f;
                            firstShotDelay = chargeTime;
                        }},
                        new ShootSpread(7, 3.0f)
                    ),
                    new BasicBulletType(10.5f, 75.0f){{
                        lifetime = 30.0f;
                        width = 8.0f;
                        height = 10.0f;
                        status = FRStatusEffects.disintegrated;
                        statusDuration = 600.0f;
                        pierce = pierceBuilding = true;
                        pierceCap = 4;

                        chargeEffect = chargeFx;
                        shootEffect = FRFx.squareEffect(15.0f, 1, 3.0f, 45.0f, null);
                        hitEffect = despawnEffect = FRFx.hitBulletSmall(colors[3]);
                        frontColor = Pal.sapBullet;
                        backColor = hitColor = colors[3];
                    }}
                )
            );
        }};

        seaquake = new LiquidTurret("dh"){{
            requirements(Category.turret, with(
                Items.lead, 420,
                Items.metaglass, 175,
                Items.thorium, 225,
                Items.plastanium, 135,
                Items.surgeAlloy, 60
            ));
            health = 1980;
            size = 3;
            liquidCapacity = 75f;
            reload = 60f / 25f;
            range = 39 * tilesize;
            shootCone = 60f;
            inaccuracy = 2.7f;
            recoil = 2.0f;
            velocityRnd = 0.1f;
            shootEffect = Fx.shootLiquid;
            shoot.shots = 3;

            consumePower(n(200));

            ammo(

                Liquids.water, new LiquidBulletType(Liquids.water){{
                    speed = 6.0f;
                    damage = 0.4f;
                    lifetime = 54f;
                    knockback = 2.2f;
                    puddleSize = 11f;
                    orbSize = 6.0f;
                    drag = 0.001f;
                    layer = Layer.bullet - 2.0f;
                    ammoMultiplier = 0.6f;
                    statusDuration = 270f;
                }},

                Liquids.slag, new LiquidBulletType(Liquids.slag){{
                    speed = 6.0f;
                    damage = 5.75f;
                    lifetime = 54f;
                    knockback = 1.5f;
                    puddleSize = 11f;
                    orbSize = 6.0f;
                    drag = 0.001f;
                    ammoMultiplier = 0.6f;
                    statusDuration = 270f;
                }},

                Liquids.cryofluid, new LiquidBulletType(Liquids.cryofluid){{
                    speed = 6.0f;
                    damage = 0.4f;
                    lifetime = 54f;
                    knockback = 1.5f;
                    puddleSize = 11f;
                    orbSize = 6.0f;
                    drag = 0.001f;
                    ammoMultiplier = 0.6f;
                    statusDuration = 270f;
                }},

                Liquids.oil, new LiquidBulletType(Liquids.oil){{
                    speed = 6.0f;
                    damage = 0.4f;
                    lifetime = 54f;
                    knockback = 1.5f;
                    puddleSize = 11f;
                    orbSize = 6.0f;
                    drag = 0.001f;
                    layer = Layer.bullet - 2.0f;
                    ammoMultiplier = 0.6f;
                    statusDuration = 270f;
                }},

                FRLiquids.liquidNitrogen, new LiquidBulletType(FRLiquids.liquidNitrogen){{
                    speed = 6.0f;
                    damage = 5.5f;
                    lifetime = 54f;
                    knockback = 2.0f;
                    puddleSize = 11f;
                    orbSize = 6.0f;
                    drag = 0.001f;
                    boilTime = 120f;
                    ammoMultiplier = 0.6f;
                    statusDuration = 270f;
                }}
            );
        }};

        distance = new ItemTurret("ql"){{
            requirements(Category.turret, with(
                Items.copper, 800,
                Items.thorium, 450,
                Items.silicon, 525,
                Items.plastanium, 250,
                FRItems.hardenedAlloy, 225
            ));
            health = 2160;
            armor = 8.0f;
            size = 3;
            liquidCapacity = 45.0f;
            reload = 600.0f;
            range = 500.0f;
            shootCone = 10.0f;
            rotateSpeed = 1.2f;
            recoil = 2.0f;
            shake = 4.0f;
            coolantMultiplier = 3.0f;
            ammoPerShot = 4;
            shootSound = Sounds.missileLaunch;

            consumeCoolant(0.5f);

            ammo(

                FRItems.logicAlloy, new SpriteBulletType(3.0f, 300.0f, 18.0f, 30.0f, "fire-missile"){{
                    lifetime = 167.0f;
                    splashDamageRadius = 30.0f;
                    splashDamage = 75.0f;
                    homingPower = 0.16f;
                    despawnShake = 3.0f;
                    ammoMultiplier = 1.0f;
                    buildingDamageMultiplier = 0.2f;

                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmokeMissile;
                    hitSound = Sounds.mediumCannon;
                    hitColor = Pal.redLight;
                    hitEffect = despawnEffect = Fx.massiveExplosion;

                    trailChance = 0.1f;
                    trailColor = Color.grays(0.6f).lerp(Pal.redLight, 0.5f).a(0.4f);
                    trailEffect = new Effect(100.0f, 180.0f, e -> {

                        Draw.color(e.color, 0.5f);
                        Fx.rand.setSeed(e.id * 2L + 1);
                        e.scaled(e.lifetime * Fx.rand.random(0.5f, 1.0f), f ->
                            Angles.randLenVectors(f.id, f.fin(Interp.pow10Out), 2, 10.0f, (x, y, in, out) -> {
                                float rad = f.fout(Interp.pow5Out) * Fx.rand.random(0.5f, 1.0f) * 7.2f;

                                Fill.circle(f.x + x, f.y + y, rad);
                                Drawf.light(f.x + x, f.y + y, rad * 2.0f, f.color, 0.5f);
                            }));

                    }).layer(Layer.bullet - 1.0f);
                }},

                FRItems.detonationCompound, new SpriteBulletType(4.0f, 210.0f, 18.0f, 30.0f, "fire-missile"){{
                    lifetime = 193.0f;
                    splashDamage = 330.0f;
                    splashDamageRadius = 64.0f;
                    rangeChange = 270.0f;
                    homingPower = 0.16f;
                    despawnShake = 5.0f;
                    makeFire = true;
                    ammoMultiplier = 1.0f;
                    buildingDamageMultiplier = 0.2f;

                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmokeMissile;
                    hitSound = Sounds.mediumCannon;
                    hitColor = Pal.redLight;
                    hitEffect = despawnEffect = new Effect(50.0f, 140.0f, e -> {
                        float circleRad = 6.0f + e.finpow() * 40.0f;

                        Draw.color(e.color);
                        Lines.stroke(e.fout() * 5.0f);
                        Lines.circle(e.x, e.y, circleRad);

                        Fx.rand.setSeed(e.id);
                        for(byte i = 0; i < 12; i++){
                            float angle = Fx.rand.random(360.0f);
                            float lenRand = Fx.rand.random(0.5f, 1f);
                            Tmp.v1.trns(angle, circleRad);

                            for(int s : Mathf.signs)
                                Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 30.0f, e.fout() * 24.0f * lenRand + 6.0f, angle + 90.0f + s * 90.0f);
                        }
                    });

                    trailChance = 0.3f;
                    trailColor = Color.grays(0.6f).lerp(Pal.redLight, 0.5f).a(0.4f);
                    trailEffect = new Effect(140.0f, 240.0f, e -> {
                        final float intensity = 1.6f;

                        Draw.color(e.color, 0.7f);
                        Fx.rand.setSeed(e.id * 2L + 1);
                        e.scaled(e.lifetime * Fx.rand.random(0.5f, 1.0f), f ->
                            Angles.randLenVectors(f.id, f.fin(Interp.pow10Out), (int)(2.4f * intensity), 10.0f * intensity, (x, y, in, out) -> {
                                float rad = f.fout(Interp.pow5Out) * Fx.rand.random(0.5f, 1.0f) * (2.0f + intensity) * 2.35f;

                                Fill.circle(f.x + x, f.y + y, rad);
                                Drawf.light(f.x + x, f.y + y, rad * 2.0f, f.color, 0.5f);
                        }));

                    }).layer(Layer.bullet - 1.0f);

                    fragBullets = 3;
                    fragBullet = new MissileBulletType(3.0f, 105.0f){{
                        lifetime = 72.0f;
                        width = 20.0f;
                        height = 24.0f;
                        splashDamage = 225.0f;
                        splashDamageRadius = 48.0f;
                        homingPower = 0.2f;
                        despawnShake = 2.0f;
                        shrinkY = 1.0f;
                        buildingDamageMultiplier = 0.2f;
                        status = StatusEffects.blasted;
                        hitEffect = Fx.blastExplosion;

                        trailColor = Pal.redLight;
                        trailInterval = 25.0f;
                        trailEffect = new Effect(30.0f, e -> {
                            Draw.color(e.color);
                            Fill.circle(e.x, e.y, 1.2f * e.rotation * e.fout() + 0.8f);
                        });
                    }};
                }}
            );
        }};

        magneticDomain = new EnergyField.EnergyFieldPowerTurret("magnetic-domain"){{
            final byte r = 36;

            requirements(Category.turret, with(
                Items.lead, 500,
                FRItems.magneticAlloy, 200
            ));
            health = 1980;
            armor = 8.0f;
            size = 3;
            hasLiquids = false;
            reload = 90.0f;
            range = r * tilesize;
            recoil = 0.0f;
            rotateSpeed = 1.6f;
            shootCone = 360.0f;
            shootY = 0.0f;
            outlineIcon = false;
            playerControllable = false;
            shootSound = Sounds.spark;

            consumePower(n(600));

            shootType = new EnergyField.EnergyFieldBulletType(30.0f, 15){{
                homingRange = r * tilesize;
                knockback = 6.0f;
                pierceArmor = true;
                displayAmmoMultiplier = false;
                status = StatusEffects.slow;
                statusDuration = 180.0f;
                hitEffect = FRFx.chainLightningThin;
                lightningColor = Pal.surge;

                fragBullets = 1;
                fragBullet = new BulletType(0.0f, 0.0f){{
                    lifetime = 0.0f;
                    splashDamageRadius = homingRange;
                    status = StatusEffects.electrified;
                    statusDuration = 60.0f;
                }};
            }};
        }};

        grudge = new ItemTurret("grudge"){{
            requirements(Category.turret, with(
                Items.copper, 1350,
                Items.graphite, 475,
                Items.phaseFabric, 175,
                Items.surgeAlloy, 350,
                FRItems.hardenedAlloy, 400
            ));
            health = 4320;
            size = 4;
            armor = 20.0f;
            liquidCapacity = 75.0f;
            canOverdrive = false;
            reload = 14.0f;
            range = 45 * tilesize;
            shootCone = 24.0f;
            inaccuracy = 2.0f;
            maxAmmo = 60;
            recoil = 4.0f;
            recoilTime = 10.0f;
            shake = 2.2f;
            rotateSpeed = 6.5f;
            unitSort = FRUnitSorts.strongerPlus;
            shootSound = Sounds.shootBig;
            ammoUseEffect = Fx.casing3;
            coolantMultiplier = 0.8f;
            shoot = new ShootAlternate(6.3f){{shots=2;barrels=3;}};

            consumeCoolant(1.2f);
            ammo(

                Items.thorium, new BasicBulletType(8.0f, 90.0f){{
                    lifetime = 46.0f;
                    knockback = 0.8f;
                    width = 20.0f;
                    height = 27.0f;
                    hitSize = 6.0f;
                    pierceCap = 4;
                    pierceBuilding = true;
                    pierceArmor = true;
                    ammoMultiplier = 4;
                    status = FRStatusEffects.disintegrated;
                    statusDuration = 120.0f;
                    shootEffect = Fx.shootBig;
                    fragBullets = 4;
                    fragBullet = new BasicBulletType(4.0f, 30.0f){{
                        lifetime = 8.0f;
                        width = 8.0f;
                        height = 11.0f;
                        drag = 0.02f;
                        status = StatusEffects.corroded;
                        statusDuration = 240.0f;
                    }};
                }},

                FRItems.detonationCompound, new BasicBulletType(8.0f, 65.0f){{
                    lifetime = 46.0f;
                    knockback = 0.7f;
                    width = 18.0f;
                    height = 25.0f;
                    hitSize = 6.0f;
                    pierceCap = 2;
                    pierceBuilding = true;
                    makeFire = true;
                    ammoMultiplier = 8;
                    reloadMultiplier = 0.6f;
                    buildingDamageMultiplier = 0.2f;
                    status = FRStatusEffects.disintegrated;
                    statusDuration = 120.0f;
                    shootEffect = Fx.shootBig;
                    frontColor = Pal.lightishOrange;
                    backColor = Pal.lightOrange;
                    fragBullets = 2;
                    fragBullet = new BulletType(4.0f, 0.0f){{
                        lifetime = 1.0f;
                        splashDamageRadius = 62.0f;
                        splashDamage = 90.0f;
                        hittable = reflectable = absorbable = false;
                        status = StatusEffects.melting;
                        statusDuration = 480.0f;
                        hitSoundVolume = 0.5f;
                        hitSound = Sounds.mediumCannon;
                        hitEffect = Fx.pulverize;
                        despawnEffect = new WaveEffect(){{
                            lifetime = 16.0f;
                            sizeFrom = 0.0f;
                            sizeTo = 56.0f;
                            strokeFrom = 3.0f;
                            strokeTo = 0.0f;
                            interp = Interp.pow2Out;
                            colorFrom = Pal.lightishOrange;
                            colorTo = Color.white;
                        }};
                    }};
                }},

                FRItems.hardenedAlloy, new BasicBulletType(9.0f, 220.0f){{
                    lifetime = 72f;
                    knockback = 2.0f;
                    width = 28f;
                    height = 32f;
                    drag = 0.01f;
                    hitSize = 7.0f;
                    rangeChange = 120.0f;
                    pierceCap = 4;
                    pierceBuilding = true;
                    reloadMultiplier = 0.2f;
                    buildingDamageMultiplier = 0.2f;
                    status = FRStatusEffects.disintegrated;
                    statusDuration = 120.0f;
                    shootEffect = Fx.shootBig;
                    frontColor = Pal.sapBullet;
                    backColor = Pal.sapBulletBack;
                    fragBullets = 2;
                    fragBullet = new BulletType(48.0f, 0.0f){{
                        lifetime = 1.0f;
                        splashDamageRadius = 52.0f;
                        splashDamage = 90.0f;
                        hittable = reflectable = absorbable = false;
                        hitSoundVolume = 0.5f;
                        hitSound = Sounds.mediumCannon;
                        hitEffect = Fx.pulverizeMedium;
                        despawnEffect = new WaveEffect(){{
                            lifetime = 15.0f;
                            sizeFrom = 0.0f;
                            sizeTo = 54.0f;
                            strokeFrom = 3.0f;
                            strokeTo = 0.0f;
                            interp = Interp.pow3Out;
                            colorFrom = Pal.sapBullet;
                            colorTo = Color.white;
                        }};
                    }};
                }}
            );
        }};

        aerolite = new ItemTurret("aerolite"){{
            requirements(Category.turret, with(
                Items.copper, 1100,
                Items.graphite, 600,
                FRItems.logicAlloy, 250,
                Items.surgeAlloy, 350,
                FRItems.hardenedAlloy, 300
            ));
            health = 3840;
            size = 4;
            armor = 20.0f;
            liquidCapacity = 75.0f;
            canOverdrive = false;
            targetAir = false;
            targetGround = true;
            reload = 90.0f;
            range = 80 * tilesize;
            inaccuracy = 15.0f;
            maxAmmo = 50;
            recoil = 3.6f;
            recoilTime = 40.0f;
            shake = 2.0f;
            rotateSpeed = 2.0f;
            shootY = 10.0f;
            ammoPerShot = 5;
            consumeAmmoOnce = false;
            shoot.shots = 3;
            shoot.shotDelay = 5.0f;
            shootSound = Sounds.titanExplosion;
            coolantMultiplier = 1.35f;
            consumeCoolant(0.5f);

            ammo(

                Items.plastanium, new ArtilleryBulletType(16.0f,0.0f){{
                    lifetime = 40.0f;
                    width = 16.0f;
                    height = 12.0f;
                    knockback = 8.0f;
                    splashDamageRadius = 40.0f;
                    splashDamage = 375.0f;
                    ammoMultiplier = 8.0f;
                    status = StatusEffects.blasted;
                    frontColor = Color.white;
                    backColor = trailColor = Pal.plastanium;

                    hitEffect = new ParticleEffect(){{
                        lifetime = 75.0f;
                        particles = 1;
                        baseLength = 0.0f;
                        length = 0.0f;
                        sizeFrom = 28.0f;
                        sizeTo = 0.0f;
                        sizeInterp = Interp.pow3In;
                        colorFrom = _fffac6;
                        colorTo = _d8d97faa;
                    }};

                    fragBullets = 12;
                    fragLifeMin = 0.3f;
                    fragLifeMax = 1.0f;
                    fragRandomSpread = 120.0f;
                    fragBullet = new BasicBulletType(6.0f,55.0f){{
                        lifetime = 30.0f;
                        width = 9.0f;
                        height = 6.0f;
                        knockback = 6.0f;
                        pierceCap = 4;
                    }};
                }},

                Items.blastCompound, new ArtilleryBulletType(4.0f,0.0f){{
                    Color color1 = _ea8878.cpy().lerp(Pal.redLight, 0.5f);

                    lifetime = 160.0f;
                    width = 16.0f;
                    height = 16.0f;
                    knockback = 8.0f;
                    splashDamageRadius = 160.0f;
                    splashDamage = 500.0f;
                    ammoMultiplier = 12.0f;
                    status = StatusEffects.blasted;
                    frontColor = Color.white;
                    backColor = color1;
                    trailColor = color1;

                    hitEffect = new MultiEffect(

                        new WaveEffect(){{
                            lifetime = 30.0f;
                            interp = Interp.pow3Out;
                            sizeFrom = 0.0f;
                            sizeTo = 160.0f;
                            strokeFrom = 8.0f;
                            strokeTo = 0.0f;
                            colorFrom = color1;
                            colorTo = _ea8878_a07;
                        }},

                        new ParticleEffect(){{
                            lifetime = 80.0f;
                            particles = 20;
                            baseLength = 20.0f;
                            length = 140.0f;
                            interp = Interp.pow3Out;
                            sizeInterp = Interp.pow3In;
                            sizeFrom = 20.0f;
                            sizeTo = 0.0f;
                            colorFrom = color1;
                            colorTo = _ea8878_a07;
                        }},

                        new ParticleEffect(){{
                            lifetime = 110.0f;
                            particles = 12;
                            baseLength = 0.0f;
                            length = 100.0f;
                            interp = Interp.pow3Out;
                            sizeInterp = Interp.pow3In;
                            sizeFrom = 24.0f;
                            sizeTo = 0.0f;
                            colorFrom = color1;
                            colorTo = _ea8878_a07;
                        }},

                        new ParticleEffect(){{
                            lifetime = 130.0f;
                            particles = 6;
                            baseLength = 0.0f;
                            length = 60.0f;
                            interp = Interp.pow3Out;
                            sizeInterp = Interp.pow3In;
                            sizeFrom = 32.0f;
                            sizeTo = 0.0f;
                            colorFrom = color1;
                            colorTo = _ea8878_a07;
                        }}
                    );
                }},

                Items.pyratite, new ArtilleryBulletType(16.0f,0.0f){{
                    lifetime = 40.0f;
                    width = 16.0f;
                    height = 12.0f;
                    knockback = 10.0f;
                    splashDamageRadius = 80.0f;
                    splashDamage = 430.0f;
                    ammoMultiplier = 12.0f;
                    makeFire = true;
                    status = StatusEffects.burning;
                    statusDuration = 840.0f;
                    frontColor = Color.white;
                    backColor = trailColor = Pal.lightishOrange;

                    hitEffect = new MultiEffect(

                        new WaveEffect(){{
                            lifetime = 45.0f;
                            interp = Interp.pow3Out;
                            sizeFrom = 0.0f;
                            sizeTo = 80.0f;
                            strokeFrom = 8.0f;
                            strokeTo = 0.0f;
                            colorFrom = colorTo = Color.white;
                        }},

                        new ParticleEffect(){{
                            lifetime = 45.0f;
                            particles = 1;
                            baseLength = 0.0f;
                            length = 0.0f;
                            sizeFrom = 32.0f;
                            sizeTo = 0.0f;
                            sizeInterp = Interp.pow3In;
                            colorFrom = Pal.lightishOrange;
                            colorTo = Pal.lightishOrange.cpy().a(0.7f);
                        }}
                    );
                }},

                FRItems.detonationCompound, new ArtilleryBulletType(8.0f,0.0f){{
                    lifetime = 80.0f;
                    width = 16.0f;
                    height = 16.0f;
                    knockback = 8.0f;
                    splashDamageRadius = 100.0f;
                    splashDamage = 600.0f;
                    ammoMultiplier = 15.0f;
                    status = StatusEffects.blasted;

                    hitEffect = new MultiEffect(

                        new WaveEffect(){{
                            lifetime = 50.0f;
                            interp = Interp.pow3Out;
                            sizeFrom = 0.0f;
                            sizeTo = 120.0f;
                            strokeFrom = 8.0f;
                            strokeTo = 0.0f;
                            colorFrom = _ea8878;
                            colorTo = _ea8878_a07;
                        }},

                        new ParticleEffect(){{
                            lifetime = 100.0f;
                            particles = 16;
                            baseLength = 20.0f;
                            length = 80.0f;
                            interp = Interp.pow5Out;
                            sizeInterp = Interp.pow5In;
                            sizeFrom = 20.0f;
                            sizeTo = 0.0f;
                            colorFrom = _ea8878;
                            colorTo = _ea8878_a07;
                        }},

                        new ParticleEffect(){{
                            lifetime = 130.0f;
                            particles = 12;
                            baseLength = 0.0f;
                            length = 40.0f;
                            interp = Interp.pow5Out;
                            sizeInterp = Interp.pow5In;
                            sizeFrom = 24.0f;
                            sizeTo = 0.0f;
                            colorFrom = _ea8878;
                            colorTo = _ea8878_a07;
                        }},

                        new ParticleEffect(){{
                            lifetime = 160.0f;
                            particles = 6;
                            baseLength = 0.0f;
                            length = 20.0f;
                            interp = Interp.pow5Out;
                            sizeInterp = Interp.pow5In;
                            sizeFrom = 32.0f;
                            sizeTo = 0.0f;
                            colorFrom = _ea8878;
                            colorTo = _ea8878_a07;
                        }}
                    );

                    fragBullets = 5;
                    fragLifeMin = 0.4f;
                    fragLifeMax = 1.0f;
                    fragBullet = new BasicBulletType(6.0f,60.0f){{
                        lifetime = 50.0f;
                        width = 9.0f;
                        height = 6.0f;
                        knockback = 4.0f;
                        splashDamageRadius = 50.0f;
                        splashDamage = 100.0f;
                        status = StatusEffects.burning;
                        statusDuration = 300.0f;

                        hitEffect = new ParticleEffect(){{
                            lifetime = 60.0f;
                            particles = 1;
                            baseLength = 0.0f;
                            length = 0.0f;
                            interp = Interp.pow5Out;
                            sizeInterp = Interp.pow5In;
                            sizeFrom = 45.0f;
                            sizeTo = 0.0f;
                            colorFrom = _ea8878;
                            colorTo = _ea8878_a07;
                        }};
                    }};
                }}
            );
        }};

        magneticSphere = new PowerTurret("magnetic-sphere"){{
            requirements(Category.turret, with(
                Items.plastanium, 425,
                Items.surgeAlloy, 250,
                FRItems.logicAlloy, 400,
                FRItems.magneticAlloy, 125
            ));
            health = 6000;
            size = 4;
            liquidCapacity = 75.0f;
            canOverdrive = false;
            reload = 270.0f;
            range = 75 * tilesize;
            shootCone = 6.0f;
            recoil = 5.0f;
            rotateSpeed = 2.7f;
            coolantMultiplier = 0.8f;
            targetGround = false;
            moveWhileCharging = false;
            unitSort = UnitSorts.strongest;
            shootSound = Sounds.laser;
            shoot.firstShotDelay = 90.0f;

            consumePower(n(1920));
            consumeCoolant(1.0f);

            shootType = new BulletType(15.5f, 1800.0f){

                /** Prevent endless creating instances, since its terminal state is always the same. */
                private BulletType type;

                @Override
                public void update(Bullet b){
                    super.update(b);
                    if(b.time <= homingDelay || b.type == type) return;

                    if(Units.closestTarget(
                        b.team, b.x, b.y, homingRange - 40.0f,
                        e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                        t -> t != null && collidesGround && !b.hasCollided(t.id)) == null
                    ){
                        b.hit = true;    //if there's no target nearby, remove and don't create frag bullets
                        hitEffect.at(b); //but keep effect
                        b.remove();

                    }else{
                        type = b.type.copy();
                        type.speed = b.vel.setLength(8.0f).len();
                        type.drag = 0.0f;

                        b.type = type;
                    }
                }

                @Override
                public void draw(Bullet b){
                    super.draw(b);

                    // special thanks to Extra Utilities mod
                    // this is really lazy
                    for(byte i = 0; i < 4; i++){
                        Draw.color(_92f3fd);
                        Draw.alpha((0.3f + Mathf.absin(Time.time, 2.0f + i * 2.0f, 0.3f + i * 0.05f)));
                        Draw.blend(Blending.additive);
                        Draw.rect(Core.atlas.find("impact-reactor-plasma-" + i), b.x, b.y, 28.0f, 28.0f, Time.time * (12.0f + i * 6.0f));
                        Draw.blend();
                    }
                    Draw.reset();
                }
                {
                    lifetime = 600.0f;
                    hitSize = 6.0f;
                    pierceCap = 10;
                    drag = 0.024f;
                    ammoMultiplier = 1.0f;
                    collidesGround = false;

                    homingRange = 260.0f;
                    homingPower = 0.2f;
                    homingDelay = 120.0f;

                    trailChance = 0.4f;
                    trailRotation = true;
                    trailColor = _92f3fd;
                    trailEffect = new Effect(25.0f, e -> {
                        Draw.color(Color.white, e.color, e.fin());
                        Lines.stroke(1.4f + e.fout() * 3.4f);
                        Fx.rand.setSeed(e.id);

                        for(byte i = 0; i < 3; i++){
                            float rot = e.rotation + Fx.rand.range(15.0f) + 180.0f;
                            Fx.v.trns(rot, Fx.rand.random(e.fin() * 27.0f));
                            Lines.lineAngle(e.x + Fx.v.x, e.y + Fx.v.y, rot, e.fout() * Fx.rand.random(3.0f, 8.0f) + 2.0f);
                        }
                    });

                    chargeEffect = new Effect(shoot.firstShotDelay, 20.0f, e -> {
                        Draw.color(_92f3fd);
                        Lines.stroke(e.fin() * 2.0f);
                        Lines.circle(e.x, e.y, 4.0f + e.fout() * 20.0f);
                        Fill.circle(e.x, e.y, e.fin() * 16.0f);
                        Angles.randLenVectors(e.id, 16, 32.0f * e.fout(), (x, y) -> {
                            Fill.circle(e.x + x, e.y + y, e.fin() * 4.0f);
                            Drawf.light(e.x + x, e.y + y, e.fin() * 12.0f, _92f3fd, 0.7f);
                        });
                        Draw.color();
                        Fill.circle(e.x, e.y, e.fin() * 8);
                        Drawf.light(e.x, e.y, e.fin() * 16.0f, _92f3fd, 0.7f);
                    }).rotWithParent(true);

                    shootEffect = new Effect(18.0f, e -> {
                        Draw.color(_92f3fd, Color.lightGray, e.fin());
                        Angles.randLenVectors(e.id, 18, 7.0f + e.finpow() * 19.0f, (x, y) ->
                            Fill.square(e.x + x, e.y + y, e.fout() * 2.8f + 0.7f, 0.0f));
                    });

                    hitEffect = new Effect(14.0f, e -> {
                        Draw.color(_92f3fd, Color.lightGray, e.fin());
                        Angles.randLenVectors(e.id, 12, 9.0f + e.finpow() * 22.0f, (x, y) ->
                            Fill.square(e.x + x, e.y + y, e.fout() * 3.2f + 1.5f, 45.0f));
                    });

                    // create lightning while flying
                    bulletInterval = 2.0f;
                    intervalBullets = 1;
                    intervalBullet = new LightningBulletType(){{
                        damage = 5;
                        lightningColor = _92f3fd;
                        lightningLength = 10;
                        collidesGround = false;

                        fragBullets = 1;
                        fragBullet = new LightningPointBulletType(165.0f){{
                            homingRange = 120.0f;
                            lightningChancePercentage = 60;
                            lightningColor = _92f3fd;
                            collidesGround = false;
                        }};
                    }};

                    fragRandomSpread = 0.0f;
                    fragSpread = 60.0f;
                    fragBullets = 6;
                    fragBullet = new BasicBulletType(12.0f, 225.0f){{
                        lifetime = 45.0f;
                        width = 3.2f;
                        height = 4.0f;
                        collidesGround = false;
                        homingRange = 150.0f;
                        homingPower = 0.4f;
                        homingDelay = 5.0f;

                        backColor = _92f3fd;
                        frontColor = Color.white;
                        trailLength = 8;
                        trailWidth = 4;
                        trailColor = _92f3fd;
                    }};
                }};
        }};

        magneticRail = new ItemBulletStackTurret("magnetic-rail"){{
            final float
                chargeTime = 150.0f,
                baseRange = 1200.0f, extraRange = 400.0f;

            Item
                item_1 = FRItems.hardenedAlloy,
                item_2 = FRItems.magneticAlloy;

            BulletType
                bullet_1_1,
                bullet_2_1,
                bullet_2_2,
                bullet_2_3;

            final float
                speed_1_1 = 60.0f,
                lifetime_1_1 = 35.0f;

            bullet_1_1 = new BasicBulletType(speed_1_1, 18000.0f){{
                lifetime = lifetime_1_1;
                width = 20.0f;
                height = 60.0f;
                hitSize = 20.0f;
                rangeChange = extraRange;
                splashDamageRadius = 180.0f;
                splashDamage = 11500.0f;
                ammoMultiplier = 1.0f;
                reflectable = false;
                absorbable = false;
                hittable = false;

                backColor = _ec7458;
                frontColor = Color.white;
                trailColor = _ec7458;
                trailLength = 12;
                trailWidth = 3.0f;
                chargeEffect = new MultiEffect(
                    FRFx.railChargeEffect(chargeTime, _ec7458, 3.0f, baseRange + extraRange, 80.0f)
                );

                spawnBullets.add(
                    new FoldingBulletType(speed_1_1,18000.0f, 20, lifetime_1_1){{
                        lifetime = lifetime_1_1;
                        width = 16.0f;
                        height = 48.0f;
                        hitSize = 30.0f;
                        rangeChange = extraRange;
                        splashDamageRadius = 240.0f;
                        splashDamage = 14500.0f;
                        reflectable = false;
                        absorbable = false;
                        hittable = false;

                        backColor = _ec7458;
                        frontColor = Color.white;
                        trailColor = _ec7458;
                        trailLength = 10;
                        trailWidth = 2.4f;
                    }}
                );
            }};

            bullet_2_1 = new BasicBulletType(18f, 600f){{
                lifetime = 30f;
                width = 12f;
                height = 15f;
                trailLength = 5;
                trailWidth = 3;
                splashDamageRadius = 50f;
                splashDamage = 200f;
                drag = -0.05f;
                ammoMultiplier = 1;
                pierceCap = 30;
                pierceBuilding = true;
                collidesGround = true;
                reflectable = false;
                absorbable = false;
                hittable = false;

                backColor = _ec7458;
                frontColor = Color.white;
                trailColor = _ec7458;
                chargeEffect = new MultiEffect(
                    FRFx.railChargeEffect(chargeTime, _ec7458, 3.0f, baseRange, 80f)
                );
                despawnEffect = new MultiEffect(

                    //wave
                    new WaveEffect(){{
                        lifetime = 30f;
                        interp = Interp.circleOut;
                        sizeFrom = 120f;
                        sizeTo = 4.0f;
                        strokeFrom = 4.0f;
                        strokeTo = 0.0f;
                        colorFrom = _ec7458;
                        colorTo = _ec7458_a07;
                    }},

                    //circles
                    new ParticleEffect(){{
                        lifetime = 24f;
                        particles = 6;
                        baseLength = 24f;
                        sizeFrom = 12f;
                        sizeTo = 0.0f;
                        colorFrom = _ec7458;
                        colorTo = _ec7458_a07;
                    }},

                    //lines
                    new ParticleEffect(){{
                        lifetime = 24f;
                        particles = 15;
                        length = 24f;
                        baseLength = 16f;
                        line = true;
                        strokeFrom = 4.0f;
                        strokeTo = 0.0f;
                        lenFrom = 20f;
                        lenTo = 0.0f;
                        colorFrom = _ec7458;
                        colorTo = _ec7458_a07;
                    }}
                );

                // create lightning upon despawn
                lightningColor = _ec7458;
                lightning = 4;
                lightningLength = 6;
                lightningLengthRand = 2;
                lightningDamage = 10.0f;

                // create lightning while flying
                bulletInterval = 1.5f;
                intervalRandomSpread = 0.0f;
                intervalSpread = 359f;
                intervalBullets = 1;
                intervalBullet = new LightningBulletType(){{
                    damage = 5.0f;
                    lightningColor = _ec7458;
                    lightningLength = 4;
                }};

                fragRandomSpread = 30f;
                fragSpread = 60f;
            }};

            bullet_2_2 = bullet_2_1.copy();
            bullet_2_2.chargeEffect = Fx.none;
            bullet_2_2.smokeEffect = new MultiEffect(

                new ParticleEffect(){{
                    lifetime = 67f;
                    particles = 40;
                    sizeInterp = Interp.pow5Out;
                    interp = Interp.pow5In;
                    length = 180f;
                    baseLength = -180f;
                    sizeFrom = 0.0f;
                    sizeTo = 10.0f;
                    colorFrom = _ec7458_a07;
                    colorTo = _ec7458;
                }},

                new ParticleEffect(){{
                    lifetime = 67f;
                    particles = 3;
                    sizeInterp = Interp.pow3In;
                    length = 0.0f;
                    sizeFrom = 0.0f;
                    sizeTo = 40f;
                    colorFrom = _ec7458_a07;
                    colorTo = _ec7458;
                }},

                new ParticleEffect(){{
                    lifetime = 20f;
                    particles = 3;
                    length = 0.0f;
                    sizeFrom = 40f;
                    sizeTo = 15f;
                    colorFrom = _ec7458;
                    colorTo = _ec7458_a07;
                }}.startDelay(45.0f)
            );

            bullet_2_3 = new BasicBulletType(18f, 2400f){{
                lifetime = 30f;
                width = 24f;
                height = 30f;
                trailLength = 11;
                trailWidth = 6;
                splashDamageRadius = 210f;
                splashDamage = 1200f;
                drag = -0.05f;
                ammoMultiplier = 1;
                pierceCap = 30;
                pierceBuilding = true;
                collidesGround = true;
                reflectable = false;
                absorbable = false;
                hittable = false;

                backColor = _ec7458;
                frontColor = Color.white;
                trailColor = _ec7458;
                despawnEffect = new MultiEffect(

                    //wave
                    new WaveEffect(){{
                        lifetime = 75f;
                        interp = Interp.circleOut;
                        sizeFrom = 192f;
                        sizeTo = 8.0f;
                        strokeFrom = 8.0f;
                        strokeTo = 0.0f;
                        colorFrom = colorTo = _ec7458;
                    }},

                    //circles
                    new ParticleEffect(){{
                        lifetime = 60f;
                        particles = 12;
                        baseLength = 72f;
                        sizeFrom = 32f;
                        sizeTo = 0.0f;
                        colorFrom = colorTo = _ec7458;
                    }},

                    //lines
                    new ParticleEffect(){{
                        lifetime = 60f;
                        particles = 30;
                        length = 72f;
                        baseLength = 48f;
                        line = true;
                        strokeFrom = 8.0f;
                        strokeTo = 0.0f;
                        lenFrom = 60f;
                        lenTo = 0.0f;
                        colorFrom = colorTo = _ec7458;
                    }}
                );

                // create lightning upon despawn
                lightningColor = _ec7458;
                lightning = 16;
                lightningLength = 24;
                lightningLengthRand = 4;
                lightningDamage = 40f;

                // create lightning while flying
                bulletInterval = 0.5f;
                intervalRandomSpread = 0.0f;
                intervalSpread = 359f;
                intervalBullets = 2;
                intervalBullet = new LightningBulletType(){{
                    damage = 5.0f;
                    lightningColor = _ec7458;
                    lightningLength = 16;
                }};

                fragRandomSpread = 30f;
                fragSpread = 60f;
                fragBullets = 6;
                fragBullet = new BasicBulletType(12f, 800f){{
                    lifetime = 45f;
                    width = 24f;
                    height = 30f;
                    drag = 0.03f;
                    trailLength = 8;
                    trailWidth = 6;
                    splashDamageRadius = 220f;
                    splashDamage = 300f;
                    pierceCap = 1;
                    pierceBuilding = true;
                    collidesGround = true;
                    reflectable = false;
                    absorbable = false;
                    hittable = false;

                    backColor = _ec7458;
                    frontColor = Color.white;
                    trailColor = _ec7458;
                    despawnEffect = new MultiEffect(

                        //wave
                        new WaveEffect(){{
                            lifetime = 24.0f;
                            interp = Interp.circleOut;
                            sizeFrom = 8.0f;
                            sizeTo = 108.0f;
                            strokeFrom = 4.0f;
                            strokeTo = 0.0f;
                            colorFrom = colorTo = _ec7458;
                        }},

                        //circles
                        new ParticleEffect(){{
                            lifetime = 30.0f;
                            particles = 6;
                            baseLength = 40.0f;
                            sizeFrom = 20.0f;
                            sizeTo = 0.0f;
                            colorFrom = colorTo = _ec7458;
                        }},

                        //lines
                        new ParticleEffect(){{
                            lifetime = 30.0f;
                            particles = 12;
                            length = 40.0f;
                            baseLength = 24.0f;
                            line = true;
                            strokeFrom = 4.0f;
                            strokeTo = 0.0f;
                            lenFrom = 40.0f;
                            lenTo = 0.0f;
                            colorFrom = colorTo = _ec7458;
                        }}
                    );

                    // create lightning upon despawn
                    lightningColor = _ec7458;
                    lightning = 8;
                    lightningLength = 16;
                    lightningLengthRand = 2;
                    lightningDamage = 15f;

                    // create lightning while flying
                    bulletInterval = 0.5f;
                    intervalRandomSpread = 0.0f;
                    intervalSpread = 359f;
                    intervalBullets = 1;
                    intervalBullet = new LightningBulletType(){{
                        damage = 5.0f;
                        lightningColor = _ec7458;
                        lightningLength = 12;
                    }};

                    fragRandomSpread = 30f;
                    fragSpread = 72f;
                    fragBullets = 5;
                    fragBullet = new BasicBulletType(6f, 300f){{
                        lifetime = 16f;
                        width = 24f;
                        height = 30f;
                        drag = -0.06f;
                        trailLength = 6;
                        trailWidth = 6;
                        splashDamageRadius = 120f;
                        splashDamage = 180f;
                        collidesGround = true;
                        reflectable = false;
                        absorbable = false;
                        hittable = false;
                        backColor = _ec7458;
                        frontColor = Color.white;
                        trailColor = _ec7458;
                    }};
                }};
            }};

            requirements(Category.turret, with(
                Items.thorium, 3200,
                Items.plastanium, 4000,
                FRItems.conductor, 14400,
                FRItems.hardenedAlloy, 3600
            ));
            scaledHealth = 1000;
            size = 12;
            liquidCapacity = 1800f;
            canOverdrive = false;
            reload = 420f;
            range = baseRange;
            shootCone = 0.5f;
            recoil = 12.0f;
            recoilTime = 300.0f;
            rotateSpeed = 1.2f;
            ammoPerShot = 10;
            coolantMultiplier = 0.075f;
            unitSort = UnitSorts.strongest;
            moveWhileCharging = false;
            shootSound = Sounds.laser;
            shoot.firstShotDelay = chargeTime;

            stack(
                (int)item_2.id, Seq.with(new BulletStack(30, bullet_2_2), new BulletStack(60, bullet_2_3))
            );

            consumePower(n(300000));
            consumeCoolant(8.0f);

            drawer = new DrawTurret(){{
                parts.addAll(
                    new ShapePart(){{
                        progress = DrawPart.PartProgress.smoothReload;
                        layer = Layer.effect;
                        x = y = 0.0f;
                        radius = 57f;
                        radiusTo = 120f;
                        stroke = 8.0f;
                        strokeTo = 0.0f;
                        circle = true;
                        hollow = true;
                        color = _ec7458;
                    }},
                    new ShapePart(){{
                        progress = DrawPart.PartProgress.smoothReload;
                        layer = Layer.effect;
                        x = y = 0.0f;
                        radius = 58f;
                        radiusTo = 32f;
                        stroke = 4.0f;
                        strokeTo = 0.0f;
                        circle = true;
                        hollow = true;
                        color = _ec7458;
                    }},
                    new ShapePart(){{
                        progress = DrawPart.PartProgress.smoothReload;
                        layer = Layer.effect;
                        x = y = 0.0f;
                        radius = 32f;
                        radiusTo = 0.0f;
                        stroke = 3.0f;
                        strokeTo = 0.0f;
                        circle = true;
                        hollow = true;
                        color = _ec7458;
                    }},
                    new ShapePart(){{
                        progress = DrawPart.PartProgress.smoothReload;
                        layer = Layer.effect;
                        x = y = 0.0f;
                        radius = 4.0f;
                        radiusTo = 0.0f;
                        stroke = 2.0f;
                        strokeTo = 0.0f;
                        circle = true;
                        hollow = false;
                        color = _ec7458;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0.0f;
                        radius = 4.0f;
                        radiusTo = 0.0f;
                        stroke = 4.0f;
                        strokeTo = 0.0f;
                        haloRadius = 8.0f;
                        haloRotation = 0.0f;
                        haloRotateSpeed = 0.6f;
                        rotateSpeed = 0.0f;
                        sides = 5;
                        hollow = false;
                        tri = false;
                        color = _ec7458;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0.0f;
                        radius = 8.0f;
                        radiusTo = 0.0f;
                        stroke = 4.0f;
                        strokeTo = 0.0f;
                        haloRadius = 24f;
                        haloRotation = 0.0f;
                        haloRotateSpeed = -1.8f;
                        rotateSpeed = 0.0f;
                        sides = 5;
                        hollow = false;
                        tri = false;
                        color = _ec7458;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0.0f;
                        radius = 12f;
                        radiusTo = 0.0f;
                        stroke = 4.0f;
                        strokeTo = 0.0f;
                        haloRadius = 43f;
                        haloRotation = 0.0f;
                        haloRotateSpeed = 2.2f;
                        rotateSpeed = 0.0f;
                        sides = 5;
                        hollow = false;
                        tri = false;
                        color = _ec7458;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0.0f;
                        radius = 12f;
                        radiusTo = 0.0f;
                        stroke = 4.0f;
                        strokeTo = 0.0f;
                        haloRadius = 43f;
                        haloRotation = 0.0f;
                        haloRotateSpeed = 2.2f;
                        rotateSpeed = 0.0f;
                        sides = 8;
                        hollow = false;
                        tri = false;
                        color = _ec7458;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0.0f;
                        radius = 18f;
                        radiusTo = 0.0f;
                        stroke = 4.0f;
                        strokeTo = 0.0f;
                        haloRadius = 63f;
                        haloRotation = 0.0f;
                        haloRotateSpeed = -1.2f;
                        rotateSpeed = 0.0f;
                        sides = 3;
                        shapes = 8;
                        hollow = false;
                        tri = false;
                        color = _ec7458;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0.0f;
                        radius = 10.0f;
                        radiusTo = 0.0f;
                        stroke = 4.0f;
                        strokeTo = 0.0f;
                        haloRadius = 63f;
                        haloRotation = 0.0f;
                        haloRotateSpeed = 2.8f;
                        rotateSpeed = 0.0f;
                        sides = 3;
                        shapes = 6;
                        hollow = false;
                        tri = false;
                        color = _ec7458;
                    }}
                );
            }};

            ammo(
                item_1, bullet_1_1.copy(),
                item_2, bullet_2_1
            );
        }};

        //endregion
        //region production

        chopper = new WallCrafter("fmj"){{
            requirements(Category.production, with(
                Items.copper, 20,
                Items.lead, 15,
                Items.titanium, 10,
                Items.silicon, 15
            ));
            researchCost = mult(requirements, 5);
            health = 65;
            size = 1;
            ambientSound = Sounds.drill;
            ambientSoundVolume = 0.01f;
            attribute = FRAttribute.tree;
            drillTime = 120;
            output = FRItems.timber;

            consumePower(n(24));
        }};

        treeFarm = new AttributeCrafter("sc"){{
            requirements(Category.production, with(
                Items.copper, 50,
                Items.lead, 30,
                Items.metaglass, 20,
                Items.titanium, 25
            ));
            size = 2;
            hasPower = true;
            hasLiquids = true;
            updateEffect = new Effect(60f, e -> {
                Draw.color(_6aa85e);
                Draw.alpha(e.fslope());
                Fx.rand.setSeed(e.id);
                for(byte i = 0; i < 2; i++){
                    Fx.v.trns(Fx.rand.random(360f), Fx.rand.random(e.finpow() * 9.0f)).add(e.x, e.y);
                    Fill.circle(Fx.v.x, Fx.v.y, Fx.rand.random(1.4f, 2.4f));
                }
            }).layer(Layer.bullet - 1.0f);

            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.water),
                new DrawDefault()
            );
            attribute = FRAttribute.grass;
            maxBoost = 1.0f;
            craftTime = 240f;
            outputItem = new ItemStack(FRItems.timber, 4);

            consumePower(n(150));
            consumeLiquid(Liquids.water, n(12));
        }};

        vapourCondenser = new GenericCrafter("sqlnq"){{
            requirements(Category.production, with(
                Items.lead, 50,
                Items.graphite, 30,
                Items.metaglass, 30,
                Items.titanium, 20
            ));
            size = 2;
            hasPower = true;
            hasItems = false;
            hasLiquids = true;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.water),
                new DrawDefault()
            );
            craftTime = 120f;
            outputLiquid = new LiquidStack(Liquids.water, 0.4f);

            consumePower(n(210));
        }};

        biomassCultivator = new AttributeCrafter("swzzsj"){{
            requirements(Category.production, with(
                Items.copper, 80,
                Items.lead, 105,
                Items.titanium, 50,
                Items.silicon, 75
            ));
            size = 3;
            researchCostMultiplier = 0.6f;
            hasPower = true;
            hasLiquids = true;
            itemCapacity = 20;
            liquidCapacity = 30f;
            updateEffect = new Effect(60f, e -> {
                Draw.color(_9e78dc);
                Draw.alpha(e.fslope());
                Fx.rand.setSeed(e.id);
                for(byte i = 0; i < 2; i++){
                    Fx.v.trns(Fx.rand.random(360f), Fx.rand.random(e.finpow() * 12f)).add(e.x, e.y);
                    Fill.circle(Fx.v.x, Fx.v.y, Fx.rand.random(1.8f, 2.8f));
                }
            }).layer(Layer.bullet - 1.0f);

            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.water),
                new DrawDefault(),
                new DrawCultivator(),
                new DrawRegion("-top")
            );
            attribute = Attribute.spores;
            maxBoost = 3.5f;
            craftTime = 60.0f;
            outputItem = new ItemStack(Items.sporePod, 3);

            consumePower(n(180));
            consumeLiquid(Liquids.water, n(30));
        }};

        stackedCultivator = new AttributeCrafter("stacked-cultivator"){{
            requirements(Category.production, with(
                Items.graphite, 300,
                FRItems.logicAlloy, 250,
                FRItems.hardenedAlloy, 300
            ));
            size = 6;
            hasPower = true;
            hasLiquids = true;
            itemCapacity = 75;
            liquidCapacity = 900.0f;
            updateEffect = new Effect(60f, e -> {
                Draw.color(_9e78dc);
                Draw.alpha(e.fslope());
                Fx.rand.setSeed(e.id);
                for(byte i = 0; i < 2; i++){
                    Fx.v.trns(Fx.rand.random(360f), Fx.rand.random(e.finpow() * 12f)).add(e.x, e.y);
                    Fill.circle(Fx.v.x, Fx.v.y, Fx.rand.random(1.8f, 2.8f));
                }
            }).layer(Layer.bullet - 1.0f);

            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.water),
                new DrawCultivator(),
                new DrawDefault()
            );
            attribute = FRAttribute.sporesWater;
            baseEfficiency = 0.0f;
            minEfficiency = 0.0001f;
            boostScale = 0.625f;
            maxBoost = 2.25f;
            craftTime = 25.0f;
            outputItem = new ItemStack(Items.sporePod, 10);
            outputLiquid = new LiquidStack(Liquids.water, n(60));
            dumpTime = 1;

            consumePower(n(3750));
        }};

        fissionDrill = new BurstDrill("lbzt"){{
            requirements(Category.production, with(
                Items.copper, 160,
                Items.metaglass, 80,
                Items.thorium, 375,
                Items.silicon, 145)
            );
            health = 1120;
            size = 5;
            itemCapacity = 75;
            liquidCapacity = 30.0f;
            drillEffect = new MultiEffect(
                Fx.mineImpact,
                Fx.drillSteam
            );
            baseExplosiveness = 5.0f;
            destroyBullet = destroyBullet(800.0f, 48.0f);

            tier = 8;
            drillTime = 45.0f;
            liquidBoostIntensity = 1.0f;
            dumpTime = 1;
            shake = 4.0f;
            baseArrowColor = _989aa4;

            consumeLiquid(Liquids.water, n(12));
        }};

        constraintExtractor = new BeamExtractor("constraint-extractor"){{
            requirements(Category.production, with(
                Items.metaglass, 75,
                FRItems.logicAlloy, 40,
                FRItems.magneticAlloy, 5
            ));
            health = 220;
            armor = 12.0f;
            size = 2;
            hasItems = true;
            hasPower = true;
            hasLiquids = true;
            itemCapacity = 20;
            liquidCapacity = 20.0f;

            tier = 3;
            range = 190;
            drillTime = 30;
            hardnessDrillMultiplier = 10;
            warmupSpeed = 0.05f;
            boostScale = 4.0f;
            shootY = 4;
            baseColor.set(Pal.accent);
            boostColor.set(((BeamDrill)Blocks.plasmaBore).boostHeatColor); //lazy
            updateEffect = Fx.pulverizeSmall;
            updateEffectChancePercentage = 2;
            ambientSound = Sounds.drill;
            ambientSoundVolume = 0.02f;

            consumePower(n(150));
            consumeLiquid(Liquids.cryofluid, n(6)).boost();
        }};

        //endregion
        //region distribution

        compositeConveyor = new Conveyor("fhcsd"){{
            requirements(Category.distribution, with(
                Items.thorium, 1,
                Items.plastanium, 1
            ));
            health = 85;
            speed = 0.22f;
            displayedSpeed = 25.0f;
            junctionReplacement = Blocks.invertedSorter;

            compositeMap.put(id, Blocks.titaniumConveyor.id);
        }};

        hardenedAlloyConveyor = new StackConveyor("hardened-alloy-conveyor"){{
            requirements(Category.distribution, with(
                Items.graphite, 1,
                Items.silicon, 1,
                FRItems.hardenedAlloy, 1
            ));
            health = 240;
            armor = 12.0f;
            speed = 0.1167f; // 7/60
            itemCapacity = 20;
            placeableLiquid = true;
        }};

        compositeBridgeConveyor = new ItemBridge("composite-bridge-conveyor"){{
            requirements(Category.distribution, with(
                Items.metaglass, 4,
                Items.titanium, 6,
                Items.thorium, 4,
                Items.plastanium, 4
            ));
            health = 85;
            hasPower = false;
            itemCapacity = 12;
            range = 8;
            pulse = true;
            ((Conveyor)compositeConveyor).bridgeReplacement = this;

            compositeMap.put(id, Blocks.itemBridge.id);
        }};

        //endregion
        //region liquid

        magneticRingPump = new Pump("magnetic-ring-pump"){{
            requirements(Category.liquid, with(
                FRItems.logicAlloy, 30,
                FRItems.hardenedAlloy, 90,
                FRItems.magneticAlloy, 30
            ));
            health = 720;
            armor = 16.0f;
            size = 2;
            hasPower = true;
            liquidCapacity = 80.0f;
            pumpAmount = 0.5f;
            consumePower(n(90));
        }};

        compositeLiquidRouter = new LiquidRouter("composite-liquid-router"){{
            requirements(Category.liquid, with(
                Items.metaglass, 8,
                Items.titanium, 12,
                Items.thorium, 6,
                Items.plastanium, 8
            ));
            health = 85;
            liquidCapacity = 150.0f;
            solid = false;
            underBullets = true;

            compositeMap.put(id, Blocks.liquidRouter.id);
        }};

        hardenedLiquidTank = new LiquidRouter("hardened-liquid-tank"){{
            requirements(Category.liquid, with(
                Items.graphite, 100,
                FRItems.mirrorglass, 60,
                FRItems.hardenedAlloy, 150
            ));
            health = 6000;
            armor = 20.0f;
            size = 4;
            liquidCapacity = 4500.0f;
        }};

        compositeBridgeConduit = new LiquidBridge("composite-bridge-conduit"){{
            requirements(Category.liquid, with(
                Items.metaglass, 6,
                Items.titanium, 8,
                Items.thorium, 4,
                Items.plastanium, 4
            ));
            hasPower = false;
            liquidCapacity = 108.0f;
            range = 8;
            pulse = true;

            compositeMap.put(id, Blocks.bridgeConduit.id);
        }};

        //endregion
        //region power

        conductorPowerNode = new BatteryNode("zjjd"){{
            requirements(Category.power, with(
                Items.lead, 10,
                FRItems.conductor, 5
            ));
            health = 225;
            size = 2;

            maxNodes = 8;
            laserRange = 25;
            consumePowerBuffered(25000);
        }};

        flameGenerator = new ConsumeGenerator("yrfdj"){{
            requirements(Category.power, with(
                Items.lead, 225,
                Items.thorium, 100,
                Items.silicon, 160,
                Items.plastanium, 75,
                FRItems.conductor, 75
            ));
            health = 840;
            size = 3;
            hasLiquids = true;
            itemCapacity = 20;
            liquidCapacity = 30f;
            baseExplosiveness = 5.0f;
            destroyBullet = destroyBullet(960f, 45f);
            generateEffect = new MultiEffect(
                Fx.explosion,
                Fx.fuelburn,
                Fx.generatespark,
                Fx.smeltsmoke
            );
            ambientSound = Sounds.steam;
            ambientSoundVolume = 0.02f;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawWarmupRegion(),
                new DrawLiquidRegion(Liquids.cryofluid)
            );

            itemDuration = 120f;
            powerProduction = 32f;

            consume(new ConsumeItemFlammable(1.15f));
            consumeLiquid(Liquids.cryofluid, n(9));
        }};

        hydroelectricGenerator = new HydroelectricGenerator("hydroelectric-generator"){{
            requirements(Category.power, with(
                Items.metaglass, 80,
                Items.graphite, 120,
                Items.plastanium, 60,
                FRItems.logicAlloy, 60
            ));
            health = 640;
            size = 2;
            drawer = new DrawMulti(
                new DrawRegion("-turbine", 2.4f, true),
                new DrawDefault()
            );
            powerProduction = 6.0f;
            floating = true;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;
        }};

        hydroelectricGeneratorLarge = new HydroelectricGenerator("hydroelectric-generator-large"){{
            requirements(Category.power, with(
                Items.graphite, 300,
                Items.metaglass, 220,
                Items.plastanium, 220,
                FRItems.logicAlloy, 160,
                FRItems.hardenedAlloy, 120
            ));
            health = 2560;
            armor = 4.0f;
            size = 4;
            drawer = new DrawMulti(
                new DrawRegion("-turbine", 2.0f, true),
                new DrawDefault()
            );
            powerProduction = 35.0f;
            floating = true;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;
        }};

        //TODO...
        burstReactor = new ImpactReactor("burst-reactor"){{
            requirements(Category.power, BuildVisibility.hidden, with(
                FRItems.magneticAlloy, 500
            ));
            health = 9600;
            size = 6;
            itemCapacity = 40;
            liquidCapacity = 150f;
            baseExplosiveness = 10.0f;
            ambientSoundVolume = 0.06f;
            ambientSound = Sounds.pulse;

            itemDuration = 240f;
            powerProduction = 2000f;
            warmupSpeed = 0.0008f;
            explosionRadius = 120;
            explosionDamage = 11600;
            explosionShake = 8.0f;
            explosionShakeDuration = 30f;
            explodeEffect = new MultiEffect(
                Fx.impactReactorExplosion
            );

            consumePower(n(5760));
            consumeItem(FRItems.detonationCompound, 8);
            consumeLiquid(FRLiquids.liquidNitrogen, n(50));
        }};

        //endregion
        //region defense

        damWall = new Wall("sbq"){{
            requirements(Category.defense, with(
                Items.metaglass, 3,
                Items.titanium, 4
            ));
            health = 720;
            size = 1;
            requiresWater = true;
        }};

        damWallLarge = new Wall("sbqdx"){{
            requirements(Category.defense, mult(damWall.requirements, 4));
            health = damWall.health * 4;
            size = 2;
            requiresWater = true;
        }};

        hardenedWall = new ArmorWall("hardened-wall"){{
            requirements(Category.defense, with(
                Items.surgeAlloy, 3,
                FRItems.hardenedAlloy, 6
            ));
            health = 1440;
            armor = 12;
            size = 1;
            placeableLiquid = true;
            insulated = true;
            absorbLasers = true;
            armorIncrease = 20;
            maxHealthLossPercentage = 70;
            increasePattern = Interp.pow2Out;
        }};

        hardenedWallLarge = new ArmorWall("hardened-wall-large"){{
            requirements(Category.defense, mult(hardenedWall.requirements, 4));
            health = hardenedWall.health * 4;
            armor = 12;
            size = 2;
            placeableLiquid = true;
            insulated = true;
            absorbLasers = true;
            armorIncrease = 20;
            maxHealthLossPercentage = 70;
            increasePattern = Interp.pow2Out;
        }};

        fleshWall = new RegenWall("xrq"){{
            requirements(Category.defense, with(
                FRItems.flesh, 24,
                FRItems.logicAlloy, 12
            ));
            researchCost = ItemStack.with(
                FRItems.flesh, 200
            );

            health = 8000;
            size = 2;
            enableDrawStatus = false;
            healPercent = 0.0834f;
            optionalMultiplier = 2.0f;
            chanceHeal = 15;
            chanceDeflect = 15;
            regenPercent = 0.5f;
            flashHit = true;
            flashColor.set(Pal.health);
            frames = 20;
            frameTime = 6;

            consumeLiquid(Liquids.water, 0.0076f).boost();
        }};

        //endregion
        //region crafting

        thermalKiln = new AttributeCrafter("rnyl"){{
            requirements(Category.crafting, with(
                Items.copper, 60,
                Items.lead, 45,
                Items.graphite, 30
            ));
            researchCost = with(
                Items.copper, 120,
                Items.lead, 80,
                Items.graphite, 40
            );
            size = 2;
            hasPower = true;
            hasLiquids = false;
            itemCapacity = 20;
            craftEffect = Fx.smeltsmoke;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawFlame(_ffe099)
            );

            craftTime = 60f;
            outputItem = new ItemStack(FRItems.glass, 6);
            attribute = Attribute.heat;
            boostScale = 1.0f / 3.0f;

            consumePower(n(30));
            consumeItems(with(
                Items.sand, 6,
                Items.coal, 1
            ));
        }};

        metaglassPlater = new GenericCrafter("dgj"){{
            requirements(Category.crafting, with(
                Items.lead, 75,
                Items.titanium, 55,
                Items.silicon, 40
            ));
            researchCost = ItemStack.with(
                Items.lead, 200,
                Items.titanium, 135,
                Items.silicon, 60
            );
            size = 2;
            hasPower = true;
            hasLiquids = false;
            itemCapacity = 20;
            craftEffect = Fx.smeltsmoke;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawFade()
            );

            craftTime = 10.0f;
            outputItem = new ItemStack(Items.metaglass, 2);

            consumePower(n(120));
            consumeItems(with(
                Items.lead, 1,
                FRItems.glass, 2
            ));
        }};

        mirrorglassPolisher = new GenericCrafter("dmj"){{
            requirements(Category.crafting, with(
                Items.graphite, 45,
                Items.titanium, 60,
                Items.silicon, 75
            ));
            researchCost = ItemStack.with(
                Items.graphite, 160,
                Items.titanium, 200,
                Items.silicon, 180
            );
            size = 2;
            hasPower = true;
            hasLiquids = false;
            craftEffect = Fx.smeltsmoke;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawFade()
            );

            craftTime = 90f;
            outputItem = new ItemStack(FRItems.mirrorglass, 1);

            consumePower(n(120));
            consumeItem(Items.metaglass, 2);
        }};

        sulflameExtractor = new GenericCrafter("hhhjtqq"){{
            requirements(Category.crafting, with(
                Items.lead, 85,
                Items.graphite, 55,
                Items.titanium, 40
            ));
            size = 2;
            hasPower = true;
            hasLiquids = true;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.slag),
                new DrawDefault(),
                new DrawFlame(_ffcf99){{
                    flameRadius = 2.0f;
                }}
            );

            craftTime = 60f;
            outputItem = new ItemStack(FRItems.sulflameAlloy, 2);

            consumePower(n(90));
            consumeItems(with(
                Items.coal, 3,
                Items.sporePod, 2
            ));
            consumeLiquid(Liquids.slag, n(24));
        }};

        kindlingExtractor = new GenericCrafter("hhhjcqc"){{
            requirements(Category.crafting, with(
                Items.graphite, 90,
                Items.titanium, 80,
                Items.silicon, 60
            ));
            size = 2;
            hasPower = true;
            hasLiquids = false;
            craftEffect = Fx.smeltsmoke;
            updateEffect = Fx.explosion;

            craftTime = 60f;
            outputItem = new ItemStack(FRItems.kindlingAlloy, 1);

            consumePower(n(120));
            consumeItems(with(
                Items.coal, 1,
                FRItems.sulflameAlloy, 1
            ));
        }};

        conductorFormer = new GenericCrafter("dtgcy"){{
            requirements(Category.crafting, with(
                Items.lead, 75,
                Items.surgeAlloy, 20,
                FRItems.mirrorglass, 25
            ));
            size = 2;
            hasPower = true;
            hasLiquids = false;
            craftEffect = new MultiEffect(
                Fx.lightning,
                Fx.smeltsmoke
            );
            drawer = new DrawMulti(
                new DrawArcSmelt(),
                new DrawDefault(),
                new DrawFade()
            );

            craftTime = 120;
            outputItem = new ItemStack(FRItems.conductor, 2);

            consumePower(n(200));
            consumeItems(with(
                Items.copper, 2,
                Items.silicon, 3
            ));
        }};

        logicAlloyProcessor = new GenericCrafter("logic-alloy-processor"){{
            requirements(Category.crafting, with(
                Items.titanium, 105,
                Items.silicon, 60,
                Items.plastanium, 55
            ));
            size = 2;
            hasPower = true;
            hasLiquids = false;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawFade()
            );

            craftTime = 120f;
            outputItem = new ItemStack(FRItems.logicAlloy, 2);

            consumePower(n(120));
            consumeItems(with(
                Items.copper, 3,
                Items.titanium, 2,
                Items.silicon, 3
            ));
        }};

        detonationMixer = new GenericCrafter("detonation-mixer"){{
            requirements(Category.crafting, with(
                Items.thorium, 45,
                Items.plastanium, 30,
                FRItems.logicAlloy, 55
            ));
            size = 2;
            hasPower = true;
            hasLiquids = false;
            itemCapacity = 20;
            craftEffect = Fx.smeltsmoke;
            updateEffect = Fx.explosion;

            craftTime = 300f;
            outputItem = new ItemStack(FRItems.detonationCompound, 6);

            consumePower(n(90));
            consumeItems(with(
                Items.blastCompound, 4,
                Items.pyratite, 4,
                FRItems.logicAlloy, 3
            ));
        }};

        slagCooler = new GenericCrafter("gslqq"){{
            requirements(Category.crafting, with(
                Items.copper, 90,
                Items.graphite, 55,
                Items.titanium, 70
            ));
            size = 2;
            hasPower = true;
            hasLiquids = true;
            liquidCapacity = 30f;
            craftEffect = Fx.blastsmoke;
            updateEffect = Fx.smeltsmoke;
            updateEffectChance = 0.08f;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.slag),
                new DrawDefault(),
                new DrawFade()
            );

            craftTime = 60.0f;
            outputItem = new ItemStack(FRItems.flamefluidCrystal, 2);

            consumePower(n(90));
            consumeLiquids(LiquidStack.with(
                Liquids.slag, n(24),
                Liquids.cryofluid, n(3)
            ));
        }};

        crusher = new GenericCrafter("tqjsj"){{
            requirements(Category.crafting, with(
                Items.copper, 45,
                Items.lead, 75,
                Items.graphite, 30
            ));
            size = 2;
            hasPower = true;
            hasLiquids = false;
            updateEffect = Fx.pulverizeMedium;
            craftEffect = Fx.blastsmoke;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawRegion("-spinner", 10, true),
                new DrawDefault(),
                new DrawFade()
            );

            craftTime = 30f;
            outputItem = new ItemStack(Items.scrap, 2);

            consumePower(n(30));
            consumeItems(with(
                Items.copper, 1,
                Items.lead, 1
            ));
        }};

        timberBurner = new GeneratorCrafter("timber-burner", 3.5f){{
            requirements(Category.crafting, with(
                Items.copper, 50,
                Items.lead, 25,
                Items.metaglass, 15,
                Items.graphite, 20
            ));
            size = 2;
            updateEffect = Fx.generatespark;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawWarmupRegion()
            );

            craftTime = 30.0f;
            outputItem = new ItemStack(Items.coal, 1);

            consumeItem(FRItems.timber);
        }};

        electrothermalSiliconFurnace = new GenericCrafter("drgl"){{
            requirements(Category.crafting, with(
                Items.copper, 275,
                Items.graphite, 200,
                Items.titanium, 160,
                Items.surgeAlloy, 85
            ));
            size = 3;
            hasPower = true;
            hasLiquids = false;
            itemCapacity = 30;
            craftEffect = Fx.smeltsmoke;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawFlame(_ffef99)
            );

            craftTime = 10.0f;
            outputItem = new ItemStack(Items.silicon, 2);

            consumePower(n(900));
            consumeItem(Items.sand, 3);
        }};

        fleshSynthesizer = new AttributeCrafter("flesh-synthesizer"){{
            requirements(Category.crafting, with(
                Items.lead, 120,
                Items.graphite, 60,
                Items.silicon, 75,
                Items.plastanium, 50
            ));
            size = 3;
            hasPower = true;
            hasLiquids = false;
            itemCapacity = 30;
            liquidCapacity = 20f;
            craftEffect = Fx.blastsmoke;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.neoplasm),
                new DrawWeave(),
                new DrawDefault()
            );

            attribute = FRAttribute.flesh;
            craftTime = 45.0f;
            outputItem = new ItemStack(FRItems.flesh, 1);

            consumePower(n(100));
            consumeItems(with(
                Items.plastanium, 2,
                Items.phaseFabric, 1,
                Items.sporePod, 1
            ));
            consumeLiquid(Liquids.neoplasm, n(12));
        }};

        liquidNitrogenCompressor = new GenericCrafter("ydysj"){{
            requirements(Category.crafting, with(
                Items.lead, 220,
                Items.metaglass, 175,
                Items.silicon, 185,
                Items.plastanium, 130
            ));
            size = 3;
            hasPower = true;
            hasLiquids = true;
            itemCapacity = 30;
            liquidCapacity = 75.0f;
            baseExplosiveness = 5.0f;
            lightLiquid = FRLiquids.liquidNitrogen;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.cryofluid),
                new DrawLiquidTile(FRLiquids.liquidNitrogen){{drawLiquidLight = true;}},
                new DrawDefault()
            );

            craftTime = 120.0f;
            outputLiquid = new LiquidStack(FRLiquids.liquidNitrogen, n(50));

            consumePower(n(400));
            consumeItems(with(
                Items.blastCompound, 4,
                FRItems.kindlingAlloy, 2
            ));
            consumeLiquid(Liquids.cryofluid, n(60));
        }};

        hardenedAlloySmelter = new GenericCrafter("hardened-alloy-smelter"){{
            requirements(Category.crafting, with(
                Items.graphite, 135,
                Items.thorium, 50,
                Items.silicon, 90,
                Items.plastanium, 80
            ));
            armor = 4.0f;
            size = 3;
            hasPower = true;
            itemCapacity = 20;
            craftEffect = Fx.smeltsmoke;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawFlame(_ffcf99)
            );

            craftTime = 60f;
            outputItem = new ItemStack(FRItems.hardenedAlloy, 3);

            consumePower(n(600));
            consumeItems(with(
                Items.thorium, 3,
                Items.plastanium, 6,
                FRItems.kindlingAlloy, 2
            ));
        }};

        magneticAlloyFormer = new GenericCrafter("magnetic-alloy-former"){{
            requirements(Category.crafting, with(
                Items.plastanium, 135,
                FRItems.logicAlloy, 180,
                FRItems.hardenedAlloy, 75
            ));
            armor = 8.0f;
            size = 3;
            hasPower = true;
            itemCapacity = 20;
            baseExplosiveness = 5.0f;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawArcSmelt(),
                new DrawDefault()
            );

            craftTime = 90.0f;
            outputItem = new ItemStack(FRItems.magneticAlloy, 2);

            consumePower(n(1440));
            consumeItems(with(
                Items.surgeAlloy, 1,
                FRItems.conductor, 3,
                FRItems.hardenedAlloy, 2
            ));
        }};

        cryofluidMixerLarge = new GenericCrafter("cryofluid-mixer-large"){{
            requirements(Category.crafting, with(
                Items.lead, 240,
                FRItems.mirrorglass, 75,
                FRItems.logicAlloy, 200,
                FRItems.hardenedAlloy, 75
            ));
            scaledHealth = 90.0f;
            armor = 8.0f;
            size = 4;
            hasPower = true;
            hasLiquids = true;
            itemCapacity = 20;
            liquidCapacity = 150.0f;
            lightLiquid = Liquids.cryofluid;
            updateEffectChance = 0.03f;
            updateEffect = FRFx.squareEffect(30.0f, 6, 2.4f, 0.0f, Liquids.cryofluid.color.cpy().mul(1.05f));
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.water),
                new DrawLiquidTile(Liquids.cryofluid){{drawLiquidLight = true;}},
                new DrawDefault()
            );

            craftTime = 120.0f;
            outputLiquid = new LiquidStack(Liquids.cryofluid, n(90));

            consumePower(n(240));
            consumeItem(Items.titanium, 4);
            consumeLiquid(Liquids.water, n(90));
        }};

        magnetismConcentratedRollingMill = new GenericCrafter("magnetism-concentrated-rolling-mill"){{
            requirements(Category.crafting, with(
                Items.lead, 450,
                Items.graphite, 220,
                FRItems.mirrorglass, 180,
                FRItems.logicAlloy, 320,
                FRItems.hardenedAlloy, 225
            ));
            scaledHealth = 90.0f;
            armor = 16.0f;
            size = 5;
            hasPower = true;
            hasLiquids = true;
            itemCapacity = 30;
            liquidCapacity = 50.0f;
            craftEffect = Fx.formsmoke;
            updateEffect = Fx.plasticburn;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawPlasma(), //sprites are scaled from the vanilla ones by 1.25x  lol
                new DrawDefault(),
                new DrawFade()
            );

            craftTime = 40.0f;
            outputItem = new ItemStack(Items.plastanium, 6);
            consumePower(n(840));
            consumeItem(Items.titanium, 8);
            consumeLiquid(Liquids.oil, n(90));
        }};

        magneticRingSynthesizer = new GenericCrafter("magnetic-ring-synthesizer"){{
            requirements(Category.crafting, with(
                Items.lead, 450,
                Items.phaseFabric, 125,
                FRItems.logicAlloy, 500,
                FRItems.hardenedAlloy, 225,
                FRItems.magneticAlloy, 125
            ));
            scaledHealth = 90.0f;
            armor = 16.0f;
            size = 5;
            hasPower = true;
            itemCapacity = 75;
            ambientSound = Sounds.techloop;
            ambientSoundVolume = 0.015f;
            updateEffectChance = 0.03f;
            updateEffect = new Effect(33.0f, e -> {
                Draw.color(Items.phaseFabric.color, Pal.accent, e.fin() * 0.8f);
                Lines.stroke(3.0f * e.fout());
                Lines.spikes(e.x, e.y, 10.0f * e.finpow(), 2.0f * e.fout() + 4.0f * e.fslope(), 4, 45.0f);
            });
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawWeavePlus(8, 0.8f),
                new DrawDefault()
            );

            craftTime = 60.0f;
            outputItem = new ItemStack(Items.phaseFabric, 6);
            consumePower(n(3360));
            consumeItems(with(
                Items.sand, 33,
                Items.thorium, 18
            ));
        }};

        electromagnetismDiffuser = new SurgeCrafter("electromagnetism-diffuser"){{
            requirements(Category.crafting, with(
                Items.surgeAlloy, 325,
                FRItems.logicAlloy, 500,
                FRItems.hardenedAlloy, 375,
                FRItems.magneticAlloy, 250
            ));
            scaledHealth = 90.0f;
            armor = 16.0f;
            size = 5;
            hasPower = true;
            hasLiquids = true;
            itemCapacity = 75;
            liquidCapacity = 75.0f;
            craftSound = Sounds.spark;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawArcSmelt(){{circleSpace=3.0f;flameColor=_e3ae6f;}},
                new DrawDefault()
            );
            craftEffect = new Effect(45.0f, e -> {
                for(byte i = 0; i < 3; i++){
                    Fx.rand.setSeed(e.id * i * 2L);
                    Draw.color(Pal.surge, e.fout());
                    Lines.stroke(4.0f * e.fout());
                    Lines.spikes(e.x + Fx.rand.range(size * 2.0f), e.y + Fx.rand.range(size * 2.0f), 7.0f * e.finpow(), 1.8f * e.fout() + 4.0f * e.fslope(), 4, 45.0f);
                }
            });

            craftTime = 240.0f;
            outputItems = ItemStack.with(
                Items.surgeAlloy, 32,
                FRItems.hardenedAlloy, 2
            );

            fragBullets = 6;
            fragBullet = new BasicBulletType(4.0f, 250.0f){

                /** I am disgusted. */
                @Override
                public void updateTrail(Bullet b){
                    if(!headless){
                        final byte length = (byte)(trailLength * Mathf.pow(speed / b.vel.len(), 0.6f));
                        if(b.trail == null) b.trail = new Trail(length);
                        b.trail.length = length;
                        b.trail.update(b.x, b.y, trailInterp.apply(b.fin()));
                    }
                }{
                    lifetime = craftTime - 0.1f;
                    width = 8.0f;
                    height = 8.0f;
                    homingRange = 120.0f;
                    homingDelay = 10.0f;
                    homingPower = 0.35f;
                    pierceArmor = true;

                    backColor = Pal.surge;
                    frontColor = Color.white;
                    trailLength = 16;
                    trailWidth = 3.0f;
                    trailColor = Pal.surge;
                    hitEffect = despawnEffect = new Effect(20.0f, e -> {
                        Draw.color(Pal.surge);
                        Lines.stroke(5.0f * e.fout());
                        Lines.spikes(e.x, e.y, 10.0f * e.finpow(), 3.0f * e.fout() + 4.0f * e.fslope(), 4, 45.0f);
                    });
                }
            };

            signs = i -> new boolean[]{true, false, Mathf.randomBoolean(), i % 2 == 0};
            bullets = (b, sign) -> {
                if(Units.closestTarget(b.team, b.x, b.y, b.type.homingRange, e -> e != null && !b.hasCollided(e.id)) != null)
                    return; //checks whether there's any target first
                b.rotation(b.rotation() + 720.0f*sign * Time.delta / b.lifetime);
            };

            consumePower(n(7200));
            consumeItems(with(
                FRItems.flamefluidCrystal, 32,
                FRItems.magneticAlloy, 2
            ));
            consumeLiquid(Liquids.cryofluid, n(30));
        }};

        hardenedAlloyCrucible = new EnergyCrafter("hardened-alloy-crucible"){{
            requirements(Category.crafting, with(
                Items.metaglass, 550,
                Items.titanium, 425,
                Items.silicon, 275,
                FRItems.hardenedAlloy, 500
            ));
            scaledHealth = 10.0f;
            armor = 20.0f;
            size = 6;
            hasPower = true;
            hasLiquids = true;
            itemCapacity = 60;
            liquidCapacity = 120.0f;
            craftEffect = new MultiEffect(
                FRFx.crossEffect(45.0f, 4.0f, 0.0f, true, null),
                Fx.mineImpact,
                FRFx.drillSteamFast,
                FRFx.mineImpactWaveZ
            );
            updateEffect = new Effect(45.0f, e -> {
                Draw.color(Pal.reactorPurple, 0.7f);
                Lines.stroke(e.fout() * 2.0f);
                Lines.circle(e.x, e.y, 4.0f + e.finpow() * 60f);
            });
            updateEffectChance = 0.01f;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawFlame(_ffcf99){{flameRadius = 2.0f;}},
                new DrawArrows(2, _feb380, _6e7080)
            );

            craftTime = 120.0f;
            outputItem = new ItemStack(FRItems.hardenedAlloy, 20);

            explosionRadius = 240.0f;
            explosionDamage = 2880.0f;
            explosionShake = 6.0f;
            explosionShakeDuration = 30.0f;
            explodeSound = Sounds.explosionbig;
            explodeEffect = FRFx.reactorExplosionLarge;

            maxInstability = 360.0f;
            stabilizeInterval = 900.0f;

            lightningDamage = 80.0f;
            lightningAmount = 8;

            fragBullets = 3;
            fragBulletsRand = true;
            fragRound = 12;
            fragRoundRand = true;
            fragDelay = 10;
            fragDelayRand = true;
            fragBullet = new BasicBulletType(30.0f, 80.0f, "fire-frag-front"){{
                fragVelocityMin = 0.8f;
                fragVelocityMax = 1.2f;

                backSprite = "fire-frag-back";
                lifetime = 24f;
                width = 20f;
                height = 20f;
                drag = 0.12f;
                hitSize = 5.0f;
                frontColor = Color.white;
                backColor = Pal.reactorPurple;

                trailLength = 4;
                trailWidth = 4;
                trailColor = Pal.reactorPurple;

                status = FRStatusEffects.disintegrated;
                statusDuration = 180f;

                hitSound = Sounds.shotgun;
                hitEffect = despawnEffect = new MultiEffect(

                    new ParticleEffect(){{
                        lifetime = 20f;
                        particles = 4;
                        length = 8.0f;
                        baseLength = 4.0f;
                        line = true;
                        strokeFrom = 2.0f;
                        strokeTo = 0.0f;
                        lenFrom = 8.0f;
                        lenTo = 0.0f;
                        colorFrom = Pal.reactorPurple;
                        colorTo = Color.white;
                    }},

                    new WaveEffect(){{
                        lifetime = 20f;
                        sizeFrom = 48f;
                        sizeTo = 0.0f;
                        strokeFrom = 1.6f;
                        strokeTo = 0.0f;
                        interp = Interp.pow3In;
                        colorFrom = Pal.reactorPurple;
                        colorTo = Color.white;
                    }}
                );

                fragBullets = 4;
                fragBullet = new LightningBulletType(){{
                    damage = 40.0f;
                    lightningLength = 10;
                    lightningLengthRand = 1;
                    lightningColor = Pal.reactorPurple;
                }};
            }};

            craftSound = Sounds.release;
            baseColor.set(_67474b);
            circleColor = new Color[]{Pal.reactorPurple, Pal.thoriumPink, Pal.lightishOrange, Pal.surge, Pal.plastanium};

            consumePower(n(18000));
            consumeItems(with(
                Items.thorium, 12,
                Items.plastanium, 20
            ));
            consumeLiquid(Liquids.water, n(120));
        }};

        //endregion
        //region units

        fleshReconstructor = new UnitFactory("flesh-reconstructor"){

            @Override
            public boolean canPlaceOn(Tile tile, Team team, int rotation){
                return state.rules.infiniteResources || tile.getLinkedTilesAs(this, tempTiles).sumf(o -> o.floor().attributes.get(FRAttribute.flesh)) >= 1.0f;
            }
            {
                requirements(Category.units, with(
                    FRItems.flesh, 100
                ));
                size = 3;
                hasLiquids = true;
                liquidCapacity = 30f;

                plans.add(
                    new UnitFactory.UnitPlan(FRUnitTypes.blade, 2700.0f, with(
                        FRItems.flesh, 55,
                        FRItems.logicAlloy, 65
                    ))
                );

                consumePower(n(270));
                consumeLiquid(Liquids.neoplasm, n(48));
            }
        };

        //endregion
        //region effect

        buildingHealer = new RegenProjector("buildingHealer"){{
            requirements(Category.effect, with(
                Items.titanium, 30,
                Items.silicon, 25,
                FRItems.logicAlloy, 10
            ));
            health = 320;
            size = 2;
            hasLiquids = false;
            baseColor = Pal.heal;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawPulseShape(false){{
                    color = Pal.heal;
                }},
                new DrawShape(){{
                    layer = Layer.effect;
                    color = Pal.heal;
                    radius = 2.4f;
                    timeScl = 2.0f;
                    useWarmupRadius = true;
                }}
            );
            range = 32;
            healPercent = 0.04f;
            optionalMultiplier = 1.5f;
            optionalUseTime = 200f;

            consumePower(n(100));
            consumeItem(Items.silicon).boost();
        }};

        campfire = new Campfire.CampfireBlock("gh"){{
            requirements(Category.effect, with(
                Items.copper, 300,
                Items.metaglass, 220,
                Items.plastanium, 175,
                FRItems.timber, 200
            ));
            size = 5;
            itemCapacity = 20;
            separateItemCapacity = true;
            updateEffectChance = 0.03f;
            updateEffect = new MultiEffect(
                Fx.blastsmoke,
                Fx.generatespark
            );

            reload = 60.0f;
            range = 20 * tilesize;
            useTime = 240.0f;
            speedBoost = 1.5f;
            speedBoostPhase = 0.25f;
            phaseRangeBoost = 32.0f;
            allyStatus = FRStatusEffects.inspired;
            enemyStatus = StatusEffects.sapped;

            consume(new ConsumePowerCustom(n(2560), 0.0f, false, this));
            consume(new Campfire.ConsumeCampfire(this));
        }};

        skyDome = new ForceProjector("sky-dome"){{
            requirements(Category.effect, with(
                Items.lead, 300,
                Items.phaseFabric, 120,
                FRItems.logicAlloy, 225,
                FRItems.hardenedAlloy, 180
            ));
            armor = 4.0f;
            size = 5;
            liquidCapacity = 20.0f;

            shieldHealth = 3000.0f;
            radius = 201.7f;
            phaseRadiusBoost = 80.0f;
            phaseShieldBoost = 1000.0f;
            cooldownNormal = 5.0f;
            cooldownLiquid = 1.2f;
            cooldownBrokenBase = 3.0f;
            coolantConsumption = 0.2f;

            consumePower(n(1200));
            itemConsumer = consumeItem(Items.phaseFabric).boost();
        }};

        buildIndicator = new BuildTurret("jzzsq"){{
            requirements(Category.effect, with(
                Items.lead, 120,
                Items.thorium, 50,
                FRItems.logicAlloy, 30
            ));
            health = 720;
            size = 2;

            range = 285.0f;
            buildSpeed = 0.75f;

            consumePower(n(120));
        }};

        coreBulwark = new ForceCoreBlock("zjhx"){{
            requirements(Category.effect, with(
                Items.copper, 9000,
                Items.lead, 8000,
                Items.metaglass, 2500,
                Items.thorium, 3500,
                Items.silicon, 6000,
                Items.plastanium, 1750
            ));
            researchCostMultiplier = 0.4f;
            buildCostMultiplier = 0.4f;
            health = 12000;
            armor = 8;
            size = 5;
            itemCapacity = 11000;
            enableDrawStatus = false;

            unitType = FRUnitTypes.omicron;
            unitCapModifier = 18;
            radius = 96.0f;
            shieldHealth = 800.0f;
            cooldownNormal = 1.2f;
            cooldownBroken = 1.5f;

            consume(new ConsumePowerCustom(n(600), 0.0f, false, this));
        }};

        javelinPad = new MechPad("javelin-pad", FRUnitTypes.javelin){{
            requirements(Category.effect, with(
                Items.lead, 350,
                Items.titanium, 500,
                Items.silicon, 450,
                Items.plastanium, 400,
                Items.phaseFabric, 200
            ));
            health = 1200;
            size = 2;

            powerCons = 240.0f;
        }};

        compositeUnloader = new AdaptDirectionalUnloader("composite-unloader"){{
            requirements(Category.effect, with(
                Items.metaglass, 15,
                Items.titanium, 30,
                Items.thorium, 15,
                Items.silicon, 20
            ));
            health = 85;
            underBullets = true;

            allowCoreUnload = true;
            speed = 25.0f;

            compositeMap.put(id, Blocks.unloader.id);
        }};

        primaryInterplanetaryAccelerator = new AcceleratorCutscene(
            "primary-interplanetary-accelerator",
            "fire.planetarySceneTexts",
            "fire.planetarySceneSource",
            new int[]{90, 210, 810, 2010, 2130},
            new short[]{0, 41, 70, 185, 79}
        ){{
            requirements(Category.effect, BuildVisibility.campaignOnly, with(
                Items.copper, 13000,
                Items.lead, 13000,
                Items.silicon, 6500,
                Items.thorium, 6500,
                Items.titanium, 3250
            ));
            researchCostMultiplier = 0.0f;
            size = 5;
            hasPower = true;

            chargeDuration = 160.0f;
            buildDuration = 300.0f;
            chargeRings = 2;
            ringRadBase = 40.0f;
            ringRadSpacing = 15.0f;
            ringHandleLen = 20.0f;
            launchLightning = 12;

            //commented: see AcceleratorCutscene part, byd Anuke
            //launchCandidates = Seq.with(FRPlanets.lysetta);
            launchBlock = Blocks.coreShard;
            powerBufferRequirement = 500000.0f;
            consumePower(10.0f);
        }};

        //endregion
        //region env

        envEteriverStronghold = new EnvBlock("env-eteriver-stronghold"){{
            buildType = () -> new EnvBlockBuild(){

                /** Apollo spawn. */
                @Override
                public void add(){
                    super.add();
                    if(!Groups.weather.contains(w -> w.weather == Weathers.rain)){
                        if(!Groups.weather.isEmpty()) Groups.weather.clear();
                        Weathers.rain.create();
                    }
                }

                /** Apollo death. */
                @Override
                public void onRemoved(){
                    super.onRemoved();
                    Groups.weather.clear();
                }
            };
        }};

        envStormyCoast1 = new EnvBlock("env-stormy-coast1"){{
            buildType = () -> new EnvBlockBuild(){

                /** Default color -> according to {@link mindustry.game.Rules#ambientLight ambientLight}. */
                final Color defaultColor = new Color(0.01f, 0.01f, 0.04f, 0.99f);
                final Color specificColor = new Color(0.1f, 0.1f, 0.1f);
                float alpha;

                /** Wave 41-47 / 60. */
                @Override
                protected void updateStart(){
                    if(alpha == 0.0f){
                        if(!renderer.drawWeather){
                            Core.settings.put("showweather", true);
                            ui.announce(Core.bundle.format("fire.settingEnabled", Core.bundle.get("setting.showweather.name")));
                        }
                        if(!renderer.drawLight){
                            Core.settings.put("drawlight", true);
                            Time.runTask(180.0f, () -> ui.announce(Core.bundle.format("fire.settingEnabled", Core.bundle.get("setting.drawlight.name"))));
                        }
                    }

                    state.rules.lighting = true;
                    state.rules.ambientLight = specificColor;
                    alpha = Mathf.lerpDelta(alpha, 0.4f, 0.003f);
                    state.rules.ambientLight.a = alpha;

                    if(!Groups.weather.contains(w -> w.weather == Weathers.rain)){
                        if(!Groups.weather.isEmpty()) Groups.weather.clear();
                        Weathers.rain.create();
                    }
                }

                /** When wave 47 is half. */
                @Override
                protected void updateStop(){
                    state.rules.ambientLight.a = Mathf.lerpDelta(state.rules.ambientLight.a, 0.0f, 0.003f);

                    if(state.rules.ambientLight.a <= 0.01f){
                        if(state.isCampaign())
                            state.rules.ambientLight = defaultColor;
                        else
                            state.rules.ambientLight = Color.clear; //might be buggy if default light is customized

                        kill();
                    }
                }

                @Override
                public void onRemoved(){
                    if(isStarter()) return;

                    Groups.weather.clear();
                    if(state.isCampaign())
                        state.rules.ambientLight = defaultColor;
                    else
                        state.rules.ambientLight = Color.clear;
                }
            };
        }};

        envStormyCoast2 = new EnvBlock("env-stormy-coast2"){{
            buildType = () -> new EnvBlockBuild(){

                /** Default color -> according to {@link mindustry.game.Rules#ambientLight ambientLight}. */
                final Color defaultColor = new Color(0.01f, 0.01f, 0.04f, 0.99f);
                final Color specificColor = new Color(0.1f, 0.1f, 0.1f);
                float alpha;

                /** Wave 59 / 60. */
                @Override
                protected void updateStart(){
                    if(alpha == 0.0f){
                        if(!renderer.drawWeather){
                            Core.settings.put("showweather", true);
                            ui.announce(Core.bundle.format("fire.settingEnabled", Core.bundle.get("setting.showweather.name")));
                        }
                        if(!renderer.drawLight){
                            Core.settings.put("drawlight", true);
                            Time.runTask(180.0f, () -> ui.announce(Core.bundle.format("fire.settingEnabled", Core.bundle.get("setting.drawlight.name"))));
                        }
                    }

                    state.rules.lighting = true;
                    state.rules.ambientLight = specificColor;
                    alpha = Mathf.lerpDelta(alpha, 0.8f, 0.004f);
                    state.rules.ambientLight.a = alpha;

                    if(!Groups.weather.contains(w -> w.weather == FRWeathers.rainstorm)){
                        if(!Groups.weather.isEmpty()) Groups.weather.clear();
                        FRWeathers.rainstorm.create();
                    }
                }

                /** After all the apollos are destructed. */
                @Override
                protected void updateStop(){
                    state.rules.ambientLight.a = Mathf.lerpDelta(state.rules.ambientLight.a, 0.0f, 0.004f);

                    if(state.rules.ambientLight.a <= 0.01f){
                        if(state.isCampaign())
                            state.rules.ambientLight = defaultColor;
                        else
                            state.rules.ambientLight = Color.clear;

                        kill();
                    }
                }

                @Override
                public void onRemoved(){
                    if(isStarter()) return;

                    Groups.weather.clear();
                    if(state.isCampaign())
                        state.rules.ambientLight = defaultColor;
                    else
                        state.rules.ambientLight = Color.clear; //might be buggy if default light is customized
                }
            };
        }};

        envGlaciatedPeaks = new EnvBlock("env-glaciated-peaks"){{
            buildType = () -> new EnvBlockBuild(){

                /** Limits invasion times. */
                private byte counter;

                /** Landing. */
                @Override
                public void add(){
                    super.add();
                    if(!state.isCampaign()) return;

                    int n = Mathf.random(4, 6);
                    for(var s : state.getPlanet().sectors)
                        if(counter++ < n && s.hasBase()){
                            Events.fire(new EventType.SectorInvasionEvent(s)); //false invasions, visually only
                        }

                    kill();
                }
            };
        }};

        //endregion
        //region DEBUG

        DEBUG_TURRET = new DEBUG.DEBUG_Turret("DEBUG_TURRET");
        DEBUG_MEND = new DEBUG.DEBUG_Mend("DEBUG_MEND");

        //endregion

    }

    /** Divide numbers by 60. */
    private static float n(int amountPerSec){
        return amountPerSec / 60.0f;
    }
}
