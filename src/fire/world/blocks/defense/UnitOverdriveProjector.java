package fire.world.blocks.defense;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import fire.world.meta.FireStat;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.OverdriveProjector;

import static fire.FireLib.*;

public class UnitOverdriveProjector extends OverdriveProjector{
    public StatusEffect
        allyStatus = StatusEffects.overclock,
        enemyStatus = StatusEffects.sapped;
    public float statusDuration = 60f;
    public float updateEffectChance = 0.04f;
    public Effect updateEffect = Fx.none;

    public UnitOverdriveProjector(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FireStat.allyStatusEffect, allyStatus.emoji() + allyStatus);
        stats.add(FireStat.enemyStatusEffect, enemyStatus.emoji() + enemyStatus);
    }

    public class UnitOverdriveBuild extends OverdriveBuild{
        @Override
        public void updateTile(){
            super.updateTile();
            if(consValid(this)){
                Units.nearby(null, x, y, range, unit -> unit.apply(unit.team == this.team ? allyStatus : enemyStatus, statusDuration));
                //Units.nearbyEnemies(team, x - range, y - range, range * 2f, range * 2f, u -> {
                //    if(u.within(x, y, range)) u.apply(enemyStatus, 60f);
                //});
                if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                }
            }
        }

        @Override
        public void draw(){
            super.draw();
            if(getSetting("showBlockRange")){
                Draw.color(consValid(this) ? Pal.redLight : Color.black, 1f);
                Lines.stroke(1.5f);
                Lines.circle(x, y, range);
                Draw.alpha(0.2f);
                Fill.circle(x, y, range);
            }
        }
    }
}
