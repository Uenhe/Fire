package fire.content;

import fire.logic.FRLogicStatements;
import fire.world.meta.FRAttribute;
import mindustry.ai.UnitCommand;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.gen.LogicIO;
import mindustry.logic.LAssembler;
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
        alpha.defaultCommand = beta.defaultCommand = UnitCommand.mineCommand;

        flare.speed += 0.5f;
        flare.trailLength += 3;


        //region liquid
        Liquids.neoplasm.effect = FRStatusEffects.overgrown;


        //region logic
        LAssembler.customParsers.put(FRLogicStatements.RemoveProcessorStatement.name, FRLogicStatements.RemoveProcessorStatement::new);
        LogicIO.allStatements.addUnique(FRLogicStatements.RemoveProcessorStatement::new);
    }
}
