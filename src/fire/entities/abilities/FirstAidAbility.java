package fire.entities.abilities;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.IntMap;
import arc.util.Strings;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import java.util.Arrays;

public class FirstAidAbility extends mindustry.entities.abilities.Ability{

    /** Ticks between two triggers. */
    public final short cooldown;
    /** Triggers if the unit loses such health percentage in detectDuration; 50 => 50%. */
    public final byte healthLossPercentage;
    /** Amount to heal at trigger. */
    public final int healAmount;
    /** Amount to heal at trigger. Do extra heal depend on unit's max health; 50 => 50%. */
    public final byte healPercentage;
    /** Status effect applied at trigger. */
    public final StatusEffect status;
    /** Status effect duration. */
    public final short statusDuration;
    public final Effect effect;

    private final byte healthSize;
    private float timerDetect;
    private float timerCooldown;
    /** Frames between two detections. */
    private static final byte detectInterval = 5;
    /** Used to record health; Why I have to use a map or buggy????????????? */
    private static final IntMap<float[]> healthMap = new IntMap<>();

    public FirstAidAbility(int cd, int lossP, int healA, int healP, StatusEffect se, int sed, int detectDuration, Effect fx){
        cooldown = (short)cd;
        healthLossPercentage = (byte)lossP;
        healAmount = healA;
        healPercentage = (byte)healP;
        status = se;
        statusDuration = (short)sed;
        effect = fx;

        healthSize = (byte)(detectDuration / detectInterval);
    }

    @Override
    public String localized(){
        return Core.bundle.get("ability.fire-firstaid");
    }

    @Override
    public void addStats(Table t){
        t.add("[lightgray]" + Stat.cooldownTime.localized() + ": [white]" + Strings.autoFixed(cooldown / 60.0f, 2) + StatUnit.seconds.localized());
        t.row();
        t.add("[lightgray]" + Stat.healing.localized() + ": [white]" + healAmount);
        t.row();
        if(healPercentage > 0){
            t.add("[lightgray]" + Stat.healing.localized() + ": [white]" + healPercentage + StatUnit.percent.localized());
            t.row();
        }
        t.row();
        t.add(status.emoji() + " [accent]" + status.localizedName + "[white], " + Strings.autoFixed(statusDuration / 60.0f, 2) + StatUnit.seconds.localized());
    }

    @Override
    public void update(Unit unit){
        var health = healthMap.get(unit.id, new float[healthSize]);
        if(health[0] == 0.0f) Arrays.fill(health, unit.health);

        if(timerCooldown == 0.0f){
            timerDetect += Time.delta;

            if(timerDetect >= detectInterval){
                timerDetect -= detectInterval;

                for(byte i = 0; i < health.length - 1; i++)
                    health[i] = health[i + 1];

                health[health.length - 1] = unit.health;
                healthMap.put(unit.id, health);
            }

            if(health[0] - unit.health < unit.maxHealth * healthLossPercentage / 100.0f) return;

            unit.heal(healAmount + unit.maxHealth * healPercentage / 100.0f);
            unit.apply(status, statusDuration);
            effect.at(unit);

            // enter cooldown
            timerCooldown = Mathf.FLOAT_ROUNDING_ERROR;

        }else if(timerCooldown >= cooldown){
            timerCooldown = 0.0f;

        }else{
            timerCooldown += Time.delta;
        }
    }
}
