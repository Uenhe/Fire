package fire.world.kits;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.util.Scaling;
import fire.world.consumers.ConsumePowerCustom;
import fire.world.draw.DrawArrows;
import fire.world.meta.FRStat;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

import static fire.FRVars.displayRange;
import static mindustry.Vars.*;

public class Campfire{

    public static class CampfireBlock extends mindustry.world.blocks.defense.OverdriveProjector{

        public StatusEffect
            allyStatus = StatusEffects.overclock,
            enemyStatus = StatusEffects.sapped;
        public float statusDuration;
        public float updateEffectChance;
        public Effect updateEffect = Fx.none;
        public DrawArrows drawArrows;
        public final float maxBoost = 3.38f; //hardcoded;

        public CampfireBlock(String name){
            super(name);
            buildType = CampfireBuild::new;
        }

        @Override
        public void load(){
            super.load();
            if(drawArrows != null) drawArrows.load(this);
        }

        @Override
        public void setStats(){
            super.setStats();
            stats.add(FRStat.statusEffectApplied, table -> {
                table.row();

                for(byte i = 0; i < 2; i++){
                    var s = i == 0 ? allyStatus : enemyStatus;

                    table.table(Styles.grayPanel, t -> {
                        t.left().button(new TextureRegionDrawable(s.uiIcon), Styles.emptyi, 40.0f, () -> ui.content.show(s)).size(40.0f).pad(10.0f).scaling(Scaling.fit);
                        t.left().table(info -> {
                            String detail = s == allyStatus
                            ? FRStat.allyStatusEffect.localized()
                            : FRStat.enemyStatusEffect.localized();

                            info.left().add("[accent]" + detail).left();
                            info.row();
                            info.left().add(s.localizedName).color(s.color).left();
                        });
                    }).growX().pad(5.0f).row();
                }
            });
        }

        public class CampfireBuild extends OverdriveBuild implements fire.world.draw.DrawArrows.SmoothCrafter, ConsumeCampfire.CampfireConsume, ConsumePowerCustom.CustomPowerConsumer{

            private float smoothProgress, smoothOffset;

            /** Now only affects {@code optionalEfficiency}. */
            @Override
            public void updateEfficiencyMultiplier(){
                if(cheating())
                    optionalEfficiency = (maxBoost - speedBoost + 1.0f) / speedBoostPhase;
                else
                    optionalEfficiency *= items.sum(((item, amount) -> item.flammability));
            }

            @Override
            public float range(){
                return range + phaseHeat * phaseRangeBoost;
            }

            @Override
            public float smoothProgress(){
                return Mathf.clamp(smoothProgress + smoothOffset);
            }

            @Override
            public float boostScale(){
                return phaseHeat;
            }

            @Override
            public float consPowerScale(){
                return phaseHeat + 1.0f;
            }

            @Override
            public void updateTile(){
                super.updateTile();

                if(Mathf.equal(phaseHeat, 0.0f, 0.001f))
                    phaseHeat = 0.0f;
                else if(Mathf.equal(phaseHeat, optionalEfficiency, 0.001f))
                    phaseHeat = optionalEfficiency;

                smoothProgress = Mathf.lerpDelta(smoothProgress, (realBoost() - 1.0f) / maxBoost, 0.1f);
                smoothOffset = Mathf.sin(totalProgress(), 12.0f, 0.3f);

                if(efficiency <= 0.0f) return;

                Units.nearby(null, x, y, range(), unit ->
                    unit.apply(unit.team == team ? allyStatus : enemyStatus, statusDuration));

                if(wasVisible && Mathf.chanceDelta(updateEffectChance))
                    updateEffect.at(x + Mathf.range(size * tilesize / 2), y + Mathf.range(size * tilesize / 2));
            }

            @Override
            public void draw(){
                super.draw();

                if(drawArrows != null) drawArrows.draw(this);

                if(!displayRange) return;
                Draw.color(efficiency > 0.0f ? Pal.redLight : Color.black, 0.8f);
                Lines.stroke(1.2f);
                Lines.circle(x, y, range());
                Draw.alpha(0.15f);
                Fill.circle(x, y, range());
                Draw.reset();
            }
        }
    }

    /** Accepts every item, but only consume those which has flammability.
     * @implSpec Implement {@link CampfireConsume} in building class. */
    public static class ConsumeCampfire extends mindustry.world.consumers.ConsumeItemFilter{

        private final CampfireBlock block;

        public ConsumeCampfire(CampfireBlock block){
            this.block = block;
            filter = i -> true; //accepts every item
            boost();
        }

        /** Only support consumes 1 item each time, currently. Multiple items need extra judgment. */
        @Override
        public void trigger(Building build){
            build.items.each((item, amount) -> {
                if(item.flammability > 0.0f)
                    build.items.remove(item, 1);
            });
        }

        @Override
        public float efficiencyMultiplier(Building build){
            assert build instanceof CampfireConsume;
            return ((CampfireConsume)build).boostScale();
        }

        @Override
        public void display(Stats stats){
            stats.remove(Stat.booster);
            stats.add(Stat.booster, c -> {
                c.row().table(Styles.grayPanel, t ->
                    t.row().left().add(
                        Core.bundle.format("stat.consumecampfire", block.speedBoostPhase * 100, block.phaseRangeBoost / tilesize)
                    ).growX().pad(5.0f));

                byte i = 0;
                for(var item : content.items()){
                    if(item.flammability <= 0.0f || item.isHidden()) continue;

                    c.row().table(Styles.grayPanel, t ->
                        t.table(info -> {
                            String icon = item.hasEmoji() ? item.emoji() + " " : item.minfo.mod.meta.displayName + "-";
                            info.left().add(icon + item.localizedName).left().row();
                            info.add(Core.bundle.format("stat.campfire", item.flammability * block.speedBoostPhase * 100, item.flammability * block.phaseRangeBoost / tilesize)).left();
                        }).grow()).growX().pad(5.0f);

                    if(++i % 2 == 0) c.row();
                }
            });
        }

        public interface CampfireConsume{
            float boostScale();
        }
    }
}
