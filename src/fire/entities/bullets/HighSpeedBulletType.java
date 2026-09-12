package fire.entities.bullets;

import arc.math.Mathf;
import arc.struct.ObjectIntMap;
import arc.util.Nullable;
import arc.util.Time;
import fire.content.FRFx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

import static fire.FRVars.find;

public class HighSpeedBulletType extends BasicBulletType{
    public float velDamageBoost = 30.0f; //speed + 1 -> damage + velDamageBoost
    public int hitFrags = 5;
    public float pierceSpeed = 0.5f;
    public float statusDuration = 300.0f;
    public float despawnSplashDamage = 300.0f;
    public StatusEffect shootStatus = StatusEffects.overclock;
    public float healingPercent = 100.0f;
    public float extraShield = 1.0f;
    public BulletType frag;

    public HighSpeedBulletType(float speed,float damage){
        super(speed,damage);
    }

    @Override
    public void hit(Bullet b){
        super.hit(b);

        specialHeal(b, b.vel.len() * velDamageBoost * healingPercent * 0.01f, shootStatus, statusDuration);

        Damage.damage(b.x, b.y, 12, b.vel.len() * velDamageBoost);
    }

    @Override
    public void handlePierce(Bullet b, float initialHealth, float x, float y){
        super.handlePierce(b, initialHealth, x, y);
        Damage.damage(x, y, 12, b.vel.len() * 40.0f);
        if(b.vel.len() >= 20.0f){
            FRFx.swordMarkEffect(40.0f, x + 16.0f, y + 16.0f, x - 16.0f, y - 16.0f, 4.0f, 8.0f, find("ffd8e8"), false).at(b.x, b.y);
            FRFx.swordMarkEffect(40.0f, x - 16.0f, y + 16.0f, x + 16.0f, y - 16.0f, 4.0f, 8.0f, find("ffd8e8"), false).at(b.x, b.y);
        }

        specialHeal(b, b.vel.len() * velDamageBoost * healingPercent * 0.01f, shootStatus, statusDuration);

        b.vel.x *= pierceSpeed;
        b.vel.y *= pierceSpeed;
        b.lifetime = Math.min(b.lifetime + (b.lifetime - b.time), 40.0f);
        if(fragOnHit && frag != null){
            frag.create(b, b.x, b.y, b.rotation() + 5.0f * 16.0f / b.vel.len(), b.vel.len());
            frag.create(b, b.x, b.y, b.rotation() - 5.0f * 16.0f / b.vel.len(), b.vel.len());
        }
    }

    @Override
    public void removed(Bullet b){
        if(b.vel.len() <= 1.25f){
            Damage.damage(b.x, b.y, 30.0f, despawnSplashDamage);
            FRFx.swordMarkEffect(40.0f, b.x + 16.0f, b.y + 16.0f, b.x - 16.0f, b.y - 16.0f, 4.0f, 8.0f, find("ffd8e8"), false).at(b.x, b.y);
            FRFx.swordMarkEffect(40.0f, b.x - 16.0f, b.y + 16.0f, b.x + 16.0f, b.y - 16.0f, 4.0f, 8.0f, find("ffd8e8"), false).at(b.x, b.y);
        }else if(frag != null){
            float rot = 360.0f / b.vel.len() / hitFrags;
            for(int i = 0; i < hitFrags; i++){
                frag.create(b, b.x, b.y, b.rotation() - rot * hitFrags * 0.5f + rot * i, b.vel.len());
            }
        }
        super.removed(b);
    }

    public void specialHeal(Bullet b, float amount, @Nullable StatusEffect effect, float duration){
        Unit unit = (Unit)b.owner;
        if(unit.health >= unit.maxHealth){
            if(unit.shield < unit.maxHealth * extraShield){
                unit.shield(Math.min(unit.shield + amount, unit.maxHealth));
            }
        }else{
            unit.heal(amount);
        }
        if(effect != null)unit.apply(effect, duration);
    }
}
