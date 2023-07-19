//special thanks to Extra Utilities mod (https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod)

package fire.world.blocks.power;

import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;

import static fire.FireLib.consValid;

public class CrafterGenerator extends ConsumeGenerator{
    public Item outputItem = Items.copper;

    public CrafterGenerator(String name){
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
        protected float p, gp;
        protected boolean full;

        @Override
        public void updateTile(){
            super.updateTile();
            full = items.get(outputItem) >= itemCapacity;
            if(consValid(this) && !full){
                p += getProgressIncrease(itemDuration);
                gp += getProgressIncrease(itemDuration);
            }
            if(p > 1f && !full){
                items.add(outputItem, 1);
                p %= 1;
            }
            if(gp > 1 && !full){
                consume();
                gp %= 1;
                generateEffect.at(x + Mathf.range(3f), y + Mathf.range(3f));
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
            if(full && consValid(this)) return BlockStatus.noOutput;
            return BlockStatus.noInput;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(p);
            write.f(gp);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            p = read.f();
            gp = read.f();
        }
    }
}
