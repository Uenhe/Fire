package fire.world.blocks.defense;

import arc.math.Interp;

import static mindustry.Vars.*;

public class ArmorWall extends mindustry.world.blocks.defense.Wall{

    /** Armor increases in total. */
    protected float armorIncrease = 10f;
    /** The armor will no longer increase if {@code current health percentage} < {@code 1 - this} */
    protected float maxHealthLose = 0.5f;
    protected Interp increasePattern = Interp.linear;

    protected ArmorWall(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(fire.world.meta.FireStat.maxArmorIncrease, armorIncrease);
    }

    @Override
    public void setBars(){
        super.setBars();

        final float max = armor + armorIncrease;
        addBar("currentarmor", (ArmorWallBuild build) -> new mindustry.ui.Bar(
            () -> arc.Core.bundle.format("bar.currentarmor", (int)(armor + build.extraArmor), (int)max),
            () -> mindustry.graphics.Pal.accent,
            () -> (armor + build.extraArmor) / max
        ));
    }

    public class ArmorWallBuild extends WallBuild{

        private float extraArmor;

        @Override
        public float handleDamage(float damage){

            final float healthMul = state.rules.blockHealth(team);
            if(arc.math.Mathf.zero(healthMul)){
                return health + 1f;
            }

            final float dmg = (damage - extraArmor) / healthMul;
            if(dmg < 1f) return damage * minArmorDamage;
            return dmg;
        }

        /** Might be a hacky way to update {@code extraArmor} without {@code updateTile()}... */
        @Override
        public void draw(){
            super.draw();
            extraArmor = armorIncrease * increasePattern.apply(Math.min((1f - health / maxHealth) / maxHealthLose, 1f));
        }
    }
}
