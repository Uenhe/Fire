package fire.content;

import arc.math.Mathf;
import fire.entities.abilities.RegenFieldAbility;
import fire.world.blocks.units.ElementUnitFactory;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.PointDefenseWeapon;

import static fire.content.FRUnitTypes.*;
import static mindustry.content.UnitTypes.*;

public class FRMath{
    public static float getDPS_B(BulletType b){
        if(b == null) return 0;

        float finalDPS = 0;
        if(b.fragBullets > 0)
            finalDPS += getDPS_B(b.fragBullet);

        if(b.spawnBullets.size > 0)
            for(BulletType sb : b.spawnBullets)
                finalDPS += getDPS_B(sb);

        if(b.bulletInterval > 0)
            finalDPS += getDPS_B(b.intervalBullet) * (b.lifetime - b.intervalDelay) * b.intervalBullets / b.bulletInterval;

        if(b instanceof ContinuousBulletType)
            return finalDPS + b.damage * b.lifetime * 60.0f / ((ContinuousBulletType)b).damageInterval * (1.0f + (b.pierce ? b.pierceCap == -1 ? 4.0f : 4.0f - 4.0f / b.pierceCap : 0.0f));

        return finalDPS + b.splashDamage * Math.max(0.75f, 5.0f - 160.0f / b.splashDamageRadius) + b.damage * (1.0f + (b.pierce ? b.pierceCap == -1 ? 4.0f : 4.0f - 4.0f / b.pierceCap : 0.0f));
    }

    public static float getDPS_W(Weapon w){
        if(w instanceof PointDefenseWeapon)
            return getDPS_B(w.bullet) * 10.0f / w.reload;

        return getDPS_B(w.bullet) * 60.0f / w.reload;
    }

    public static float getDPS_U(UnitType u){
        float finalDPS = 1.0f;
        for(var weapon : u.weapons)
            finalDPS += getDPS_W(weapon);

        return finalDPS;
    }

    public static float getHPS_A(Ability ability){
        if(ability instanceof ShieldArcAbility)
            return ((ShieldArcAbility)ability).regen * 60.0f;
        if(ability instanceof ShieldRegenFieldAbility)
            return ((ShieldRegenFieldAbility)ability).amount * 60.0f / ((ShieldRegenFieldAbility)ability).reload;
        if(ability instanceof ForceFieldAbility)
            return ((ForceFieldAbility)ability).regen * 60.0f;
        if(ability instanceof RegenAbility)
            return (((RegenAbility)ability).amount + ((RegenAbility)ability).percentAmount * 500.0f) * 60.0f;
        if(ability instanceof RegenFieldAbility)
            return ((RegenFieldAbility)ability).amount * 60.0f;
        if(ability instanceof RepairFieldAbility)
            return ((RepairFieldAbility)ability).amount * 60.0f / ((RepairFieldAbility)ability).reload;
        if(ability instanceof StatusFieldAbility)
            return ((StatusFieldAbility)ability).duration / ((StatusFieldAbility)ability).reload;
        if(ability instanceof EnergyFieldAbility)
            return ((EnergyFieldAbility)ability).healPercent * 500.0f * 60.0f / ((EnergyFieldAbility)ability).reload;
        return 0;
    }

    public static float getHPS_U(UnitType unitType){
        float hps = 0;
        for(Ability ability : unitType.abilities)
            hps += getHPS_A(ability);

        return hps;
    }

    public static ElementUnitFactory.UnitValue getValue(UnitType unit){
        float u, v, w;
        if(unit == pioneer){
            u = 3.8f;
            v = 4.4f;
            w = 4.6f;
        }else if(unit == firefly){
            u = 1.2f;
            v = 1.4f;
            w = 0.5f;
        }else if(unit == candlight){
            u = 2.3f;
            v = 2.5f;
            w = 1.8f;
        }else if(unit == lampryo){
            u = 3.4f;
            v = 3.85f;
            w = 3.2f;
        }else if(unit == lumiflame){
            u = 4.7f;
            v = 4.9f;
            w = 4.45f;
        }else if(unit == radiance){
            u = 5.6f;
            v = 5.9f;
            w = 5.9f;
        }else if(unit == guarding){
            u = 1.2f;
            v = 0.5f;
            w = 1.3f;
        }else if(unit == resisting){
            u = 2.6f;
            v = 2.8f;
            w = 2.9f;
        }else if(unit == garrison){
            u = 3.3f;
            v = 3.4f;
            w = 2.95f;
        }else if(unit == shelter){
            u = 4.5f;
            v = 4.9f;
            w = 4.75f;
        }else if(unit == blessing){
            u = 5.9f;
            v = 5.9f;
            w = 5.9f;
        }else if(unit == apollo){
            u = 5.95f;
            v = 6.3f;
            w = 5.6f;
        }else if(unit == pluto){
            u = 6.25f;
            v = 6.55f;
            w = 6.3f;
        }else if(unit == mechanicalTide){
            u = 6.8f;
            v = 6.65f;
            w = 6.1f;
        }else{
            u = (Mathf.log(10, unit.health) * 1.8f - 2.9f) + unit.armor * 0.01f;
            v = Mathf.log(3, unit.estimateDps() + 1.0f) - 3.8f;
            w = Math.max((u + v) * 0.5f, Mathf.log(4, FRMath.getHPS_U(unit) + 3.0f) - 0.5f);
            if(unit.naval)
                u += 0.5f;
            if(unit.flying || unit.speed >= arkyid.speed)
                v += 0.5f;
            if(unit.range >= fortress.range)
                v += 1.1f;
            if(unit.range >= corvus.range)
                v += 0.9f;
            if(unit.buildSpeed > 0.0f || unit.mineTier > 0)
                w += 0.4f;
            if(unit.buildSpeed >= gamma.buildSpeed || unit.mineTier >= mega.mineTier)
                w += 0.3f;
            if(unit.itemCapacity >= 50.0f)
                w += 0.3f;
            if(unit.itemCapacity >= 120.0f)
                w += 0.6f;
            u = (int)(Math.max(0.5f, u) * 20) * 0.05f;
            v = (int)(Math.max(0.5f, v) * 20) * 0.05f;
            w = (int)(Math.max(0.5f, w) * 20) * 0.05f;
            v = Math.min(u + 1.5f, Math.max(u - 1.5f, v));
        }
        return new ElementUnitFactory.UnitValue(u, v, w);
    }

    public static float getDamage_fusionBomb(float lifetime, float maxTime){
        float damage = maxTime, cnt = 1;
        while(lifetime >= 300.0f){
            damage += 300.0f * cnt;
            lifetime -= 300.0f;
            damage += cnt * maxTime + 100.0f;
        }
        return damage + lifetime * cnt;
    }

    public static float getRange_fusionBomb(float lifetime){
        return Mathf.sqrt(lifetime) * 100.0f;
    }

    public static float getReload1_fusionBomb(float lifetime){
        if(lifetime >= 1200.0f)
            return 10.0f;

        return 9000.0f / (lifetime - 300.0f);
    }
}
