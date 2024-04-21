package fire.entities.abilities;

import arc.struct.Seq;
import mindustry.entities.Effect;

import static fire.content.FireStatusEffects.*;
import static mindustry.content.StatusEffects.*;

public class DebuffRemoveFieldAbility extends mindustry.entities.abilities.Ability{

    /*
    private static final Seq<StatusEffect> BUFFS = Seq.with(
        fast, overdrive, overclock, shielded, boss, invincible,
        inspired
    );
     */

    private static final Seq<mindustry.type.StatusEffect> DE_BUFFS = Seq.with(
        burning, freezing, unmoving, slow, wet, muddy, melting, sapped,
        electrified, sporeSlowed, tarred, shocked, blasted, corroded, disarmed,
        frostbite, overgrown, disintegrated
    );

    private final float range;
    private final float reload;
    private final Effect removeEffect;

    private float timer;

    public DebuffRemoveFieldAbility(float range, float reload, Effect effect){
        this.range = range;
        this.reload = reload;
        this.removeEffect = effect;
    }

    @Override
    public void update(mindustry.gen.Unit unit){
        if((timer += arc.util.Time.delta) >= reload){
            timer %= reload;

            final var any = new boolean[]{false};
            mindustry.entities.Units.nearby(unit.team, unit.x, unit.y, range, u -> {

                for(final var e : DE_BUFFS){
                    if(!u.hasEffect(e)) continue; //why not { if(u.hasEffect(e)) } ????

                    if(!(u.type instanceof fire.type.FleshUnitType && e == overgrown)){
                        u.unapply(e);
                        any[0] = true;
                    }
                }
            });

            if(any[0]) removeEffect.at(unit);
        }
    }
}
