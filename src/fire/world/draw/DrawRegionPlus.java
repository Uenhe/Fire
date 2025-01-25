package fire.world.draw;

import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.draw.DrawRegion;

public class DrawRegionPlus extends DrawRegion{
    public float magnification = 1.0f;

    public DrawRegionPlus(){

    }

    public void draw(Building build){
        if(layer > 0.0f)
            Draw.z(layer);

        if(spinSprite)
            Drawf.spinSprite(region, build.x + x, build.y + y, magnification * build.totalProgress() * rotateSpeed * build.efficiency + rotation + (buildingRotate ? build.rotdeg() : 0.0f));
        else
            Draw.rect(region, build.x + x, build.y + y, magnification * build.totalProgress() * rotateSpeed * build.efficiency + rotation + (buildingRotate ? build.rotdeg() : 0.0f));

        Draw.z(Draw.z());
    }
}
