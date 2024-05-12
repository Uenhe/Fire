// special thanks to Extra Utilities mod (https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod)

package fire.world.blocks.power;

import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.type.Item;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;

import static fire.FLib.consValid;

public class CrafterGenerator extends mindustry.world.blocks.power.ConsumeGenerator{

    protected Item outputItem;

    protected CrafterGenerator(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.output, outputItem);
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    public class CrafterGeneratorBuild extends ConsumeGeneratorBuild{

        private float progress, gp;
        private boolean full;

        @Override
        public void updateTile(){
            full = items.get(outputItem) >= itemCapacity;
            if(consValid(this) && !full){
                progress += getProgressIncrease(itemDuration);
                if(progress >= 1f){
                    progress %= 1f;
                    items.add(outputItem, 1);
                    consume();
                    generateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4f));
                }
            }
            productionEfficiency = Mathf.num(consValid(this)) * Mathf.num(!full);
            dump(outputItem);
            produced(outputItem);
            warmup = Mathf.lerpDelta(warmup, Mathf.num(consValid(this) && !full), 0.05f);
        }

        @Override
        public float getPowerProduction(){
            return Mathf.num(consValid(this)) * Mathf.num(!full) * powerProduction;
        }

        @Override
        public BlockStatus status(){
            if(consValid(this) && !full) return BlockStatus.active;
            if(full) return BlockStatus.noOutput;
            return BlockStatus.noInput;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.f(gp);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            gp = read.f();
        }
    }
}
