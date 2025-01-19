package fire.entities;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.Lightning;
import mindustry.game.Team;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

/**
 * Only used in weather {@link fire.content.FWeathers#rainstorm Rainstorm}.<p>
 * A new attempt of Pool.
 */
public class LightningCloud implements arc.util.pooling.Pool.Poolable{

    static final short interval = 300;
    static final short chargeTime = 150;

    int x, y;
    float intensity;
    float timer;
    public static final Seq<LightningCloud> clouds = new Seq<>();

    public LightningCloud(int x, int y){
        this.x = x;
        this.y = y;
        intensity = Mathf.random(0.75f, 1.33f);
        clouds.add(this);
    }

    public void update(){
        timer += Time.delta;
        if(time() >= chargeTime && time() < time2() + 10.0f && Mathf.chanceDelta(0.11 + 0.04 * intensity))
            Lightning.create(Team.derelict, Pal.lancerLaser, (int)(12 * intensity), x, y, Mathf.random(360), (int)(10 * Mathf.pow(intensity, 1.2f)));

        if(time() > time2() + 31.0f)
            reset();
    }

    public void draw(){
        if(time() < 0.0f) return;

        float a;
        if(time() <= time1())
            a = time() / time1();
        else if(time() <= time2())
            a = 1.0f;
        else
            a = 1.0f - (time() - time2()) / time1();

        Draw.alpha(a);
        Drawf.light(x, y, size(), Pal.lancerLaser, a);
        Draw.blend(Blending.additive);
        Draw.rect(Core.atlas.find("fire-portal"), x, y, size() * intensity, size() * intensity, timer);
        Draw.blend();
    }

    private float time(){
        return timer - interval;
    }

    private float time1(){
        return 30.0f;
    }

    private float time2(){
        return chargeTime * intensity * 1.8f;
    }

    private float size(){
        final float scl = 6.0f, mag = 8.0f;

        if(time() <= time1())
            return 20.0f + (time() / time1()) * 20.0f;
        else if(time() <= time2())
            return 40.0f + Mathf.sin(time() - time1(), scl, mag);
        else
            return (1.0f - (time() - time2()) / time1()) * (40.0f + Mathf.sin(time2() - time1(), scl, mag));
    }

    @Override
    public void reset(){
        clouds.remove(this);
        x = 0;
        y = 0;
        intensity = 0.0f;
        timer = 0.0f;
    }
}
