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
import fire.FRUtils;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.entities.Mover;
import mindustry.game.Team;
import mindustry.gen.Entityc;
import mindustry.graphics.Drawf;

public class LightningCloudBulletType extends mindustry.entities.bullet.BulletType{

    public final byte baseChancePercentage; //a value of 50 -> 50%
    public final byte baseSize;
    public final byte plusSize;

    private static final TextureRegion region = new TextureRegion();
    /** In; Charge; Release; Out. */
    private static final FRUtils.TimeNode node = new FRUtils.TimeNode(30, 150, 300, 330);

    public LightningCloudBulletType(float dmg, int len, int chance, int bs, int ps, Color color){
        super(0.0f, dmg);
        lightningLength = len;
        baseChancePercentage = (byte)chance;
        baseSize = (byte)bs;
        plusSize = (byte)ps;
        lightningColor = color;
        collides = false;
        hitEffect = despawnEffect = Fx.none;
    }

    @Override
    public void load(){
        if(region.u == 0.0f) region.set(FRUtils.find("lightning-cloud"));
        super.load();
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
        bullet.lifetime = node.last();
        bullet.intensity = Mathf.random(0.75f, 1.33f);
        bullet.add();
        return bullet;
    }

    public static class LightningCloud extends mindustry.gen.Bullet{

        private float intensity;

        public static LightningCloud create(){
            return Pools.obtain(LightningCloud.class, LightningCloud::new);
        }

        @Override
        public void update(){
            time += delta();

            if(time >= node.get(1) && time < node.get(2) + 10.0f && Mathf.chanceDelta(type().baseChancePercentage * 0.01 + intensity * 0.04)){
                Lightning.create(team, type.lightningColor, damage * intensity, x, y, Mathf.random(360.0f), (int)((type.lightningLength + Mathf.random(type.lightningLengthRand)) * Mathf.pow(intensity, 1.2f)));
            }else if(time >= lifetime){
                remove();
            }
        }

        @Override
        public void draw(){
            final float alpha = 0.1f +
                (node.checkBelonging(time, 0) ? time / node.first()
                : node.checkBelonging(time, 1) ? 1.0f
                : 1.0f - (time - node.get(2)) / node.lastQuantum());

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
            final float
                base = type().baseSize, plus = type().plusSize,
                scl = plus * 0.3f, mag = plus * 0.4f;

            return node.checkBelonging(time, 0) ? base + plus * (time / node.first())
                : node.checkBelonging(time, 1, 2) ? base + plus + Mathf.sin(time - node.first(), scl, mag)
                : (1.0f - (time - node.get(2)) / node.lastQuantum()) * (base + plus + Mathf.sin(node.getQuantum(1, 2), scl, mag));
        }

        private float delta(){
            return Time.delta / intensity;
        }
    }
}
