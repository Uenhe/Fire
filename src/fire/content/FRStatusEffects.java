package fire.content;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Reflect;
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
import mindustry.entities.units.StatusEntry;
import mindustry.gen.MechUnit;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

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
            private boolean added;

            @Override
            public void setStats(){
                super.setStats();
                stats.add(FRStat.clearDebuffUponApply, true);
            }

            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);

                if(!added){
                    Fx.healWave.at(unit);

                    var debuffs = DebuffRemoveFieldAbility.DE_BUFFS;
                    for(var e : debuffs)
                        if(!(unit.type instanceof FleshUnitType && e == overgrown))
                            unit.unapply(e);

                    added = true;
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
            speedMultiplier = 3.0f;
        }};

        overgrown = new StatusEffect("overgrown"){

            /** Health Multiplier for neoplasm-about unit. For general unit, use {@code healthMultiplier}. */
            private static final float neo_healthMultiplier = 1.1f;
            /** Health and Shield regen percent for neoplasm-about unit per second. */
            private static final float neo_regenPercent = 0.024f;
            /** Extra regen percent for reaction on neoplasm-about unit per second. */
            private static final float neo_extraRegenPercent = 0.012f;
            /** Damage percent for reaction to deal on general unit per second. */
            private static final float damagePercent = 0.01f;
            private static final StatusEffect status = StatusEffects.wet;

            @Override
            public void setStats(){
                stats.addMultModifier(Stat.healthMultiplier, healthMultiplier);
                stats.addMultModifier(FRStat.neoHealthMultiplier, neo_healthMultiplier);
                stats.addMultModifier(Stat.speedMultiplier, speedMultiplier);
                stats.add(Stat.damage, damage * 60.0f, StatUnit.perSecond);
                stats.add(FRStat.neoHealing, neo_regenPercent * 100.0f, FRStatUnit.percentPerSec);

                stats.add(Stat.affinities, status.emoji() + status.localizedName + Core.bundle.format("stat.reaction", damagePercent * 100.0f + FRStatUnit.percentPerSec.localized(), neo_extraRegenPercent * 100.0f + FRStatUnit.percentPerSec.localized()));
            }

            @SuppressWarnings("unchecked")
            @Override
            public void update(Unit unit, float time){
                var clazz = unit.getClass();
                while(clazz.getSuperclass() != Unit.class)
                    clazz = (Class<? extends Unit>)clazz.getSuperclass();

                Seq<StatusEntry> entries = Reflect.get(clazz, unit, "statuses");
                boolean wet = entries.contains(e -> e.effect == status); //why hasEffect() is buggy

                // check whether unit is neoplasm-about
                if(unit.type instanceof FleshUnitType){
                    unit.healthMultiplier *= neo_healthMultiplier;
                    unit.heal((neo_regenPercent + (wet ? neo_extraRegenPercent : 0.0f)) / 60.0f * unit.maxHealth * Time.delta);

                    final float maxShield = 1200.0f;
                    if(unit.shield < maxShield)
                        unit.shield = Math.min(unit.shield + neo_regenPercent / 60.0f * Time.delta * maxShield, maxShield);

                }else{
                    // nerf otherwise
                    unit.healthMultiplier *= healthMultiplier;
                    unit.damageContinuousPierce(damage + (wet ? damagePercent / 60.0f * unit.maxHealth : 0.0f));
                }

                if(wet) entries.find(e -> e.effect == this).time += 2.0f * Time.delta;

                if(Mathf.chanceDelta(effectChance)){
                    Tmp.v1.rnd(Mathf.range(unit.type.hitSize * 0.5f));
                    effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0.0f, color, unit);
                }
            }

            @Override
            public boolean applyTransition(Unit unit, StatusEffect to, StatusEntry entry, float time){
                var trans = transitions.get(to);
                if(trans != null) trans.handle(unit, entry, time);
                return false; //make them coexist
            }
            {
                color = Liquids.neoplasm.color;
                damage = 0.6f;
                speedMultiplier = 0.9f;
                effectChance = 0.1f;
                healthMultiplier = 0.9f;
                effect = new Effect(40.0f, e -> {
                    Draw.color(overgrown.color);
                    Angles.randLenVectors(e.id, 2, 1.0f + e.fin() * 2.2f, (x, y) ->
                        Fill.circle(e.x + x, e.y + y, 1.0f + e.fout() * 1.4f * (e.data instanceof Unit u ? Mathf.sqrt(u.type.hitSize) * 0.6f : 1.0f))
                    );
                });
                init(() -> affinity(StatusEffects.wet, (unit, result, time) -> {})); //handled in update()
            }
        };

        disintegrated = new StatusEffect("disintegrated"){

            /** The armor that unit reduces at most. */
            private static final byte maxArmorReduction = 10;

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
            }
            {
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
                if(unit.dead){
                    unit.vel.scl(1.0f - 0.05f * Time.delta);
                }else if(unit.moving() && Mathf.chanceDelta(0.06)){
                    unit.vel.scl(Mathf.random(0.1f, 1.5f));
                }
            }
            {
                color = Color.valueOf("98ffa8");
                damage = 2.0f;
                transitionDamage = 10.0f;
                speedMultiplier = dragMultiplier = 0.95f;
                reloadMultiplier = 0.8f;
                effectChance = 0.1f;
                effect = new Effect(24.0f, e -> {
                    Draw.color(color, e.fin());
                    Lines.stroke(2.7f * e.fout());
                    Lines.spikes(e.x, e.y, 8.0f * e.finpow(), 2.4f * e.fout() + 3.6f * e.fslope(), 4, 45.0f);
                });
                init(() ->
                    affinity(StatusEffects.shocked, (unit, result, time) ->
                        unit.damagePierce(transitionDamage)));
            }
        };
    }
}
