package fire.entities.abilities;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Time;
import fire.input.FBinding;
import fire.world.meta.FStat;
import mindustry.ai.types.CommandAI;
import mindustry.content.StatusEffects;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class DashAbility extends mindustry.entities.abilities.Ability{

    public final float speedMultiplier;
    /** Tick that unit becomes invincible while dashing. */
    public final short invincibleTime;
    /** Dash cooldown, in tick. */
    public final short cooldown;
    /** Number of afterimages to be displayed while dashing. */
    public final byte afterimage;

    float timer;
    public static final String name = "ability.fire-dash";

    public DashAbility(float speedMul, int invTime, int cooldown, int afterimage){
        this.speedMultiplier = speedMul;
        this.invincibleTime = (short)invTime;
        this.cooldown = (short)cooldown;
        this.afterimage = (byte)afterimage;
    }

    @Override
    public String localized(){
        return Core.bundle.get(name);
    }

    /** TODO Reconstruct <a href="https://github.com/Anuken/Mindustry/pull/9654">if v147 is released</a> */
    @Override
    public void addStats(Table t){
        t.add(Core.bundle.get(name + ".description"));
        t.row();
        t.add("[lightgray]" + FStat.invincibleTime.localized() + ": [white]" + Strings.autoFixed(invincibleTime / 60.0f, 2) + " " + StatUnit.seconds.localized());
        t.row();
        t.add("[lightgray]" + mindustry.world.meta.Stat.cooldownTime.localized() + ": [white]" + Strings.autoFixed(cooldown / 60.0f, 2) + " " + StatUnit.seconds.localized());
    }

    @Override
    public void update(Unit unit){
        timer = Math.min(timer + Time.delta, cooldown);

        if(unit.controller() instanceof Player || unit.controller() instanceof CommandAI)
            dash(unit, null);
    }

    @Override
    public void draw(Unit unit){
        if(timer > invincibleTime) return;

        Draw.color(arc.graphics.Color.white);
        Draw.z((unit.type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) - 0.001f);

        for(byte i = 0; i < afterimage; i++){
            float offset = unit.type.engineOffset * 0.5f * (1.0f + (unit.type.useEngineElevation ? unit.elevation : 1f)) + (i * unit.speedMultiplier * 8.0f);
            float
                cx = unit.x + Angles.trnsx(unit.rotation + 180.0f, offset),
                cy = unit.y + Angles.trnsy(unit.rotation + 180.0f, offset);
            Draw.alpha(0.6f * (1.0f - (timer / invincibleTime)) * (1.0f - (float)i / afterimage));
            Draw.rect(unit.type.name, cx, cy, unit.rotation - 90.0f);
        }

        Draw.reset();
    }

    public void dash(Unit unit, Position pos){
        if(timer < cooldown || !unit.moving() || !(canDash(unit, pos))) return;

        timer -= cooldown;
        unit.apply(StatusEffects.invincible, invincibleTime);
        unit.vel.setLength(unit.speed() * speedMultiplier);
    }

    private boolean canDash(Unit unit, @Nullable Position pos){
        return (unit.controller() instanceof Player && ((Core.app.isDesktop() && Core.input.keyDown(FBinding.unit_ability)) || (Core.app.isMobile() && Mathf.dst(unit.x, unit.y, Core.camera.position.x, Core.camera.position.y) >= dst(unit))))
            || (unit.controller() instanceof CommandAI c && c.hasCommand() && correctDirection(unit, c.targetPos))
            || (pos != null && correctDirection(unit, pos));
    }

    private boolean correctDirection(Unit unit, Position pos){
        return !unit.within(pos, dst(unit)) && Angles.near(unit.rotation, unit.angleTo(pos), 15.0f);
    }

    private float dst(Unit unit){
        return unit.speed() * speedMultiplier * tilesize * 2.0f;
    }
}
