package fire.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Fires;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

import java.util.concurrent.atomic.AtomicBoolean;

import static mindustry.Vars.*;

public class ExtinguishFieldAbility extends mindustry.entities.abilities.Ability{

    public final float range;
    public final Color color = new Color();

    private float warmup;

    public ExtinguishFieldAbility(float range, Color color){
        this.range = range;
        this.color.set(color);
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

    /** @see Fires#extinguish(mindustry.world.Tile, float)*/
    @Override
    public void update(Unit unit){
        var any = new AtomicBoolean(false);

        Geometry.circle(unit.tileX(), unit.tileY(), Mathf.ceil(range / tilesize), ((x, y) -> {
            var fire = Fires.get(world.tile(x, y));
            if(fire != null){
                fire.time += 100.0f * Time.delta;
                Fx.steam.at(fire);
                warmup = Math.min(warmup + 0.001f * Time.delta, 0.8f);
                any.set(true);
            }
        }));
        warmup = Math.min(Mathf.lerpDelta(warmup, Mathf.num(any.get()), 0.04f), 0.8f);
    }

    @Override
    public void draw(Unit unit){
        if(warmup <= 0.4f) return;

        //uses 24-sided polygon instead circle, since circle looks strange
        if(renderer.animateShields){
            Draw.z(Layer.shields - 0.001f);
            Draw.color(Color.clear, color, Mathf.clamp(warmup));
            Fill.poly(unit.x, unit.y, 24, range);

        }else{
            Draw.z(Layer.shields);
            Draw.alpha(1.0f);
            Draw.color(color);
            Lines.stroke(1.5f);
            Lines.poly(unit.x, unit.y, 24, range, 0.0f);
        }

        Draw.reset();
    }
}
