package fire.game;

import arc.func.Intc;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.Structs;
import fire.content.FRStatusEffects;
import mindustry.content.StatusEffects;
import mindustry.game.SpawnGroup;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;

import static arc.math.Mathf.sqrt;
import static fire.content.FRUnitTypes.*;
import static mindustry.content.UnitTypes.*;

/** @see mindustry.game.Waves */
public class FRWaves2{

    public static Seq<SpawnGroup> generate(float difficulty, Rand rand, boolean airOnly, boolean naval){
        UnitType[][] species = {
            {dagger, mace, fortress, scepter, reign},
            {nova, pulsar, quasar, vela, corvus},
            {crawler, atrax, spiroct, arkyid, toxopid},
            {mace, atrax, garrison, shelter, blessing},
            {risso, minke, bryde, sei, omura},
            {risso, oxynoe, cyerce, aegires, navanax},
            {flare, horizon, zenith, rand.chance(0.5) ? quad : antumbra, rand.chance(0.15) ? apollo : eclipse},
            {firefly, candlight, lampryo, lumiflame, eclipse}
        };

        if(airOnly)
            species = Structs.filter(UnitType[].class, species, v -> v[0].flying);

        if(naval)
            species = Structs.filter(UnitType[].class, species, v -> v[0].flying || v[0].naval);
        else
            species = Structs.filter(UnitType[].class, species, v -> !v[0].naval);

        var fspec = species;

        //required progression:
        //- extra periodic patterns

        var out = new Seq<SpawnGroup>();

        //max reasonable wave, after which everything gets boring
        final int cap = 70;

        float shieldsPerWave = 30.0f + difficulty * 40.0f;
        float[] scaling = {1.0f, 1.5f, 2.0f, 3.0f, 4.0f};

        Intc createProgression = start -> {
            //main sequence
            var curSpecies = Structs.random(rand, fspec);
            int curTier = 0;

            for(int i = start; i < cap;){
                int f = i;
                int next = rand.random(8, 16) + (int)Mathf.lerp(5f, 0f, difficulty) + curTier * 4;
                int space = start == 0 ? 1 : rand.random(1, 2);
                int ctier = curTier;

                StatusEffect sfx;
                if(f <= 15)
                    sfx = StatusEffects.none;
                else if(f <= 30)
                    sfx = rand.chance(0.5) ? StatusEffects.overclock : StatusEffects.overdrive;
                else if(f <= 45)
                    sfx = rand.chance(0.3) ? FRStatusEffects.inspired : StatusEffects.overclock;
                else if(f <= 60)
                    sfx = rand.chance(0.2) ? StatusEffects.shielded : FRStatusEffects.inspired;
                else
                    sfx = rand.chance(0.5) ? StatusEffects.shielded : FRStatusEffects.inspired;

                //main progression
                out.add(new SpawnGroup(curSpecies[Math.min(curTier, curSpecies.length - 1)]){{
                    unitAmount = (int)(4 + sqrt(f * 0.04f + difficulty)) / (int)scaling[ctier];
                    begin = f;
                    end = f + f >= cap ? never : 10 + f * 2;
                    max = unitAmount * 4;
                    unitScaling = (difficulty < 0.4f ? rand.random(1.2f, 2.5f) : rand.random(1.0f, 2.2f)) * scaling[ctier];
                    shieldScaling = (sqrt(f * f * f) / 2 + 8 + difficulty * 10) * space;
                    shields = shieldScaling * 5;
                    spacing = space;
                    effect = type == arkyid ? StatusEffects.fast : sfx;
                }});

                //extra progression that tails out, blends in
                out.add(new SpawnGroup(curSpecies[Math.min(curTier, curSpecies.length - 1)]){{
                    unitAmount = (int)(4 + sqrt(f * 0.04f + difficulty)) / (int)scaling[ctier];
                    begin = f + 1;
                    end = f + f >= cap ? never : 10 + f * 2;
                    max = unitAmount * 3;
                    unitScaling = (difficulty < 0.4f ? rand.random(1.2f, 2.5f) : rand.random(1.0f, 2.2f)) * scaling[ctier];
                    shieldScaling = (f + 10.0f + difficulty * 10.0f) * space;
                    shields = shieldScaling * 3;
                    spacing = space;
                    effect = type == arkyid ? StatusEffects.fast : sfx;
                }});

                i += next + 1;
                if(curTier < 3 || (rand.chance(0.05) && difficulty > 0.8f)){
                    curTier++;
                }

                //do not spawn bosses
                curTier = Math.min(curTier, 3);

                //small chance to switch species
                if(rand.chance(0.3)){
                    curSpecies = Structs.random(rand, fspec);
                }
            }
        };

        createProgression.get(0);

        int step = 5 + rand.random(5);

        while(step <= cap){
            createProgression.get(step);
            step += (int)(rand.random(15, 30) * Mathf.lerp(1.0f, 0.5f, difficulty));
        }

        int bossWave = (int)(rand.random(40, 60) * Mathf.lerp(1.0f, 0.5f, difficulty));
        int bossSpacing = (int)(rand.random(20, 30) * Mathf.lerp(1.0f, 0.5f, difficulty));

        int bossTier = difficulty < 0.6f ? 3 : 4;

        //main boss progression
        out.add(new SpawnGroup(Structs.random(rand, species)[bossTier]){{
            unitAmount = 1;
            begin = bossWave;
            spacing = bossSpacing;
            end = never;
            max = 16;
            unitScaling = bossSpacing;
            shieldScaling = shieldsPerWave;
            effect = StatusEffects.boss;
        }});

        //alt boss progression
        out.add(new SpawnGroup(Structs.random(rand, species)[bossTier]){{
            unitAmount = 1;
            begin = bossWave + rand.random(3, 5) * bossSpacing;
            spacing = bossSpacing;
            end = never;
            max = 16;
            unitScaling = bossSpacing;
            shieldScaling = shieldsPerWave;
            effect = StatusEffects.boss;
        }});

        int finalBossStart = 45 + rand.random(15);

        //final boss waves
        out.add(new SpawnGroup(Structs.random(rand, species)[bossTier]){{
            unitAmount = 2;
            begin = finalBossStart;
            spacing = bossSpacing / 2;
            end = never;
            unitScaling = bossSpacing;
            shields = 2000.0f;
            shieldScaling = shieldsPerWave * 10.0f;
            effect = StatusEffects.boss;
        }});

        //final boss waves (alt)
        out.add(new SpawnGroup(Structs.random(rand, species)[bossTier]){{
            unitAmount = 2;
            begin = finalBossStart + 15;
            spacing = bossSpacing / 2;
            end = never;
            unitScaling = bossSpacing;
            shields = 2000.0f;
            shieldScaling = shieldsPerWave * 10.0f;
            effect = StatusEffects.boss;
        }});

        //shift back waves on higher difficulty for a harder start
        int shift = Math.max((int)(difficulty * 14 - 5), 0);
        for(var group : out){
            group.begin -= shift;
            group.end -= shift;
        }

        return out;
    }
}
