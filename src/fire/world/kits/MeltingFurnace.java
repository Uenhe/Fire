package fire.world.kits;

import arc.Core;
import arc.math.Mathf;
import arc.util.Strings;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.content;

public class MeltingFurnace{

    public static class MeltingFurnaceBlock extends mindustry.world.blocks.production.GenericCrafter{

        protected float basePowerProduction = 600;
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
            stats.add(Stat.basePowerGeneration, basePowerProduction * 60.0f, StatUnit.powerSecond);
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

        public class MeltingFurnaceBuild extends GenericCrafterBuild{

            @Override
            public float getPowerProduction(){
                if(efficiency == 0.0f) return 0.0f;

                float[] sum = {0.0f};
                items.each((item, n) -> {
                    if(item.flammability > cons.value)
                        sum[0] += item.flammability;
                });

                return sum[0] * basePowerProduction / 60.0f;
            }

            /** To scale liquid dump amount. */
            @Override
            public float getProgressIncrease(float baseTime){
                return super.getProgressIncrease(baseTime) * (baseTime == 1.0f ? cons.efficiencyMultiplier(this) : 1.0f);
            }

            @Override
            public BlockStatus status(){
                if(efficiency > 0.0f && cons.efficiencyMultiplier(this) == 0.0f) return BlockStatus.noOutput;
                return super.status();
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
