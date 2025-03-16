package fire.entities.bullets;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.graphics.Drawf;

public class LightningCloudBulletType extends BulletType{

    public final float baseChance;
    public final byte baseSize;
    public final byte plusSize;

    private static TextureRegion region;
    /** Set manually.<p>1st -> In<p>2nd -> Charge<p>3rd -> Release<p>4th -> Out */
    private static final short[] timeNodes = {30, 150, 300, 330}; //TODO should these be put into type?

    public LightningCloudBulletType(float dmg, int len, float chance, int bs, int ps, Color color){
        super(0.0f, dmg);
        lightningLength = len;
        baseChance = chance;
        baseSize = (byte)bs;
        plusSize = (byte)ps;
        lightningColor = color;
        collides = false;
        hitEffect = despawnEffect = Fx.none;
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find("fire-lightning-cloud");
    }

    public LightningCloud create(Entityc owner, float x, float y){
        return create(owner, null, null, x, y ,0.0f, damage, 0.0f, 0.0f, null, null, 0.0f, 0.0f);
    }

    /** Highly customized. */
    @Override
    public LightningCloud create(Entityc owner, Entityc e, Team t, float x, float y, float a, float damage, float v, float l, Object d, Mover m, float p, float q){
        var bullet = LightningCloud.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.set(x, y);
        bullet.damage = damage * bullet.damageMultiplier();
        bullet.time = 0.0f;
        bullet.lifetime = timeNodes[timeNodes.length - 1];
        bullet.intensity = Mathf.random(0.75f, 1.33f);
        bullet.add();
        return bullet;
    }

    public static class LightningCloud extends Bullet{

        private float intensity;

        public static LightningCloud create(){
            return Pools.obtain(LightningCloud.class, LightningCloud::new);
        }

        @Override
        public void update(){
            time += delta();

            if(time >= timeNodes[1] && time < timeNodes[2] + 10.0f && Mathf.chanceDelta(type().baseChance + intensity * 0.04f)){
                Lightning.create(team, type.lightningColor, damage * intensity, x, y, Mathf.random(360.0f), (int)((type.lightningLength + Mathf.random(type.lightningLengthRand)) * Mathf.pow(intensity, 1.2f)));
            }else if(time >= lifetime){
                remove();
            }
        }

        @Override
        public void draw(){
            float alpha;
            if(time <= timeNodes[0])
                alpha = time / timeNodes[0];
            else if(time <= timeNodes[1])
                alpha = 1.0f;
            else
                alpha = 1.0f - (time - timeNodes[2]) / timeNodes[0];

            alpha += 0.1f;
            Draw.color(type.lightningColor);
            Draw.alpha(alpha);
            Fill.circle(x, y, size() * 0.1f);
            Drawf.light(x, y, size(), type.lightningColor, alpha);
            Draw.blend(Blending.additive);
            Draw.rect(region, x, y, size() * intensity, size() * intensity, time * intensity * Mathf.sign(id % 2 == 0));
            Draw.blend();
        }

        @Override
        public LightningCloudBulletType type(){
            return (LightningCloudBulletType)type;
        }

        @Override
        public void reset(){
            super.reset();
            intensity = 0.0f;
        }

        private float size(){
            float
                base = type().baseSize, plus = type().plusSize,
                scl = plus * 0.3f, mag = plus * 0.4f;

            if(time <= timeNodes[0])
                return base + (time / timeNodes[0]) * plus;
            else if(time <= timeNodes[2])
                return base + plus + Mathf.sin(time - timeNodes[0], scl, mag);
            else
                return (1.0f - (time - timeNodes[2]) / timeNodes[0]) * (base + plus + Mathf.sin(timeNodes[2] - timeNodes[0], scl, mag));
        }

        private float delta(){
            return Time.delta / intensity;
        }
    }
}
