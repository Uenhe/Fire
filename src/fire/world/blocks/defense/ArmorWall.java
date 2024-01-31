package fire.world.blocks.defense;

import arc.Core;
import arc.math.Interp;
import arc.math.Mathf;
import fire.world.meta.FireStat;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;

import static mindustry.Vars.*;

public class ArmorWall extends mindustry.world.blocks.defense.Wall{
    /** Armor increases in total. */
    public float armorIncrease = 10f;
    /** The armor will no longer increase if {@code current health percentage} < {@code 1 - this} */
    public float maxHealthLose = 0.5f;
    public Interp increasePattern = Interp.linear;

    public ArmorWall(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FireStat.maxArmorIncrease, armorIncrease);
    }

    @Override
    public void setBars(){
        super.setBars();
        float max = armor + armorIncrease;
        addBar("currentarmor", (ArmorWallBuild build) -> new Bar(
            () -> Core.bundle.format("bar.currentarmor", (int)(armor + build.extraArmor), (int)max),
            () -> Pal.accent,
            () -> (armor + build.extraArmor) / max
        ));
    }

    public class ArmorWallBuild extends WallBuild{
        public float extraArmor;

        @Override
        public float handleDamage(float damage){
            float healthMul = state.rules.blockHealth(team);
            if(Mathf.zero(healthMul)){
                return health + 1f;
            }

            float dmg = (damage - extraArmor) / healthMul;
            if(dmg < 1f) dmg = damage * minArmorDamage;
            return dmg;
        }

        @Override
        public void draw(){
            super.draw();
            //might be a hacky way to update extraArmor without updateTile()
            extraArmor = armorIncrease * increasePattern.apply(Math.min((1f - health / maxHealth) / maxHealthLose, 1f));
        }
    }
}
