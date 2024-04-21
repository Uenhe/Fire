package fire.world.blocks.defense;

import arc.Core;
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
import mindustry.ui.Styles;

import static fire.FireLib.consValid;
import static mindustry.Vars.ui;

public class UnitOverdriveProjector extends mindustry.world.blocks.defense.OverdriveProjector{

    protected StatusEffect
        allyStatus = StatusEffects.overclock,
        enemyStatus = StatusEffects.sapped;
    protected float statusDuration = 60f;
    protected float updateEffectChance = 0.04f;
    protected Effect updateEffect = Fx.none;

    protected UnitOverdriveProjector(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FireStat.statusEffectApplied, table -> {

            table.row();
            for(final var s : new StatusEffect[]{allyStatus, enemyStatus}){

                table.table(Styles.grayPanel, t -> {

                    t.left().button(new arc.scene.style.TextureRegionDrawable(s.uiIcon), Styles.emptyi, 40f, () -> ui.content.show(s)).size(40f).pad(10f).scaling(arc.util.Scaling.fit);
                    t.left().table(info -> {

                        final var detail =
                            s == allyStatus
                            ? FireStat.allyStatusEffect.localized()
                            : FireStat.enemyStatusEffect.localized();

                        info.left().add("[accent]" + detail).left();
                        info.row();
                        info.left().add(s.localizedName).color(s.color).left();
                    });
                }).growX().pad(5f);
                table.row();
            }
        });
    }

    public class UnitOverdriveBuild extends OverdriveBuild{

        @Override
        public void updateTile(){
            super.updateTile();

            if(consValid(this)){
                Units.nearby(null, x, y, range, unit ->
                    unit.apply(unit.team == team ? allyStatus : enemyStatus, statusDuration));

                if(wasVisible && Mathf.chanceDelta(updateEffectChance))
                    updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4f));
            }
        }

        @Override
        public void draw(){
            super.draw();
            if(!Core.settings.getBool("showBlockRange")) return;

            Draw.color(consValid(this) ? Pal.redLight : Color.black, 1f);
            Lines.stroke(1.5f);
            Lines.circle(x, y, range);
            Draw.alpha(0.2f);
            Fill.circle(x, y, range);

            Draw.reset();
        }
    }
}
