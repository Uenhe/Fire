package fire.entities.abilities;

import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.meta.StatUnit;

import java.util.Arrays;

public class FirstAidAbility extends mindustry.entities.abilities.Ability{

    /** Ticks between two triggers. */
    public final short cooldown;
    /** Triggers if the unit is damaged the percentage in detectDuration; 50 -> 50% */
    public final byte healthLossPercentage;
    /** Amount to heal at trigger. */
    public final int healAmount;
    /** Percentage to heal at trigger. 50 -> 50% */
    public final byte healPercentage;
    /** Status effect applied at trigger. */
    public final StatusEffect status;
    /** Status effect duration. */
    public final short statusDuration;
    /** Effect created at trigger. */
    public final Effect effect;

    private float detectTimer;
    private float cooldownTimer;
    private float[] healthArray;

    /** Frames between two detections. */
    private static final byte detectInterval = 5;

    public FirstAidAbility(int cd, int lossPerc, int healAmount, int healPerc, StatusEffect sfx, int sfxDuration, int detectDuration, Effect fx){
        cooldown = (short)cd;
        healthLossPercentage = (byte)lossPerc;
        this.healAmount = healAmount;
        healPercentage = (byte)healPerc;
        status = sfx;
        statusDuration = (short)sfxDuration;
        effect = fx;
        healthArray = new float[(byte)(detectDuration / detectInterval)];
    }

    @Override
    public String getBundle(){
        return "ability.fire-firstaid";
    }

    @Override
    public void addStats(Table t){
        super.addStats(t);
        t.add(abilityStat("cooldown", Strings.autoFixed(cooldown / 60.0f, 2))).row();
        t.add(abilityStat("healing", healAmount, healPercentage)).row();
        t.add((status.hasEmoji() ? status.emoji() : "") + "[stat]" + status.localizedName + "[white], " + Strings.autoFixed(statusDuration / 60.0f, 2) + StatUnit.seconds.localized());
    }

    @Override
    public void created(Unit unit){
        Arrays.fill(healthArray, unit.health);
    }

    @Override
    public void update(Unit unit){
        if(!unit.isValid()) return;

        if(cooldownTimer == 0.0f){
            detectTimer += Time.delta;

            if(detectTimer >= detectInterval){
                for(byte i = 0, lenm1 = (byte)(healthArray.length - 1); i < lenm1; i++)
                    healthArray[i] = healthArray[i + 1];
                healthArray[healthArray.length - 1] = unit.health;

                detectTimer -= detectInterval;
            }

            if(healthArray[0] - unit.health < unit.maxHealth * healthLossPercentage * 0.01f) return;

            unit.heal(healAmount + unit.maxHealth * healPercentage * 0.01f);
            unit.apply(status, statusDuration);
            effect.at(unit);

            //enter cooldown
            cooldownTimer = Mathf.FLOAT_ROUNDING_ERROR;

        }else if(cooldownTimer >= cooldown){
            cooldownTimer = 0.0f;

        }else{
            cooldownTimer += Time.delta;
        }
    }

    private FirstAidAbility setHealthArray(float[] array){
        healthArray = array;
        return this;
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        return ((FirstAidAbility)super.clone()).setHealthArray(healthArray);
    }
}
