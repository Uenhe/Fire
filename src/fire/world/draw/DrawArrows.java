package fire.world.draw;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;

/** @see mindustry.world.blocks.production.BurstDrill.BurstDrillBuild#draw() */
public class DrawArrows extends mindustry.world.draw.DrawBlock{

    public final byte arrows;
    public final Color arrowColor, baseArrowColor;

    private final TextureRegion[] arrowRegions, arrowBlurRegions;

    public DrawArrows(int n, Color c1, Color c2){
        arrows = (byte)n;
        arrowColor = c1;
        baseArrowColor = c2;
        arrowRegions = new TextureRegion[n];
        arrowBlurRegions = new TextureRegion[n];
    }

    @Override
    public void load(Block block){
        for(byte i = 0, arrows = this.arrows; i < arrows; i++){
            arrowRegions[i] = Core.atlas.find(block.name + "-arrow" + (i + 1));
            arrowBlurRegions[i] = Core.atlas.find(block.name + "-arrow-blur" + (i + 1));
        }
    }

    @Override
    public void draw(Building build){
        if(!(build instanceof SmoothCrafter)) return;

        float sp = ((SmoothCrafter)build).smoothProgress();
        for(byte i = 0, arrows = this.arrows; i < arrows; i++){
            float a = Mathf.clamp(sp * (i + 1));

            Draw.z(Layer.blockAdditive);
            Draw.color(baseArrowColor, arrowColor, a);
            Draw.rect(arrowRegions[i], build.x, build.y);

            Draw.color(arrowColor);
            Draw.z(Layer.blockAdditive + 0.01f);
            Draw.blend(Blending.additive);
            Draw.alpha(Mathf.pow(a, 10.0f));
            Draw.rect(arrowBlurRegions[i], build.x, build.y);
            Draw.blend();
        }
    }

    public interface SmoothCrafter{
        float smoothProgress();
    }
}
