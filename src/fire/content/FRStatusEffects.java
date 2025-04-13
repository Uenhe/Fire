package fire.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import fire.entities.abilities.DebuffRemoveFieldAbility;
import fire.type.FleshUnitType;
import fire.world.meta.FRStat;
import fire.world.meta.FRStatUnit;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;

public class FRStatusEffects{

    public static StatusEffect
        frostbite, inspired, sanctuaryGuard, mu, overgrown, disintegrated, magnetized;

    public static void load(){

        frostbite = new StatusEffect("frostbite"){{
            color = Color.valueOf("ff0000");
            damage = 8.0f / 60.0f;
            speedMultiplier = 0.55f;
            healthMultiplier = 0.75f;
            transitionDamage = 22.0f;
            effect = new Effect(40.0f, e -> {
                Draw.color(frostbite.color);
                Angles.randLenVectors(e.id, 2, 1.0f + e.fin() * 2.0f, (x, y) ->
                    Fill.circle(e.x + x, e.y + y, e.fout() * 1.2f)
                );
            });
            init(() -> {
                opposite(StatusEffects.melting, StatusEffects.burning);
                affinity(StatusEffects.blasted, (unit, result, time) ->
                    unit.damagePierce(transitionDamage)
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

        sanctuaryGuard = new StatusEffect("sanctuary-guard"){
            /** This is buggy... If the world reloads, it triggers again. */
            private boolean added = true;

            @Override
            public void setStats(){
                super.setStats();
                stats.add(FRStat.clearDebuffUponApply, true);
            }

            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);

                if(added){
                    Fx.healWave.at(unit);

                    for(var e : DebuffRemoveFieldAbility.DE_BUFFS)
                        if(!(unit.type instanceof FleshUnitType && e == overgrown))
                            unit.unapply(e);

                    added = false;
                }
            }

            @Override
            public void onRemoved(Unit unit){
                unit.heal(0.05f * unit.maxHealth);
            }{
                color = Pal.accent;
                damage = -2.4f;
                healthMultiplier = 2.25f;
            }
        };

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
                stats.add(Stat.healing, regenPercent * 100.0f * 60.0f, FRStatUnit.percentPerSec);
                super.setStats();
            }

            @Override
            public void update(Unit unit, float time){

                // check whether unit is neoplasm-about
                if(unit.type instanceof FleshUnitType){
                    unit.healthMultiplier *= healthMultiplier;
                    unit.heal(regenPercent * Time.delta * unit.maxHealth);

                    final float maxShield = 1200.0f;

                    if(unit.shield < maxShield)
                        unit.shield = Math.min(unit.shield + regenPercent * Time.delta * maxShield, maxShield);

                }else{
                    // nerf otherwise
                    unit.damageContinuousPierce(damage);
                    unit.healthMultiplier *= unfHealthMultiplier;
                }

                if(Mathf.chanceDelta(effectChance)){
                    Tmp.v1.rnd(Mathf.range(unit.type.hitSize * 0.5f));
                    effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0f, color, parentizeEffect ? unit : null);
                }
            }{
                color = Liquids.neoplasm.color;
                damage = 0.6f;
                speedMultiplier = 0.9f;
                effectChance = 0.1f;
                healthMultiplier = 1.1f;

                effect = new Effect(40.0f, e -> {
                    float scl = e.data instanceof Unit u
                        ? 1.0f + u.type.hitSize * 0.4f
                        : 1.0f;

                    Draw.color(overgrown.color);
                    Angles.randLenVectors(e.id, 2, 1.0f + e.fin() * 2.2f, (x, y) ->
                        Fill.circle(e.x + x, e.y + y, 1.0f + e.fout() * scl * 1.4f)
                    );
            }   );
            }
        };

        disintegrated = new StatusEffect("disintegrated"){

            /** The armor that unit reduces at most. */
            private final float maxArmorReduction = 10.0f;

            @Override
            public void setStats(){
                super.setStats();
                stats.add(FRStat.armorPierce, maxArmorReduction);
            }

            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);

                if(time >= 60.0f)
                    // linearly reduces armor in 1s
                    unit.armor = Math.max(unit.armor - maxArmorReduction / 60.0f, unit.type.armor - maxArmorReduction);

                else
                    // linearly puts armor back in 1s
                    unit.armor += maxArmorReduction / 60.0f * Time.delta;
            }

            @Override
            public void onRemoved(Unit unit){
                unit.armor = unit.type.armor;
            }{
                damage = 4.0f;
                speedMultiplier = 0.6f;
                effectChance = 0.15f;
                parentizeEffect = true;
                effect = Fx.unitShieldBreak;
            }
        };

        magnetized = new StatusEffect("magnetized"){

            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);
                if(unit.moving() && Mathf.chanceDelta(0.05))
                    unit.vel.scl(Mathf.random(0.2f, 2.5f));
            }{
                damage = 2.0f;
                transitionDamage = 10;
                speedMultiplier = 0.95f;
                reloadMultiplier = 0.8f;
                init(() ->
                    affinity(StatusEffects.shocked, (unit, result, time) ->
                        unit.damagePierce(transitionDamage)));
            }
        };
    }
}
