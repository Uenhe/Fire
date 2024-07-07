package fire.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;

import static mindustry.Vars.tilesize;

public class FFx{

    /** Special thanks to Extra Utilities mod. */
    public static Effect railChargeEffect(float lifetime, Color color, float width, float range, float spacing){
        return new Effect(lifetime, range * 2f, e -> {

            float track = Mathf.curve(e.fin(Interp.pow2Out), 0f, 0.25f) * Mathf.curve(e.fout(Interp.pow4Out), 0f, 0.3f) * e.fin();

            Draw.color(color);
            for(short i = 0; i <= range / spacing; i++){
                Tmp.v1.trns(e.rotation, i * spacing);
                float f = Interp.pow3Out.apply(Mathf.clamp((e.fin() * range - i * spacing) / spacing)) * (0.6f + track * 0.4f);
                Draw.rect("fire-aim-shoot", e.x + Tmp.v1.x, e.y + Tmp.v1.y, 144f * Draw.scl * f, 144f * Draw.scl * f, e.rotation - 90f);
            }

            Tmp.v1.trns(e.rotation, 0f, (2f - track) * tilesize * width);
            Lines.stroke(track * 2f);
            for(int i : Mathf.signs){
                Lines.lineAngle(e.x + Tmp.v1.x * i, e.y + Tmp.v1.y * i, e.rotation, range * (0.75f + track / 4f) * Mathf.curve(e.fout(Interp.pow5Out), 0f, 0.1f));
            }
        });
    }

    public static Effect jackpotChargeEffect(float lifetime, float speed, float radius, int amount, Color[] colors){
        return new Effect(lifetime, e -> {

            final float orbSize = 2f * e.fout() + 1f;
            final float r = radius * e.fout();
            final float w = e.time * speed * e.fout(Interp.swingOut);
            final float x = Mathf.cos(w) + e.x;
            final float y = Mathf.sin(w) + e.y;

            for(byte i = 0; i < amount; i++) {
                final float angle = Angles.angle(e.x, e.y, x, y) + 360f / amount * i;
                final float
                    cx = e.x + r * Mathf.sinDeg(angle),
                    cy = e.y + r * Mathf.cosDeg(angle);

                Draw.color(colors[i]);
                Lines.circle(cx, cy, orbSize);
                Fill.circle(cx, cy, orbSize);
            }
        });
    }

    public static Effect gamblerShootEffect(float lifetime, int amount){
        return new Effect(lifetime, e -> {
            Draw.color(e.color, Color.lightGray, e.fin());
            Angles.randLenVectors(e.id, amount, 5f + e.finpow() * 22f, (x, y) ->
                Fill.square(e.x + x, e.y + y, e.fout() * 2.5f + 0.5f, 45f));
        });
    }

    public static Effect hitBulletSmall(Color color){
        return new Effect(14f, e -> {
            Draw.color(Color.white, color, e.fin());
            e.scaled(7f, s -> {
                Lines.stroke(0.5f + s.fout());
                Lines.circle(e.x, e.y, s.fin() * 5f);
            });

            Lines.stroke(0.5f + e.fout());
            Angles.randLenVectors(e.id, 5, e.fin() * 15f, (x, y) ->
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3f + 1f)
            );
            Drawf.light(e.x, e.y, 20f, color, 0.6f * e.fout());
        });
    }
}
