package fire.entities.bullets;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;

/** Animated version. */
public class SpritesBulletType extends mindustry.entities.bullet.BulletType{

    public final byte width, height;
    public final float frameTime;

    private final String name;
    private final TextureRegion[] regions;

    public SpritesBulletType(float speed, float damage, int w, int h, int n, float t, String name){
        super(speed, damage);
        width = (byte)w;
        height = (byte)h;
        regions = new TextureRegion[n];
        frameTime = t;
        this.name = name;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0, len = (byte)regions.length; i < len;)
            regions[i] = Core.atlas.find(name + ++i);
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);
        Draw.z(Layer.effect + 1.0f); //not affected by bloom
        Draw.rect(regions[(int)(Time.time * speedScale(b) / frameTime) % regions.length], b.x, b.y, width * sizeScale(b), height * sizeScale(b), b.rotation() - 90.0f);
    }

    protected float speedScale(Bullet b){
        return 1.0f;
    }

    protected float sizeScale(Bullet b){
        return 1.0f;
    }
}
