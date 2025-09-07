package fire.entities.bullets;

import arc.Events;
import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import arc.util.Time;
import fire.content.FRItems;
import fire.content.FRStatusEffects;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.type.Item;

public class FleshBulletType extends SpritesBulletType{

    public byte adhereChancePercentage; //a value of 50 -> 50%
    public byte removeAmount;
    public float maxSpread;
    public float spreadIntensity;

    private final FleshBulletType adhereType;
    private static final Item ITEM = FRItems.flesh;

    private static final ObjectFloatMap<Bullet> intensityMap = new ObjectFloatMap<>();

    static{
        Events.on(EventType.ResetEvent.class, e -> intensityMap.clear());
    }

    public FleshBulletType(float speed, float damage, int size, float subDamage, float subLifetime){
        super(speed, damage, size, size, ITEM.frames, ITEM.frameTime, ITEM.name);
        hitSize = size;
        status = FRStatusEffects.overgrown;

        var type = (FleshBulletType)copy();
        type.speed = 0.0f;
        type.damage = subDamage; //damage deals in 1s
        type.lifetime = subLifetime;
        type.pierceArmor = true;
        type.collidesTiles = type.collidesAir = type.collidesGround = type.collides = false;
        adhereType = type;
    }

    protected void afterAssignment(){
        adhereType.removeAmount = removeAmount;
        adhereType.maxSpread = maxSpread;
        adhereType.spreadIntensity = spreadIntensity;
    }

    private float intensity(Bullet b){
        return intensityMap.get(b, 1.0f);
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
        if(!Mathf.chance(adhereChancePercentage * 0.01)) return;
        adhereType.create(b, b.x, b.y, b.rotation()).stickTo(entity);
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(!(b.stickyTarget instanceof Healthc entity) || entity.health() <= 0.0f) return;

        if(entity instanceof Building build){
            b.damage *= Time.delta;
            build.collision(b);
            b.damage /= Time.delta;

            float amount = removeAmount * Time.delta;
            if(intensity(b) < maxSpread && build.liquids != null && build.liquids.get(Liquids.water) > amount){
                build.liquids.remove(Liquids.water, amount);
                intensityMap.increment(b, 1.0f, spreadIntensity * Time.delta);
                b.time -= Time.delta * 0.95f;
            }

        }else{
            if(intensity(b) < maxSpread && ((Unit)entity).hasEffect(StatusEffects.wet)){
                ((Unit)entity).apply(StatusEffects.wet, ((Unit)entity).getDuration(StatusEffects.wet) - 10.0f);
                intensityMap.increment(b, 1.0f, spreadIntensity * Time.delta);
                b.time -= Time.delta * 0.95f;
            }
            entity.damageContinuousPierce(damage * intensity(b));
        }
    }

    @Override
    public void removed(Bullet b){
        intensityMap.remove(b, 0.0f);
        super.removed(b);
    }

    @Override
    protected float speedScale(Bullet b){
        return b.stickyTarget != null ? b.foutpow() * intensity(b) : super.speedScale(b);
    }

    @Override
    protected float sizeScale(Bullet b){
        return b.stickyTarget != null ? b.foutpow() * intensity(b) : super.sizeScale(b);
    }
}
