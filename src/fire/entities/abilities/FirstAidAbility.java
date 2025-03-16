package fire.entities.abilities;

import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.IntMap;
import arc.util.Strings;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.meta.StatUnit;

import java.util.Arrays;

public class FirstAidAbility extends mindustry.entities.abilities.Ability{

    /** Ticks between two triggers. */
    public short cooldown;
    /** Triggers if the unit loses such health percentage in detectDuration; 50 => 50%. */
    public byte healthLossPercentage;
    /** Amount to heal at trigger. */
    public int healAmount;
    /** Amount to heal at trigger. Do extra heal depend on unit's max health; 50 => 50%. */
    public byte healPercentage;
    /** Status effect applied at trigger. */
    public StatusEffect status;
    /** Status effect duration. */
    public short statusDuration;
    public Effect effect;

    private byte healthSize;
    private float detectTimer;
    private float cooldownTimer;
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
    public void update(Unit unit){
        var health = healthMap.get(unit.id, new float[healthSize]);
        if(health[0] == 0.0f) Arrays.fill(health, unit.health);

        if(cooldownTimer == 0.0f){
            detectTimer += Time.delta;

            if(detectTimer >= detectInterval){
                detectTimer -= detectInterval;

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
            cooldownTimer = Mathf.FLOAT_ROUNDING_ERROR;

        }else if(cooldownTimer >= cooldown){
            cooldownTimer = 0.0f;

        }else{
            cooldownTimer += Time.delta;
        }
    }

    @Override
    public void death(Unit unit){
        healAmount = cooldown = statusDuration = healthLossPercentage = healPercentage = healthSize = 0;
        detectTimer = cooldownTimer = 0.0f;
        status = null;
        effect = null;
    }
}
