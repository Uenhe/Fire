package fire.entities.abilities;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.geom.Position;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Time;
import fire.input.FBinding;
import fire.world.meta.FStat;
import mindustry.ai.types.CommandAI;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.world.meta.StatUnit;

public class DashAbility extends mindustry.entities.abilities.Ability{

    private final float speedMultiplier;
    /** Tick that unit becomes invincible while dashing. */
    private final float invincibleTime;
    /** Dash cooldown. */
    private final float cooldown;
    /** Number of afterimages to be displayed while dashing. */
    private final byte afterimage;

    private static final String name = "ability.fire-dash";
    private float timer;

    public DashAbility(float speedMul, float invTime, float cooldown, int afterimage){
        this.speedMultiplier = speedMul;
        this.invincibleTime = invTime;
        this.cooldown = cooldown;
        this.afterimage = (byte)afterimage;
    }

    @Override
    public String localized(){
        return Core.bundle.get(name);
    }

    /** TODO Change this if v147 is released: <a href="https://github.com/Anuken/Mindustry/pull/9654">DETAIL</a> */
    @Override
    public void addStats(arc.scene.ui.layout.Table t){
        t.add(Core.bundle.get(name + ".description"));
        t.row();
        t.add("[lightgray]" + FStat.invincibleTime.localized() + ": [white]" + Strings.autoFixed(invincibleTime / 60f, 2) + " " + StatUnit.seconds.localized());
        t.row();
        t.add("[lightgray]" + mindustry.world.meta.Stat.cooldownTime.localized() + ": [white]" + Strings.autoFixed(cooldown / 60f, 2) + " " + StatUnit.seconds.localized());
        t.row();
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
            final float offset = unit.type.engineOffset * 0.5f * (1f + (unit.type.useEngineElevation ? unit.elevation : 1f)) + (i * 8f);
            final float
                cx = unit.x + Angles.trnsx(unit.rotation + 180f, offset),
                cy = unit.y + Angles.trnsy(unit.rotation + 180f, offset);
            Draw.alpha(0.6f * (1f - (timer / invincibleTime)) * (1f - (float)i / afterimage));
            Draw.rect(unit.type.name, cx, cy, unit.rotation - 90f);
        }

        Draw.reset();
    }

    public void dash(Unit unit, Position pos){
        if(timer < cooldown || !unit.moving() || !(canDash(unit, pos))) return;

        timer %= cooldown;
        unit.apply(mindustry.content.StatusEffects.invincible, invincibleTime);
        unit.vel.setLength(unit.speed() * speedMultiplier);
    }

    private boolean canDash(Unit unit, @Nullable Position pos){
        return (unit.controller() instanceof Player && ((Core.app.isDesktop() && Core.input.keyDown(FBinding.unit_ability)) || (Core.app.isMobile() && arc.math.Mathf.dst(unit.x, unit.y, Core.camera.position.x, Core.camera.position.y) >= dst(unit))))
            || (unit.controller() instanceof CommandAI c && c.hasCommand() && correctPos(unit, c.targetPos))
            || (pos != null && correctPos(unit, pos));
    }

    private boolean correctPos(Unit unit, Position pos){
        return !unit.within(pos, dst(unit)) && Angles.near(unit.rotation, unit.angleTo(pos), 15f);
    }

    private float dst(Unit unit){
        return unit.speed() * speedMultiplier * mindustry.Vars.tilesize * 1.3f;
    }
}
