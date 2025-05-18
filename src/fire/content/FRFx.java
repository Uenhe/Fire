package fire.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static mindustry.Vars.tilesize;

public class FRFx{

    /** Special thanks to Extra Utilities mod. */
    public static Effect railChargeEffect(float lifetime, Color color, float width, float range, float spacing){
        return new Effect(lifetime, range * 2.0f, e -> {

            float track = Mathf.curve(e.fin(Interp.pow2Out), 0.0f, 0.25f) * Mathf.curve(e.fout(Interp.pow4Out), 0.0f, 0.3f) * e.fin();
            Draw.color(color);

            for(short i = 0, interval = (short)(range / spacing); i <= interval; i++){
                Tmp.v1.trns(e.rotation, i * spacing);
                float f = Interp.pow3Out.apply(Mathf.clamp((e.fin() * range - i * spacing) / spacing)) * (0.6f + track * 0.4f);
                Draw.rect("fire-aim-shoot", e.x + Tmp.v1.x, e.y + Tmp.v1.y, 144.0f * Draw.scl * f, 144.0f * Draw.scl * f, e.rotation - 90.0f);
            }

            Tmp.v1.trns(e.rotation, 0.0f, (2.0f - track) * tilesize * width);
            Lines.stroke(track * 2.0f);
            for(int i : Mathf.signs)
                Lines.lineAngle(e.x + Tmp.v1.x * i, e.y + Tmp.v1.y * i, e.rotation, range * (0.75f + track * 0.25f) * Mathf.curve(e.fout(Interp.pow5Out), 0.0f, 0.1f));
        });
    }

    public static Effect jackpotChargeEffect(float lifetime, float speed, float radius, int amount, Color[] colors){
        return new Effect(lifetime, e -> {
            float orbSize = 2.0f * e.fout() + 1.0f;

            for(byte i = 0; i < amount; i++){
                float theta = e.time * e.fout(Interp.swingOut) + Mathf.PI2 * (float)i / amount / speed,
                    mag = radius * e.fout(),
                    x = Mathf.cos(theta, 1.0f / speed, mag) + e.x,
                    y = Mathf.sin(theta, 1.0f / speed, mag) + e.y;

                Draw.color(colors[i]);
                Lines.circle(x, y, orbSize);
                Fill.circle(x, y, orbSize);
            }
        });
    }

    public static Effect squareEffect(float lifetime, int amount, float size, float rotation, @Nullable Color color){
        return new Effect(lifetime, e -> {
            Draw.color(color == null ? e.color : color, Color.white, e.fin());
            Angles.randLenVectors(e.id, amount, size * (e.finpow() * 4.0f + 1.0f), (x, y) ->
                Fill.square(e.x + x, e.y + y, e.fout() * size + 1.0f, rotation));
        });
    }

    public static Effect crossEffect(float lifetime, float size, float rotation, boolean circle, @Nullable Color color){
        return new Effect(lifetime, 100.0f, e -> {
            float circleRad = size * (e.finpow() * 16.0f + 1.0f);
            var col = color == null ? e.color : color;

            Drawf.light(e.x, e.y, circleRad * 1.6f, col, e.fout());

            if(circle){
                Draw.color(col);
                Lines.stroke(e.fout() * 3.0f);
                Lines.circle(e.x, e.y, circleRad);
            }

            for(byte i = 0; i < 4; i++){
                Draw.color(col);
                Drawf.tri(e.x, e.y, size * 1.8f, size * 24.0f * e.foutpow(), i * 90.0f + rotation);
                Draw.color();
                Drawf.tri(e.x, e.y, size * 0.8f, size * 9.0f * e.foutpow(), i * 90.0f + rotation);
            }
        });
    }

    /** @see Fx#hitBulletSmall */
    public static Effect hitBulletSmall(Color color){
        return new Effect(14.0f, e -> {
            Draw.color(Color.white, color, e.fin());
            e.scaled(7.0f, s -> {
                Lines.stroke(0.5f + s.fout());
                Lines.circle(e.x, e.y, s.fin() * 5.0f);
            });

            Lines.stroke(0.5f + e.fout());
            Angles.randLenVectors(e.id, 5, e.fin() * 15.0f, (x, y) ->
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3.0f + 1.0f)
            );
            Drawf.light(e.x, e.y, 20.0f, color, 0.6f * e.fout());
        });
    }

    /** @see Fx#instTrail */
    public static final Effect instTrailPurple = new Effect(10.0f, e -> {
        for(byte i = 0; i < 2; i++){
            float m = i == 0 ? 1.0f : 0.5f;
            float w = 15.0f * e.fout() * m;

            Draw.color(i == 0 ? Pal.reactorPurple2 : Pal.reactorPurple);
            Drawf.tri(e.x, e.y, w, (30.0f + Mathf.randomSeedRange(e.id, 10.0f)) * m, e.rotation + 180.0f);
            Drawf.tri(e.x, e.y, w, 10.0f * m, e.rotation + 180.0f);
        }

        Drawf.light(e.x, e.y, 60.0f, Pal.reactorPurple2, 0.6f * e.fout());
    });

    /** @see Fx#drillSteam */
    public static final Effect drillSteamFast = new Effect(160.0f, e -> {
        float length = 4.0f + e.finpow() * 24.0f;
        var rand = Fx.rand;
        for(byte i = 0; i < 16; i++){
            rand.setSeed(e.id * 2L + i);
            Fx.v.trns(rand.random(360.0f), rand.random(length));

            e.scaled(e.lifetime * rand.random(0.5f, 1.0f), b -> {
                Draw.color(Color.gray, b.fslope() * 0.93f);
                Fill.circle(e.x + Fx.v.x, e.y + Fx.v.y, rand.random(1.4f, 4.0f) + b.fslope() * 1.4f);
            });
        }
    });

    /** @see Fx#mineImpactWave */
    public static final Effect mineImpactWaveZ = new Effect(45.0f, e -> {
        Draw.color(e.color);
        Lines.stroke(e.fout() * 1.5f);
        Angles.randLenVectors(e.id, 12, 4.0f + e.finpow() * 45.0f, (x, y) ->
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 5.0f + 1.0f)
        );

        e.scaled(30.0f, b -> {
            Lines.stroke(5.0f * b.fout());
            Lines.circle(e.x, e.y, b.finpow() * 28.0f);
        });
    });

    /** @see Fx#chainLightning */
    public static final Effect chainLightningThin = new Effect(20.0f, 300.0f, e -> {
        if(!(e.data instanceof Position p)) return;

        final float rangeBetweenPoints = 12.0f,
            tx = p.getX(), ty = p.getY(),
            dst = Mathf.dst(e.x, e.y, tx, ty);
        short links = (short)Mathf.ceil(dst / rangeBetweenPoints);
        float spacing = dst / links;
        var v = Tmp.v1;
        var rand = Fx.rand;
        rand.setSeed(e.id * 2L);

        v.set(p).sub(e.x, e.y).nor();
        float nx = v.x, ny = v.y;

        Lines.stroke(1.2f * e.fout());
        Draw.color(Color.white, e.color, e.fin());
        Lines.beginLine();
        Lines.linePoint(e.x, e.y);

        for(short i = 0; i < links; i++){
            if(i == links - 1){
                Lines.linePoint(tx, ty);
            }else{
                float len = (i + 1) * spacing;
                v.setToRandomDirection(rand).scl(rangeBetweenPoints * 0.5f);
                Lines.linePoint(e.x + nx * len + v.x, e.y + ny * len + v.y);
            }
        }

        Lines.endLine();
    }).followParent(false);

    public static final Effect reactorExplosionLarge = new Effect(0.0f, 500.0f, b -> {
        float intensity = 6.8f * (b.data instanceof Float f ? f : 1.0f),
            baseLifetime = 25.0f + intensity * 11.0f;

        b.lifetime = 40.0f + intensity * 45.0f;

        Draw.color(Pal.reactorPurple2);
        Draw.alpha(0.7f);
        var rand = Fx.rand;
        for(byte i = 0; i < 4; i++){
            byte j = i;
            rand.setSeed(b.id * 2L + j);

            b.scaled(b.lifetime * rand.random(0.4f, 1.0f), e ->
                Angles.randLenVectors(e.id + j - 1, e.fin(Interp.pow10Out), (int)(2.7f * intensity), 22.0f * intensity, (x, y, in, out) -> {
                    float rad = e.fout(Interp.pow5Out) * rand.random(0.5f, 1.0f) * ((2.0f + intensity) * 2.3f);
                    Fill.circle(e.x + x, e.y + y, rad);
                    Drawf.light(e.x + x, e.y + y, rad * 2.5f, Pal.reactorPurple, 0.5f);
            }));
        }

        b.scaled(baseLifetime, e -> {
            Draw.color();
            e.scaled(5.0f + intensity * 2.0f, i -> {
                Lines.stroke((3.1f + intensity / 5.0f) * i.fout());
                Lines.circle(e.x, e.y, (3.0f + i.fin() * 14.0f) * intensity);
                Drawf.light(e.x, e.y, i.fin() * 14.0f * 2.0f * intensity, Color.white, 0.9f * e.fout());
            });

            Draw.color(Pal.lighterOrange, Pal.reactorPurple, e.fin());
            Lines.stroke((2.0f * e.fout()));

            Draw.z(Layer.effect + 0.001f);
            Angles.randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(8.0f * intensity), 28.0f * intensity, (x, y, in, out) -> {
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1.0f + out * 4.0f * (4.0f + intensity));
                Drawf.light(e.x + x, e.y + y, (out * 4.0f * (3.0f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
            });
        });
    });
}
