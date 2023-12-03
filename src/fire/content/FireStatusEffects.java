package fire.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import fire.type.ArmorPierceStatusEffect;
import fire.type.NeoplasmStatusEffect;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;

public class FireStatusEffects{
    public static StatusEffect
        frostbite, inspired, mu, overgrown, disintegrated;

    public static void load(){

        frostbite = new StatusEffect("frostbite"){{
            color = Color.valueOf("ff0000");
            damage = 8f / 60f;
            speedMultiplier = 0.55f;
            healthMultiplier = 0.75f;
            transitionDamage = 22f;
            effect = new Effect(40f, e -> {
                Draw.color(frostbite.color);
                Angles.randLenVectors(e.id, 2, 1f + e.fin() * 2f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 1.2f);
                });
            });
            init(() -> {
                opposite(StatusEffects.melting, StatusEffects.burning);
                affinity(StatusEffects.blasted, (unit, result, time) -> {
                    unit.damagePierce(frostbite.transitionDamage);
                });
            });
        }};

        inspired = new StatusEffect("inspired"){{
            color = Pal.accent;
            healthMultiplier = 1.15f;
            speedMultiplier = 1.4f;
            reloadMultiplier = 1.2f;
            buildSpeedMultiplier = 2f;
            effectChance = 0.07f;
            effect = Fx.overdriven;
        }};

        mu = new StatusEffect("mu"){{
            damageMultiplier = 0.4f;
            speedMultiplier = 3f;
        }};

        overgrown = new NeoplasmStatusEffect("overgrown"){{
            color = Liquids.neoplasm.color;
            damage = 0.6f;
            speedMultiplier = 0.9f;
            effectChance = 0.1f;
            healthMultiplier = 1.1f;
            unfHealthMultiplier = 0.9f;
            regenPercent = 0.0004f;
            maxShield = 1200f;
            effect = new Effect(40f, e -> {
                Draw.color(overgrown.color);
                Angles.randLenVectors(e.id, 2, 1f + e.fin() * 2f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 1.2f);
                });
            });
        }};

        disintegrated = new ArmorPierceStatusEffect("disintegrated"){{
            damage = 5f;
            speedMultiplier = 0.6f;
            maxArmorReduction = 10f;
            effectChance = 0.2f;
            parentizeEffect = true;
            effect = Fx.unitShieldBreak;
        }};
    }
}
