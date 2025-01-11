package fire.world.draw;

import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.draw.DrawRegion;

public class DrawRegionPlus extends DrawRegion {
    public float magnification=1f;

    public DrawRegionPlus() {
    }

    public void draw(Building build) {
        float z = Draw.z();
        if (this.layer > 0.0F) {
            Draw.z(this.layer);
        }

        if (this.spinSprite) {
            Drawf.spinSprite(this.region, build.x + this.x, build.y + this.y, magnification * build.totalProgress() * this.rotateSpeed * build.efficiency + this.rotation + (this.buildingRotate ? build.rotdeg() : 0.0F));
        } else {
            Draw.rect(this.region, build.x + this.x, build.y + this.y, magnification * build.totalProgress() * this.rotateSpeed * build.efficiency + this.rotation + (this.buildingRotate ? build.rotdeg() : 0.0F));
        }

        Draw.z(z);
    }
}
