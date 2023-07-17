package fire.type;

import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import fire.world.meta.FireStatUnit;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;

public class NeoplasmStatusEffect extends StatusEffect{
    /** Health Multiplier for non-neoplasm-about unit. For neoplasm-about unit, use *healthMultiplier*. */
    public float unfHealthMultiplier = 0.75f;
    /** Health and Shield that unit regen **Per Frame**. */
    public float regenPercent = 0.001f;
    /** Shield that unit can regen at most. */
    public float maxShield = 250f;

    public NeoplasmStatusEffect(String name){
        super(name);
    }

    @Override
    public void setStats(){
        stats.addPercent(Stat.healthMultiplier, unfHealthMultiplier);
        stats.add(Stat.healing, regenPercent * 100f * 60f, FireStatUnit.percentPerSec);
        super.setStats();
    }

    @Override
    public void update(Unit unit, float time){
        if(unit.type.healColor == Pal.neoplasm1){
            //buff neoplasm-about unit
            unit.healthMultiplier *= healthMultiplier;
            unit.heal(regenPercent * Time.delta * unit.maxHealth);
            if(unit.shield < maxShield) unit.shield = Math.min(unit.shield + regenPercent * Time.delta * maxShield, maxShield);
        }else{
            //nerf otherwise
            unit.damageContinuousPierce(damage);
            unit.healthMultiplier *= unfHealthMultiplier;
        }
        if(Mathf.chanceDelta(effectChance)){
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize / 2f));
            effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0f, color, parentizeEffect ? unit : null);
        }
    }
}
