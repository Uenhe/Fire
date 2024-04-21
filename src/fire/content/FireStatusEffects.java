package fire.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import fire.type.FleshUnitType;
import fire.world.meta.FireStat;
import fire.world.meta.FireStatUnit;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;

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
                Angles.randLenVectors(e.id, 2, 1f + e.fin() * 2f, (x, y) ->
                    Fill.circle(e.x + x, e.y + y, e.fout() * 1.2f)
                );
            });
            init(() -> {
                opposite(StatusEffects.melting, StatusEffects.burning);
                affinity(StatusEffects.blasted, (unit, result, time) ->
                    unit.damagePierce(frostbite.transitionDamage)
                );
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

        overgrown = new StatusEffect("overgrown"){

            /** Health Multiplier for non-neoplasm-about unit. For neoplasm-about unit, use {@code healthMultiplier}. */
            private final float unfHealthMultiplier = 0.9f;
            /** Health and Shield unit regen per frame. */
            private final float regenPercent = 0.0004f;

            @Override
            public void setStats(){
                stats.addPercent(Stat.healthMultiplier, unfHealthMultiplier);
                stats.add(Stat.healing, regenPercent * 100f * 60f, FireStatUnit.percentPerSec);
                super.setStats();
            }

            @Override
            public void update(Unit unit, float time){

                // check whether unit is neoplasm-about
                if(unit.type instanceof FleshUnitType){

                    unit.healthMultiplier *= healthMultiplier;
                    unit.heal(regenPercent * Time.delta * unit.maxHealth);

                    /* The shield unit can regen at most. */
                    final float maxShield = 1200f;

                    if(unit.shield < maxShield){
                        unit.shield = Math.min(unit.shield + regenPercent * Time.delta * maxShield, maxShield);
                    }

                }else{
                    // nerf otherwise
                    unit.damageContinuousPierce(damage);
                    unit.healthMultiplier *= unfHealthMultiplier;
                }

                if(Mathf.chanceDelta(effectChance)){
                    Tmp.v1.rnd(Mathf.range(unit.type.hitSize / 2f));
                    effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0f, color, parentizeEffect ? unit : null);
                }
            }
            {
            color = Liquids.neoplasm.color;
            damage = 0.6f;
            speedMultiplier = 0.9f;
            effectChance = 0.1f;
            healthMultiplier = 1.1f;

            effect = new Effect(40f, e -> {
                Draw.color(overgrown.color);
                final float scl = e.data instanceof Unit u ? 1f + u.type.hitSize / 4f : 1f;

                Angles.randLenVectors(e.id, 2, 1f + e.fin() * 2f, (x, y) ->
                    Fill.circle(e.x + x, e.y + y, e.fout() * scl * 1.2f)
                );
            });
        }};

        disintegrated = new StatusEffect("disintegrated"){

            /** The armor unit can reduce at most. */
            private final float maxArmorReduction = 10f;

            @Override
            public void setStats(){
                super.setStats();
                stats.add(FireStat.armorPierce, maxArmorReduction);
            }

            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);
                final float min = unit.type.armor - maxArmorReduction;

                if(time >= 60f){
                    // linearly reduces armor in 1s
                    unit.armor = Math.max(unit.armor - maxArmorReduction / 60f, min);

                }else{
                    // linearly puts armor back in 1s
                    unit.armor += maxArmorReduction / 60f * Time.delta;
                }
            }
            {
            damage = 4.2f;
            speedMultiplier = 0.6f;
            effectChance = 0.2f;
            parentizeEffect = true;
            effect = Fx.unitShieldBreak;
        }};
    }
}
