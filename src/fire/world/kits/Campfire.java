package fire.world.kits;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.util.Scaling;
import arc.util.Tmp;
import fire.annotation.Modified;
import fire.content.FRStatusEffects;
import fire.world.DEBUG;
import fire.world.consumers.ConsumePowerCustom;
import fire.world.draw.DrawArrows;
import fire.world.meta.FRStat;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.StatusEffect;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

import static fire.FRVars.displayRange;
import static fire.FRVars.find;
import static fire.content.FRItems.timber;
import static mindustry.Vars.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;

public class Campfire{

    public static class CampfireBlock extends mindustry.world.blocks.defense.OverdriveProjector{

        public final StatusEffect allyStatus, enemyStatus;
        public final float statusDuration;
        public final float updateEffectChance;
        public final Effect updateEffect;
        public final Effect updateEffectSp = new Effect(48f, e -> {
            if(!(e.data instanceof Vec2 origin)) return;
            Draw.color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin());
            Angles.randLenVectors(e.id, 8, 8f + e.finpow() * 36f, Angles.angle(e.x - origin.x, e.y - origin.y), 35f, (x, y) ->
                Fill.circle(e.x + x, e.y + y, 0.6f + e.fout() * 3.0f));
        });
        public final DrawArrows drawArrows;

        final float maxBoost = 3.3625f; //hardcoded; equals to 'Vars.content.items().sumf(item => item.flammability) - 0.1'

        public CampfireBlock(){
            super("gh");
            buildType = CampfireBuild::new;

            requirements(Category.effect, with(
                copper, 300,
                metaglass, 220,
                plastanium, 175,
                timber, 200
            ));
            size = 5;
            itemCapacity = 20;
            separateItemCapacity = true;
            updateEffectChance = 0.03f;
            updateEffect = new MultiEffect(
                Fx.blastsmoke,
                Fx.generatespark
            );
            drawArrows = new DrawArrows(2, Pal.lightishOrange, find("c75807"));

            reload = 30.0f;
            range = 20 * tilesize;
            useTime = 240.0f;
            speedBoost = 1.5f;
            speedBoostPhase = 0.25f;
            phaseRangeBoost = 32.0f;
            statusDuration = 180.0f;
            allyStatus = FRStatusEffects.inspired;
            enemyStatus = StatusEffects.sapped;

            consume(new ConsumePowerCustom(2160 / 60, 0.0f, false, this));
            consume(new ConsumeCampfire(this));
        }

        @Override
        public void load(){
            super.load();
            drawArrows.load(this);
        }

        @Override
        public void setStats(){
            super.setStats();
            stats.add(FRStat.statusEffectApplied, table -> {
                table.row();

                for(int i = 0; i < 2;){
                    var sfx = i++ == 0 ? allyStatus : enemyStatus;

                    table.table(Styles.grayPanel, t -> {
                        t.left().button(new TextureRegionDrawable(sfx.uiIcon), Styles.emptyi, 40.0f, () -> ui.content.show(sfx)).size(40.0f).pad(10.0f).scaling(Scaling.fit);
                        t.left().table(info -> {
                            String detail = sfx == allyStatus
                            ? FRStat.allyStatusEffect.localized()
                            : FRStat.enemyStatusEffect.localized();

                            info.left().add("[accent]" + detail).left();
                            info.row();
                            info.left().add(sfx.localizedName).color(sfx.color).left();
                        });
                    }).growX().pad(5.0f).row();
                }
            });
        }

        @Override
        @Modified
        public void drawPlace(int x, int y, int rotation, boolean valid){
            if(DEBUG.isDeveloper()){
                float wx = x * tilesize + offset, wy = y * tilesize + offset;
                drawPotentialLinks(x, y);

                float[] ranges = {range, range + phaseRangeBoost * (2.25f - speedBoost) / speedBoostPhase, range + phaseRangeBoost * (4.2f - speedBoost) / speedBoostPhase};
                Color[] colors = {baseColor, phaseColor, phaseColor.cpy().mul(1.15f)};
                for(int i = 0; i < 3; i++)
                    Drawf.dashCircle(wx, wy, ranges[i], colors[i]);
                for(int i = 2; i >= 0; i--){
                    var color = colors[i];
                    indexer.eachBlock(player.team(), wx, wy, ranges[i],
                        other -> other.block.canOverdrive,
                        other -> Drawf.selected(other, Tmp.c1.set(color).a(Mathf.absin(4.0f, 1.0f))));
                }

            }else{
                super.drawPlace(x, y, rotation, valid);
            }
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

                if(wasVisible && phaseHeat >= 4.0f && Mathf.chanceDelta(0.015f + (phaseHeat - 4.0f) * 0.007f))
                    updateEffectSp.at(x + Mathf.range(8, 16), y + Mathf.range(8, 16), 0, new Vec2(x, y));
            }

            @Override
            @Modified
            public void draw(){
                Draw.rect(block.region, x, y, drawrot());
                drawArrows.draw(this);

                if(!displayRange) return;
                Draw.color(efficiency > 0 ? Pal.redLight : Color.black, 0.8f);
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
                c.row().table(Styles.grayPanel, t -> t.row().left()
                    .add(Core.bundle.format("stat.consumecampfire", block.speedBoostPhase * 100, block.phaseRangeBoost / tilesize))
                    .growX().pad(5.0f));

                int i = 0;
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
