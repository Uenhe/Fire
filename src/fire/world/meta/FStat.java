package fire.world.meta;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class FStat{

    public static final Stat
        armorPierce = new Stat("armorPierce"),
        baseHealChance = new Stat("baseHealChance"),
        maxArmorIncrease = new Stat("maxArmorIncrease"),
        allyStatusEffect = new Stat("allyStatusEffect"),
        enemyStatusEffect = new Stat("enemyStatusEffect"),
        clearDebuffUponApply = new Stat("clearDebuffUponApply"),
        floorMultiplier = new Stat("floorMultiplier"),

        statusEffectApplied = new Stat("statusEffectApplied", StatCat.function),
        lightningLength = new Stat("lightningLength", StatCat.function),
        lightningAmount = new Stat("lightningAmount", StatCat.function),
        invincibleTime = new Stat("invincibleTime", StatCat.function),
        maxTargets = new Stat("maxTargets", StatCat.function);
}
