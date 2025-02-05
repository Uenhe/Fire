package fire.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

import static mindustry.Vars.tilesize;

public class FFx{

    /** Special thanks to Extra Utilities mod. */
    public static Effect railChargeEffect(float lifetime, Color color, float width, float range, float spacing){
        return new Effect(lifetime, range * 2.0f, e -> {

            float track = Mathf.curve(e.fin(Interp.pow2Out), 0.0f, 0.25f) * Mathf.curve(e.fout(Interp.pow4Out), 0.0f, 0.3f) * e.fin();
            Draw.color(color);

            for(short i = 0; i <= range / spacing; i++){
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

            float orbSize = 2f * e.fout() + 1f;
            float r = radius * e.fout();
            float w = e.time * speed * e.fout(Interp.swingOut);
            float x = Mathf.cos(w) + e.x;
            float y = Mathf.sin(w) + e.y;

            for(byte i = 0; i < amount; i++){
                float angle = Angles.angle(e.x, e.y, x, y) + 360.0f / amount * i;
                float
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

    /** @see Fx#instTrail */
    public static final Effect instTrailPurple = new Effect(10.0f, e -> {
        for(byte i = 0; i < 2; i++){
            float m = i == 0 ? 1.0f : 0.5f;
            float w = 15.0f * e.fout() * m;
            float rot = e.rotation + 180.0f;

            Draw.color(i == 0 ? Pal.reactorPurple2 : Pal.reactorPurple);
            Drawf.tri(e.x, e.y, w, (30.0f + Mathf.randomSeedRange(e.id, 10.0f)) * m, rot);
            Drawf.tri(e.x, e.y, w, 10.0f * m, rot + 180.0f);
        }

        Drawf.light(e.x, e.y, 60.0f, Pal.reactorPurple2, 0.6f * e.fout());
    });

    /** @see Fx#instBomb */
    public static final Effect instBombPurple = new Effect(10.0f, 100.0f, e -> {

        for(byte i = 0; i < 4; i++){
            Draw.color(Pal.reactorPurple2);
            Drawf.tri(e.x, e.y, 5.0f, 80.0f * e.fout(), i * 90.0f + 45.0f);
            Draw.color();
            Drawf.tri(e.x, e.y, 2.5f, 40.0f * e.fout(), i * 90.0f + 45.0f);
        }

        Drawf.light(e.x, e.y, 150.0f, Pal.reactorPurple2, 0.9f * e.fout());
    });

    /** @see Fx#drillSteam */
    public static final Effect drillSteamFast = new Effect(160.0f, e -> {
        float length = 4.0f + e.finpow() * 24.0f;
        Fx.rand.setSeed(e.id);
        
        for(byte i = 0; i < 16; i++){
            Fx.v.trns(Fx.rand.random(360.0f), Fx.rand.random(length));
            float sizer = Fx.rand.random(1.4f, 4.0f);

            e.scaled(e.lifetime * Fx.rand.random(0.5f, 1.0f), b -> {
                Draw.color(Color.gray, b.fslope() * 0.93f);
                Fill.circle(e.x + Fx.v.x, e.y + Fx.v.y, sizer + b.fslope() * 1.4f);
            });
        }
    });

    /** @see Fx#dynamicSpikes */
    public static final Effect dynamicSpikesZ = new Effect(40.0f, 100.0f, e -> {
        float circleRad = 4.0f + e.finpow() * 30.0f;
        Lines.stroke(e.fout() * 2.0f);
        Lines.circle(e.x, e.y, circleRad);

        for(byte i = 0; i < 4; i++){
            Draw.color(e.color);
            Drawf.tri(e.x, e.y, 8.0f, 60.0f * e.fout(), i * 90.0f);
            Draw.color();
            Drawf.tri(e.x, e.y, 4.0f, 19.2f * e.fout(), i * 90.0f);
        }

        Drawf.light(e.x, e.y, circleRad * 1.8f, Pal.heal, e.fout());
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

        float tx = p.getX(), ty = p.getY();
        float dst = Mathf.dst(e.x, e.y, tx, ty);

        Tmp.v1.set(p).sub(e.x, e.y).nor();

        final float rangeBetweenPoints = 12.0f;

        int links = Mathf.ceil(dst / rangeBetweenPoints);
        float spacing = dst / links;
        float nx = Tmp.v1.x, ny = Tmp.v1.y;

        Lines.stroke(1.2f * e.fout());
        Draw.color(Color.white, e.color, e.fin());
        Lines.beginLine();
        Lines.linePoint(e.x, e.y);

        Fx.rand.setSeed(e.id);

        for(int i = 0; i < links; i++){
            if(i == links - 1){
                Lines.linePoint(tx, ty);
            }else{
                float len = (i + 1) * spacing;
                Tmp.v1.setToRandomDirection(Fx.rand).scl(rangeBetweenPoints * 0.5f);
                Lines.linePoint(
                    e.x + nx * len + Tmp.v1.x,
                    e.y + ny * len + Tmp.v1.y
                );
            }
        }

        Lines.endLine();
    }).followParent(false).rotWithParent(false);
}
