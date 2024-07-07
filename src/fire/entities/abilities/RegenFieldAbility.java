package fire.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class RegenFieldAbility extends mindustry.entities.abilities.Ability{

    private final float amount;
    private final float radius;
    private final Color lineColor;

    private float warmup, totalProgress;
    private final Seq<Unit> targets = new Seq<>();

    public RegenFieldAbility(float amount, float radius, Color color){
        this.amount = amount;
        this.radius = radius;
        this.lineColor = color;
    }

    @Override
    public String localized(){
        return Core.bundle.get("ability.fire-regenfield");
    }

    @Override
    public void addStats(Table t){
        t.add("[lightgray]" + Stat.repairSpeed.localized() + ": [white]" + Strings.autoFixed(amount * 60f, 2) + StatUnit.perSecond.localized());
        t.row();
        t.add("[lightgray]" + Stat.shootRange.localized() + ": [white]" + Strings.autoFixed(radius / tilesize, 2) + " " + StatUnit.blocks.localized());
    }

    @Override
    public void update(Unit unit){
        final float lineSpeed = 120.0f;

        targets.clear();
        Units.nearby(unit.team, unit.x, unit.y, radius, u -> {
            if(u.damaged()) targets.add(u);
        });

        boolean any = false;
        for(var target : targets)
            if(target.damaged()){
                target.heal(amount * Time.delta);
                any = true;
            }

        warmup = Mathf.lerpDelta(warmup, Mathf.num(any), 0.08f);
        totalProgress += Time.delta / lineSpeed;
    }

    @Override
    public void draw(Unit unit){
        if(warmup < 0.001f) return;

        final float lineStroke = 3.0f;
        float mod = totalProgress % 1.0f;

        Draw.z(Layer.effect);
        Draw.color(lineColor);
        Lines.stroke(lineStroke * (1.0f - mod) * warmup);
        Lines.circle(unit.x, unit.y, radius * mod);

        Draw.reset();
    }
}
