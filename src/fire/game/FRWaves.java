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

/** @author fy
 * @see mindustry.game.Waves */
public class FRWaves{

    public static Seq<SpawnGroup> generate(float difficulty, Rand rand, boolean airOnly){
        UnitType[][] species = {
            {dagger, mace, fortress, scepter, reign},
            {nova, pulsar, quasar, vela, corvus},
            {crawler, atrax, spiroct, arkyid, toxopid},
            {flare, horizon, zenith, rand.chance(0.5) ? quad : antumbra, rand.chance(0.1) ? quad : eclipse},
            {flare, horizon, omicron, rand.chance(0.5) ? quad : pioneer, eclipse},
            {guarding, resisting, garrison, shelter, blessing},
            {firefly, candlight, lampryo, pioneer, eclipse}
        };//sorry but there are no lakes on Lysetta:(


        if(airOnly){
            species = Structs.filter(UnitType[].class, species, v -> v[0].flying);
        }

        //required progression:
        //- extra periodic patterns

        var out = new Seq<SpawnGroup>();

        //max reasonable wave, after which everything gets boring
        final short cap = 225;

        float[] scaling = {1, 1.6f, 2f, 3.1f, 4.4f};

        UnitType[][] finalSpecies = species;
        Intc createProgression = start -> {
            //main sequence
            int tier;
            int type;

            for(short i = (short)start; i < cap;){
                float f = i;

                if(f < 10){
                    tier = rand.random(10) > 4 ? 1 : 2;
                }else if(f < 25){
                    tier = rand.random(10) > 5 ? 2 : 3;
                }else if(f < 40){
                    tier = rand.random(10) > 6 ? 3 : 4;
                }else if(f < 60){
                    tier = rand.random(10) > 3 ? rand.random(5) > 3 ? 3 : 4 : 5;
                }else if(f < 100){
                    tier = rand.random(5) > 3 ? 4 : 5;
                }else if(f < 150){
                    tier = rand.random(5) > 2 ? 4 : 5;
                }else{
                    tier = 5;
                }
                tier = tier - 1;
                //basicEnemies
                int counts = rand.random(6);
                StatusEffect ChosenEffect;
                if(f < 15){
                    ChosenEffect = StatusEffects.none;
                }else if(f <= 30){
                    ChosenEffect = rand.random(10) < 6 ? StatusEffects.overclock : StatusEffects.overdrive;
                }else if(f <= 70){
                    ChosenEffect = rand.random(10) < 7 ? rand.random(4) <= 2 ? StatusEffects.overdrive : StatusEffects.overclock : StatusEffects.shielded;
                }else if(f <= 140){
                    ChosenEffect = rand.random(3) <= 2 ? rand.random(2) == 1 ? StatusEffects.shielded : StatusEffects.fast : FRStatusEffects.inspired;
                }else{
                    ChosenEffect = rand.random(2) <= 1 ? StatusEffects.shielded : FRStatusEffects.inspired;
                }
                for(int j = 0; j < counts; j++){
                    type = rand.random(6);
                    int finalTier = tier;
                    int finalJ = j;
                    if(tier >= 1){
                        out.add(new SpawnGroup(finalSpecies[type][Math.min(finalTier, 5) - 1]){{
                            unitAmount = (int) (6 + sqrt(f / 10 + difficulty)) / (int) scaling[finalTier] * 2;
                            begin = (int) (f + finalJ);
                            end = (int) (f + f >= cap ? never : 10 + f * 2);
                            max = unitAmount * 8;
                            unitScaling = (difficulty < 0.4f ? rand.random(2.5f, 5f) : rand.random(1f, 4f)) * scaling[finalTier] * 2;
                            shieldScaling = (sqrt(f * f * f) / 2 + 8 + difficulty * 10) * counts;
                            shields = shieldScaling * 5;
                            spacing = counts;
                            effect = ChosenEffect;
                        }});
                    }
                    out.add(new SpawnGroup(finalSpecies[type][Math.min(tier, 5)]){{
                        unitAmount = (int) (6 + sqrt(f / 10 + difficulty)) / (int) scaling[finalTier];
                        begin = (int) (f + finalJ);
                        end = (int) (f + f >= cap ? never : 10 + f * 2);
                        max = unitAmount * 4;
                        unitScaling = (difficulty < 0.4f ? rand.random(2.5f, 5f) : rand.random(1f, 4f)) * scaling[finalTier];
                        shieldScaling = (sqrt(f * f * f) + 15 + difficulty * 20) * counts;
                        shields = shieldScaling * 3;
                        spacing = counts;
                        effect = ChosenEffect;
                    }});
                }
                i += (short)(rand.random(10, 18 + counts) - difficulty * 0.5f + tier * 5 + counts + 5);
            }
        };

        createProgression.get(0);

        int step = 5 + rand.random(5);

        while(step <= cap){
            createProgression.get(step);
            step += (int) (rand.random(15, 30) * Mathf.lerp(1f, 0.5f, difficulty));
        }

        return out;
    }
}
