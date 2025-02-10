package fire.entities.bullets;

import arc.math.Mathf;
import arc.struct.IntFloatMap;
import arc.struct.IntMap;
import arc.util.Time;
import fire.content.FItems;
import fire.content.FStatusEffects;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.type.Item;

public class FleshBulletType extends SpritesBulletType{

    public float adhereChance;

    public byte removeAmount;
    public float maxSpread;
    public float spreadIntensity;

    private final FleshBulletType adhereType;
    public static final Item ITEM = FItems.flesh;

    private static final IntMap<Healthc> adheringMap = new IntMap<>();
    private static final IntFloatMap intensityMap = new IntFloatMap();

    public FleshBulletType(float speed, float damage, int size, float subDamage, float subLifetime){
        super(speed, damage, size, size, ITEM.frames, ITEM.frameTime, ITEM.name);
        hitSize = size;
        status = FStatusEffects.overgrown;
        hitEffect = despawnEffect = Fx.none;

        var type = (FleshBulletType)copy();
        type.speed = 0.0f;
        type.damage = subDamage; //damage deals in 1s
        type.lifetime = subLifetime;
        type.pierceArmor = true;
        type.collidesTiles = type.collidesAir = type.collidesGround = type.collides = false;
        adhereType = type;
    }

    private Healthc adhering(Bullet b){
        return adheringMap.get(b.id);
    }

    private float intensity(Bullet b){
        return intensityMap.get(b.id, 1.0f);
    }

    @Override
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct){
        super.hitTile(b, build, x, y, initialHealth, direct);
        afterHit(b, build);
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health){
        super.hitEntity(b, entity, health);
        afterHit(b, (Healthc)entity);
    }

    private void afterHit(Bullet b, Healthc entity){
        if(Mathf.chance(adhereChance)){
            var bullet = adhereType.create(b, b.x, b.y, b.rotation());
            // these are unused... right?
            bullet.originX = b.x - entity.x();
            bullet.originY = b.y - entity.y();
            adheringMap.put(bullet.id, entity);
        }
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        var entity = adhering(b);
        if(entity == null || entity.health() <= 0.0f) return;

        b.set(entity.x() + b.originX, entity.y() + b.originY);

        if(entity instanceof Building build){
            b.damage *= Time.delta;
            build.collision(b);
            b.damage /= Time.delta;

            if(intensity(b) < maxSpread && build.liquids != null && build.liquids.get(Liquids.water) > removeAmount * Time.delta){
                build.liquids.remove(Liquids.water, removeAmount * Time.delta);
                intensityMap.increment(b.id, 1.0f, spreadIntensity * Time.delta);
                b.time -= Time.delta;
            }

        }else{
            entity.damageContinuousPierce(damage * intensity(b));
        }
    }

    @Override
    public void removed(Bullet b){
        super.removed(b);
        adheringMap.remove(b.id);
        intensityMap.remove(b.id, 1.0f);
    }

    @Override
    protected float speedScale(Bullet b){
        return adhering(b) != null ? b.foutpow() * intensity(b) : super.speedScale(b);
    }

    @Override
    protected float sizeScale(Bullet b){
        return adhering(b) != null ? b.foutpow() * intensity(b) : super.sizeScale(b);
    }
}
