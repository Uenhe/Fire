package fire.entities.abilities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

import static mindustry.Vars.renderer;

public class ExtinguishFieldAbility extends mindustry.entities.abilities.Ability{

    private final float range;
    private final Color fieldColor;

    private float warmup;

    public ExtinguishFieldAbility(float range, Color color){
        this.range = range;
        this.fieldColor = color;
    }

    /** see {@link mindustry.entities.Fires#extinguish(mindustry.world.Tile, float)} */
    @Override
    public void update(Unit unit){
        boolean any = false;

        for(final var fire : mindustry.gen.Groups.fire){

            if(arc.math.geom.Intersector.isInRegularPolygon(24, unit.x, unit.y, range, 0f, fire.x, fire.y)){
                any = true;

                fire.time(fire.time + 100f * arc.util.Time.delta);
                mindustry.content.Fx.steam.at(fire);
            }
        }

        warmup = Math.min(Mathf.lerpDelta(warmup, Mathf.num(any), 0.04f), 0.8f);
    }

    @Override
    public void draw(Unit unit){
        if(warmup <= 0.4f) return;

        // use 24-sided polygon instead of circle, since circle looks strange
        if(renderer.animateShields){
            Draw.z(Layer.shields - 0.001f);
            Draw.color(Color.clear, fieldColor, Mathf.clamp(warmup));
            Fill.poly(unit.x, unit.y, 24, range);

        }else{
            Draw.z(Layer.shields);
            Draw.alpha(1f);
            Draw.color(fieldColor);
            Lines.stroke(1.5f);
            Lines.poly(unit.x, unit.y, 24, range, 0f);
        }

        Draw.reset();
    }
}
