package fire.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

import static mindustry.Vars.tilesize;

public class RegenFieldAbility extends mindustry.entities.abilities.Ability{

    public final float amount;
    public final float radius;
    public final Color lineColor = new Color();

    private float warmup, totalProgress;

    public RegenFieldAbility(float amount, float radius, Color color){
        this.amount = amount;
        this.radius = radius;
        this.lineColor.set(color);
    }

    @Override
    public String getBundle(){
        return "ability.fire-regenfield";
    }

    @Override
    public void addStats(Table t){
        super.addStats(t);
        t.add(Core.bundle.format("bullet.range", Strings.autoFixed(radius / tilesize, 2))).row();
        t.add(abilityStat("repairspeed", Strings.autoFixed(amount * 60.0f, 2)));
    }

    @Override
    public void update(Unit unit){
        final float spd = 120.0f;

        boolean[] any = new boolean[1];
        Units.nearby(unit.team, unit.x, unit.y, radius, u -> {
            if(u.damaged()){
                u.heal(amount * Time.delta);
                any[0] = true;
            }
        });

        totalProgress += Time.delta / spd;
        warmup = Mathf.lerpDelta(warmup, Mathf.num(any[0]), 0.08f);
    }

    @Override
    public void draw(Unit unit){
        if(warmup < 0.001f) return;

        final float lineStroke = 3.0f,
        mod = totalProgress % 1.0f;

        Draw.z(Layer.effect);
        Draw.color(lineColor);
        Lines.stroke(lineStroke * (1.0f - mod) * warmup);
        Lines.circle(unit.x, unit.y, radius * mod);

        Draw.reset();
    }
}
