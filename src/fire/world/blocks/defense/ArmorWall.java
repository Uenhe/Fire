package fire.world.blocks.defense;

import arc.math.Mathf;
import fire.world.meta.FireStat;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.Wall;

import static fire.FireLib.format;
import static mindustry.Vars.*;

public class ArmorWall extends Wall{
    /** Armor that wall increases when loses every 1% health. */
    public float armorIncrease = 0.1f;

    public ArmorWall(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FireStat.maxArmorIncrease, armorIncrease * 100f);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("armor", (ArmorWallBuild e) -> new Bar(
            () -> format("bar.armorincrease", (int)(armor + e.extraArmor), (int)(armor + armorIncrease * 100f)),
            () -> Pal.accent,
            () -> (armor + e.extraArmor) / (armor + armorIncrease * 100f)
        ));
    }

    public class ArmorWallBuild extends WallBuild{
        protected float extraArmor;

        @Override
        public float handleDamage(float damage){
            float healthMul = state.rules.blockHealth(team);
            if(Mathf.zero(healthMul)) return health + 1f;
            float dmg = (damage - extraArmor) / healthMul;
            if(dmg < 1f) dmg = damage * minArmorDamage;
            return dmg;
        }

        @Override
        public void draw(){
            super.draw();
            //might be a hacky way to update extraArmor without updateTile()
            extraArmor = armorIncrease * (1f - (health / maxHealth)) * 100f;
        }
    }
}
