package fire.world.draw;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.draw.DrawWeave;

import static mindustry.Vars.tilesize;

public class DrawWeavePlus extends DrawWeave{

    public final byte lines;
    public final float rotateSpeed;

    private TextureRegion glowRegion;

    public DrawWeavePlus(int lines, float rotateSpeed){
        this.lines = (byte)lines;
        this.rotateSpeed = rotateSpeed;
    }

    @Override
    public void load(Block block){
        super.load(block);
        glowRegion = Core.atlas.find(block.name + "-glow");
    }

    @Override
    public void draw(Building build){
        if(build.warmup() > 0.001f){
            Draw.color(Pal.accent, Color.gray, 0.5f);
            Draw.alpha(build.warmup());
            Draw.rect(glowRegion, build.x, build.y);

            Draw.color(Pal.accent, Color.white, 0.5f);
            Draw.alpha(build.warmup());

            for(byte i = 0; i < lines; i++){
                rand.setSeed(build.id * i * 2L);

                float value = Mathf.sin(build.totalProgress() + rand.random(10.0f), 8.0f / rotateSpeed * rand.random(0.8f, 1.2f), build.block.size * tilesize * 0.33f),
                    x = build.x + (i % 2 == 0 ? value : 0.0f),
                    y = build.y + (i % 2 == 1 ? value : 0.0f),
                    angle = i % 2 == 0 ? 90.0f : 0.0f;

                Lines.lineAngleCenter(x, y, angle, build.block.size * tilesize * 0.5f);
            }
        }

        Draw.color();
        Draw.rect(weave, build.x, build.y, build.totalProgress() * rotateSpeed);

        Draw.reset();
    }
}
