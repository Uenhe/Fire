package fire.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Time;
import fire.input.FRBinding;
import mindustry.ai.types.CommandAI;
import mindustry.content.StatusEffects;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

import static mindustry.Vars.tilesize;

/** @implSpec Ensure this is the first to be added to unit's abilities. */
public class DashAbility extends mindustry.entities.abilities.Ability{

    public final float speedMultiplier;
    public final short invincibleTime;
    public final short cooldown;
    /** Number of afterimages to be displayed while dashing. */
    public final byte afterimage;

    private float timer;

    public DashAbility(float speedMul, int invTime, int cd, int image){
        speedMultiplier = speedMul;
        timer = invincibleTime = (short)invTime;
        cooldown = (short)cd;
        afterimage = (byte)image;
    }

    @Override
    public String getBundle(){
        return "ability.fire-dash";
    }

    @Override
    public void addStats(Table t){
        super.addStats(t);
        t.add(abilityStat("invincibletime", Strings.autoFixed(invincibleTime / 60.0f, 2))).row();
        t.add(abilityStat("cooldown", Strings.autoFixed(cooldown / 60.0f, 2)));
    }

    @Override
    public void update(Unit unit){
        timer = Math.min(timer + Time.delta, cooldown);

        if(unit.controller() instanceof Player || unit.controller() instanceof CommandAI)
            dash(unit, null);
    }

    @Override
    public void draw(Unit unit){
        float t = timer * 0.6f;
        if(t > invincibleTime) return;

        Draw.color(Color.white);
        Draw.z((unit.type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) - 0.001f);

        for(int i = 0, afterimage = this.afterimage; i < afterimage; i++){
            float offset = unit.type.engineOffset * 0.5f * (1.0f + (unit.type.useEngineElevation ? unit.elevation : 1.0f)) + (i * unit.speedMultiplier * 8.0f * (1.0f - t / invincibleTime));
            Draw.alpha(0.8f * (1.0f - (t / invincibleTime)) * (1.0f - (float)i / afterimage));
            Draw.rect(unit.type.name,
                unit.x + Angles.trnsx(unit.rotation + 180.0f, offset),
                unit.y + Angles.trnsy(unit.rotation + 180.0f, offset),
                unit.rotation - 90.0f
            );
        }

        Draw.reset();
    }

    public void dash(Unit unit, Position pos){
        if(timer < cooldown || !unit.moving() || !(dashable(unit, pos))) return;

        timer -= cooldown;
        unit.apply(StatusEffects.invincible, invincibleTime);
        unit.vel.scl(speedMultiplier);
    }

    private boolean dashable(Unit unit, @Nullable Position pos){
        return (pos != null && correctDirection(unit, pos))
            || (unit.controller() instanceof Player && ((Core.app.isDesktop() && Core.input.keyDown(FRBinding.unitAbility)) || (Core.app.isMobile() && Mathf.dst(unit.x, unit.y, Core.camera.position.x, Core.camera.position.y) >= dst(unit))))
            || (unit.controller() instanceof CommandAI c && c.hasCommand() && correctDirection(unit, c.targetPos));
    }

    private boolean correctDirection(Unit unit, Position pos){
        return !unit.within(pos, dst(unit)) && Angles.near(unit.rotation, unit.angleTo(pos), 10.0f);
    }

    private float dst(Unit unit){
        return unit.speed() * speedMultiplier * tilesize * 1.6f;
    }
}
