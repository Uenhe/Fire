package fire.world.kits;

import arc.Core;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import fire.content.FRPlanets;
import mindustry.core.UI;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.content;
import static mindustry.Vars.ui;

public class MeltingFurnace{

    public static class MeltingFurnaceBlock extends mindustry.world.blocks.production.AttributeCrafter{

        protected float basePowerProduction;
        private final ConsumeMeltingFurnace cons;

        public MeltingFurnaceBlock(String name, ConsumeMeltingFurnace cons){
            super(name);
            this.cons = consume(cons);
            outputsPower = consumesPower = true;
            buildType = MeltingFurnaceBuild::new;
        }

        @Override
        public void init(){
            super.init();
            for(var item : content.items())
                itemFilter[item.id] = true;
        }

        @Override
        public void setStats(){
            super.setStats();
            final float size = 32.0f;

            stats.add(Stat.basePowerGeneration, basePowerProduction * 60.0f, StatUnit.powerSecond);
            stats.add(Stat.input, mt -> {
                mt.row().table(Styles.grayPanel, t ->
                    t.left().add(Core.bundle.format("stat.consumefurnace", (int)(cons.value * 100))).growX().pad(10.0f)
                );

                int i = 0;
                Table tableSlag = mt.row().table(Styles.grayPanel, t -> t.add("@stat.furnaceslag").minWidth(size).maxWidth(size).pad(10.0f).row()).left().get(),
                    tablePower = mt.row().table(Styles.grayPanel, t -> t.add("@stat.furnacepower").minWidth(size).maxWidth(size).pad(10.0f).row()).left().get();
                for(var item : content.items()){
                    if(item.isHidden() || !item.isOnPlanet(FRPlanets.lysetta)) continue;
                    if(item.flammability > cons.value){
                        tablePower.stack(
                            new Table(t -> t.button(new TextureRegionDrawable(item.uiIcon), Styles.emptyi, size, () -> ui.content.show(item)).size(size).pad(10.0f).scaling(Scaling.fit)),
                            new Table(t -> t.top().right().add("[accent]" + Strings.fixed(basePowerProduction * item.flammability * 0.06f, 1) + UI.thousands + StatUnit.perSecond.localized()).style(Styles.outlineLabel).fontScale(0.8f))
                        );
                    }else{
                        tableSlag.button(new TextureRegionDrawable(item.uiIcon), Styles.emptyi, size, () -> ui.content.show(item)).size(size).pad(10.0f).scaling(Scaling.fit);
                        if(++i % 10 == 0) tableSlag.row();
                    }
                }
            });
        }

        @Override
        public void setBars(){
            super.setBars();
            addBar("power", build -> new Bar(
                () -> Core.bundle.format("bar.poweroutput", Strings.fixed(build.getPowerProduction() * build.timeScale() * 60.0f, 1)),
                () -> Pal.powerBar,
                () -> Mathf.num(build.efficiency > 0.0f)
            ));
        }

        public class MeltingFurnaceBuild extends AttributeCrafterBuild implements fire.world.consumers.ConsumePowerCustom.CustomPowerConsumer{

            @Override
            public float getPowerProduction(){
                if(efficiency == 0.0f) return 0.0f;

                float[] sum = {0.0f};
                items.each((item, n) -> {
                    if(item.flammability > cons.value)
                        sum[0] += item.flammability;
                });

                return sum[0] * basePowerProduction;
            }

            /** To scale liquid dump amount. */
            @Override
            public float getProgressIncrease(float baseTime){
                return super.getProgressIncrease(baseTime) * (baseTime == 1.0f ? cons.efficiencyMultiplier(this) : 1.0f / efficiencyMultiplier());
            }

            @Override
            public BlockStatus status(){
                if(efficiency > 0.0f && cons.efficiencyMultiplier(this) == 0.0f) return BlockStatus.noOutput;
                return super.status();
            }

            @Override
            public float consPowerScale(){
                return items.sum((i, n) -> 1.0f);
            }
        }
    }

    public static class ConsumeMeltingFurnace extends mindustry.world.consumers.Consume{

        private final float value;

        public ConsumeMeltingFurnace(float value){
            this.value = value;
        }

        @Override
        public void trigger(Building build){
            build.items.each((item, n) ->
                build.items.remove(item, 1));
        }

        @Override
        public float efficiency(Building build){
            return build.consumeTriggerValid() || build.items.any() ? 1.0f : 0.0f;
        }

        @Override
        public float efficiencyMultiplier(Building build){
            byte[] sum = {0};
            build.items.each((item, n) -> {
                if(item.flammability <= value)
                    sum[0]++;
            });

            return sum[0];
        }
    }
}
