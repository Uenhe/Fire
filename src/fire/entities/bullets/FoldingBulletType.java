package fire.entities.bullets;

import arc.Events;
import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import arc.util.Time;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Bullet;

/** Bullet type that folds around its owner bullet. */
public class FoldingBulletType extends mindustry.entities.bullet.BasicBulletType{

    public final byte foldDegree;
    public final float foldInterval;

    private static float mapCleanTimer;
    private static final ObjectFloatMap<Bullet> timerMap = new ObjectFloatMap<>();

    public FoldingBulletType(float ownerSpeed, float dmg, int degree, float ownerLifetime){
        speed = ownerSpeed / Mathf.cosDeg(degree);
        damage = dmg;
        foldDegree = (byte)degree;
        foldInterval = ownerLifetime / 4; //fold for 4 times
    }

    @Override
    public void load(){
        super.load();

        // clean every 300 ticks
        final short interval = 300;
        Events.run(Trigger.update, () -> {
            mapCleanTimer += Time.delta;

            if(mapCleanTimer >= interval){
                mapCleanTimer -= interval;

                for(var entry : timerMap)
                    if(!entry.key.isAdded())
                        timerMap.remove(entry.key, 0.0f);
            }
        });
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.rotation(b.rotation() + foldDegree);
        timerMap.put(b, foldInterval * 0.5f);
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        timerMap.increment(b, 0.0f, Time.delta);

        if(timerMap.get(b, 0.0f) >= foldInterval){
            timerMap.increment(b, 0.0f, -foldInterval);
            b.rotation(b.rotation() + foldDegree * 2.0f * Mathf.sign((int)(b.time / foldInterval) % 2 == 1));
        }
    }
}
