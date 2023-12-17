package fire.content;

import fire.gen.MutableMechUnit;
import fire.world.meta.FireAttribute;
import mindustry.ai.UnitCommand;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.power.LightBlock;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.Pump;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;

import java.util.Arrays;

public class FireOverride{

    private static UnitType[] unitUpgrade(UnitType from, UnitType to){
        var a = Arrays.copyOf(((Reconstructor)Blocks.tetrativeReconstructor).upgrades.get(0), 2);
        a[0] = from;
        a[1] = to;
        return a;
    }

    public static void load(){

        //region block environment

        Blocks.sandWater.itemDrop = Items.sand;
        Blocks.darksandWater.itemDrop = Items.sand;
        Blocks.darksandTaintedWater.itemDrop = Items.sand;
        Blocks.sporePine.attributes.set(FireAttribute.tree, 1.5f);
        Blocks.snowPine.attributes.set(FireAttribute.tree, 1.5f);
        Blocks.pine.attributes.set(FireAttribute.tree, 1.5f);
        Blocks.whiteTreeDead.attributes.set(FireAttribute.tree, 1f);
        Blocks.whiteTree.attributes.set(FireAttribute.tree, 1f);
        Blocks.grass.attributes.set(FireAttribute.grass, 0.25f);

        //endregion
        //region block turret

        Blocks.wave.liquidCapacity += 10f;
        ((LiquidTurret) Blocks.wave).ammoTypes.put(FireLiquids.liquidNitrogen, new LiquidBulletType(FireLiquids.liquidNitrogen){{
            damage = 4.55f;
            knockback = 0.7f;
            drag = 0.001f;
        }});
        Blocks.tsunami.liquidCapacity += 20f;
        ((LiquidTurret) Blocks.tsunami).ammoTypes.put(FireLiquids.liquidNitrogen, new LiquidBulletType(FireLiquids.liquidNitrogen){{
            speed = 4f;
            damage = 6.25f;
            lifetime = 49f;
            knockback = 1.3f;
            ammoMultiplier = 0.4f;
            statusDuration = 240f;
            puddleSize = 8f;
            orbSize = 4f;
            drag = 0.001f;
        }});

        //endregion
        //region block production

        ((Drill) Blocks.laserDrill).drillTime -= 10f;
        ((Drill) Blocks.laserDrill).hardnessDrillMultiplier -= 5f;
        ((Drill) Blocks.blastDrill).drillTime -= 25f;
        ((Drill) Blocks.blastDrill).hardnessDrillMultiplier -= 5f;

        //endregion
        //region block distribution

        Blocks.phaseConveyor.itemCapacity += 5;
        ((ItemBridge) Blocks.phaseConveyor).transportTime -= 1f;
        ((MassDriver) Blocks.massDriver).rotateSpeed += 5f;
        ((MassDriver) Blocks.massDriver).bulletSpeed += 9.5f;

        //endregion
        //region block liquid

        ((Pump) Blocks.mechanicalPump).pumpAmount += 0.2f / 60f;
        ((Pump) Blocks.impulsePump).pumpAmount += 1.2f / 9f / 60f;
        Blocks.phaseConduit.liquidCapacity += 14f;

        //endregion
        //region units

        ((UnitFactory) Blocks.groundFactory).plans.add(
            new UnitFactory.UnitPlan(FireUnitTypes.guarding, 1500f, ItemStack.with(
                Items.lead, 20,
                Items.titanium, 25,
                Items.silicon, 30
            ))
        );
        ((UnitFactory) Blocks.airFactory).plans.add(
            new UnitFactory.UnitPlan(UnitTypes.alpha, 2400f, ItemStack.with(
                Items.copper, 30,
                Items.lead, 40,
                Items.silicon, 30
            )),
            new UnitFactory.UnitPlan(FireUnitTypes.firefly, 2400f, ItemStack.with(
                Items.lead, 20,
                Items.metaglass, 10,
                Items.coal, 10,
                Items.silicon, 15
            ))
        );
        ((Reconstructor) Blocks.additiveReconstructor).upgrades.add(
            unitUpgrade(UnitTypes.alpha, UnitTypes.beta),
            unitUpgrade(FireUnitTypes.guarding, FireUnitTypes.resisting),
            unitUpgrade(FireUnitTypes.blade, FireUnitTypes.hatchet),
            unitUpgrade(FireUnitTypes.firefly, FireUnitTypes.candlelight)
        );
        ((Reconstructor) Blocks.multiplicativeReconstructor).upgrades.add(
            unitUpgrade(UnitTypes.beta, FireUnitTypes.omicron),
            unitUpgrade(FireUnitTypes.resisting, FireUnitTypes.garrison),
            unitUpgrade(FireUnitTypes.hatchet, FireUnitTypes.castle)
        );
        ((Reconstructor) Blocks.exponentialReconstructor).upgrades.add(
            unitUpgrade(FireUnitTypes.omicron, FireUnitTypes.pioneer),
            unitUpgrade(FireUnitTypes.garrison, FireUnitTypes.shelter)
        );

        //endregion
        //region block effect

        ((LightBlock) Blocks.illuminator).brightness += 0.25f;
        ((LightBlock) Blocks.illuminator).radius += 60f;

        //endregion
        //region unit

        UnitTypes.dagger.constructor = () -> new MutableMechUnit(FireUnitTypes.blade);
        UnitTypes.mace.constructor = () -> new MutableMechUnit(FireUnitTypes.hatchet);
        UnitTypes.fortress.constructor = () -> new MutableMechUnit(FireUnitTypes.castle);

        UnitTypes.alpha.coreUnitDock = true;
        UnitTypes.beta.coreUnitDock = true;
        UnitTypes.gamma.coreUnitDock = true;
        UnitTypes.alpha.defaultCommand = UnitCommand.mineCommand;
        UnitTypes.beta.defaultCommand = UnitCommand.mineCommand;

        //endregion
        //region liquid

        Liquids.neoplasm.effect = FireStatusEffects.overgrown;
    }
}
