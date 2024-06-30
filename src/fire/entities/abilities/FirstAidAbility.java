package fire.entities.abilities;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.struct.FloatSeq;
import arc.util.Strings;
import arc.util.Time;
import fire.world.meta.FStat;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

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
        t.add(effect.emoji() + " [accent]" + effect.localizedName + "[white], " + Strings.autoFixed(effectDuration / 60.0f, 2) + StatUnit.seconds.localized());
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
