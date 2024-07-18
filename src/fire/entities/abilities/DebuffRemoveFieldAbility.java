package fire.entities.abilities;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.entities.Effect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static fire.content.FStatusEffects.*;
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
    public String localized(){
        return arc.Core.bundle.get("ability.fire-debuffremovefield");
    }

    @Override
    public void addStats(Table t){
        t.add("[lightgray]" + Stat.shootRange.localized() + ": [white]" +  Strings.autoFixed(range / mindustry.Vars.tilesize, 2) + " " + StatUnit.blocks.localized());
        t.row();
        t.add("[lightgray]" + Stat.cooldownTime.localized() + ": [white]" + Strings.autoFixed(reload / 60f, 2) + " " + StatUnit.seconds.localized());
    }

    @Override
    public void update(mindustry.gen.Unit unit){
        if((timer += arc.util.Time.delta) >= reload){
            timer %= reload;

            boolean[] any = new boolean[]{false};
            mindustry.entities.Units.nearby(unit.team, unit.x, unit.y, range, u -> {

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
}
