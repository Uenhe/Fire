package fire.entities.abilities;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Unit;

import static fire.content.FRStatusEffects.disintegrated;
import static fire.content.FRStatusEffects.frostbite;
import static fire.content.FRStatusEffects.overgrown;
import static mindustry.Vars.tilesize;
import static mindustry.content.StatusEffects.*;

public class DebuffRemoveFieldAbility extends mindustry.entities.abilities.Ability{

    /*
    private static final Seq<StatusEffect> BUFFS = Seq.with(
        fast, overdrive, overclock, shielded, boss, invincible,
        inspired
    );
     */

    public static final Seq<mindustry.type.StatusEffect> DE_BUFFS = Seq.with(
        burning, freezing, unmoving, slow, wet, muddy, melting, sapped,
        electrified, sporeSlowed, tarred, shocked, blasted, corroded, disarmed,
        frostbite, overgrown, disintegrated
    );

    public float range;
    public float reload;
    public Effect removeEffect;

    private float timer;

    public DebuffRemoveFieldAbility(float range, float reload, Effect effect){
        this.range = range;
        this.reload = reload;
        this.removeEffect = effect;
    }

    @Override
    public String getBundle(){
        return "ability.fire-debuffremovefield";
    }

    @Override
    public void addStats(Table t){
        super.addStats(t);
        t.add(Core.bundle.format("bullet.range", Strings.autoFixed(range / tilesize, 2))).row();
        t.add(abilityStat("cooldown", Strings.autoFixed(reload / 60.0f, 2)));
    }

    @Override
    public void update(Unit unit){
        if((timer += Time.delta) >= reload){
            timer %= reload;

            boolean[] any = new boolean[]{false};
            Units.nearby(unit.team, unit.x, unit.y, range, u -> {

                for(var e : DE_BUFFS){
                    if(!u.hasEffect(e)) continue;

                    if(!(u.type instanceof fire.type.FleshUnitType && e == overgrown)){
                        u.unapply(e);
                        any[0] = true;
                    }
                }
            });

            if(any[0]) removeEffect.at(unit);
        }
    }

    @Override
    public void death(Unit unit){
        range = reload = timer = 0.0f;
        removeEffect = null;
    }
}
