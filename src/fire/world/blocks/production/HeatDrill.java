/*
package fire.world.blocks.production;

import arc.math.Mathf;
import mindustry.world.meta.Stat;

public class HeatDrill extends mindustry.world.blocks.production.Drill{
    public float heatRequirement = 10f;
    public float maxBoost = 4f;
    public HeatDrill(String name){
        super(name);
    }

    @Override
    public void setStats(){
        stats.remove(Stat.booster);
    }

    public class HeatDrillBuild extends DrillBuild{

        @Override
        public void updateTile(){
            if(dominantItem == null) return;

            if(timer(timerDump, dumpTime)){
                dump(dominantItem != null && items.has(dominantItem) ? dominantItem : null);
            }
            timeDrilled += warmup * delta();
            float delay = getDrillTime(dominantItem);
            if(items.total() < itemCapacity && dominantItems > 0 && efficiency > 0){
                float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;
                lastDrillSpeed = (speed * dominantItems * warmup) / delay;
                warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
                progress += delta() * dominantItems * speed * warmup;
                if(Mathf.chanceDelta(updateEffectChance * warmup)){
                    updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));
                }
            }else{
                lastDrillSpeed = 0f;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                return;
            }
            if(dominantItems > 0 && progress >= delay && items.total() < itemCapacity){
                offload(dominantItem);
                progress %= delay;
                if(wasVisible && Mathf.chanceDelta(updateEffectChance * warmup)) drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
            }
        }
    }
}
*/
