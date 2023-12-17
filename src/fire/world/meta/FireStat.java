package fire.world.meta;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class FireStat{
    public static final Stat

        armorPierce = new Stat("armorPierce"),
        baseHealChance = new Stat("baseHealChance"),
        maxArmorIncrease = new Stat("maxArmorIncrease"),
        allyStatusEffect = new Stat("allyStatusEffect", StatCat.function),
        enemyStatusEffect = new Stat("enemyStatusEffect", StatCat.function),
        lightningLength = new Stat("lightningLength", StatCat.function),
        lightningAmount = new Stat("lightningAmount", StatCat.function),
        invincibleTime = new Stat("invincibleTime", StatCat.function);
}
