package fire.entities.abilities;

import arc.struct.FloatSeq;
import arc.util.Time;
import mindustry.type.StatusEffect;

public class FirstAidAbility extends mindustry.entities.abilities.Ability{

    /** Frames between two triggers. */
    private final short cooldown;
    /** Triggers if the unit loses such health in detectDuration; 50 => 50%. */
    private final byte healthLossPercentage;
    /** Amount to heal at trigger. */
    private final int healAmount;
    /** Amount to heal at trigger. Do extra heal depend on unit's max health; 50 => 50%. */
    private final byte healPercentage;
    /** Status effect applied at trigger. */
    private final StatusEffect effect;
    /** Status effect duration. */
    private final short effectDuration;

    /** Frames between two detections. */
    private static final byte detectInterval = 10;
    /** Used to record health. */
    private final FloatSeq healths;
    private float timer;
    private float cooldownTimer;

    public FirstAidAbility(int cooldown, int healthLossPercentage, int healAmount, int healPercentage, StatusEffect effect, int effectDuration, int detectDuration){
        this.cooldown = (short)cooldown;
        this.healthLossPercentage = (byte)healthLossPercentage;
        this.healAmount = healAmount;
        this.healPercentage = (byte)healPercentage;
        this.effect = effect;
        this.effectDuration = (short)effectDuration;

        healths = new FloatSeq(detectDuration / detectInterval);
    }

    @Override
    public void update(mindustry.gen.Unit unit){

        //init
        if(healths.isEmpty())
            for(byte i = 0; i < healths.items.length; i++)
                healths.add(unit.health);

        if(cooldownTimer == 0.0f){
            timer += Time.delta;

            if(timer >= detectInterval){
                timer %= detectInterval;

                healths.removeIndex(0);
                healths.add(unit.health);

                if((healths.get(0) - unit.health) >= unit.maxHealth * healthLossPercentage / 100.0f){

                    unit.heal(healAmount + healPercentage / 100.0f);
                    unit.apply(effect, effectDuration);

                    //enter cooldown
                    cooldownTimer = 0.001f;
                }
            }
        }else if(cooldownTimer >= cooldown){
            cooldownTimer = 0.0f;

        }else{
            cooldownTimer += Time.delta;
        }
    }
}
