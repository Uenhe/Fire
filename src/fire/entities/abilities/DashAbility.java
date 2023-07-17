package fire.entities.abilities;

import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import fire.input.FireBinding;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

import static fire.FireLib.desktop;

public class DashAbility extends Ability{
    /** Unstable... */
    public float speedMultiplier;
    /** How many frames unit becomes invincible after dashing. */
    public float invincibleTime;
    /** Dash cooldown. */
    public float cooldown;
    /** Effect blooms while dashing. */
    public Effect dashEffect;

    protected float cooldownTimer;

    public DashAbility(float speedMul, float invTime, float cd, Effect dashFx){
        speedMultiplier = speedMul;
        invincibleTime = invTime;
        cooldown = cd;
        dashEffect = dashFx;
    }

    @Override
    public void update(Unit unit){
        super.update(unit);
        if(cooldownTimer < invincibleTime){
            float offset = unit.type.engineOffset / 2f * (1f + (unit.type.useEngineElevation ? unit.elevation : 1f));
            float cx = unit.x + Angles.trnsx(unit.rotation + 180f, offset);
            float cy = unit.y + Angles.trnsy(unit.rotation + 180f, offset);
            float chance = Mathf.lerpDelta(invincibleTime, cooldownTimer, 0.16f);
            if(Mathf.chanceDelta(chance / invincibleTime)) dashEffect.at(cx, cy);
        }
        if(cooldownTimer < cooldown){
            cooldownTimer += Time.delta;
        }else if(unit.moving() && desktop() && Core.input.keyDown(FireBinding.unit_ability)){
            cooldownTimer = 0f;
            unit.apply(StatusEffects.invincible, invincibleTime);
            unit.vel.setLength(unit.speed() * speedMultiplier);
            dashEffect.at(unit);
        }
    }
}
