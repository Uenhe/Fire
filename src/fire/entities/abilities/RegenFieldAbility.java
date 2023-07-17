package fire.entities.abilities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

import static mindustry.Vars.tilesize;
import static fire.FireLib.format;

public class RegenFieldAbility extends Ability{
    public float healAmount, healRadius, lineSpeed, lineStroke;
    public Color lineColor;

    protected float warmup, totalProgress;
    protected Seq<Unit> targets = new Seq<>();

    public RegenFieldAbility(float amount, float radius, Color color, float speed, float stroke){
        healAmount = amount;
        healRadius = radius;
        lineColor = color;
        lineSpeed = speed;
        lineStroke = stroke;
    }

    @Override
    public String localized(){
        return format("ability.fire-regenfield", healAmount * 60, healRadius / tilesize);
    }

    @Override
    public void update(Unit unit){
        targets.clear();
        Units.nearby(unit.team, unit.x, unit.y, healRadius, u -> {
            if(u.damaged()) targets.add(u);
        });
        boolean draws = false;
        for(Unit target : targets){
            if(target.damaged()){
                target.heal(healAmount);
                draws = true;
            }
        }
        warmup = Mathf.lerpDelta(warmup, Mathf.num(draws), 0.08f);
        totalProgress += Time.delta / lineSpeed;
    }

    @Override
    public void draw(Unit unit){
        if(warmup > 0.001f){
            Draw.z(Layer.effect);
            float mod = totalProgress % 1f;
            Draw.color(lineColor);
            Lines.stroke(lineStroke * (1f - mod) * warmup);
            Lines.circle(unit.x, unit.y, healRadius * mod);
            Draw.reset();
        }
    }
}
