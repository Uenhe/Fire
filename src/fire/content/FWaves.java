package mindustry.game;

import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import fire.content.FStatusEffects;
import mindustry.content.*;
import mindustry.type.*;

import static arc.math.Mathf.sqrt;
import static fire.content.FUnitTypes.*;
import static mindustry.content.UnitTypes.*;

public class FWaves{
    public static final int waveVersion = 7;

    private Seq<SpawnGroup> spawns;

    public static Seq<SpawnGroup> generate(float difficulty){
        //apply power curve to make starting sectors easier
        return generate(Mathf.pow(difficulty, 1.12f), new Rand(), false);
    }

    public static Seq<SpawnGroup> generate(float difficulty, Rand rand, boolean attack){
        return generate(difficulty, rand, attack, false);
    }

    public static Seq<SpawnGroup> generate(float difficulty, Rand rand, boolean attack, boolean airOnly){
        UnitType[][] species = {
                {dagger, mace, fortress, scepter, reign},
                {nova, pulsar, quasar, vela, corvus},
                {crawler, atrax, spiroct, arkyid, toxopid},
                {flare, horizon, zenith, rand.chance(0.5) ? quad : antumbra, rand.chance(0.1) ? quad : eclipse},
                {flare, horizon, omicron, rand.chance(0.5) ? quad : pioneer, eclipse},
                {guarding, resisting, garrison, shelter, blessing},
                {firefly, candlelight, lampflame,pioneer,eclipse}
        };//sorry but there are no lakes in Risetar:(


        if(airOnly){
            species = Structs.filter(UnitType[].class, species, v -> v[0].flying);
        }

        //required progression:
        //- extra periodic patterns

        Seq<SpawnGroup> out = new Seq<>();

        //max reasonable wave, after which everything gets boring
        int cap = 225;

        float[] scaling = {1, 1.6f, 2f, 3.1f, 4.4f};

        UnitType[][] finalSpecies = species;
        Intc createProgression = start -> {
            //main sequence
            int Tier = 0;
            int Chosentype;

            for(int i = start; i < cap;){
                float f = (float)i;

                if(f<10){
                    Tier=rand.random(10)>4?1:2;
                } else if (f<25) {
                    Tier=rand.random(10)>5?2:3;
                }else if (f<40) {
                    Tier=rand.random(10)>6?3:4;
                }else if (f<60) {
                    Tier=rand.random(10)>3?rand.random(5)>3?3:4:5;
                }else if (f<100) {
                    Tier=rand.random(5)>3?4:5;
                }else if(f<150){
                    Tier=rand.random(5)>2?4:5;
                }else{
                    Tier=5;
                }
                Tier=Tier-1;
                //basicEnemies
                int counts= rand.random(6);
                StatusEffect ChosenEffect = null;
                if(f<=15){
                    ChosenEffect = null;
                } else if(f>15&&f<30){
                    ChosenEffect=rand.random(10)<6?StatusEffects.overclock:StatusEffects.overdrive;
                } else if (f<=70) {
                    ChosenEffect=rand.random(10)<7?rand.random(4)<=2?StatusEffects.overdrive:StatusEffects.overclock:StatusEffects.shielded;
                } else if(f<=140){
                    ChosenEffect=rand.random(3)<=2?rand.random(2)==1?StatusEffects.shielded:StatusEffects.fast: FStatusEffects.inspired;
                } else {
                    ChosenEffect=rand.random(2)<=1?StatusEffects.shielded:FStatusEffects.inspired;
                }
                for(int j = 0;j < counts;j++){
                    Chosentype =rand.random(6);
                    StatusEffect finalChosenEffect = ChosenEffect;
                    int finalTier = Tier;
                    int finalJ = j;
                    if(Tier>=1){
                        out.add(new SpawnGroup(finalSpecies[Chosentype][Math.min(finalTier, 5)-1]){{
                            unitAmount = (int)(6+sqrt(f/10+difficulty)) / (int)scaling[finalTier]*2;
                            begin = (int)(f+ finalJ);
                            end = (int)(f + f >= cap ? never : 10+f*2);
                            max = unitAmount*8;
                            unitScaling = (difficulty < 0.4f ? rand.random(2.5f, 5f) : rand.random(1f, 4f)) * scaling[finalTier]*2;
                            shieldScaling = (sqrt(f*f*f)/2+8+difficulty*10)*counts;
                            shields = shieldScaling*5;
                            spacing = counts;
                            effect= finalChosenEffect;
                        }});
                    }
                    out.add(new SpawnGroup(finalSpecies[Chosentype][Math.min(Tier, 5)]){{
                        unitAmount = (int)(6+sqrt(f/10+difficulty)) / (int)scaling[finalTier];
                        begin = (int)(f+finalJ);
                        end = (int)(f + f >= cap ? never : 10+f*2);
                        max = unitAmount*4;
                        unitScaling = (difficulty < 0.4f ? rand.random(2.5f, 5f) : rand.random(1f, 4f)) * scaling[finalTier];
                        shieldScaling = (sqrt(f*f*f)+15+difficulty*20)*counts;
                        shields = shieldScaling*3;
                        spacing = counts;
                        effect= finalChosenEffect;
                    }});
                }
                i += (int) (rand.random(10, 18+counts) - difficulty*0.5 + Tier * 5) +counts+5;
            }
        };

        createProgression.get(0);

        int step = 5 + rand.random(5);

        while(step <= cap){
            createProgression.get(step);
            step += (int)(rand.random(15, 30) * Mathf.lerp(1f, 0.5f, difficulty));
        }

        return out;
    }
}
