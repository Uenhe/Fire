package fire.entities;

import arc.Core;

import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;

import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.Lightning;

import mindustry.game.Team;
import mindustry.graphics.Pal;

/** Only used in weather {@link fire.content.FWeathers#rainstorm Rainstorm}. */
public class LightningCloud{

    final int x, y;
    final short interval = 300;
    final short chargeTime = 150;

    private float timer;
    public static final Seq<LightningCloud> clouds = new Seq<>();

    public LightningCloud(int x, int y){
        this.x = x;
        this.y = y;
        clouds.add(this);
    }

    public void update(){

        if((timer += Time.delta) >= interval){

            if(time() >= chargeTime){
                if(Mathf.chanceDelta(0.14))
                    Lightning.create(Team.derelict, Pal.lancerLaser, 10, x, y, Mathf.random(360), 12);

                if(time() >= time2() * 1.001f){
                    timer = 0.0f;
                    clouds.remove(this);
                }
            }
        }
    }

    public void draw(){

        if(time() >= 0.0f){
            if(time() > time2())
                Draw.alpha(1.0f - (time() - time2()) / time1());
            else
                Draw.alpha(time() / time1());

            Draw.blend(Blending.additive);
            Draw.rect(Core.atlas.find("fire-portal"), x, y, size(), size(), timer);
            Draw.blend();
        }
    }

    private float time(){
        return timer - interval;
    }

    private float time1(){
        return 30.0f;
    }

    private float time2(){
        return chargeTime * 2.2f;
    }

    private float size(){
        final float scl = 6.0f, mag = 8.0f;

        if(time() <= time1())
            return 20.0f + (time() / time1()) * 20.0f;
        else if(time() > time1() && time() <= time2())
            return 40.0f + Mathf.sin(time() - time1(), scl, mag);
        else
            return (1.0f - (time() - time2()) / time1()) * (40.0f + Mathf.sin(time2() - time1(), scl, mag));
    }
}
