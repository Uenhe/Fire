package fire.world.blocks.defense;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import fire.world.consumers.ConsumeItemEachFlammable;
import fire.world.meta.FStat;
import mindustry.content.StatusEffects;
import mindustry.type.StatusEffect;
import mindustry.ui.Styles;

import static mindustry.Vars.ui;

/** Collocates with {@link fire.world.consumers.ConsumeItemEachFlammable}..? */
public class Campfire extends mindustry.world.blocks.defense.OverdriveProjector{

    public StatusEffect
        allyStatus = StatusEffects.overclock,
        enemyStatus = StatusEffects.sapped;
    public float updateEffectChance;
    public mindustry.entities.Effect updateEffect = mindustry.content.Fx.none;

    public Campfire(String name){
        super(name);
        buildType = CampfireBuild::new;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FStat.statusEffectApplied, table -> {

            table.row();
            for(var s : new mindustry.type.StatusEffect[]{allyStatus, enemyStatus}){

                table.table(Styles.grayPanel, t -> {

                    t.left().button(new arc.scene.style.TextureRegionDrawable(s.uiIcon), Styles.emptyi, 40.0f, () -> ui.content.show(s)).size(40.0f).pad(10.0f).scaling(arc.util.Scaling.fit);
                    t.left().table(info -> {

                        var detail =
                            s == allyStatus
                            ? FStat.allyStatusEffect.localized()
                            : FStat.enemyStatusEffect.localized();

                        info.left().add("[accent]" + detail).left();
                        info.row();
                        info.left().add(s.localizedName).color(s.color).left();
                    });
                }).growX().pad(5.0f);
                table.row();
            }
        });
    }

    public class CampfireBuild extends OverdriveBuild{

        /** Only affects {@code optionalEfficiency}. */
        @Override
        public void updateEfficiencyMultiplier(){
            optionalEfficiency *= consumeBuilder.find(c -> c instanceof ConsumeItemEachFlammable).efficiencyMultiplier(this);
        }

        public float realRange(){
            return range + phaseHeat * phaseRangeBoost;
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(efficiency > 0.0f){

                mindustry.entities.Units.nearby(null, x, y, realRange(), unit ->
                    unit.apply(unit.team == team ? allyStatus : enemyStatus, reload));

                if(wasVisible && Mathf.chanceDelta(updateEffectChance))
                    updateEffect.at(x + Mathf.range(size * 4.0f), y + Mathf.range(size * 4.0f));
            }
        }

        @Override
        public void draw(){
            super.draw();
            if(!arc.Core.settings.getBool("showBlockRange")) return;

            Draw.color(efficiency > 0.0f ? mindustry.graphics.Pal.redLight : arc.graphics.Color.black, 1.0f);
            Lines.stroke(1.5f);
            Lines.circle(x, y, realRange());
            Draw.alpha(0.2f);
            Fill.circle(x, y, realRange());

            Draw.reset();
        }
    }
}
