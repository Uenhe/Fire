package fire.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Nullable;
import fire.type.FleshUnitType;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;

import static mindustry.Vars.tilesize;

public class FRFx{

    /** Special thanks to Extra Utilities mod. */
    public static Effect railChargeEffect(float lifetime, Color color, float width, float range, float spacing){
        return new Effect(lifetime, range * 2.0f, e -> {

            float track = Mathf.curve(e.fin(Interp.pow2Out), 0.0f, 0.25f) * Mathf.curve(e.fout(Interp.pow4Out), 0.0f, 0.3f) * e.fin();
            Draw.color(color);

            var v = Fx.v;
            for(int i = 0, interval = (int)(range / spacing); i <= interval; i++){
                v.trns(e.rotation, i * spacing);
                float f = Interp.pow3Out.apply(Mathf.clamp((e.fin() * range - i * spacing) / spacing)) * (0.6f + track * 0.4f);
                Draw.rect("fire-aim-shoot", e.x + v.x, e.y + v.y, 144.0f * Draw.scl * f, 144.0f * Draw.scl * f, e.rotation - 90.0f);
            }

            v.trns(e.rotation, 0.0f, (2.0f - track) * tilesize * width);
            Lines.stroke(track * 2.0f);
            for(int i : Mathf.signs)
                Lines.lineAngle(e.x + v.x * i, e.y + v.y * i, e.rotation, range * (0.75f + track * 0.25f) * Mathf.curve(e.fout(Interp.pow5Out), 0.0f, 0.1f));
        });
    }

    /** Special thanks to New Horizon mod. */
    public static Effect scanEffect(float lifetime, Color color, float range){
        return new Effect(lifetime, range * 2.5f, e -> {
            var rand = Fx.rand;
            rand.setSeed(e.id);
            Draw.color(color);

            float f = Interp.pow4Out.apply(Mathf.curve(e.fin(), 0.0f, 0.3f)), rad = range * f,
            stroke = Mathf.clamp(range * 0.0125f, 3.0f, 8.0f);

            Lines.stroke(2.0f * e.fout());
            Lines.circle(e.x, e.y, rad * 0.125f);

            Lines.stroke(stroke * e.fout() + e.fout(Interp.pow5In));
            Lines.circle(e.x, e.y, rad);

            Lines.stroke(stroke * Mathf.curve(e.fin(), 0.0f, 0.1f) * Mathf.curve(e.fout(), 0.05f, 0.15f));
            float angle = 360.0f * e.fin(Interp.pow5);
            Lines.lineAngle(e.x, e.y, angle, rad - Lines.getStroke() * 0.5f);
            Lines.stroke(stroke * Mathf.curve(e.fin(), 0.0f, 0.1f) * e.fout(0.05f));

            Draw.z(Layer.bullet - 1.0f);

            float p = Mathf.clamp(e.fin(Interp.pow5));

            int sides = Lines.circleVertices(rad), i = 0;

            float space = 360.0f / sides, len = 2.0f * rad * Mathf.sinDeg(space * 0.5f);

            for(; i < sides * p - 1; i++){
                float a = space * i;
                Draw.alpha(Mathf.curve(i / (sides * p), 0.6f + 0.35f * Interp.pow2InInverse.apply(Mathf.curve(e.fin(), 0, 0.8f)), 1) * Mathf.curve(e.fout(), 0.2f, 0.25f) / 1.5f);
                Fill.tri(e.x + rad * Mathf.cosDeg(a), e.y + rad * Mathf.sinDeg(a), e.x + rad * Mathf.cosDeg(a + space), e.y + rad * Mathf.sinDeg(a + space), e.x, e.y);
            }

            float a = space * i;
            Fx.v.trns(a, 0.0f, len * (sides * p - i - 1));
            Draw.alpha(Mathf.curve(e.fout(), 0.2f, 0.25f) / 1.5f);
            Fill.tri(e.x + rad * Mathf.cosDeg(a), e.y + rad * Mathf.sinDeg(a), e.x + rad * Mathf.cosDeg(a + space) + Fx.v.x, e.y + rad * Mathf.sinDeg(a + space) + Fx.v.y, e.x, e.y);

            Draw.z(Layer.effect);

            Angles.randLenVectors(e.id, (int)(e.rotation * 0.025f), e.rotation * 0.85f * f, angle, 0.0f, (x, y) ->
                Lines.lineAngle(e.x + x, e.y + y, angle, e.rotation * rand.random(0.05f, 0.15f) * e.fout(0.15f))
            );

            Draw.color(color);
            Lines.stroke(stroke * 1.25f * e.fout(0.2f));
            Fill.circle(e.x, e.y, Lines.getStroke());
            Draw.color(Color.white, color, e.fin());
            Fill.circle(e.x, e.y, Lines.getStroke() * 0.5f);

            Drawf.light(e.x, e.y, rad * 1.35f * e.fout(0.15f), color, 0.6f);
        });
    }

    /** Special thanks to New Horizon mod. */
    public static final Effect transitionEffect = new Effect(120.0f, 3000.0f, e -> {
        if (!(e.data instanceof TransitionFxData data)) return;

        var type = data.type;
        float angleOffset = data.transOut ? 0.0f : 180.0f;

        Draw.color(type.engineColor == null ? e.color : type.engineColor);
        Draw.z(type.engineLayer > 0.0f
            ? type.engineLayer
            : (type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) - 0.001f);

        var v = Fx.v;
        v.trns(e.rotation, type.engineOffset, 0.0f);
        e.scaled(80.0f, f -> {
            tri(f.x + v.x, f.y + v.y, type.engineSize * 1.5f * f.fout(Interp.slowFast), 2000.0f * type.engineSize / (type.engineSize + 4.0f), f.rotation + angleOffset);
            Fill.circle(f.x + v.x, f.y + v.y, type.engineSize * 1.5f * f.fout(Interp.slowFast));
        });
        Angles.randLenVectors(e.id, 16, 200.0f * type.engineSize / (type.engineSize + 4.0f), e.rotation + angleOffset, 0.0f, (x, y) ->
            Lines.lineAngle(e.x + x + v.x, e.y + y + v.y, Mathf.angle(x, y), e.fout() * 60.0f)
        );

        Draw.color();
        Draw.mixcol(e.color, 1.0f);
        Draw.rect(type.fullIcon, e.x, e.y, type.fullIcon.width * e.fout(Interp.pow2Out) * Draw.scl * 1.2f, type.fullIcon.height * e.fout(Interp.pow2Out) * Draw.scl * 1.2f, e.rotation - 90.0f);
        Draw.reset();
    });

    /** Special thanks to Testing Utilities mod. */
    public static Effect fleshTeleportEffect = new Effect(80.0f, e -> {
        if(!(e.data instanceof TpFxData data)) return;

        var oldType = ((FleshUnitType)data.spawned.type).origin;
        float scl = e.fout(Interp.pow2Out), p = Draw.scl, z = Draw.z();

        Draw.z(Layer.effect + 0.1f);
        Draw.scl *= scl;
        Draw.mixcol(data.spawned.team.color, 1.0f);
        Draw.rect(oldType.fullIcon, e.x, e.y, e.rotation);
        Draw.rect(data.spawned.type.fullIcon, data.x, data.y, e.rotation);
        Draw.reset();
        Draw.scl = p;
        Draw.z(z);

        Draw.color(data.spawned.team.color);
        float stroke = (oldType.hitSize + data.spawned.type.hitSize) * 0.5f * scl,
        cos = Mathf.cosDeg(e.rotation) * stroke,
        sin = Mathf.sinDeg(e.rotation) * stroke;

        Fill.quad(
            e.x + cos * 0.25f, e.y + sin * 0.25f,
            data.x + cos, data.y + sin,
            data.x - cos, data.y - sin,
            e.x - cos * 0.25f, e.y - sin * 0.25f
        );
    });

    public static Effect jackpotChargeEffect(float lifetime, float speed, float radius, int amount, Color[] colors){
        return new Effect(lifetime, e -> {
            float orbSize = 2.0f * e.fout() + 1.0f;

            for(int i = 0; i < amount; i++){
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

            for(int i = 0; i < 4; i++){
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
        for(int i = 0; i < 2; i++){
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
        var v = Fx.v;
        for(int i = 0; i < 16; i++){
            rand.setSeed(e.id * 2L + i);
            v.trns(rand.random(360.0f), rand.random(length));

            e.scaled(e.lifetime * rand.random(0.5f, 1.0f), b -> {
                Draw.color(Color.gray, b.fslope() * 0.93f);
                Fill.circle(e.x + v.x, e.y + v.y, rand.random(1.4f, 4.0f) + b.fslope() * 1.4f);
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
    public static final Effect chainLightningThin = new Effect(24.0f, 300.0f, e -> {
        if(!(e.data instanceof Position p)) return;

        final float rangeBetweenPoints = 12.0f,
            tx = p.getX(), ty = p.getY(),
            dst = Mathf.dst(e.x, e.y, tx, ty);
        int links = Mathf.ceil(dst / rangeBetweenPoints);
        float spacing = dst / links;
        var rand = Fx.rand;
        rand.setSeed(e.id * 2L);
        var v = Fx.v.set(p).sub(e.x, e.y).nor();
        float nx = v.x, ny = v.y;

        Lines.stroke(1.2f * e.fout());
        Draw.color(Color.white, e.color, e.fin());
        Lines.beginLine();
        Lines.linePoint(e.x, e.y);

        for(int i = 0; i < links; i++){
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

    /** @see Fx#chainEmp */
    public static final Effect chainEmpThin = new Effect(15.0f, 200.0f, e -> {
        if(!(e.data instanceof Position p)) return;
        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
        var v = Fx.v.set(p).sub(e.x, e.y).nor();
        var rand = Fx.rand;

        final float range = 6.0f, normx = v.x, normy = v.y;
        int links = Mathf.ceil(dst / range);
        float spacing = dst / links;

        Lines.stroke(3.0f * e.fout());
        Draw.color(Color.white, e.color, e.fin());
        Lines.beginLine();
        Lines.linePoint(e.x, e.y);

        rand.setSeed(e.id * 2L);

        for(int i = 0; i < links; i++){
            float nx, ny;
            if(i == links - 1){
                nx = tx;
                ny = ty;
            }else{
                float len = (i + 1) * spacing;
                v.setToRandomDirection(rand).scl(range * 0.5f);
                nx = e.x + normx * len + v.x;
                ny = e.y + normy * len + v.y;
            }

            Lines.linePoint(nx, ny);
        }

        Lines.endLine();
    }).followParent(false).rotWithParent(false);

    /** @see Fx#reactorExplosion */
    public static final Effect reactorExplosionLarge = new Effect(30.0f, 500.0f, b -> {
        float intensity = 6.8f * (b.data instanceof Float f ? f : 1.0f),
            baseLifetime = 25.0f + intensity * 11.0f;

        b.lifetime = 40.0f + intensity * 45.0f;

        Draw.color(Pal.reactorPurple2);
        Draw.alpha(0.7f);
        var rand = Fx.rand;
        for(int i = 0; i < 4; i++){
            int j = i;
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
                Lines.stroke((3.1f + intensity  * 0.2f) * i.fout());
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

    public static final Effect ghostEffect = new Effect(12.0f, e -> {
        if(!(e.data instanceof TextureRegion region)) return;
        Draw.color(e.color, e.fout());
        Draw.rect(region, e.x, e.y, e.rotation);
    });
    
    private static void tri(float x, float y, float width, float length, float angle) {
        float wx = Angles.trnsx(angle + 90, width), wy = Angles.trnsy(angle + 90, width);
        Fill.tri(x + wx, y + wy, x - wx, y - wy, Angles.trnsx(angle, length) + x, Angles.trnsy(angle, length) + y);
    }

    public static class TransitionFxData{
        public final UnitType type;
        public final boolean transOut;
        public TransitionFxData(UnitType type, boolean transOut){
            this.type = type;
            this.transOut = transOut;
        }
    }

    public static class TpFxData{
        public final Unit spawned;
        public final float x;
        public final float y;
        public TpFxData(Unit spawned, float x, float y){
            this.spawned = spawned;
            this.x = x;
            this.y = y;
        }
    }
}
