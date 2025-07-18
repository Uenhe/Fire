package fire.entities.abilities;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import fire.type.FleshUnitType;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

import static fire.content.FRStatusEffects.*;
import static mindustry.Vars.tilesize;
import static mindustry.content.StatusEffects.*;

public class DebuffRemoveFieldAbility extends mindustry.entities.abilities.Ability{

    private static final StatusEffect[] DEBUFFS = {
        burning, freezing, unmoving, slow, wet, muddy, melting, sapped, electrified,
        sporeSlowed, tarred, shocked, blasted, corroded, disarmed,
        frostbite, overgrown, disintegrated, magnetized
    };

    public final float range;
    public final float reload;
    public final Effect removeEffect;

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
            timer -= reload;
            Units.nearby(unit.team, unit.x, unit.y, range, u -> {
                if(removeDebuff(u)) removeEffect.at(u);
            });
        }
    }

    public static boolean removeDebuff(Unit unit){
        boolean any = false;
        for(var fx : DEBUFFS){
            if(!unit.hasEffect(fx) || (unit.type instanceof FleshUnitType && (fx == wet || fx == overgrown))) continue;
            unit.unapply(fx);
            any = true;
        }
        return any;
    }
}
