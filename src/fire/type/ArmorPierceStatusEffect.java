package fire.type;

import fire.world.meta.FireStat;
import mindustry.gen.Unit;

public class ArmorPierceStatusEffect extends mindustry.type.StatusEffect{
    /** Armor unit reduces at most. */
    public float maxArmorReduction = 5f;

    public ArmorPierceStatusEffect(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FireStat.armorPierce, maxArmorReduction);
    }

    @Override
    public void update(Unit unit, float time){
        super.update(unit, time);

        float min = unit.type.armor - maxArmorReduction;
        if(time >= 10f){

            //linearly reduce armor in 1s
            unit.armor = Math.max(unit.armor - maxArmorReduction / 60f, min);
        }else{

            //put armor back when status almost ends
            unit.armor = unit.type.armor;
        }
    }
}
