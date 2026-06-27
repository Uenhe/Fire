package fire.world.meta;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class FRStat{

    public static final Stat
        armorPierce = new Stat("armorPierce"),
        maxArmorIncrease = new Stat("maxArmorIncrease"),
        allyStatusEffect = new Stat("allyStatusEffect"),
        enemyStatusEffect = new Stat("enemyStatusEffect"),
        clearDebuffUponApply = new Stat("clearDebuffUponApply"),
        floorMultiplier = new Stat("floorMultiplier"),
        neoHealthMultiplier = new Stat("neoHealthMultiplier"),
        neoHealing = new Stat("neoHealing"),
        percentageHealing = new Stat("percentageHealing"),
        elementLevel = new Stat("elementLevel"),
        minerweapon1 = new Stat("minerweapon1"),
        minerweapon2 = new Stat("minerweapon2"),
        minerweapon3 = new Stat("minerweapon3"),
        minerweapon4 = new Stat("minerweapon4"),
        minerweapon5 = new Stat("minerweapon5"),

    statusEffectApplied = new Stat("statusEffectApplied", StatCat.function),
        maxTargets = new Stat("maxTargets", StatCat.function),
        ammoDetails = new Stat("ammoDetails", StatCat.function)
            ;
}
