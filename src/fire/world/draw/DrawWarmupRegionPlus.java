package fire.world.draw;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Time;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawWarmupRegion;

public class DrawWarmupRegionPlus extends DrawWarmupRegion{
    public String suffix = "-top";
    public float sinMag = 0.6f, sinScl = 8f;
    public Color color = Color.valueOf("ff9b59");
    public TextureRegion region;

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){

    }

    @Override
    public void draw(Building build){
        Draw.color(color);
        Draw.alpha(build.warmup() * (1f - sinMag) + Mathf.absin(Time.time, sinScl, sinMag) * build.warmup());
        Draw.rect(region, build.x, build.y);
        Draw.reset();
    }

    @Override
    public void load(Block block){
        super.load(block);

        region = Core.atlas.find(block.name + suffix);
    }
}
