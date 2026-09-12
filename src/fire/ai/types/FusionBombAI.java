package fire.ai.types;

import mindustry.gen.TimedKillc;

public class FusionBombAI extends mindustry.entities.units.AIController{
    @Override
    public void updateMovement(){
        unloadPayloads();
        float time = unit instanceof TimedKillc t ? t.time() : 1000000f;
        unit.moveAt(vec.trns(unit.rotation, Math.max(0.0f, unit.speed() - time * 0.01f)));
    }
}
