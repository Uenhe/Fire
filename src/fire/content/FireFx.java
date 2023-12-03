package fire.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Effect;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class FireFx{

    public static Effect railChargeEffect(float lifetime, Color color, float width, float range, float spacing){

        /*
         * special thanks to Extra Utilities mod,
         * TODO absolutely require rewritten in java...
         */

        return new Effect(lifetime, range * 2f, e -> {

            Object data = e.data != null ? e.data : range;
            Draw.color(color);
            float track = Mathf.curve(e.fin(Interp.pow2Out), 0f, 0.25f) * Mathf.curve(e.fout(Interp.pow4Out), 0f, 0.3f) * e.fin();

            for(int i = 0; i <= (float)data / spacing; i++){
                Tmp.v1.trns(e.rotation, i * spacing);
                float f = Interp.pow3Out.apply(Mathf.clamp((e.fin() * (float)data - i * spacing) / spacing)) * (0.6f + track * 0.4f);
                Draw.rect("fire-aim-shoot", e.x + Tmp.v1.x, e.y + Tmp.v1.y, 144f * Draw.scl * f, 144f * Draw.scl * f, e.rotation - 90f);
            }

            Tmp.v1.trns(e.rotation, 0f, (2f - track) * tilesize * width);
            Lines.stroke(track * 2f);

            for(int i : Mathf.signs){
                Lines.lineAngle(e.x + Tmp.v1.x * i, e.y + Tmp.v1.y * i, e.rotation, (float)data * (0.75f + track / 4f) * Mathf.curve(e.fout(Interp.pow5Out), 0f, 0.1f));
            }
        });
    }

    public static Effect jackpotChargeEffect(float lifetime, float speed, float radius, int amount, Color[] colors){

        byte range = Byte.MAX_VALUE;
        final float[] w = new float[range];

        return new Effect(lifetime, e -> {

            byte a = (byte)Mathf.randomSeed(e.id, 0, range - 1);
            float r = radius * e.fout();

            if(!state.isPaused()){
                w[a] += Time.delta * speed * e.fout(Interp.pow2Out);
                w[a] %= 360f;
            }

            float x = Mathf.cos(w[a]);
            float y = Mathf.sin(w[a]);
            x += e.x;
            y += e.y;

            for(int i = 0; i < amount; i++) {

                float
                    angle = 360f / amount * i,
                    angle0 = Angles.angle(e.x, e.y, x, y),

                    ax = e.x + r * Mathf.sinDeg(angle + angle0),
                    ay = e.y + r * Mathf.cosDeg(angle + angle0);

                Draw.color(colors[i]);
                Lines.circle(ax, ay, 2f);
                Fill.circle(ax, ay, 2f);
            }
        });
    }

    public static Effect gamblerShootEffect(float lifetime, int amount){
        return new Effect(lifetime, e -> {
            Draw.color(e.color, Color.lightGray, e.fin());
            Angles.randLenVectors(e.id, amount, 5f + e.finpow() * 22f, (x, y) -> {
                Fill.square(e.x + x, e.y + y, e.fout() * 2.5f + 0.5f, 45f);
            });
        });
    }
}
