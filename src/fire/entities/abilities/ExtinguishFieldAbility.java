package fire.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

public class ExtinguishFieldAbility extends mindustry.entities.abilities.Ability{

    public float range;
    public Color color;

    private float warmup;

    public ExtinguishFieldAbility(float range, Color color){
        this.range = range;
        this.color = color;
    }

    @Override
    public String getBundle(){
        return "ability.fire-extinguishfield";
    }

    @Override
    public void addStats(Table t){
        super.addStats(t);
        t.add(Core.bundle.format("bullet.range", Strings.autoFixed(range / tilesize, 2))).row();
    }

    /** @see mindustry.entities.Fires#extinguish(mindustry.world.Tile, float)*/
    @Override
    public void update(Unit unit){
        boolean any = false;

        for(var fire : Groups.fire)
            if(Intersector.isInRegularPolygon(24, unit.x, unit.y, range, 0.0f, fire.x, fire.y)){
                any = true;
                fire.time(fire.time + 100.0f * Time.delta);
                Fx.steam.at(fire);
            }

        warmup = Math.min(Mathf.lerpDelta(warmup, Mathf.num(any), 0.04f), 0.8f);
    }

    @Override
    public void draw(Unit unit){
        if(warmup <= 0.4f) return;

        // uses 24-sided polygon instead circle, since circle looks strange
        if(renderer.animateShields){
            Draw.z(Layer.shields - 0.001f);
            Draw.color(Color.clear, color, Mathf.clamp(warmup));
            Fill.poly(unit.x, unit.y, 24, range);

        }else{
            Draw.z(Layer.shields);
            Draw.alpha(1f);
            Draw.color(color);
            Lines.stroke(1.5f);
            Lines.poly(unit.x, unit.y, 24, range, 0f);
        }

        Draw.reset();
    }

    @Override
    public void death(Unit unit){
        range = warmup = 0.0f;
        color = null;
    }
}
