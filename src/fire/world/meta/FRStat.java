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

    statusEffectApplied = new Stat("statusEffectApplied", StatCat.function),
    maxTargets = new Stat("maxTargets", StatCat.function),
    ammoDetails = new Stat("ammoDetails", StatCat.function)
        ;
}
