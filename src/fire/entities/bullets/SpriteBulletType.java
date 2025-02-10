package fire.entities.bullets;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;

/** Draw the desired sprite at bullet's position directly, without any color, alpha or something else. */
public class SpriteBulletType extends mindustry.entities.bullet.BulletType{

    public final float width, height;

    private final String name;
    private TextureRegion region;

    public SpriteBulletType(float speed, float damage, float w, float h, String name){
        super(speed, damage);
        width = w;
        height = h;
        this.name = name;
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name);
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);
        Draw.z(Layer.effect + 1.0f); //not affected by bloom
        Draw.rect(region, b.x, b.y, width, height, b.rotation() - 90.0f);
    }
}
