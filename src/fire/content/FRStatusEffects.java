package fire.content;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.IntFloatMap;
import arc.struct.IntIntMap;
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
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.units.StatusEntry;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.content;

public class FRStatusEffects{

    public static final StatusEffect
        frostbite, inspired, sanctuaryGuard, mu, overgrown, disintegrated, magnetized, informationalProjection/*, starfire*/;

    static{
        frostbite = new StatusEffect("frostbite"){{
            Color.valueOf(color, "ff0000");
            damage = 8.0f / 60.0f;
            speedMultiplier = 0.55f;
            healthMultiplier = 0.75f;
            transitionDamage = 22.0f;
            effect = new Effect(40.0f, e -> {
                Draw.color(color);
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
            color.set(Pal.accent);
            healthMultiplier = 1.15f;
            speedMultiplier = 1.4f;
            reloadMultiplier = 1.2f;
            buildSpeedMultiplier = 2f;
            effectChance = 0.07f;
            effect = Fx.overdriven;
        }};

        sanctuaryGuard = new StatusEffect("sanctuary-guard"){

            @Override
            public void setStats(){
                super.setStats();
                stats.add(FRStat.clearDebuffUponApply, true);
            }

            @Override
            public void applied(Unit unit, float time, boolean extend){
                super.applied(unit, time, extend);
                DebuffRemoveFieldAbility.removeDebuff(unit);
                Fx.healWave.at(unit);
            }

            @Override
            public void onRemoved(Unit unit){
                unit.heal(0.05f * unit.maxHealth);
            }
            {
                color.set(Pal.accent);
                damage = -2.4f;
                healthMultiplier = 2.25f;
            }
        };

        mu = new StatusEffect("mu"){{
            color.set(Pal.accent);
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
            public void update(Unit unit, StatusEntry entry){
                var clazz = unit.getClass();
                while(Unit.class != clazz.getSuperclass())
                    clazz = (Class<? extends Unit>)clazz.getSuperclass();

                Seq<StatusEntry> entries = Reflect.get(clazz, unit, "statuses");
                boolean wet = entries.contains(e -> e.effect == status); //hasEffect() here is buggy but why???

                if(unit.type instanceof FleshUnitType){
                    float perc = (neo_regenPercent + (wet ? neo_extraRegenPercent : 0.0f)) / 60.0f * Time.delta, maxShield = 0.6f * unit.maxHealth + 600.0f;
                    unit.healthMultiplier *= neo_healthMultiplier / healthMultiplier;
                    unit.heal(perc * unit.maxHealth);

                    if(unit.shield < maxShield)
                        unit.shield = Math.min(unit.shield + Math.min(perc * maxShield, 10.0f * Time.delta), maxShield);

                }else{
                    unit.damageContinuousPierce(damage + (wet ? damagePercent / 60.0f * unit.maxHealth : 0.0f));
                }

                if(wet) entries.find(e -> e.effect == this).time += 2.0f * Time.delta;

                if(Mathf.chanceDelta(effectChance)){
                    Tmp.v6.rnd(Mathf.range(unit.type.hitSize * 0.5f));
                    effect.at(unit.x + Tmp.v6.x, unit.y + Tmp.v6.y, 0.0f, color, unit);
                }
            }

            @Override
            public boolean applyTransition(Unit unit, StatusEffect to, StatusEntry entry, float time){
                var trans = transitions.get(to);
                if(trans != null) trans.handle(unit, entry, time);
                return false; //make them coexist
            }
            {
                color.set(Liquids.neoplasm.color);
                damage = 0.6f;
                speedMultiplier = 0.9f;
                effectChance = 0.1f;
                healthMultiplier = 0.9f;
                effect = new Effect(40.0f, e -> {
                    Draw.color(color);
                    Angles.randLenVectors(e.id, 2, 1.0f + e.fin() * 2.2f, (x, y) ->
                        Fill.circle(e.x + x, e.y + y, 1.0f + e.fout() * 1.4f * (e.data instanceof Float hitSize ? Mathf.sqrt(hitSize) * 0.6f : 1.0f))
                    );
                });
                init(() -> affinity(status, (unit, result, time) -> {})); //handled in update()
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
            public void update(Unit unit, StatusEntry entry){
                super.update(unit, entry);

                if(entry.time >= 60.0f)
                    //linearly reduces armor in 1s
                    unit.armor = Math.max(unit.armor - maxArmorReduction / 60.0f, unit.type.armor - maxArmorReduction);

                else
                    //linearly puts armor back in 1s
                    unit.armor += maxArmorReduction / 60.0f * Time.delta;
            }

            @Override
            public void onRemoved(Unit unit){
                unit.armor = unit.type.armor;
            }
            {
                Color.valueOf(color, "989aa4");
                damage = 3.5f;
                healthMultiplier = 0.85f;
                effectChance = 0.14f;
                parentizeEffect = true;
                effect = Fx.unitShieldBreak;
            }
        };

        magnetized = new StatusEffect("magnetized"){
            @Override
            public void update(Unit unit, StatusEntry entry){
                super.update(unit, entry);
                if(unit.dead)
                    unit.vel.scl(1.0f - 0.05f * Time.delta);
                else if(unit.moving() && Mathf.chanceDelta(0.06))
                    unit.vel.scl(Mathf.random(0.1f, 1.5f));
            }
            {
                Color.valueOf(color, "98ffa8");
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

        //Maintained by fy, horrible.
        informationalProjection = new StatusEffect("informational-projection"){
            private static final Color[] colors = {Color.red, Color.green, Color.blue};
            private static final IntFloatMap timerMap = new IntFloatMap();
            private static final IntFloatMap timerMap2 = new IntFloatMap();
            private static final IntIntMap shootingMap = new IntIntMap();
            private static final BulletType[] bullets = new BulletType[255];

            //Sorry for the shit-liked code :(
            private static int checkBullet(UnitType unit){
                for(var weapon : unit.weapons){
                    var b = weapon.bullet;
                    if(b instanceof ArtilleryBulletType || b instanceof ExplosionBulletType){
                        return 2;
                    }else if(b instanceof LaserBulletType || b instanceof PointBulletType || b instanceof PointLaserBulletType || b instanceof LaserBoltBulletType || b instanceof ContinuousLaserBulletType || b instanceof ShrapnelBulletType || b instanceof SapBulletType){
                        if(!unit.flying) return 1;
                    }
                }
                return 0;
            }

            static{
                Events.on(EventType.ResetEvent.class, e -> {
                    timerMap.clear();
                    timerMap2.clear();
                    shootingMap.clear();
                });

                Events.on(EventType.ContentInitEvent.class, e -> {
                    for(int i = 0, n = content.units().size; i < n; i++){
                        var u = content.units().get(i);
                        int type = checkBullet(content.units().get(i));
                        float Damage = Math.min(u.health * 0.05f + 40.0f, u.health * 0.01f + 60.0f);
                        float Lifetime = 60.0f + Damage * 0.01f;
                        float Speed = Mathf.sqrt(Damage * 0.5f);
                        if(type == 1){
                            float Length = Lifetime * Speed * 0.5f;
                            bullets[i] = new RailBulletType(){{
                                damage = Damage * 2.5f;
                                length = Length;
                                pointEffectSpace = 20f;
                                despawnEffect = pierceEffect = hitEffect = shootEffect = new ParticleEffect(){{
                                    particles = 1;
                                    lifetime = Lifetime;
                                    length = 0.0f;
                                    sizeFrom = Length / 80.0f;
                                    sizeInterp = Interp.pow5In;
                                    colorFrom = colorTo = Color.green;
                                }};
                                pointEffectSpace = Length / 10.0f;
                                pointEffect = FRFx.lineTrailEffect(Lifetime, pointEffectSpace + 1, Length / 100.0f, 0, Color.green, 1);
                                smokeEffect = Fx.shootBig2;
                                pierceDamageFactor = 0.4f;
                            }};
                        }/*
                        else if(type == 2){
                            bullets[i] = new ArtilleryBulletType(){{
                                damage = Damage * 0.5f;
                                splashDamage = u.health * 0.018f;
                                splashDamageRadius = Mathf.sqrt(damage * 1.5f) * 4.0f;
                                if(damage >= 100.0f)damage = Mathf.sqrt(damage)/4.0f + 97.5f;
                                lifetime = Lifetime * 2.2f;
                                speed = Speed * 0.5f + 3.5f;
                                drag = 0.04f;
                                width = height = Mathf.sqrt(damage * 0.14f);
                                backColor = trailColor = Color.green;
                                frontColor = Pal.heal;
                                if(splashDamage >= 1200.0f){
                                    damage *= 3.0f;
                                    speed *= 0.4f;
                                    lifetime *= 2.5f;
                                    drag = 0.02f;
                                    weaveScale = 20.0f;
                                    weaveMag = 2.0f;
                                    width *= 1.3f;
                                    height *= 1.3f;
                                    splashDamageRadius += 24.0f;
                                    hitEffect = FRFx.powerfulBlastEffect(90.0f,splashDamageRadius,0,0,Pal.heal,null);
                                }else {
                                    hitEffect = new WaveEffect(){{
                                        lifetime = 40.0f;
                                        colorFrom = Color.green;
                                        colorTo = Color.white;
                                        sizeFrom = 0.0f;
                                        sizeTo = splashDamageRadius;
                                        strokeFrom = 4.0f;
                                        interp = Interp.pow5Out;
                                        lightInterp = Interp.pow5Out;
                                    }};
                                }

                                fragBullets = 2 + (int)Math.log(splashDamage);
                                fragBullet = new ArtilleryBulletType(){{
                                    splashDamage = u.health * 0.018f;
                                    splashDamageRadius = Mathf.sqrt(splashDamage * 0.1f);
                                    damage = 40.0f + Mathf.sqrt(splashDamage) * 0.2f;
                                    lifetime = Lifetime * 0.6f;
                                    speed = Speed * 0.2f;
                                    width = height = Mathf.sqrt(damage * 0.14f);
                                    backColor = trailColor = Color.green;
                                    frontColor = Pal.heal;
                                    hitEffect = new WaveEffect(){{
                                        lifetime = 40.0f;
                                        colorFrom = Color.green;
                                        colorTo = Color.white;
                                        sizeFrom = 0.0f;
                                        sizeTo = splashDamageRadius;
                                        strokeFrom = 4.0f;
                                        interp = Interp.pow5Out;
                                        lightInterp = Interp.pow5Out;
                                    }};
                                    if(splashDamage >= 300){
                                        fragBullets = 3;
                                        fragBullet = new ArtilleryBulletType(){{
                                            splashDamage = u.health * 0.005f;
                                            splashDamageRadius = Mathf.sqrt(splashDamage * 0.1f) * 0.3f;
                                            damage = 20.0f + Mathf.sqrt(splashDamage) * 0.1f;
                                            lifetime = Lifetime * 0.4f;
                                            speed = Speed * 0.1f;
                                            width = height = Mathf.sqrt(damage * 0.14f) * 0.7f;
                                            backColor = trailColor = Color.green;
                                            frontColor = Pal.heal;
                                            hitEffect = new WaveEffect(){{
                                                lifetime = 40.0f;
                                                colorFrom = Color.green;
                                                colorTo = Color.white;
                                                sizeFrom = 0.0f;
                                                sizeTo = splashDamageRadius;
                                                strokeFrom = 4.0f;
                                                interp = Interp.pow5Out;
                                                lightInterp = Interp.pow5Out;
                                            }};
                                        }};
                                    }
                                }};
                            }};
                        }*/else
                            bullets[i] = new BasicBulletType(){{
                                damage = Damage;
                                splashDamage = damage >= 65.0f ? u.health * 0.015f : 0;
                                splashDamageRadius = Mathf.sqrt(damage * 0.75f) * 3.0f;
                                lifetime = Lifetime;
                                speed = Speed;
                                if(damage >= 100.0f)
                                    damage = Mathf.sqrt(damage) / 4.0f + 97.5f;
                                pierceCap = (int)(damage * 0.05f);
                                if(pierceCap >= 1){
                                    pierce = true;
                                }
                                drag = 0.05f;
                                width = height = Mathf.sqrt(damage * 0.08f);
                                trailWidth = width * 1.2f;
                                trailLength = (int)(trailWidth * 8.0f);
                                homingPower = 0.12f;
                                homingRange = lifetime * speed * 0.2f;

                                fragBullets = 0;

                                backColor = trailColor = Color.green;
                                frontColor = Pal.heal;

                                if(splashDamage >= 1200.0f){
                                    pierce = false;
                                    damage *= 3.0f;
                                    speed *= 0.4f;
                                    lifetime *= 2.5f;
                                    drag = 0.02f;
                                    weaveScale = 20.0f;
                                    weaveMag = 2.0f;
                                    width *= 1.3f;
                                    height *= 1.3f;
                                    splashDamageRadius += 24.0f;
                                    despawnEffect = FRFx.powerfulBlastEffect(90.0f, splashDamageRadius, 0, 0, Pal.heal, null);
                                }else if(splashDamage >= 120.0f){
                                    pierce = false;
                                    damage *= 2.0f;
                                    weaveScale = 15.0f;
                                    weaveMag = 1.2f;
                                    speed *= 0.8f;
                                    lifetime *= 1.2f;
                                    despawnEffect = new WaveEffect(){{
                                        lifetime = 40.0f;
                                        colorFrom = Color.green;
                                        colorTo = Color.white;
                                        sizeFrom = 0.0f;
                                        sizeTo = splashDamageRadius;
                                        strokeFrom = 4.0f;
                                        interp = Interp.pow5Out;
                                        lightInterp = Interp.pow5Out;
                                    }};
                                }
                            }};
                    }
                });
            }


            @Override
            public void setStats(){
                super.setStats();
                stats.add(FRStat.percentageHealing, 1, FRStatUnit.percentPerSec);
            }

            @Override
            public void onRemoved(Unit unit){
                super.onRemoved(unit);
                timerMap.remove(unit.id, 0.0f);
            }

            @Override
            public void update(Unit unit, StatusEntry entry){
                super.update(unit, entry);
                unit.team = Team.get(4);
                unit.heal(unit.maxHealth * Time.delta / 3600.0f);

                float timer = timerMap.get(unit.id);
                float timer2 = timerMap2.get(unit.id);
                int shooting = shootingMap.get(unit.id);
                if((timer += Time.delta) >= 15.0f){
                    if(unit.type.id < bullets.length && shooting > 0){
                        shooting--;
                        bullets[unit.type.id].create(unit, unit.x, unit.y, unit.rotation + (10.0f * shooting - 30.0f) * Mathf.sign(shooting % 2 == 0));
                    }
                    timer -= 15.0f;
                    FRFx.ghostEffect.at(unit.x + Mathf.range(unit.hitSize * 0.8f), unit.y + Mathf.range(unit.hitSize * 0.8f), unit.rotation - 90.0f, colors[Mathf.random(2)], unit.type.fullIcon);
                }
                if((timer2 += Time.delta) >= 300.0f){
                    timer2 -= 300.0f;
                    if(bullets[unit.type.id] instanceof BasicBulletType){
                        for(int i = 0; i < 6; i++){
                            bullets[unit.type.id].create(unit, unit.x, unit.y, unit.rotation + 60.0f * i);
                        }
                    }else if(bullets[unit.type.id] instanceof ArtilleryBulletType){
                        for(int i = 0; i < 3; i++){
                            bullets[unit.type.id].create(unit, unit.x, unit.y, unit.rotation + 120.0f * i);
                        }
                    }else
                        shooting = 7;
                }
                timerMap.put(unit.id, timer);
                timerMap2.put(unit.id, timer2);
                shootingMap.put(unit.id, shooting);
            }

            {
                healthMultiplier = 3.0f;
                damageMultiplier = 1.4f;
                reloadMultiplier = 0.8f;
                speedMultiplier = 1.3f;
                damage = 1.0f;
                effectChance = 0.05f;
            }
        };

        /*starfire = new StatusEffect("starfire"){

            private static final Color[] colors = {Color.red, Color.green, Color.blue};
            private static final IntFloatMap timerMap = new IntFloatMap();

            static{
                Events.on(EventType.ResetEvent.class, e -> timerMap.clear());
            }

            @Override
            public void setStats(){
                super.setStats();
                stats.add(FRStat.percentageHealing, 1, FRStatUnit.percentPerSec);
            }

            @Override
            public void onRemoved(Unit unit){
                super.onRemoved(unit);
                timerMap.remove(unit.id, 0.0f);
            }

            @Override
            public void update(Unit unit, StatusEntry entry){
                super.update(unit, entry);
                unit.damagePierce(damage * entry.time / 60f);
            }
            {
                effect = new Effect(40.0F, (e) -> {
                    Draw.color(Pal.lightFlame, Pal.darkFlame, e.fin());
                    Angles.randLenVectors((long)e.id, 5, 3.0F + e.fin() * 8.0F, (x, y) -> {
                        Fill.circle(e.x + x, e.y + y, 0.1F + e.fout() * 1.2F);
                    });
                });
                healthMultiplier = 0.95f;
                reloadMultiplier = 0.9f;
                effectChance = 0.05f;
                init(() -> {
                    opposite(StatusEffects.wet, StatusEffects.freezing);
                    affinity(StatusEffects.tarred, (unit, result, time) -> {
                        unit.damagePierce(transitionDamage);
                        Fx.burning.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f));
                        unit.apply(StatusEffects.burning, 300.0f);
                        unit.apply(StatusEffects.melting, 180.0f);
                    });
                });
            }
        };*/
    }

    public static void load(){}
}
