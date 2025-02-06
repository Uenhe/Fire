package fire.content;

import arc.Core;
import arc.Events;
import arc.struct.Seq;
import arc.util.Reflect;
import fire.ai.FUnitCommand;
import fire.world.meta.FAttribute;
import mindustry.ai.UnitCommand;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.logic.MessageBlock;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.LightBlock;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.Pump;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.BuildVisibility;

import static mindustry.Vars.content;
import static mindustry.content.Blocks.*;
import static mindustry.content.UnitTypes.alpha;
import static mindustry.content.UnitTypes.beta;
import static mindustry.content.UnitTypes.dagger;
import static mindustry.content.UnitTypes.flare;
import static mindustry.content.UnitTypes.fortress;
import static mindustry.content.UnitTypes.gamma;
import static mindustry.content.UnitTypes.mace;

public class FOverride{

    public static void load(){

        //region block environment

        sandWater.itemDrop = Items.sand;
        darksandWater.itemDrop = Items.sand;
        darksandTaintedWater.itemDrop = Items.sand;
        sporePine.attributes.set(FAttribute.tree, 1.5f);
        snowPine.attributes.set(FAttribute.tree, 1.5f);
        pine.attributes.set(FAttribute.tree, 1.5f);
        whiteTreeDead.attributes.set(FAttribute.tree, 1.0f);
        whiteTree.attributes.set(FAttribute.tree, 1.0f);
        grass.attributes.set(FAttribute.grass, 0.25f);

        Blocks.sand.playerUnmineable =
        Blocks.darksand.playerUnmineable =
        Blocks.sandWater.playerUnmineable =
        Blocks.darksandWater.playerUnmineable =
        Blocks.darksandTaintedWater.playerUnmineable = !Core.settings.getBool("allowSandMining");

        //endregion
        //region block turret

        wave.liquidCapacity += 10.0f;
        ((LiquidTurret)wave).ammoTypes.put(FLiquids.liquidNitrogen, new LiquidBulletType(FLiquids.liquidNitrogen){{
            damage = 4.55f;
            knockback = 0.7f;
            drag = 0.001f;
        }});
        tsunami.liquidCapacity += 20.0f;
        ((LiquidTurret)tsunami).ammoTypes.put(FLiquids.liquidNitrogen, new LiquidBulletType(FLiquids.liquidNitrogen){{
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

        //endregion
        //region block production

        ((Drill)laserDrill).drillTime -= 10.0f;
        ((Drill)laserDrill).hardnessDrillMultiplier -= 5.0f;
        ((Drill)blastDrill).drillTime -= 25.0f;
        ((Drill)blastDrill).hardnessDrillMultiplier -= 5.0f;

        //endregion
        //region block distribution

        phaseConveyor.itemCapacity += 5;
        ((ItemBridge)phaseConveyor).transportTime -= 1.0f;
        ((MassDriver)massDriver).rotateSpeed += 5.0f;
        ((MassDriver)massDriver).bulletSpeed += 9.5f;

        //endregion
        //region block liquid

        ((Pump)mechanicalPump).pumpAmount += 0.2f / 60.0f;
        ((Pump)impulsePump).pumpAmount += 1.2f / 9.0f / 60.0f;
        phaseConduit.liquidCapacity += 14.0f;

        //endregion
        //region block power
        ((ConsumeGenerator)steamGenerator).powerProduction += 0.5f;

        //endregion
        //region block crafting
        phaseWeaver.itemCapacity += 10;

        //endregion
        //region block unit

        ((UnitFactory)airFactory).plans.add(
            new UnitFactory.UnitPlan(alpha, 2400.0f, ItemStack.with(
                Items.copper, 30,
                Items.lead, 40,
                Items.silicon, 30
            ))
        );
        ((Reconstructor)Blocks.additiveReconstructor).upgrades.addAll(
            new UnitType[]{alpha, beta}
        );
        ((Reconstructor)Blocks.multiplicativeReconstructor).upgrades.addAll(
            new UnitType[]{beta, FUnitTypes.omicron}
        );

        //endregion
        //region block effect

        illuminator.buildVisibility = BuildVisibility.shown;
        ((LightBlock)illuminator).brightness += 0.25f;
        ((LightBlock)illuminator).radius += 60.0f;

        //endregion
        //region block logic

        ((MessageBlock)worldMessage).maxTextLength = 999;

        //endregion
        //region unit

        Events.on(EventType.UnitDrownEvent.class, e -> {
            if(!e.unit.hasEffect(FStatusEffects.overgrown)) return;

            UnitType type;
            if(e.unit.type == dagger)
                type = FUnitTypes.blade;
            else if(e.unit.type == mace)
                type = FUnitTypes.hatchet;
            else if(e.unit.type == fortress)
                type = FUnitTypes.castle;
            else
                return;

            Reflect.set(type.spawn(Team.crux, e.unit.x, e.unit.y), "statuses", Reflect.get(e.unit, "statuses"));
        });

        alpha.coreUnitDock = true;
        beta.coreUnitDock = true;
        gamma.coreUnitDock = true;
        alpha.defaultCommand = UnitCommand.mineCommand;
        beta.defaultCommand = UnitCommand.mineCommand;

        flare.speed += 0.5f;
        flare.trailLength = 3;

        // have to do this or can't control dash-able units with other units at the same time
        for(var type : content.units()){
            if(type.commands.length <= 1) continue; //the first must be moveCommand, skip

            var seq = new Seq<>(type.commands);
            seq.replace(UnitCommand.repairCommand, FUnitCommand.repairDashCommand);
            seq.replace(UnitCommand.rebuildCommand, FUnitCommand.rebuildDashCommand);
            seq.replace(UnitCommand.assistCommand, FUnitCommand.assistDashCommand);

            type.commands = seq.toArray();
        }

        //endregion
        //region liquid

        Liquids.neoplasm.effect = FStatusEffects.overgrown;

        //endregion

    }
}
