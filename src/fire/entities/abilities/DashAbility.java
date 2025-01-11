package fire.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import fire.input.FireBinding;
import fire.world.meta.FireStat;
import mindustry.ai.types.CommandAI;
import mindustry.content.StatusEffects;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class DashAbility extends mindustry.entities.abilities.Ability{
<<<<<<< Updated upstream
    /** Unstable... */
    public float speedMultiplier;
    /** The tick unit becomes invincible while dashing. */
    public float invincibleTime;
    /** Dash cooldown. */
    public float cooldown;
    /** The number of afterimages to be displayed while dashing. */
    public int afterimage;
=======

    public final float speedMultiplier;
    /** Tick that unit becomes invincible while dashing. */
    public final short invincibleTime;
    /** Dash cooldown. */
    public final short cooldown;
    /** Number of afterimages to be displayed while dashing. */
    public final byte afterimage;
>>>>>>> Stashed changes

    private float timer;

    public DashAbility(float speedMul, float invincibleTime, float cooldown, int afterimage){
        this.speedMultiplier = speedMul;
        this.invincibleTime = invincibleTime;
        this.cooldown = cooldown;
        this.afterimage = afterimage;
    }

    @Override
    public String localized(){
        return Core.bundle.get("ability.fire-dash");
    }

    @Override
    public void addStats(Table t){
        t.add("[lightgray]" + FireStat.invincibleTime.localized() + ": [white]" + Strings.autoFixed(invincibleTime / 60f, 2) + " " + StatUnit.seconds.localized());
        t.row();
        t.add("[lightgray]" + Stat.cooldownTime.localized() + ": [white]" + Strings.autoFixed(cooldown / 60f, 2) + " " + StatUnit.seconds.localized());
        t.row();
    }

    @Override
    public void update(Unit unit){
        float dst = unit.speed() * speedMultiplier * tilesize * 1.3f;
        timer += Time.delta;
        timer = Math.min(timer, cooldown);

        if(timer >= cooldown && unit.moving() &&
            (
                (unit.controller() instanceof Player && Core.app.isDesktop() && Core.input.keyDown(FireBinding.unit_ability))
            ||
                (unit.controller() instanceof Player && Core.app.isMobile() && Mathf.dst(unit.x, unit.y, Core.camera.position.x, Core.camera.position.y) >= dst)
            ||
                (unit.controller() instanceof CommandAI command && command.hasCommand() && !unit.within(command.targetPos, dst) && correctRot(unit, command.targetPos))

            )
        ){
            timer %= cooldown;
            unit.apply(StatusEffects.invincible, invincibleTime);
            unit.vel.setLength(unit.speed() * speedMultiplier);
        }
    }

    public void draw(Unit unit){
        if(timer > invincibleTime) return;

        Draw.color(Color.white);
        Draw.z((unit.type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) - 0.001f);
        for(int i = 0; i < afterimage; i++){
            float offset = unit.type.engineOffset * 0.5f * (1f + (unit.type.useEngineElevation ? unit.elevation : 1f)) + (i * 8);
            float cx = unit.x + Angles.trnsx(unit.rotation + 180f, offset);
            float cy = unit.y + Angles.trnsy(unit.rotation + 180f, offset);
            Draw.alpha(0.6f * (1f - (timer / invincibleTime)) * (1f - (float)(i / afterimage)));
            Draw.rect(unit.type.name, cx, cy, unit.rotation - 90f);
        }
        Draw.reset();
    }

    protected boolean correctRot(Unit unit, Vec2 other){
        return Angles.near(unit.rotation, Angles.angle(unit.x, unit.y, other.x, other.y), 15f);
    }
}
