package fire.content;

import arc.Core;
import arc.util.Reflect;
import fire.world.meta.FAttribute;
import mindustry.ai.UnitCommand;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.logic.MessageBlock;
import mindustry.world.blocks.power.LightBlock;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.Pump;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.BuildVisibility;

import static mindustry.content.Blocks.*;
import static mindustry.content.UnitTypes.*;

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
        ((ItemBridge)phaseConveyor).transportTime -= 1.5f;
        ((MassDriver)massDriver).rotateSpeed += 5.0f;
        ((MassDriver)massDriver).bulletSpeed += 9.5f;

        //endregion
        //region block liquid

        ((Pump)mechanicalPump).pumpAmount += 0.2f / 60.0f;
        ((Pump)impulsePump).pumpAmount += 1.2f / 9.0f / 60.0f;
        phaseConduit.liquidCapacity += 14.0f;

        //endregion
        //region units

        ((UnitFactory)groundFactory).plans.add(
            new UnitFactory.UnitPlan(FUnitTypes.guarding, 1500.0f, ItemStack.with(
                Items.lead, 20,
                Items.titanium, 25,
                Items.silicon, 30
            ))
        );
        ((UnitFactory)airFactory).plans.add(
            new UnitFactory.UnitPlan(alpha, 2400.0f, ItemStack.with(
                Items.copper, 30,
                Items.lead, 40,
                Items.silicon, 30
            )),
            new UnitFactory.UnitPlan(FUnitTypes.firefly, 2400.0f, ItemStack.with(
                Items.lead, 20,
                Items.metaglass, 10,
                Items.coal, 10,
                Items.silicon, 15
            ))
        );
        ((Reconstructor)additiveReconstructor).upgrades.addAll(
            new UnitType[]{alpha, beta},
            new UnitType[]{FUnitTypes.guarding, FUnitTypes.resisting},
            new UnitType[]{FUnitTypes.blade, FUnitTypes.hatchet},
            new UnitType[]{FUnitTypes.firefly, FUnitTypes.candlelight}
        );
        ((Reconstructor)multiplicativeReconstructor).upgrades.addAll(
            new UnitType[]{beta, FUnitTypes.omicron},
            new UnitType[]{FUnitTypes.resisting, FUnitTypes.garrison},
            new UnitType[]{FUnitTypes.hatchet, FUnitTypes.castle}
        );
        ((Reconstructor)exponentialReconstructor).upgrades.addAll(
            new UnitType[]{FUnitTypes.omicron, FUnitTypes.pioneer},
            new UnitType[]{FUnitTypes.garrison, FUnitTypes.shelter}
        );
        ((Reconstructor)tetrativeReconstructor).upgrades.addAll(
            new UnitType[]{FUnitTypes.shelter, FUnitTypes.blessing}
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

        dagger.abilities.add(new MutableAbility(FUnitTypes.blade));
        mace.abilities.add(new MutableAbility(FUnitTypes.hatchet));
        fortress.abilities.add(new MutableAbility(FUnitTypes.castle));

        alpha.coreUnitDock = true;
        beta.coreUnitDock = true;
        gamma.coreUnitDock = true;
        alpha.defaultCommand = UnitCommand.mineCommand;
        beta.defaultCommand = UnitCommand.mineCommand;

        flare.speed += 0.5f;
        flare.trailLength = 3;

        //endregion
        //region liquid

        Liquids.neoplasm.effect = FStatusEffects.overgrown;

        //endregion

    }

    private static class MutableAbility extends Ability{

        private final UnitType toRespawn;

        public MutableAbility(UnitType type){
            toRespawn = type;
            display = false;

        }

        @Override
        public void death(Unit unit){
            if(!unit.hasEffect(FStatusEffects.overgrown)) return;
            Reflect.set(toRespawn.spawn(Team.crux, unit.x, unit.y), "statuses", Reflect.get(unit, "statuses"));
        }
    }
}
