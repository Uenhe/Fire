package fire.world.blocks.defense;

import arc.Core;
import arc.math.Interp;
import arc.math.Mathf;
import fire.world.meta.FRStat;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;

import static mindustry.Vars.minArmorDamage;
import static mindustry.Vars.state;

public class ArmorWall extends mindustry.world.blocks.defense.Wall{

    /** Armor increases in total. */
    protected byte armorIncrease;
    /** The armor will no longer increase if {@code current health percentage} < {@code 100 - this}; 50 => 50%. */
    protected byte maxHealthLossPercentage = 50;
    protected Interp increasePattern = Interp.linear;

    protected ArmorWall(String name){
        super(name);
        buildType = ArmorWallBuild::new;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FRStat.maxArmorIncrease, armorIncrease);
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

        private float extraArmor;

        @Override
        public float handleDamage(float damage){
            float healthMul = state.rules.blockHealth(team);
            if(Mathf.zero(healthMul))
                return health + 1.0f;

            float dmg = (damage - extraArmor) / healthMul;
            if(dmg < 1.0f)
                return damage * minArmorDamage;

            return dmg;
        }

        /** Might be a hacky way to update {@code extraArmor} without {@code updateTile()}... */
        @Override
        public void draw(){
            super.draw();
            extraArmor = armorIncrease * increasePattern.apply(Math.min((1.0f - health / maxHealth) / maxHealthLossPercentage * 100.0f, 1.0f));
        }
    }
}
