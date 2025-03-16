package fire.entities.bullets;

import arc.math.Mathf;
import arc.struct.IntIntMap;
import arc.util.Time;
import mindustry.gen.Bullet;

/** Bullet type that folds around its owner bullet. */
public class FoldingBulletType extends mindustry.entities.bullet.BasicBulletType{

    public final byte foldAngle;
    private final float foldInterval;

    private static final IntIntMap foldTimesMap = new IntIntMap();

    public FoldingBulletType(float ownerSpeed, float dmg, int angle, float ownerLifetime){
        speed = ownerSpeed / Mathf.cosDeg(angle);
        damage = dmg;
        foldAngle = (byte)angle;
        foldInterval = ownerLifetime / 4; //fold for 4 times
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.rotation(b.rotation() + foldAngle);
        b.mover = bl -> {
            int ft = foldTimesMap.get(bl.id);
            if(bl.time < foldInterval * (0.5f + ft) && bl.time + Time.delta >= foldInterval * (0.5f + ft)){
                foldTimesMap.increment(bl.id, 1);
                bl.rotation(bl.rotation() + 2.0f * foldAngle * Mathf.sign(ft % 2 == 1));
            }
        };
    }

    @Override
    public void removed(Bullet b){
        super.removed(b);
        foldTimesMap.remove(b.id);
    }
}
