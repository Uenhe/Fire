/*
package fire.ai.types;

import arc.math.Angles;
import arc.util.Time;

import static mindustry.Vars.tilesize;

public class FlyingEOCAI extends mindustry.ai.types.FlyingAI{

    private static float timer = 0f;

    @Override
    public void updateMovement(){
        super.updateMovement();
        if(target == null) return;
        if(unit.health >= unit.maxHealth * 0.6f){
            unit.lookAt(target);
            circle(target, unit.speed() * tilesize * 0.7f);
            timer += Time.delta;
            if(timer >= 180f){
                timer %= 180f;
                for(byte i = 0; i < 3; i += 1){
                    unit.rotation = Angles.angle(unit.x, unit.y, target.x(), target.y());

                    }
                }
            }
        }
    }
}
*/
