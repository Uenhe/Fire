package fire.world.blocks.production;

public class GeneratorCrafter extends mindustry.world.blocks.production.GenericCrafter{

    /** Per frame. */
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
        stats.add(mindustry.world.meta.Stat.basePowerGeneration, powerProduction * 60.0f, mindustry.world.meta.StatUnit.powerSecond);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("power", (GeneratorCrafterBuild build) -> new mindustry.ui.Bar(
            () -> arc.Core.bundle.format("bar.poweroutput", arc.util.Strings.fixed(build.getPowerProduction() * 60.0f * build.timeScale(), 1)),
            () -> mindustry.graphics.Pal.powerBar,
            () -> arc.math.Mathf.num(build.efficiency > 0.0f)
        ));
    }

    public class GeneratorCrafterBuild extends GenericCrafterBuild{

        @Override
        public float getPowerProduction(){
            return efficiency > 0.0f ? powerProduction : 0.0f;
        }
    }
}
