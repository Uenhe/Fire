package fire.world.blocks.defense;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
<<<<<<< Updated upstream:src/fire/world/blocks/defense/UnitOverdriveProjector.java
import arc.scene.style.TextureRegionDrawable;
import arc.util.Scaling;
import fire.world.meta.FireStat;
import mindustry.content.Fx;
=======
import fire.world.consumers.ConsumeItemEachFlammable;
import fire.world.meta.FStat;
>>>>>>> Stashed changes:src/fire/world/blocks/defense/Campfire.java
import mindustry.content.StatusEffects;
import mindustry.type.StatusEffect;
import mindustry.ui.Styles;

import static fire.FireLib.consValid;
import static mindustry.Vars.ui;

<<<<<<< Updated upstream:src/fire/world/blocks/defense/UnitOverdriveProjector.java
public class UnitOverdriveProjector extends mindustry.world.blocks.defense.OverdriveProjector{
    public StatusEffect
        allyStatus = StatusEffects.overclock,
        enemyStatus = StatusEffects.sapped;
    public float statusDuration = 60f;
    public float updateEffectChance = 0.04f;
    public Effect updateEffect = Fx.none;

    public UnitOverdriveProjector(String name){
        super(name);
=======
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
>>>>>>> Stashed changes:src/fire/world/blocks/defense/Campfire.java
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FireStat.statusEffectApplied, table -> {

            table.row();
            for(var s : new mindustry.type.StatusEffect[]{allyStatus, enemyStatus}){

                table.table(Styles.grayPanel, t -> {

                    t.left().button(new TextureRegionDrawable(s.uiIcon), Styles.emptyi, 40f, () -> ui.content.show(s)).size(40f).pad(10f).scaling(Scaling.fit);
                    t.left().table(info -> {

                        var detail =
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

<<<<<<< Updated upstream:src/fire/world/blocks/defense/UnitOverdriveProjector.java
    public class UnitOverdriveBuild extends OverdriveBuild{
=======
    public class CampfireBuild extends OverdriveBuild{

        /** Only affects {@code optionalEfficiency}. */
        @Override
        public void updateEfficiencyMultiplier(){
            optionalEfficiency *= consumeBuilder.find(c -> c instanceof ConsumeItemEachFlammable).efficiencyMultiplier(this);
        }

        public float realRange(){
            return range + phaseHeat * phaseRangeBoost;
        }

>>>>>>> Stashed changes:src/fire/world/blocks/defense/Campfire.java
        @Override
        public void updateTile(){
            super.updateTile();

<<<<<<< Updated upstream:src/fire/world/blocks/defense/UnitOverdriveProjector.java
            if(consValid(this)){
                Units.nearby(null, x, y, range, unit ->
                    unit.apply(unit.team == this.team ? allyStatus : enemyStatus, statusDuration));
=======
            if(efficiency > 0.0f){

                mindustry.entities.Units.nearby(null, x, y, realRange(), unit ->
                    unit.apply(unit.team == team ? allyStatus : enemyStatus, reload));
>>>>>>> Stashed changes:src/fire/world/blocks/defense/Campfire.java

                if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4f));
                }
            }
        }

        @Override
        public void draw(){
            super.draw();
<<<<<<< Updated upstream:src/fire/world/blocks/defense/UnitOverdriveProjector.java

            if(Core.settings.getBool("showBlockRange")){
                Draw.color(consValid(this) ? Pal.redLight : Color.black, 1f);
                Lines.stroke(1.5f);
                Lines.circle(x, y, range);
                Draw.alpha(0.2f);
                Fill.circle(x, y, range);
            }
=======
            if(!arc.Core.settings.getBool("showBlockRange")) return;

            Draw.color(efficiency > 0.0f ? mindustry.graphics.Pal.redLight : arc.graphics.Color.black, 1.0f);
            Lines.stroke(1.5f);
            Lines.circle(x, y, realRange());
            Draw.alpha(0.2f);
            Fill.circle(x, y, realRange());

            Draw.reset();
>>>>>>> Stashed changes:src/fire/world/blocks/defense/Campfire.java
        }
    }
}
