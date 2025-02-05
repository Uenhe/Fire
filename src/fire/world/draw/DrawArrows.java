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
import mindustry.world.draw.DrawRegion;

/** @see mindustry.world.blocks.production.BurstDrill.BurstDrillBuild#draw() */
public class DrawArrows extends DrawRegion{

    public final byte arrows;
    public final Color arrowColor, baseArrowColor;

    final TextureRegion[] arrowRegion, arrowBlurRegion;

    public DrawArrows(int n, Color c1, Color c2){
        arrows = (byte)n;
        arrowColor = c1;
        baseArrowColor = c2;
        arrowRegion = new TextureRegion[n];
        arrowBlurRegion = new TextureRegion[n];
    }

    @Override
    public void load(Block block){
        super.load(block);
        for(byte i = 0; i < arrows; i++){
            arrowRegion[i] = Core.atlas.find(block.name + "-arrow" + (i + 1));
            arrowBlurRegion[i] = Core.atlas.find(block.name + "-arrow-blur" + (i + 1));
        }
    }

    @Override
    public void draw(Building build){
        super.draw(build);
        if(!(build instanceof SmoothCrafter b)) return;

        for(byte i = 0; i < arrows; i++){
            float a = Mathf.clamp(b.smoothProgress() * (i + 1));

            Draw.z(Layer.blockAdditive);
            Draw.color(baseArrowColor, arrowColor, a);
            Draw.rect(arrowRegion[i], build.x, build.y);

            Draw.color(arrowColor);
            Draw.z(Layer.blockAdditive + 0.01f);
            Draw.blend(Blending.additive);
            Draw.alpha(Mathf.pow(a, 10.0f));
            Draw.rect(arrowBlurRegion[i], build.x, build.y);
            Draw.blend();
        }
    }

    public interface SmoothCrafter{
        float smoothProgress();
    }
}
