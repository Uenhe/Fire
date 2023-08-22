package fire.entities.abilities;

import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import fire.input.FireBinding;
import mindustry.ai.types.CommandAI;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Player;
import mindustry.gen.Unit;

import static mindustry.Vars.*;

public class DashAbility extends Ability{
    /** Unstable... */
    public float speedMultiplier;
    /** The tick unit becomes invincible after dashing. */
    public float invincibleTime;
    /** Dash cooldown. */
    public float cooldown;
    /** Effect blooms while dashing. */
    public Effect dashEffect;

    protected float cooldownTimer;

    public DashAbility(float speedMul, float invincibleTime, float cooldown, Effect dashFx){
        this.speedMultiplier = speedMul;
        this.invincibleTime = invincibleTime;
        this.cooldown = cooldown;
        this.dashEffect = dashFx;
    }

    @Override
    public void update(Unit unit){
        super.update(unit);
        dash(unit);
    }

    protected boolean correctRot(Unit unit, float tarX, float tarY){
        return Angles.angleDist(unit.rotation, Angles.angle(unit.x, unit.y, tarX, tarY)) < 15f;
    }

    public void dash(Unit unit, float targetX, float targetY){
        float dst = unit.speed() * speedMultiplier * unit.dragMultiplier * state.rules.dragMultiplier * tilesize;
        if(cooldownTimer < invincibleTime){
            float offset = unit.type.engineOffset / 2f * (1f + (unit.type.useEngineElevation ? unit.elevation : 1f));
            float cx = unit.x + Angles.trnsx(unit.rotation + 180f, offset);
            float cy = unit.y + Angles.trnsy(unit.rotation + 180f, offset);
            float chance = Mathf.lerpDelta(invincibleTime, cooldownTimer, 0.16f);
            if(Mathf.chanceDelta(chance / invincibleTime)) dashEffect.at(cx, cy);
        }
        if(cooldownTimer < cooldown){
            cooldownTimer += Time.delta;
        }else if(
            //I hate this condition
            (
                (
                    unit.controller() instanceof Player && Core.input.keyDown(FireBinding.unit_ability)
                ) || (
                    unit.controller() instanceof CommandAI command && command.hasCommand() && !unit.within(command.targetPos, dst) && correctRot(unit, command.targetPos.x, command.targetPos.y)
                ) || (
                    targetX == 0f && targetY == 0f && !unit.within(targetX, targetY, dst) && correctRot(unit, targetX, targetY)
                )
            ) && unit.moving()
        ){
            cooldownTimer = 0f;
            unit.apply(StatusEffects.invincible, invincibleTime);
            unit.vel.setLength(unit.speed() * speedMultiplier);
            dashEffect.at(unit);
        }
    }

    public void dash(Unit unit){
        dash(unit, 0f, 0f);
    }
}
