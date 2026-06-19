package fire.content;

import arc.graphics.g2d.Draw;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import fire.entities.bullets.SpriteBulletType;
import fire.logic.FRLogicStatements;
import fire.world.meta.FRAttribute;
import mindustry.ai.UnitCommand;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.Bullet;
import mindustry.gen.LogicIO;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.logic.LAssembler;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.logic.MessageBlock;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.LightBlock;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.Pump;
import mindustry.world.meta.BuildVisibility;

import static mindustry.content.Blocks.*;
import static mindustry.content.UnitTypes.*;

public class FROverride{

    public static void loadDebug(){
        final float tDecel = 60, tFire = 80, tSafe = tFire + 20, tLife = 140;

        var weapon = corvus.weapons.get(0);
        weapon.reload = 120;
        weapon.shoot = new ShootSpread(15, 7){{
            shotDelay = 4;
        }};
        weapon.bullet = new SpriteBulletType(10, 200, 34, 34, "fire-alt"){

            final SpriteBulletType despawnBullet = new SpriteBulletType(5, 200, 20, 20, "fire-alt2"){
                @Override
                protected void handleDrawRect(Bullet b){
                    Draw.alpha(0.2f + 0.8f * Interp.slope.apply(b.time / b.lifetime));
                    Draw.rect(region, b.x, b.y, width, height, b.rotation() - 45);
                }
                {
                    lifetime = 20;
                    drag = -0.02f;
                    pierceBuilding = true;
                    pierceCap = 50;
                    despawnEffect = hitEffect = Fx.none;
                }
            };

            @Override
            protected void handleDrawRect(Bullet b){
                if(b.time < tDecel)
                    Draw.alpha(Interp.pow2Out.apply(b.time / tDecel));
                else if(b.time >= tSafe)
                    Draw.alpha(0.2f + 0.8f * Interp.pow2Out.apply((tLife - b.time) / (tLife - tSafe)));

                Draw.rect(region, b.x, b.y, width, height, b.rotation() - 45);
            }

            @Override
            public void update(Bullet b){
                //hack: bullet.time doesn't increment until this method finishes
                if(b.time == 0){
                    //no one else will use bullet.timer
                    b.timer.getTimes()[0] = Mathf.atan2(b.vel.x, b.vel.y);
                    b.timer.getTimes()[1] = Mathf.range(180) + 720;
                    b.vel.setZero();

                }else if(b.time < tSafe){
                    assert b.owner instanceof Unit;
                    final float d = 108.0f;
                    float dx = d * Mathf.cos(b.timer.getTimes()[0]),
                        dy = d * Mathf.sin(b.timer.getTimes()[0]);
                    float destX = ((Unit)b.owner).x + dx,
                        destY = ((Unit)b.owner).y + dy;
                    float angle = Mathf.angle(b.aimX - destX, b.aimY - destY);

                    if(b.time < tDecel){
                        float frac = Interp.pow2Out.apply(b.time / tDecel);
                        b.set(((Unit)b.owner).x + frac * dx, ((Unit)b.owner).y + frac * dy);
                        b.rotation(b.timer.getTimes()[1] * (1 - frac) + frac * angle);

                    }else if(b.time >= tFire){
                        b.time = tSafe;
                        b.initVel(angle - 180, 12);
                        float accel = 1.6f * Time.delta;
                        b.mover = bl -> bl.vel.add(Mathf.cosDeg(angle) * accel, Mathf.sinDeg(angle) * accel);
                    }
                }
                super.update(b);
            }

            @Override
            public void despawned(Bullet b){
                final float d = 90.0f;
                float randTheta = Mathf.range(Mathf.halfPi);
                float x = b.x + d * Mathf.cos(randTheta),
                    y = b.y + d * Mathf.sin(randTheta);
                despawnBullet.create(b, x, y, Mathf.angle(b.x - x, b.y - y));
            }
            {
                lifetime = tLife;
                velocityScaleRandMin = velocityScaleRandMax = -1;
                lightning = 3;
                lightningColor = Pal.heal;
                lightningDamage = 40;
                lightningLength = 6;
                pierceBuilding = true;
                pierceCap = 50;
                keepVelocity = false;
                despawnEffect = hitEffect = Fx.none;
            }
        };
    }

    public static void load(){

        //region block environment
        sandWater.itemDrop = darksandWater.itemDrop = darksandTaintedWater.itemDrop = Items.sand;
        sporePine.attributes.set(FRAttribute.tree, 1.5f);
        snowPine.attributes.set(FRAttribute.tree, 1.5f);
        pine.attributes.set(FRAttribute.tree, 1.5f);
        whiteTreeDead.attributes.set(FRAttribute.tree, 1.0f);
        whiteTree.attributes.set(FRAttribute.tree, 1.0f);
        grass.attributes.set(FRAttribute.grass, 0.25f);
        grass.asFloor().wall = shrubs;

        //region block turret
        var scorchAmmoP = ((ItemTurret)scorch).ammoTypes.get(Items.pyratite);
        scorchAmmoP.damage = 60;
        scorchAmmoP.ammoMultiplier = 6.0f;

        wave.liquidCapacity += 10.0f;
        ((LiquidTurret)wave).ammoTypes.put(FRLiquids.liquidNitrogen, new LiquidBulletType(FRLiquids.liquidNitrogen){{
            damage = 4.55f;
            knockback = 0.7f;
            drag = 0.001f;
        }});
        tsunami.liquidCapacity += 20.0f;
        ((LiquidTurret)tsunami).ammoTypes.put(FRLiquids.liquidNitrogen, new LiquidBulletType(FRLiquids.liquidNitrogen){{
            speed = 4.0f;
            damage = 6.25f;
            lifetime = 49.0f;
            knockback = 1.3f;
            ammoMultiplier = 0.4f;
            statusDuration = 240.0f;
            puddleSize = 8.0f;
            orbSize = 4.0f;
            drag = 0.001f;
        }});


        //region block production
        ((Drill)laserDrill).drillTime -= 10.0f;
        ((Drill)laserDrill).hardnessDrillMultiplier -= 5.0f;
        ((Drill)blastDrill).drillTime -= 25.0f;
        ((Drill)blastDrill).hardnessDrillMultiplier -= 5.0f;


        //region block distribution
        phaseConveyor.itemCapacity += 5;
        ((ItemBridge)phaseConveyor).transportTime -= 1.0f;
        ((MassDriver)massDriver).rotateSpeed += 5.0f;
        ((MassDriver)massDriver).bulletSpeed += 9.5f;


        //region block liquid
        ((Pump)mechanicalPump).pumpAmount += 0.2f / 60.0f;
        ((Pump)impulsePump).pumpAmount += 1.2f / 9.0f / 60.0f;
        phaseConduit.liquidCapacity += 28.0f;


        //region block power
        ((ConsumeGenerator)steamGenerator).powerProduction += 0.5f;


        //region block effect
        illuminator.buildVisibility = BuildVisibility.shown;
        ((LightBlock)illuminator).brightness += 0.25f;
        ((LightBlock)illuminator).radius += 60.0f;


        //region block logic
        ((MessageBlock)worldMessage).maxTextLength = 999;


        //region unit
        alpha.coreUnitDock = beta.coreUnitDock = gamma.coreUnitDock = true;
        alpha.defaultCommand = UnitCommand.mineCommand;
        beta.defaultCommand = UnitCommand.assistCommand;

        flare.speed += 0.5f;
        flare.trailLength += 3;


        //region liquid
        Liquids.neoplasm.effect = FRStatusEffects.overgrown;


        //region logic
        LAssembler.customParsers.put(FRLogicStatements.TransitionEffectStatement.name, FRLogicStatements.TransitionEffectStatement::new);
        LAssembler.customParsers.put(FRLogicStatements.MaskCutsceneStatement.name, FRLogicStatements.MaskCutsceneStatement::new);
        LAssembler.customParsers.put(FRLogicStatements.FetchPlusPlusStatement.name, FRLogicStatements.FetchPlusPlusStatement::new);
        LAssembler.customParsers.put(FRLogicStatements.RemoveProcessorStatement.name, FRLogicStatements.RemoveProcessorStatement::new);
        LogicIO.allStatements.addUnique(FRLogicStatements.TransitionEffectStatement::new);
        LogicIO.allStatements.addUnique(FRLogicStatements.MaskCutsceneStatement::new);
        LogicIO.allStatements.addUnique(FRLogicStatements.FetchPlusPlusStatement::new);
        LogicIO.allStatements.addUnique(FRLogicStatements.RemoveProcessorStatement::new);
    }
}
