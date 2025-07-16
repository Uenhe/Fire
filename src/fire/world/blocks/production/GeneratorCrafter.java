package fire.world.blocks.production;

import arc.Core;
import arc.math.Mathf;
import arc.util.Strings;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class GeneratorCrafter extends mindustry.world.blocks.production.GenericCrafter{

    protected final float powerProduction;

    public GeneratorCrafter(String name, float pwr){
        super(name);
        hasPower = true;
        outputsPower = true;
        consumesPower = false;
        powerProduction = pwr;
        buildType = GeneratorCrafterBuild::new;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.basePowerGeneration, powerProduction * 60.0f, StatUnit.powerSecond);
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

    public class GeneratorCrafterBuild extends GenericCrafterBuild{

        @Override
        public float getPowerProduction(){
            return efficiency > 0.0f ? powerProduction : 0.0f;
        }
    }
}
