package fire.entities.bullets;

import arc.math.Mathf;
import fire.entities.LightningBranch;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;

/** @see mindustry.entities.bullet.LightningBulletType  */
public class LightningBranchBulletType extends mindustry.entities.bullet.BulletType{

    /** Root number of lightning tree. */
    private final byte branchAmount;
    /** Number of times that lightning branches. */
    private final byte branchTime;

    public LightningBranchBulletType(float dmg, int amount, int time){
        super(0.0f, dmg);
        branchAmount = (byte)amount;
        branchTime = (byte)time;

        lifetime = 1.0f;
        despawnEffect = Fx.none;
        hitEffect = Fx.hitLancer;
        keepVelocity = false;
        hittable = false;

        status = StatusEffects.shocked;
        lightningColor = Pal.lancerLaser;
    }

    @Override
    protected float calculateRange(){
        return (lightningLength + lightningLengthRand * 0.5f) * 6.0f * Mathf.sqrt(branchTime);
    }

    @Override
    public float estimateDPS(){
        return super.estimateDPS() * Math.max(lightningLength * 0.1f, 1.0f) * Mathf.sqrt(branchAmount);
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        LightningBranch.create(b, lightningType, lightningColor, damage, b.rotation(), lightningLength + Mathf.random(lightningLengthRand), branchAmount, branchTime);
    }
}
