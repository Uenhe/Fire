package fire.type;

import fire.world.meta.FireStat;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class ArmorPierceStatusEffect extends StatusEffect{
    /** Armor that unit reduces at most. */
    public float maxArmorReduce = 5f;

    public ArmorPierceStatusEffect(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FireStat.armorPierce, maxArmorReduce);
    }

    @Override
    public void update(Unit unit, float time){
        super.update(unit, time);
        float min = unit.type.armor - maxArmorReduce;
        if(time >= 10f){
            //linearly reduce armor in 1s
            unit.armor = Math.max(unit.armor - maxArmorReduce / 60f, min);
        }else{
            //put armor back when status almost ends
            unit.armor = unit.type.armor;
        }
    }
}
