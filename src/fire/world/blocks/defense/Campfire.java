package fire.world.blocks.defense;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.util.Scaling;
import fire.world.consumers.ConsumeItemFlammableEach;
import fire.world.consumers.ConsumePowerCustom;
import fire.world.meta.FStat;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.ui.Styles;

import static mindustry.Vars.ui;

/** Only collocates with {@link ConsumeItemFlammableEach}..? */
public class Campfire extends mindustry.world.blocks.defense.OverdriveProjector{

    public StatusEffect
        allyStatus = StatusEffects.overclock,
        enemyStatus = StatusEffects.sapped;
    public float updateEffectChance;
    public Effect updateEffect = Fx.none;

    public Campfire(String name){
        super(name);
        buildType = CampfireBuild::new;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FStat.statusEffectApplied, table -> {

            table.row();
            for(var s : new StatusEffect[]{allyStatus, enemyStatus}){

                table.table(Styles.grayPanel, t -> {

                    t.left().button(new TextureRegionDrawable(s.uiIcon), Styles.emptyi, 40.0f, () -> ui.content.show(s)).size(40.0f).pad(10.0f).scaling(Scaling.fit);
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

        /** Now only affects {@code optionalEfficiency}. */
        @Override
        public void updateEfficiencyMultiplier(){
            optionalEfficiency *= getEfficiency();
        }

        @Override
        public float range(){
            return range + phaseHeat * phaseRangeBoost;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            ((ConsumePowerCustom)consPower).scale = getEfficiency() + 1.0f;
            if(efficiency <= 0.0f) return;

            Units.nearby(null, x, y, range(), unit ->
                unit.apply(unit.team == team ? allyStatus : enemyStatus, reload));

            if(wasVisible && Mathf.chanceDelta(updateEffectChance))
                updateEffect.at(x + Mathf.range(size * 4.0f), y + Mathf.range(size * 4.0f));
        }

        @Override
        public void draw(){
            super.draw();
            if(!Core.settings.getBool("showBlockRange")) return;

            Draw.color(efficiency > 0.0f ? Pal.redLight : Color.black, 0.8f);
            Lines.stroke(1.2f);
            Lines.circle(x, y, range());
            Draw.alpha(0.15f);
            Fill.circle(x, y, range());

            Draw.reset();
        }

        float getEfficiency(){
            return consumeBuilder.find(c -> c instanceof ConsumeItemFlammableEach).efficiencyMultiplier(this);
        }
    }
}
