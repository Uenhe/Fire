package fire.type;

import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

/** @author fy */
public class AirFleshUnitType extends FleshUnitType{
    public final boolean drawShield = true;

    public AirFleshUnitType(String name, UnitType origin){
        super(name, origin);
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);
        if(!drawShield) return;

        Draw.z(Layer.turretHeat + 0.5f);
        Draw.blend(Blending.additive);
        if(unit.shield > 0.0f){
            float phase = Math.min(unit.shield / unit.maxHealth, 1.0f);
            Lines.stroke(1.0f, unit.team.color);
            Draw.alpha(phase);
            Lines.poly(unit.x, unit.y, 4, unit.hitSize * 1.5f * Mathf.sqrt2 * phase, Time.time * 2.0f);
        }
        Draw.blend();
        Draw.color();
    }
}
