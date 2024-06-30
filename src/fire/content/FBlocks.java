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
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import fire.entities.FUnitSorts;
import fire.entities.bullets.LightningPointBulletType;
import fire.ui.dialogs.InfoDialog;
import fire.world.blocks.defense.ArmorWall;
import fire.world.blocks.defense.RegenWall;
import fire.world.blocks.defense.UnitOverdriveProjector;
import fire.world.blocks.defense.turrets.ItemBulletStackTurret;
import fire.world.blocks.defense.turrets.JackpotTurret;
import fire.world.blocks.environment.EnvBlock;
import fire.world.blocks.power.BatteryNode;
import fire.world.blocks.production.AdaptBurstDrill;
import fire.world.blocks.production.EnergyCrafter;
import fire.world.blocks.production.GeneratorCrafter;
import fire.world.blocks.production.SurgeCrafter;
import fire.world.blocks.sandbox.AdaptiveSource;
import fire.world.blocks.storage.AdaptDirectionalUnloader;
import fire.world.blocks.storage.ForceCoreBlock;
import fire.world.blocks.units.MechPad;
import fire.world.meta.FAttribute;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
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
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.ui.Styles;
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
import mindustry.world.blocks.liquid.LiquidBridge;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.blocks.power.LightBlock;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeItemFlammable;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;

import static mindustry.Vars.state;
import static mindustry.type.ItemStack.mult;
import static mindustry.type.ItemStack.with;

public class FBlocks{

    /** Key is component itself, value is its inferior. */
    public static final ObjectMap<UnlockableContent, Block> compositeMap = new ObjectMap<>(5);
    public static Block

        //environment
        neoplasm, bloodyDirt,

        //sandbox & misc
        adaptiveSource, fireCompany,

        //turret
        smasher, nightmare, scab, ignite, blossom, gambler, seaquake, distance, grudge, magneticSphere, magneticRail,

        //production
        chopper, treeFarm, vapourCondenser, biomassCultivator, fissionDrill,

        //distribution
        compositeConveyor, hardenedAlloyConveyor, compositeBridgeConveyor,

        //liquid
        compositeLiquidRouter, compositeBridgeConduit,

        //power
        conductorPowerNode, flameGenerator, burstReactor,

        //defense
        damWall, damWallLarge, hardenedWall, hardenedWallLarge, fleshWall,

        //crafting
        thermalKiln, metaglassPlater, mirrorglassPolisher, impurityKindlingExtractor, kindlingExtractor, conductorFormer, logicAlloyProcessor, detonationMixer, slagCooler, crusher, timberBurner,
        electrothermalSiliconFurnace, fleshSynthesizer, liquidNitrogenCompressor, hardenedAlloySmelter, magneticAlloyFormer,
        electromagnetismDiffuser,
        hardenedAlloyCrucible,

        //units
        fleshReconstructor,

        //effect
        buildingHealer, campfire, skyDome, buildIndicator, coreArmored, javelinPad, compositeUnloader,

        //env
        envEteriverStronghold, envStormyCoast, envGlaciatedPeaks;

    private static BulletType destroyBullet(float dmg, float radius){
        return new BulletType(1f, 0f){{
            final float time = 15f;
            lifetime = time;
            splashDamage = dmg;
            splashDamageRadius = radius;
            despawnEffect = hitEffect = new WaveEffect(){{
                lifetime = time;
                sizeFrom = 0f;
                sizeTo = radius;
                strokeFrom = 10.0f;
                strokeTo = 0f;
                colorFrom = colorTo = Color.white;
            }};
        }};
    }

    public static void load(){

        //region environment

        neoplasm = new Floor("pooled-neoplasm", 0){{
            drownTime = 230.0f;
            speedMultiplier = 0.5f;
            statusDuration = 240.0f;
            status = FStatusEffects.overgrown;
            isLiquid = true;
            liquidDrop = Liquids.neoplasm;
        }};

        bloodyDirt = new Floor("bloody-dirt", 8){{
            attributes.set(FAttribute.flesh, 1.0f / 9.0f);
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

        fireCompany = new LightBlock("hzgs"){

            @Override
            public void displayExtra(Table table){
                table.button(new TextureRegionDrawable(uiIcon), Styles.emptyi, 40.0f, InfoDialog.dialog::show);
            }
        {
            requirements(Category.effect, BuildVisibility.hidden, with());
            alwaysUnlocked = true;
            hasShadow = false;
            targetable = false;
            size = 2;
        }};

        //endregion
        //region turret

        smasher = new ItemTurret("js"){{
            requirements(Category.turret, with(
                Items.copper, 80,
                Items.lead, 45,
                Items.titanium, 25
            ));
            researchCost = with(
                Items.copper, 150,
                Items.lead, 120,
                Items.titanium, 50
            );
            health = 800;
            size = 2;
            reload = 60f / 1.4f;
            range = 250f;
            shootCone = 30f;
            inaccuracy = 2.0f;
            rotateSpeed = 10.0f;
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

                FItems.impurityKindlingAlloy, new ArtilleryBulletType(6f, 105f){{
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

                FItems.detonationCompound, new ArtilleryBulletType(4f, 65f){{
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
                FItems.mirrorglass, 25
            ));
            researchCost = with(
                Items.titanium, 400,
                Items.thorium, 250,
                Items.silicon, 275,
                FItems.mirrorglass, 165
            );
            health = 1080;
            size = 2;
            reload = 21.6f;
            range = 220.0f;
            shootCone = 30.0f;
            inaccuracy = 2.0f;
            rotateSpeed = 10.0f;
            shootSound = Sounds.shootBig;
            coolantMultiplier = 1.2f;

            consumeCoolant(0.2f);

            ammo(

                Items.copper, new LaserBulletType(50f){{
                    length = 230f;
                    colors = new Color[]{Color.valueOf("ff9900").a(0.4f), Color.valueOf("ff9900"), Color.white};
                    reloadMultiplier = 0.8f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.lead, new LaserBulletType(45f){{
                    length = 230f;
                    colors = new Color[]{Color.valueOf("ccccff").a(0.4f), Color.valueOf("ccccff"), Color.white};
                    reloadMultiplier = 1.1f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.metaglass, new LaserBulletType(55f){{
                    length = 275f;
                    width = 12.5f;
                    hitSize = 3.5f;
                    colors = new Color[]{Color.valueOf("ccccff").a(0.4f), Color.valueOf("ccccff"), Color.white};
                    rangeChange = 45f;
                    ammoMultiplier = 3;
                    reloadMultiplier = 0.8f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.graphite, new LaserBulletType(40f){{
                    length = 230f;
                    colors = new Color[]{Color.valueOf("33ccff").a(0.4f), Color.valueOf("33ccff"), Color.white};
                    status = StatusEffects.freezing;
                    statusDuration = 150f;
                    ammoMultiplier = 3;
                    reloadMultiplier = 1.6f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.sand, new LaserBulletType(15f){{
                    length = 380f;
                    width = 10.0f;
                    hitSize = 3.0f;
                    colors = new Color[]{Color.valueOf("ccccff").a(0.4f), Color.valueOf("ccccff"), Color.white};
                    rangeChange = 150f;
                    ammoMultiplier = 1;
                    reloadMultiplier = 0.5f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.titanium, new LaserBulletType(60f){{
                    length = 230f;
                    colors = new Color[]{Color.valueOf("ccccff").a(0.4f), Color.valueOf("ccccff"), Color.white};
                    status = StatusEffects.corroded;
                    statusDuration = 150f;
                    reloadMultiplier = 1.2f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.thorium, new LaserBulletType(75f){{
                    length = 230f;
                    width = 20f;
                    hitSize = 5.0f;
                    colors = new Color[]{Pal.thoriumPink.cpy().a(0.4f), Pal.thoriumPink, Color.white};
                    pierceArmor = true;
                    status = StatusEffects.melting;
                    statusDuration = 150f;
                    reloadMultiplier = 0.6f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.scrap, new LaserBulletType(25f){{
                    length = 400f;
                    width = 10.0f;
                    hitSize = 3.0f;
                    colors = new Color[]{Color.valueOf("ccccff").a(0.4f), Color.valueOf("ccccff"), Color.white};
                    rangeChange = 170f;
                    ammoMultiplier = 1;
                    reloadMultiplier = 1.0f / 3.0f;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.silicon, new LaserBulletType(65f){{
                    length = 230f;
                    width = 16f;
                    hitSize = 4.5f;
                    colors = new Color[]{Color.valueOf("404040").a(0.4f), Color.valueOf("404040"), Color.white};
                    ammoMultiplier = 3;
                    buildingDamageMultiplier = 0.5f;
                }},

                Items.surgeAlloy, new LaserBulletType(90f){{
                    length = 265.0f;
                    width = 16.0f;
                    hitSize = 4.5f;
                    colors = new Color[]{Pal.surge.cpy().a(0.4f), Pal.surge, Color.white};
                    rangeChange = 35.0f;
                    pierceArmor = true;
                    status = StatusEffects.shocked;
                    lightningSpacing = 27.0f;
                    lightningLength = 1;
                    lightningDelay = 1.2f;
                    lightningLengthRand = 10;
                    lightningDamage = 20.0f;
                    lightningAngleRand = 30.0f;
                    ammoMultiplier = 4;
                    reloadMultiplier = 0.85f;
                    buildingDamageMultiplier = 0.5f;
                    hitSound = Sounds.spark;
                }},

                FItems.glass, new LaserBulletType(25f){{
                    length = 315f;
                    width = 10.0f;
                    hitSize = 3.0f;
                    colors = new Color[]{Color.valueOf("ccccff").a(0.4f), Color.valueOf("ccccff"), Color.white};
                    rangeChange = 85f;
                    ammoMultiplier = 3;
                    reloadMultiplier = 0.8f;
                    buildingDamageMultiplier = 0.5f;
                }},

                FItems.mirrorglass, new LaserBulletType(75f){{
                    length = 390f;
                    width = 10.0f;
                    hitSize = 3.0f;
                    colors = new Color[]{Color.valueOf("ccccff").a(0.4f), Color.valueOf("ccccff"), Color.white};
                    rangeChange = 160f;
                    ammoMultiplier = 4;
                    reloadMultiplier = 0.6f;
                    buildingDamageMultiplier = 0.5f;
                }}
            );
        }};

        scab = new ItemTurret("scab"){{
            requirements(Category.turret, with(
                FItems.flesh, 50
            ));
            health = 1600;
            size = 2;
            reload = 45.0f;
            range = 260.0f;
            shootCone = 15.0f;
            inaccuracy = 1.0f;
            rotateSpeed = 4.0f;

            ammo(
                FItems.flesh, new BulletType(2.0f, 75.0f){

                    @Override
                    public void draw(Bullet b){
                        super.draw(b);
                        final var item = FItems.flesh;

                        Draw.alpha(1.0f);
                        Draw.blend(Blending.additive);
                        Draw.rect(Core.atlas.find("fire-flesh" + (b.time / item.frameTime % item.frames) + 1), b.x, b.y, 10.0f, 10.0f);
                        Draw.blend();
                    }
                    {
                    lifetime = 131.0f;
                    status = FStatusEffects.overgrown;
                    statusDuration = 360.0f;
                }}
            );
        }};

        ignite = new ContinuousTurret("dr"){{
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
                final var heatP = DrawPart.PartProgress.warmup.blend(p -> Mathf.absin(2.0f, 1.0f) * p.warmup, 0.2f);

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
                        moves.add(new PartMove(PartProgress.warmup, 1.6f, 2.0f, -15f));
                    }},

                    new RegionPart("-barrel"){{
                        progress = PartProgress.warmup;
                        heatProgress = heatP;
                        mirror = true;
                        moves.add(new PartMove(PartProgress.warmup, 0f, -1.6f, 0f));
                    }}
                );
            }};

            consumePower(400f / 60f);
            consumeLiquid(Liquids.slag, 20f / 60f);

            shootType = new PointLaserBulletType(){{
                damage = 160f;
                drawSize = 800f;
                status = StatusEffects.melting;
                statusDuration = 150f;
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
            coolantMultiplier = 0.975f;
            shootSound = Sounds.missile;
            shoot = new ShootAlternate(2.4f){{
                shots = 2;
            }};

            consumePower(8f);
            consumeCoolant(0.3f);

            shootType = new FlakBulletType(4f, 40f){{
                final var col = StatusEffects.blasted.color;

                sprite = "missile-large";
                lifetime = 62f;
                width = 12f;
                height = 12f;
                drag = -0.003f;
                homingRange = 80f;
                explodeRange = 40f;
                splashDamageRadius = 36f;
                splashDamage = 65f;
                weaveScale = 10.0f;
                weaveMag = 2.0f;
                trailLength = 24;
                trailWidth = 3;
                lightRadius = 100f;
                lightOpacity = 1.2f;
                ammoMultiplier = 1;
                buildingDamageMultiplier = 0.25f;
                collidesGround = true;
                makeFire = true;
                statusDuration = 150f;
                status = StatusEffects.burning;
                trailColor = lightColor = backColor = col;
                frontColor = Color.white;
                hitSound = Sounds.explosion;
                hitEffect = new ExplosionEffect(){{
                    lifetime = 27f;
                    waveStroke = 4.0f;
                    waveLife = 8.0f;
                    waveRadBase = 8.0f;
                    waveRad = 24f;
                    sparks = 4;
                    sparkRad = 27f;
                    sparkStroke = 1.5f;
                    sparkLen = 3.0f;
                    smokes = 4;
                    waveColor = sparkColor = col;
                    smokeColor = Color.white;
                }};
                fragBullets = 6;
                fragBullet = new BasicBulletType(4f, 25f){{
                    lifetime = 16f;
                    width = 3.0f;
                    height = 5.0f;
                    splashDamageRadius = 32f;
                    splashDamage = 45f;
                    homingPower = 0.15f;
                    homingRange = 80f;
                    homingDelay = 8.0f;
                    trailLength = 3;
                    trailWidth = 3;
                    buildingDamageMultiplier = 0.25f;
                    collidesGround = true;
                    status = StatusEffects.blasted;
                    backColor = col;
                    frontColor = Color.white;
                    trailColor = col;
                }};
            }};
        }};

        gambler = new JackpotTurret("gambler"){{
            final var chargeTime = 120f;
            final var colors = new Color[]{Pal.lightOrange, Pal.thoriumPink, Pal.surge, Pal.sapBulletBack};
            final var chargeFx = FFx.jackpotChargeEffect(chargeTime, 0.17f, 40f, 4, colors);

            requirements(Category.turret, with(
                Items.thorium, 450,
                Items.plastanium, 145,
                Items.surgeAlloy, 85,
                FItems.logicAlloy, 60
            ));
            health = 1920;
            size = 3;
            hasLiquids = false;
            canOverdrive = false;
            reload = 120f;
            range = 320f;
            shootCone = 30f;
            shootY = 0f;
            recoil = 13f;
            rotateSpeed = 4.0f;
            ammoPerShot = 5;
            maxAmmo = 20;
            shootSound = Sounds.shotgun;
            shoot.firstShotDelay = chargeTime;

            jackpotAmmo.add(

                new JackpotAmmo(Items.copper, 0.45f,
                    new ShootAlternate(4f){{
                        shots = barrels = 3;
                        firstShotDelay = chargeTime;
                    }},
                    new BasicBulletType(18f, 180f){{
                    lifetime = 30f;
                    width = 6.0f;
                    height = 12f;
                    drag = 0.04f;
                    status = FStatusEffects.disintegrated;
                    statusDuration = 240f;

                    chargeEffect = chargeFx;
                    shootEffect = FFx.gamblerShootEffect(60f, 4);
                    frontColor = Color.white;
                    backColor = hitColor = colors[0];
                }}),

                new JackpotAmmo(Items.thorium, 0.3f,
                    new ShootMulti(
                        new ShootAlternate(0f){{
                            shots = 1;
                            barrels = 7;
                            firstShotDelay = chargeTime;
                        }},
                        new ShootSpread(7, 2.0f)
                    ),
                    new BasicBulletType(10.5f, 145f){{
                        lifetime = 30f;
                        width = 8.0f;
                        height = 10.0f;
                        status = FStatusEffects.disintegrated;
                        statusDuration = 360f;

                        chargeEffect = chargeFx;
                        shootEffect = FFx.gamblerShootEffect(60f, 2);
                        hitEffect = despawnEffect = FFx.hitBulletSmall(colors[1]);
                        frontColor = Color.white;
                        backColor = hitColor = colors[1];
                    }}
                ),

                new JackpotAmmo(Items.surgeAlloy, 0.15f,
                    new ShootMulti(
                        new ShootAlternate(0f){{
                            shots = 2;
                            barrels = 7;
                            shotDelay = 6.0f;
                            firstShotDelay = chargeTime;
                        }},
                        new ShootSpread(7, 3.0f)
                    ),
                    new BasicBulletType(10.5f, 100f){{
                        lifetime = 30f;
                        width = 8.0f;
                        height = 10.0f;
                        status = FStatusEffects.disintegrated;
                        statusDuration = 480f;
                        lightning = 2;
                        lightningDamage = 5.0f;
                        lightningLength = 4;
                        lightningLengthRand = 1;

                        chargeEffect = chargeFx;
                        shootEffect = FFx.gamblerShootEffect(25f, 1);
                        hitEffect = despawnEffect = FFx.hitBulletSmall(colors[2]);
                        frontColor = Color.white;
                        backColor = hitColor = colors[2];
                    }}
                ),

                new JackpotAmmo(FItems.hardenedAlloy, 0f,
                    new ShootMulti(
                        new ShootAlternate(0f){{
                            shots = 7;
                            barrels = 7;
                            shotDelay = 4.0f;
                            firstShotDelay = chargeTime;
                        }},
                        new ShootSpread(7, 3.0f)
                    ),
                    new BasicBulletType(10.5f, 75f){{
                        lifetime = 30f;
                        width = 8.0f;
                        height = 10.0f;
                        status = FStatusEffects.disintegrated;
                        statusDuration = 600f;
                        pierce = true;
                        pierceBuilding = true;
                        pierceCap = 4;

                        chargeEffect = chargeFx;
                        shootEffect = FFx.gamblerShootEffect(15f, 1);
                        hitEffect = despawnEffect = FFx.hitBulletSmall(colors[3]);
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
            range = 312f;
            shootCone = 60f;
            inaccuracy = 2.7f;
            recoil = 2.0f;
            velocityRnd = 0.1f;
            shootEffect = Fx.shootLiquid;
            shoot.shots = 3;

            consumePower(200f / 60f);

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

                FLiquids.liquidNitrogen, new LiquidBulletType(FLiquids.liquidNitrogen){{
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
                FItems.hardenedAlloy, 225
            ));
            health = 2160;
            armor = 5;
            size = 3;
            liquidCapacity = 45f;
            reload = 600f;
            range = 500f;
            shootCone = 15f;
            rotateSpeed = 1.0f;
            recoil = 2.0f;
            shake = 4.0f;
            coolantMultiplier = 3.0f;
            ammoPerShot = 4;
            shootSound = Sounds.missileLaunch;

            consumeCoolant(0.5f);

            ammo(

                FItems.logicAlloy, new MissileBulletType(3.0f, 300.0f){{
                    lifetime = 167.0f;
                    width = 25.0f;
                    height = 28.0f;
                    splashDamageRadius = 30.0f;
                    splashDamage = 75.0f;
                    homingPower = 0.16f;
                    despawnShake = 3.0f;
                    ammoMultiplier = 1.0f;
                    buildingDamageMultiplier = 0.2f;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmokeMissile;
                    hitEffect = Fx.massiveExplosion;
                    hitSound = Sounds.mediumCannon;
                }},

                FItems.detonationCompound, new MissileBulletType(4.0f, 210.0f){{
                    lifetime = 193.0f;
                    width = 25.0f;
                    height = 28.0f;
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
                    hitEffect = Fx.massiveExplosion;
                    hitSound = Sounds.mediumCannon;

                    trailColor = Pal.lightishOrange;
                    trailInterval = 20.0f;
                    trailEffect = new Effect(30.0f, e -> {
                        Draw.color(e.color);
                        Fill.circle(e.x, e.y, 1.8f * e.rotation * e.fout() + 1.4f);
                    });

                    fragBullets = 3;
                    fragBullet = new MissileBulletType(2.0f, 105.0f){{
                        lifetime = 144.0f;
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

                        trailColor = Pal.lightishOrange;
                        trailInterval = 25.0f;
                        trailEffect = new Effect(30.0f, e -> {
                            Draw.color(e.color);
                            Fill.circle(e.x, e.y, 1.2f * e.rotation * e.fout() + 0.8f);
                        });
                    }};
                }}
            );
        }};

        grudge = new ItemTurret("grudge"){{
            requirements(Category.turret, with(
                Items.copper, 1350,
                Items.graphite, 475,
                Items.phaseFabric, 175,
                Items.surgeAlloy, 400,
                FItems.hardenedAlloy, 400
            ));
            health = 4320;
            size = 4;
            armor = 10;
            liquidCapacity = 75f;
            canOverdrive = false;
            reload = 14f;
            range = 360f;
            shootCone = 24f;
            inaccuracy = 2.0f;
            maxAmmo = 60;
            recoil = 4.0f;
            recoilTime = 10.0f;
            shake = 2.2f;
            rotateSpeed = 6.5f;
            unitSort = FUnitSorts.a;
            shootSound = Sounds.shootBig;
            ammoUseEffect = Fx.casing3;
            coolantMultiplier = 0.75f;
            shoot = new ShootAlternate(6.3f){{
                shots = 2;
                barrels = 3;
            }};

            consumeCoolant(1.2f);

            ammo(

                Items.thorium, new BasicBulletType(8f, 90f){{
                    lifetime = 46f;
                    knockback = 0.8f;
                    width = 20f;
                    height = 27f;
                    hitSize = 6.0f;
                    pierceCap = 4;
                    pierceBuilding = true;
                    pierceArmor = true;
                    ammoMultiplier = 4;
                    status = FStatusEffects.disintegrated;
                    statusDuration = 120f;
                    shootEffect = Fx.shootBig;
                    fragBullets = 4;
                    fragBullet = new BasicBulletType(4f, 25f){{
                        lifetime = 8.0f;
                        width = 8.0f;
                        height = 11f;
                        drag = 0.02f;
                        status = StatusEffects.corroded;
                        statusDuration = 240f;
                    }};
                }},

                FItems.detonationCompound, new BasicBulletType(8f, 60f){{
                    lifetime = 46f;
                    knockback = 0.7f;
                    width = 18f;
                    height = 25f;
                    hitSize = 6.0f;
                    pierceCap = 2;
                    pierceBuilding = true;
                    makeFire = true;
                    ammoMultiplier = 8;
                    reloadMultiplier = 0.6f;
                    buildingDamageMultiplier = 0.1f;
                    status = FStatusEffects.disintegrated;
                    statusDuration = 120f;
                    shootEffect = Fx.shootBig;
                    frontColor = Pal.lightishOrange;
                    backColor = Pal.lightOrange;
                    fragBullets = 2;
                    fragBullet = new BulletType(4f, 0f){{
                        lifetime = 1.0f;
                        splashDamageRadius = 62f;
                        splashDamage = 90f;
                        hittable = reflectable = absorbable = false;
                        status = StatusEffects.melting;
                        statusDuration = 480f;
                        hitSoundVolume = 0.5f;
                        hitSound = Sounds.mediumCannon;
                        hitEffect = Fx.pulverize;
                        despawnEffect = new WaveEffect(){{
                            lifetime = 16f;
                            sizeFrom = 0f;
                            sizeTo = 56f;
                            strokeFrom = 3.0f;
                            strokeTo = 0f;
                            interp = Interp.pow2Out;
                            colorFrom = Pal.lightishOrange;
                            colorTo = Color.white;
                        }};
                    }};
                }},

                FItems.hardenedAlloy, new BasicBulletType(9f, 210f){{
                    lifetime = 72f;
                    knockback = 2.0f;
                    width = 28f;
                    height = 32f;
                    drag = 0.01f;
                    hitSize = 7.0f;
                    rangeChange = 120f;
                    pierceCap = 4;
                    pierceBuilding = true;
                    reloadMultiplier = 0.2f;
                    buildingDamageMultiplier = 0.1f;
                    status = FStatusEffects.disintegrated;
                    statusDuration = 120f;
                    shootEffect = Fx.shootBig;
                    frontColor = Pal.sapBullet;
                    backColor = Pal.sapBulletBack;
                    fragBullets = 2;
                    fragBullet = new BulletType(48f, 0f){{
                        lifetime = 1.0f;
                        splashDamageRadius = 52f;
                        splashDamage = 85f;
                        hittable = reflectable = absorbable = false;
                        hitSoundVolume = 0.5f;
                        hitSound = Sounds.mediumCannon;
                        hitEffect = Fx.pulverizeMedium;
                        despawnEffect = new WaveEffect(){{
                            lifetime = 15f;
                            sizeFrom = 0f;
                            sizeTo = 54f;
                            strokeFrom = 3.0f;
                            strokeTo = 0f;
                            interp = Interp.pow3Out;
                            colorFrom = Pal.sapBullet;
                            colorTo = Color.white;
                        }};
                    }};
                }}
            );
        }};

        magneticSphere = new PowerTurret("magnetic-sphere"){{
            final float chargeTime = 90f;
            final float accelTime = 120f;
            final float radius = 220f;
            final var col = Color.valueOf("92f3fd");

            requirements(Category.turret, with(
                Items.plastanium, 425,
                Items.surgeAlloy, 250,
                FItems.logicAlloy, 400,
                FItems.magneticAlloy, 125
            ));
            health = 6000;
            size = 4;
            liquidCapacity = 75f;
            canOverdrive = false;
            reload = 270f;
            range = 600f;
            shootCone = 6.0f;
            recoil = 5.0f;
            rotateSpeed = 2.7f;
            coolantMultiplier = 0.8f;
            targetGround = false;
            moveWhileCharging = false;
            unitSort = UnitSorts.strongest;
            shootSound = Sounds.laser;
            shoot.firstShotDelay = chargeTime;

            consumePower(32f);
            consumeCoolant(1f);

            shootType = new BulletType(15.5f, 1800f){

                /** Prevent endless creating instances, since its terminal state is always the same. */
                private BulletType type;

                @Override
                public void update(Bullet b){
                    super.update(b);
                    if(b.time <= accelTime) return;

                    if(Units.closestTarget(
                        b.team, b.x, b.y, radius,
                        e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                        t -> t != null && collidesGround && !b.hasCollided(t.id)) == null
                    ){
                        b.hit = true;    //if there's no target nearby, remove and don't create frag bullets
                        hitEffect.at(b); //but keep effect
                        b.remove();

                    }else{
                        if(type == null){
                            type = b.type.copy();
                            type.speed = b.vel.setLength(8f).len();
                            type.drag = 0f;
                        }
                        b.type = type;
                    }
                }

                @Override
                public void draw(Bullet b){
                    super.draw(b);

                    // special thanks to Extra Utilities mod
                    // this is really lazy
                    for(byte i = 0; i < 4; i++){
                        Draw.color(col);
                        Draw.alpha((0.3f + Mathf.absin(Time.time, 2.0f + i * 2.0f, 0.3f + i * 0.05f)));
                        Draw.blend(Blending.additive);
                        Draw.rect(Core.atlas.find("impact-reactor-plasma-" + i), b.x, b.y, 28f, 28f, Time.time * (12f + i * 6.0f));
                        Draw.blend();
                    }
                    Draw.reset();
                }
                {
                lifetime = 600f;
                hitSize = 6.0f;
                pierceCap = 10;
                drag = 0.024f;
                ammoMultiplier = 1;
                collidesGround = false;

                homingRange = radius + 40f;
                homingPower = 0.2f;
                homingDelay = accelTime;

                trailChance = 0.4f;
                trailRotation = true;
                trailColor = col;
                trailEffect = new Effect(25f, e -> {
                    Draw.color(Color.white, e.color, e.fin());
                    Lines.stroke(1.4f + e.fout() * 3.4f);
                    Fx.rand.setSeed(e.id);

                    for(byte i = 0; i < 3; i++){
                        final float rot = e.rotation + Fx.rand.range(15f) + 180f;
                        Fx.v.trns(rot, Fx.rand.random(e.fin() * 27f));
                        Lines.lineAngle(e.x + Fx.v.x, e.y + Fx.v.y, rot, e.fout() * Fx.rand.random(3f, 8.0f) + 2.0f);
                    }
                });

                chargeEffect = new Effect(chargeTime, 20f, e -> {
                    Draw.color(col);
                    Lines.stroke(e.fin() * 2.0f);
                    Lines.circle(e.x, e.y, 4.0f + e.fout() * 20f);
                    Fill.circle(e.x, e.y, e.fin() * 16f);
                    Angles.randLenVectors(e.id, 16, 32f * e.fout(), (x, y) -> {
                        Fill.circle(e.x + x, e.y + y, e.fin() * 4.0f);
                        Drawf.light(e.x + x, e.y + y, e.fin() * 12f, col, 0.7f);
                    });
                    Draw.color();
                    Fill.circle(e.x, e.y, e.fin() * 8);
                    Drawf.light(e.x, e.y, e.fin() * 16f, col, 0.7f);
                }).rotWithParent(true);

                shootEffect = new Effect(18f, e -> {
                    Draw.color(col, Color.lightGray, e.fin());
                    Angles.randLenVectors(e.id, 18, 7.0f + e.finpow() * 19f, (x, y) ->
                        Fill.square(e.x + x, e.y + y, e.fout() * 2.8f + 0.7f, 0f));
                });

                hitEffect = new Effect(14f, e -> {
                    Draw.color(col, Color.lightGray, e.fin());
                    Angles.randLenVectors(e.id, 12, 9.0f + e.finpow() * 22f, (x, y) ->
                        Fill.square(e.x + x, e.y + y, e.fout() * 3.2f + 1.5f, 45f));
                });

                // create lightning while flying
                bulletInterval = 2.0f;
                intervalBullets = 1;
                intervalBullet = new LightningBulletType(){{
                    damage = 5;
                    lightningColor = col;
                    lightningLength = 10;
                    collidesGround = false;

                    fragBullets = 1;
                    fragBullet = new LightningPointBulletType(165f){{
                        homingRange = 120f;
                        lightningChance = 0.6f;
                        lightningColor = col;
                        collidesGround = false;
                    }};
                }};

                fragRandomSpread = 0f;
                fragSpread = 60f;
                fragBullets = 6;
                fragBullet = new BasicBulletType(12f, 225f){{
                    lifetime = 45f;
                    width = 3.2f;
                    height = 4.0f;
                    collidesGround = false;
                    homingRange = 150f;
                    homingPower = 0.4f;
                    homingDelay = 5.0f;

                    backColor = col;
                    frontColor = Color.white;
                    trailLength = 8;
                    trailWidth = 4;
                    trailColor = col;
                }};
            }};
        }};

        magneticRail = new ItemBulletStackTurret("magnetic-rail"){{
            final float chargeTime = 150.0f;
            final float baseRange = 1200.0f, extraRange = 400.0f;
            final var col = Color.valueOf("ec7458");
            final var coll = Color.valueOf("ec7458bb");

            final var item_1 = FItems.hardenedAlloy;
            final var item_2 = FItems.magneticAlloy;

            final BulletType bullet_1_1;
            final BulletType bullet_2_1;
            final BulletType bullet_2_2;
            final BulletType bullet_2_3;

            bullet_1_1 = new BasicBulletType(60f, 15000f){{
                lifetime = 35f;
                width = height = 0f;
                hitSize = 12;
                rangeChange = extraRange;
                splashDamageRadius = 180f;
                splashDamage = 11500f;
                ammoMultiplier = 1;
                reflectable = false;
                absorbable = false;
                hittable = false;

                backColor = col;
                frontColor = Color.white;
                trailColor = col;
                chargeEffect = new MultiEffect(
                    FFx.railChargeEffect(chargeTime, col, 3.0f, baseRange + extraRange, 80f)
                );
                parts.addAll(
                    new HaloPart(){{
                        tri = true;
                        layer = Layer.effect;
                        shapes = 2;
                        haloRotation = 0f;
                        haloRotateSpeed = 9.0f;
                        radius = 40f;
                        triLength = 60f;
                        color = col;
                    }},
                    new HaloPart(){{
                        tri = true;
                        layer = Layer.effect;
                        shapes = 2;
                        haloRotation = 90f;
                        haloRotateSpeed = 9.0f;
                        radius = 40f;
                        triLength = 60f;
                        color = col;
                    }}
                );

                fragBullets = 3;
                fragBullet = new BasicBulletType(40f, 4320f){{
                    lifetime = 60f;
                    width = 16f;
                    height = 20f;
                    trailLength = 11;
                    trailWidth = 2;
                    splashDamageRadius = 180f;
                    splashDamage = 4560f;
                    collidesGround = true;
                    reflectable = false;
                    absorbable = false;
                    hittable = false;
                    backColor = col;
                    frontColor = Color.white;
                    trailColor = col;
                }};
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

                backColor = col;
                frontColor = Color.white;
                trailColor = col;
                chargeEffect = new MultiEffect(
                    FFx.railChargeEffect(chargeTime, col, 3.0f, baseRange, 80f)
                );
                despawnEffect = new MultiEffect(

                    //wave
                    new WaveEffect(){{
                        lifetime = 30f;
                        interp = Interp.circleOut;
                        sizeFrom = 120f;
                        sizeTo = 4.0f;
                        strokeFrom = 4.0f;
                        strokeTo = 0f;
                        colorFrom = col;
                        colorTo = coll;
                    }},

                    //circles
                    new ParticleEffect(){{
                        lifetime = 24f;
                        particles = 6;
                        baseLength = 24f;
                        sizeFrom = 12f;
                        sizeTo = 0f;
                        colorFrom = col;
                        colorTo = coll;
                    }},

                    //lines
                    new ParticleEffect(){{
                        lifetime = 24f;
                        particles = 15;
                        length = 24f;
                        baseLength = 16f;
                        line = true;
                        strokeFrom = 4.0f;
                        strokeTo = 0f;
                        lenFrom = 20f;
                        lenTo = 0f;
                        colorFrom = col;
                        colorTo = coll;
                    }}
                );

                // create lightning upon despawn
                lightningColor = col;
                lightning = 4;
                lightningLength = 6;
                lightningLengthRand = 2;
                lightningDamage = 10.0f;

                // create lightning while flying
                bulletInterval = 1.5f;
                intervalRandomSpread = 0f;
                intervalSpread = 359f;
                intervalBullets = 1;
                intervalBullet = new LightningBulletType(){{
                    damage = 5.0f;
                    lightningColor = col;
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
                    sizeFrom = 0f;
                    sizeTo = 10.0f;
                    colorFrom = coll;
                    colorTo = col;
                }},

                new ParticleEffect(){{
                    lifetime = 67f;
                    particles = 3;
                    sizeInterp = Interp.pow3In;
                    length = 0f;
                    sizeFrom = 0f;
                    sizeTo = 40f;
                    colorFrom = coll;
                    colorTo = col;
                }},

                new ParticleEffect(){{
                    lifetime = 20f;
                    particles = 3;
                    length = 0f;
                    sizeFrom = 40f;
                    sizeTo = 15f;
                    colorFrom = col;
                    colorTo = coll;
                }}.startDelay(45)
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

                backColor = col;
                frontColor = Color.white;
                trailColor = col;
                despawnEffect = new MultiEffect(

                    //wave
                    new WaveEffect(){{
                        lifetime = 75f;
                        interp = Interp.circleOut;
                        sizeFrom = 192f;
                        sizeTo = 8.0f;
                        strokeFrom = 8.0f;
                        strokeTo = 0f;
                        colorFrom = colorTo = col;
                    }},

                    //circles
                    new ParticleEffect(){{
                        lifetime = 60f;
                        particles = 12;
                        baseLength = 72f;
                        sizeFrom = 32f;
                        sizeTo = 0f;
                        colorFrom = colorTo = col;
                    }},

                    //lines
                    new ParticleEffect(){{
                        lifetime = 60f;
                        particles = 30;
                        length = 72f;
                        baseLength = 48f;
                        line = true;
                        strokeFrom = 8.0f;
                        strokeTo = 0f;
                        lenFrom = 60f;
                        lenTo = 0f;
                        colorFrom = colorTo = col;
                    }}
                );

                // create lightning upon despawn
                lightningColor = col;
                lightning = 16;
                lightningLength = 24;
                lightningLengthRand = 4;
                lightningDamage = 40f;

                // create lightning while flying
                bulletInterval = 0.5f;
                intervalRandomSpread = 0f;
                intervalSpread = 359f;
                intervalBullets = 2;
                intervalBullet = new LightningBulletType(){{
                    damage = 5.0f;
                    lightningColor = col;
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

                    backColor = col;
                    frontColor = Color.white;
                    trailColor = col;
                    despawnEffect = new MultiEffect(

                        //wave
                        new WaveEffect(){{
                            lifetime = 36f;
                            interp = Interp.circleOut;
                            sizeFrom = 108f;
                            sizeTo = 4.0f;
                            strokeFrom = 4.0f;
                            strokeTo = 0f;
                            colorFrom = colorTo = col;
                        }},

                        //circles
                        new ParticleEffect(){{
                            lifetime = 36f;
                            particles = 6;
                            baseLength = 40f;
                            sizeFrom = 20f;
                            sizeTo = 0f;
                            colorFrom = colorTo = col;
                        }},

                        //lines
                        new ParticleEffect(){{
                            lifetime = 30f;
                            particles = 12;
                            length = 40f;
                            baseLength = 24f;
                            line = true;
                            strokeFrom = 4.0f;
                            strokeTo = 0f;
                            lenFrom = 40f;
                            lenTo = 0f;
                            colorFrom = colorTo = col;
                        }}
                    );

                    // create lightning upon despawn
                    lightningColor = col;
                    lightning = 8;
                    lightningLength = 16;
                    lightningLengthRand = 2;
                    lightningDamage = 15f;

                    // create lightning while flying
                    bulletInterval = 0.5f;
                    intervalRandomSpread = 0f;
                    intervalSpread = 359f;
                    intervalBullets = 1;
                    intervalBullet = new LightningBulletType(){{
                        damage = 5.0f;
                        lightningColor = col;
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
                        backColor = col;
                        frontColor = Color.white;
                        trailColor = col;
                    }};
                }};
            }};

            requirements(Category.turret, with(
                Items.thorium, 3200,
                Items.plastanium, 4000,
                FItems.conductor, 14400,
                FItems.hardenedAlloy, 3600
            ));
            scaledHealth = 1000;
            size = 12;
            liquidCapacity = 1800f;
            canOverdrive = false;
            reload = 420f;
            range = baseRange;
            shootCone = 0.5f;
            recoil = 10.0f;
            rotateSpeed = 1.2f;
            ammoPerShot = 10;
            coolantMultiplier = 0.075f;
            unitSort = UnitSorts.strongest;
            moveWhileCharging = false;
            shootSound = Sounds.laser;
            shoot.firstShotDelay = chargeTime;

            stack(
                item_2, Seq.with(new BulletStack(30f, bullet_2_2), new BulletStack(60f, bullet_2_3))
            );

            consumePower(5000f);
            consumeCoolant(8f);

            drawer = new DrawTurret(){{
                parts.addAll(
                    new ShapePart(){{
                        progress = DrawPart.PartProgress.smoothReload;
                        layer = Layer.effect;
                        x = y = 0f;
                        radius = 57f;
                        radiusTo = 120f;
                        stroke = 8.0f;
                        strokeTo = 0f;
                        circle = true;
                        hollow = true;
                        color = col;
                    }},
                    new ShapePart(){{
                        progress = DrawPart.PartProgress.smoothReload;
                        layer = Layer.effect;
                        x = y = 0f;
                        radius = 58f;
                        radiusTo = 32f;
                        stroke = 4.0f;
                        strokeTo = 0f;
                        circle = true;
                        hollow = true;
                        color = col;
                    }},
                    new ShapePart(){{
                        progress = DrawPart.PartProgress.smoothReload;
                        layer = Layer.effect;
                        x = y = 0f;
                        radius = 32f;
                        radiusTo = 0f;
                        stroke = 3.0f;
                        strokeTo = 0f;
                        circle = true;
                        hollow = true;
                        color = col;
                    }},
                    new ShapePart(){{
                        progress = DrawPart.PartProgress.smoothReload;
                        layer = Layer.effect;
                        x = y = 0f;
                        radius = 4.0f;
                        radiusTo = 0f;
                        stroke = 2.0f;
                        strokeTo = 0f;
                        circle = true;
                        hollow = false;
                        color = col;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0f;
                        radius = 4.0f;
                        radiusTo = 0f;
                        stroke = 4.0f;
                        strokeTo = 0f;
                        haloRadius = 8.0f;
                        haloRotation = 0f;
                        haloRotateSpeed = 0.6f;
                        rotateSpeed = 0f;
                        sides = 5;
                        hollow = false;
                        tri = false;
                        color = col;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0f;
                        radius = 8.0f;
                        radiusTo = 0f;
                        stroke = 4.0f;
                        strokeTo = 0f;
                        haloRadius = 24f;
                        haloRotation = 0f;
                        haloRotateSpeed = -1.8f;
                        rotateSpeed = 0f;
                        sides = 5;
                        hollow = false;
                        tri = false;
                        color = col;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0f;
                        radius = 12f;
                        radiusTo = 0f;
                        stroke = 4.0f;
                        strokeTo = 0f;
                        haloRadius = 43f;
                        haloRotation = 0f;
                        haloRotateSpeed = 2.2f;
                        rotateSpeed = 0f;
                        sides = 5;
                        hollow = false;
                        tri = false;
                        color = col;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0f;
                        radius = 12f;
                        radiusTo = 0f;
                        stroke = 4.0f;
                        strokeTo = 0f;
                        haloRadius = 43f;
                        haloRotation = 0f;
                        haloRotateSpeed = 2.2f;
                        rotateSpeed = 0f;
                        sides = 8;
                        hollow = false;
                        tri = false;
                        color = col;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0f;
                        radius = 18f;
                        radiusTo = 0f;
                        stroke = 4.0f;
                        strokeTo = 0f;
                        haloRadius = 63f;
                        haloRotation = 0f;
                        haloRotateSpeed = -1.2f;
                        rotateSpeed = 0f;
                        sides = 3;
                        shapes = 8;
                        hollow = false;
                        tri = false;
                        color = col;
                    }},
                    new HaloPart(){{
                        progress = DrawPart.PartProgress.smoothReload.delay(1f);
                        layer = Layer.effect;
                        x = y = 0f;
                        radius = 10.0f;
                        radiusTo = 0f;
                        stroke = 4.0f;
                        strokeTo = 0f;
                        haloRadius = 63f;
                        haloRotation = 0f;
                        haloRotateSpeed = 2.8f;
                        rotateSpeed = 0f;
                        sides = 3;
                        shapes = 6;
                        hollow = false;
                        tri = false;
                        color = col;
                    }}
                );
            }};

            ammo(
                item_1, bullet_1_1,
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
            attribute = FAttribute.tree;
            drillTime = 120;
            output = FItems.timber;

            consumePower(0.4f);
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
                Draw.color(Color.valueOf("6aa85e"));
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
            attribute = FAttribute.grass;
            maxBoost = 1.0f;
            craftTime = 240f;
            outputItem = new ItemStack(FItems.timber, 4);

            consumePower(2.5f);
            consumeLiquid(Liquids.water, 0.2f);
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

            consumePower(3.5f);
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
                Draw.color(Color.valueOf("9e78dc"));
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
            craftTime = 60f;
            outputItem = new ItemStack(Items.sporePod, 3);

            consumePower(3f);
            consumeLiquid(Liquids.water, 0.5f);
        }};

        fissionDrill = new AdaptBurstDrill("lbzt"){{
            requirements(Category.production, with(
                Items.copper, 160,
                Items.metaglass, 80,
                Items.thorium, 375,
                Items.silicon, 145)
            );
            health = 1120;
            size = 5;
            itemCapacity = 75;
            liquidCapacity = 30f;
            drillEffect = new MultiEffect(
                Fx.mineImpact,
                Fx.drillSteam
            );
            baseExplosiveness = 5.0f;
            destroyBullet = destroyBullet(800f, 48f);
            tier = 8;
            drillTime = 45f;
            shake = 4.0f;
            baseArrowColor = Color.valueOf("989aa4");

            consumeLiquid(Liquids.water, 0.2f);
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
            displayedSpeed = 26f;
            junctionReplacement = Blocks.invertedSorter;

            compositeMap.put(this, Blocks.titaniumConveyor);
        }};

        hardenedAlloyConveyor = new StackConveyor("hardened-alloy-conveyor"){{
            requirements(Category.distribution, with(
                Items.graphite, 1,
                Items.silicon, 1,
                FItems.hardenedAlloy, 1
            ));
            health = 240;
            armor = 5;
            speed = 7.0f / 60f;
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

            compositeMap.put(this, Blocks.itemBridge);
        }};

        //endregion
        //region liquid

        compositeLiquidRouter = new LiquidRouter("composite-liquid-router"){{
            requirements(Category.liquid, with(
                Items.metaglass, 8,
                Items.titanium, 12,
                Items.thorium, 6,
                Items.plastanium, 8
            ));
            health = 85;
            liquidCapacity = 120f;
            solid = false;
            underBullets = true;

            compositeMap.put(this, Blocks.liquidRouter);
        }};

        compositeBridgeConduit = new LiquidBridge("composite-bridge-conduit"){{
            requirements(Category.liquid, with(
                Items.metaglass, 6,
                Items.titanium, 8,
                Items.thorium, 4,
                Items.plastanium, 4
            ));
            hasPower = false;
            liquidCapacity = 16f;
            range = 8;
            pulse = true;

            compositeMap.put(this, Blocks.bridgeConduit);
        }};

        //endregion
        //region power

        conductorPowerNode = new BatteryNode("zjjd"){{
            requirements(Category.power, with(
                Items.lead, 10,
                FItems.conductor, 5
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
                FItems.conductor, 75
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
            consumeLiquid(Liquids.cryofluid, 0.15f);
        }};

        //TODO complete.
        burstReactor = new ImpactReactor("burst-reactor"){{
            requirements(Category.power, BuildVisibility.hidden, with(
                FItems.magneticAlloy, 500
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

            consumePower(96f);
            consumeItem(FItems.detonationCompound, 8);
            consumeLiquid(FLiquids.liquidNitrogen, 50f / 60f);
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
                FItems.hardenedAlloy, 6
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
                FItems.flesh, 24,
                FItems.logicAlloy, 12
            ));
            health = 8000;
            size = 2;
            enableDrawStatus = false;
            healPercent = 5.0f / 60f;
            optionalMultiplier = 2.0f;
            chanceHeal = 0.15f;
            chanceDeflect = 12f;
            regenPercent = 0.5f;
            flashHit = true;
            flashColor = Pal.health;
            frames = 20;
            frameTime = 6.0f;

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
                new DrawFlame(Color.valueOf("ffe099"))
            );

            craftTime = 60f;
            outputItem = new ItemStack(FItems.glass, 6);
            attribute = Attribute.heat;
            boostScale = 1.0f / 3.0f;

            consumePower(0.5f);
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

            consumePower(2f);
            consumeItems(with(
                Items.lead, 1,
                FItems.glass, 2
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
            outputItem = new ItemStack(FItems.mirrorglass, 1);

            consumePower(2f);
            consumeItem(Items.metaglass, 2);
        }};

        impurityKindlingExtractor = new GenericCrafter("hhhjtqq"){{
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
                new DrawFlame(Color.valueOf("ffcf99")){{
                    flameRadius = 2.0f;
                }}
            );

            craftTime = 60f;
            outputItem = new ItemStack(FItems.impurityKindlingAlloy, 2);

            consumePower(1.5f);
            consumeItems(with(
                Items.coal, 3,
                Items.sporePod, 2
            ));
            consumeLiquid(Liquids.slag, 0.4f);
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
            outputItem = new ItemStack(FItems.kindlingAlloy, 1);

            consumePower(2f);
            consumeItems(with(
                Items.coal, 1,
                FItems.impurityKindlingAlloy, 1
            ));
        }};

        conductorFormer = new GenericCrafter("dtgcy"){{
            requirements(Category.crafting, with(
                Items.lead, 75,
                Items.surgeAlloy, 20,
                FItems.mirrorglass, 25
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
            outputItem = new ItemStack(FItems.conductor, 2);

            consumePower(200f / 60f);
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
            outputItem = new ItemStack(FItems.logicAlloy, 2);

            consumePower(2f);
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
                FItems.logicAlloy, 55
            ));
            size = 2;
            hasPower = true;
            hasLiquids = false;
            itemCapacity = 20;
            craftEffect = Fx.smeltsmoke;
            updateEffect = Fx.explosion;

            craftTime = 300f;
            outputItem = new ItemStack(FItems.detonationCompound, 6);

            consumePower(1.5f);
            consumeItems(with(
                Items.blastCompound, 4,
                Items.pyratite, 4,
                FItems.logicAlloy, 3
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
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.slag),
                new DrawDefault(),
                new DrawFade()
            );

            craftTime = 60f;
            outputItem = new ItemStack(FItems.flamefluidCrystal, 2);

            consumePower(1.5f);
            consumeLiquids(LiquidStack.with(
                Liquids.slag, 0.4f,
                Liquids.cryofluid, 0.05f
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

            consumePower(0.5f);
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

            consumeItem(FItems.timber);
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
                new DrawFlame(Color.valueOf("ffef99"))
            );

            craftTime = 10.0f;
            outputItem = new ItemStack(Items.silicon, 2);

            consumePower(15.0f);
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

            attribute = FAttribute.flesh;
            craftTime = 45f;
            outputItem = new ItemStack(FItems.flesh, 1);

            consumePower(100f / 60f);
            consumeItems(with(
                Items.plastanium, 2,
                Items.phaseFabric, 1,
                Items.sporePod, 1
            ));
            consumeLiquid(Liquids.neoplasm, 0.2f);
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
            liquidCapacity = 75f;
            baseExplosiveness = 5.0f;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.cryofluid),
                new DrawLiquidTile(FLiquids.liquidNitrogen),
                new DrawDefault()
            );

            craftTime = 120f;
            outputLiquid = new LiquidStack(FLiquids.liquidNitrogen, 50f / 60f);

            consumePower(400f / 60f);
            consumeItems(with(
                Items.blastCompound, 4,
                FItems.kindlingAlloy, 2
            ));
            consumeLiquid(Liquids.cryofluid, 1.0f);
        }};

        hardenedAlloySmelter = new GenericCrafter("hardened-alloy-smelter"){{
            requirements(Category.crafting, with(
                Items.graphite, 135,
                Items.thorium, 50,
                Items.silicon, 90,
                Items.plastanium, 80
            ));
            size = 3;
            hasPower = true;
            hasLiquids = false;
            itemCapacity = 20;
            craftEffect = Fx.smeltsmoke;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawFlame(Color.valueOf("ffef99"))
            );

            craftTime = 60f;
            outputItem = new ItemStack(FItems.hardenedAlloy, 3);

            consumePower(10f);
            consumeItems(with(
                Items.thorium, 3,
                Items.plastanium, 6,
                FItems.kindlingAlloy, 2
            ));
        }};

        magneticAlloyFormer = new GenericCrafter("magnetic-alloy-former"){{
            requirements(Category.crafting, with(
                Items.plastanium, 135,
                FItems.logicAlloy, 180,
                FItems.hardenedAlloy, 75
            ));
            size = 3;
            hasPower = true;
            hasLiquids = false;
            itemCapacity = 20;
            baseExplosiveness = 5.0f;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawArcSmelt(),
                new DrawDefault()
            );

            craftTime = 90f;
            outputItem = new ItemStack(FItems.magneticAlloy, 2);

            consumePower(24f);
            consumeItems(with(
                Items.surgeAlloy, 1,
                FItems.conductor, 3,
                FItems.hardenedAlloy, 2
            ));
        }};

        electromagnetismDiffuser = new SurgeCrafter("electromagnetism-diffuser"){{
            final float radius = 120.0f;

            requirements(Category.crafting, with(
                Items.surgeAlloy, 325,
                FItems.logicAlloy, 500,
                FItems.hardenedAlloy, 375,
                FItems.magneticAlloy, 250
            ));
            armor = 6;
            size = 5;
            hasPower = true;
            hasLiquids = true;
            itemCapacity = 40;
            liquidCapacity = 75f;
            craftSound = Sounds.spark;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawArcSmelt(){{
                    circleSpace = 3.0f;
                    flameColor = Color.valueOf("e3ae6f");
                }},
                new DrawDefault()
            );

            craftTime = 120f;
            outputItems = ItemStack.with(
                Items.surgeAlloy, 16,
                FItems.hardenedAlloy, 1
            );

            fragBullets = 6;
            fragBullet = new BasicBulletType(4f, 220f){{
                lifetime = 120.0f;
                width = 8.0f;
                height = 8.0f;
                homingRange = radius;
                homingPower = 0.35f;

                backColor = Pal.surge;
                frontColor = Color.white;
                trailLength = 16;
                trailWidth = 3;
                trailColor = Pal.surge;
            }};

            pattern = (b, i, s) -> {

                // check whether there's any target first
                if(Units.closestTarget(b.team, b.x, b.y, radius, e -> e != null && !b.hasCollided(e.id)) != null)
                    return;

                final float
                    spd = 3.0f,
                    scl = b.time / b.lifetime,

                    x = Mathf.cosDeg(s * (b.time * spd + 360.0f / fragBullets * i)) * radius * scl + b.x,
                    y = Mathf.sinDeg(s * (b.time * spd + 360.0f / fragBullets * i)) * radius * scl + b.y;

                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(x, y), 60.0f * Time.delta));
            };

            consumePower(120.0f);
            consumeItems(with(
                FItems.flamefluidCrystal, 16,
                FItems.magneticAlloy, 1
            ));
            consumeLiquid(Liquids.cryofluid, 0.5f);
        }};

        hardenedAlloyCrucible = new EnergyCrafter("hardened-alloy-crucible"){{
            requirements(Category.crafting, with(
                Items.metaglass, 550,
                Items.titanium, 425,
                Items.silicon, 275,
                FItems.hardenedAlloy, 500
            ));
            armor = 7;
            size = 6;
            hasPower = true;
            hasLiquids = true;
            itemCapacity = 60;
            liquidCapacity = 120f;
            craftEffect = Fx.smeltsmoke;
            updateEffect = new Effect(50f, e -> {
                Draw.color(Pal.reactorPurple, 0.7f);
                Lines.stroke(e.fout() * 2.0f);
                Lines.circle(e.x, e.y, 4.0f + e.finpow() * 60f);
            });
            updateEffectChance = 0.01f;
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawFlame(Color.valueOf("ffef99"))
            );

            craftTime = 120f;
            outputItem = new ItemStack(FItems.hardenedAlloy, 20);

            explosionRadius = 240f;
            explosionDamage = 2880f;
            explosionShake = 6.0f;
            explosionShakeDuration = 30f;
            explodeSound = Sounds.explosionbig;
            explodeEffect = Fx.reactorExplosion;

            maxInstability = 360f;
            stabilizeInterval = 900f;

            lightningDamage = 80f;
            lightningAmount = 8;

            fragBullets = 3;
            fragBulletsRand = true;
            fragRound = 12;
            fragRoundRand = true;
            fragDelay = 10;
            fragDelayRand = true;
            fragBulletVelRand = true;
            fragBullet = new BasicBulletType(30f, 80f, "fire-frag-front"){{
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

                status = FStatusEffects.disintegrated;
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
                        strokeTo = 0f;
                        lenFrom = 8.0f;
                        lenTo = 0f;
                        colorFrom = Pal.reactorPurple;
                        colorTo = Color.white;
                    }},

                    new WaveEffect(){{
                        lifetime = 20f;
                        sizeFrom = 48f;
                        sizeTo = 0f;
                        strokeFrom = 1.6f;
                        strokeTo = 0f;
                        interp = Interp.pow3In;
                        colorFrom = Pal.reactorPurple;
                        colorTo = Color.white;
                    }}
                );

                fragBullets = 4;
                fragBullet = new LightningBulletType(){{
                    damage = 40;
                    lightningLength = 10;
                    lightningLengthRand = 1;
                    lightningColor = Pal.reactorPurple;
                }};
            }};

            craftSound = Sounds.release;
            baseColor = Color.valueOf("67474b");
            circleColor = new Color[]{Pal.reactorPurple, Pal.thoriumPink, Pal.lightishOrange, Pal.surge, Pal.plastanium};

            consumePower(300f);
            consumeItems(with(
                Items.thorium, 12,
                Items.plastanium, 20
            ));
            consumeLiquid(Liquids.water, 2.0f);
        }};

        //endregion
        //region units

        fleshReconstructor = new UnitFactory("flesh-reconstructor"){

            @Override
            public boolean canPlaceOn(Tile tile, Team team, int rotation){
                return state.rules.infiniteResources || tile.getLinkedTilesAs(this, tempTiles).sumf(o -> o.floor().attributes.get(FAttribute.flesh)) >= 1.0f;
            }
        {
            requirements(Category.units, with(
                FItems.flesh, 100
            ));
            size = 3;
            hasLiquids = true;
            liquidCapacity = 30f;

            plans.add(
                new UnitFactory.UnitPlan(FUnitTypes.blade, 2700f, with(
                    FItems.flesh, 55,
                    FItems.logicAlloy, 65
                ))
            );

            consumePower(4.5f);
            consumeLiquid(Liquids.neoplasm, 0.8f);
        }};

        //endregion
        //region effect

        buildingHealer = new RegenProjector("buildingHealer"){{
            requirements(Category.effect, with(
                Items.titanium, 30,
                Items.silicon, 25,
                FItems.logicAlloy, 10
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

            consumePower(100f / 60f);
            consumeItem(Items.silicon).boost();
        }};

        campfire = new UnitOverdriveProjector("gh"){{
            requirements(Category.effect, with(
                Items.copper, 300,
                Items.metaglass, 220,
                Items.plastanium, 175,
                FItems.timber, 200
            ));
            size = 5;
            hasPower = false;
            hasLiquids = true;
            hasBoost = false;
            itemCapacity = 30;
            liquidCapacity = 30f;
            lightRadius = 360f;
            updateEffectChance = 0.03f;
            updateEffect = new MultiEffect(
                Fx.blastsmoke,
                Fx.generatespark
            );

            reload = 60f;
            range = 320f;
            useTime = 240f;
            speedBoost = 3.2f;
            allyStatus = FStatusEffects.inspired;
            enemyStatus = StatusEffects.sapped;
            statusDuration = 60f;

            consumeItems(with(
                Items.pyratite, 4,
                FItems.timber, 8,
                FItems.kindlingAlloy, 4,
                FItems.flamefluidCrystal, 4
            ));
            consumeLiquid(Liquids.slag, 0.4f);
        }};

        skyDome = new ForceProjector("sky-dome"){{
            requirements(Category.effect, with(
                Items.lead, 300,
                Items.phaseFabric, 120,
                FItems.logicAlloy, 225,
                FItems.hardenedAlloy, 180
            ));
            armor = 4;
            size = 5;
            liquidCapacity = 20f;

            shieldHealth = 3000f;
            radius = 201.7f;
            phaseRadiusBoost = 80f;
            phaseShieldBoost = 1000f;
            cooldownNormal = 5.0f;
            cooldownLiquid = 1.2f;
            cooldownBrokenBase = 3.0f;
            coolantConsumption = 0.2f;

            consumePower(20f);
            itemConsumer = consumeItem(Items.phaseFabric).boost();
        }};

        buildIndicator = new BuildTurret("jzzsq"){{
            requirements(Category.effect, with(
                Items.lead, 120,
                Items.thorium, 50,
                FItems.logicAlloy, 30
            ));
            health = 720;
            size = 2;

            range = 285f;
            buildSpeed = 0.75f;

            consumePower(2f);
        }};

        coreArmored = new ForceCoreBlock("zjhx"){{
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

            unitType = FUnitTypes.omicron;
            unitCapModifier = 18;
            radius = 96.0f;
            shieldHealth = 800.0f;
            cooldownNormal = 1.2f;
            cooldownBroken = 1.5f;
        }};

        javelinPad = new MechPad("javelin-pad", FUnitTypes.javelin){{
            requirements(Category.effect, with(
                Items.lead, 350,
                Items.titanium, 500,
                Items.silicon, 450,
                Items.plastanium, 400,
                Items.phaseFabric, 200
            ));
            health = 1200;
            size = 2;

            consumesPower = 240.0f;
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

            compositeMap.put(this, Blocks.unloader);
        }};

        //endregion
        //region env

        envEteriverStronghold = new EnvBlock("env-eteriver-stronghold"){{
            buildType = () -> new EnvBlockBuild(){

                /** Apollo spawn; placed() is not used since it's buggy. */
                @Override
                public void update(){
                    if(state.isEditor()) return;

                    for(var w : Groups.weather)
                        if(w.weather != Weathers.rain) w.remove();

                    if(Groups.weather.isEmpty())
                        Weathers.rain.create();

                    kill();
                }

                /** Apollo death. */
                @Override
                public void onRemoved(){
                    super.onRemoved();
                    Groups.weather.clear();
                }
            };
        }};

        envStormyCoast = new EnvBlock("env-stormy-coast"){{
            buildType = () -> new EnvBlockBuild(){

                private float alpha;
                private final Color
                    defaultColor = new Color(0.01f, 0.01f, 0.04f, 0.99f),
                    specificColor = new Color(0.1f, 0.1f, 0.1f);

                /** Wave 59/60. */
                @Override
                protected void updateStart(){
                    state.rules.lighting = true;
                    state.rules.ambientLight = specificColor;
                    alpha = Mathf.lerpDelta(alpha, 0.8f, 0.004f);
                    state.rules.ambientLight.a = alpha;

                    for(var w : Groups.weather)
                        if(w.weather != Weathers.rain) w.remove();

                    if(Groups.weather.isEmpty())
                        Weathers.rain.create();
                }

                /** After all the apollos are destructed. */
                @Override
                protected void updateStop(){
                    state.rules.ambientLight.a = Mathf.lerpDelta(state.rules.ambientLight.a, 0f, 0.004f);

                    if(state.rules.ambientLight.a <= 0.001f){
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

                    if(state.isCampaign())
                        state.rules.ambientLight = defaultColor;
                    else
                        state.rules.ambientLight = Color.clear;
                }
            };
        }};

        envGlaciatedPeaks = new EnvBlock("env-glaciated-peaks"){{
            buildType = () -> new EnvBlockBuild(){

                /** Limits invasion times. */
                private byte times;

                /** Landing; placed() is not used since it won't be called if placed by world processor... */
                @Override
                public void update(){
                    if(state.isEditor() || !state.isCampaign()) return;

                    for(var s : state.getPlanet().sectors){
                        if(times < 5 && s.hasBase() && Mathf.chance(0.6)){
                            Events.fire(new EventType.SectorInvasionEvent(s));
                            times++;
                        }
                    }

                    kill();
                }
            };
        }};

        //endregion

    }
}
