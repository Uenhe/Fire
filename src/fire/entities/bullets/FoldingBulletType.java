package fire.entities.bullets;

import arc.Events;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.Time;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Bullet;

/** Bullet type that folds around its owner bullet. */
public class FoldingBulletType extends mindustry.entities.bullet.BasicBulletType{

    public final byte foldDegree;
    public final float foldInterval;

    static float mapCleanTimer;
    static final ObjectMap<Bullet, Float> timerMap = new ObjectMap<>();

    public FoldingBulletType(float ownerSpeed, float dmg, int degree, float ownerLifetime){
        speed = ownerSpeed / Mathf.cosDeg(degree);
        damage = dmg;
        foldDegree = (byte)degree;
        foldInterval = ownerLifetime / 4; //fold for 4 times
    }

    @Override
    public void load(){
        super.load();

        Events.run(Trigger.update, () -> {
            mapCleanTimer += Time.delta;

            // clean every 300 ticks
            if(mapCleanTimer >= 300.0f){
                mapCleanTimer -= 300.0f;

                for(var entry : timerMap)
                    if(!entry.key.isAdded())
                        timerMap.remove(entry.key);
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

        timerMap.put(b, timerMap.get(b) + Time.delta);
        if(timerMap.get(b) >= foldInterval){
            timerMap.put(b, timerMap.get(b) - foldInterval);

            int sign = Mathf.sign((b.time / foldInterval) % 2 == 1);
            b.rotation(b.rotation() + foldDegree * 2.0f * sign);
        }
    }
}
