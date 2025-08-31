package fire.world.blocks.power;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.power.ImpactReactor;

import static mindustry.Vars.*;

public class BurstReactor extends ImpactReactor{

    protected final byte statusRadius = 72;
    protected final short detectRadius = 640;

    private final float
        warmupMin = Mathf.pow(1.4f, 0.2f), warmupMax = Mathf.pow(2.0f, 0.2f); //140% - 200% power gen

    public BurstReactor(String name){
        super(name);
        buildType = BurstReactorBuild::new;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        float wx = x * tilesize + offset, wy = y * tilesize + offset;
        Drawf.dashCircle(wx, wy, detectRadius, Pal.surge);
        indexer.eachBlock(player.team(), wx, wy, detectRadius, other -> other.block.consPower != null && other.block != this, other ->
            Drawf.selected(other, Tmp.c4.set(Pal.surge).a(Mathf.absin(4.0f, 1.0f))));
    }

    public class BurstReactorBuild extends ImpactReactorBuild{

        private float burstAlpha;
        private final Vec2[] burstPos = new Vec2[3];

        {
            for(int i = 0; i < burstPos.length; i++)
                burstPos[i] = new Vec2();
        }

        @Override
        public void updateTile(){
            super.updateTile();
            burstAlpha -= Time.delta * 0.06f;
            if(burstAlpha < 0.0f) burstAlpha = 0.0f;

            if(warmup != 1.0f || healthf() <= 0.5f) return;

            if(Mathf.chanceDelta(0.005)){
                warmup += Mathf.random(warmupMin, warmupMax);
                burstAlpha = 0.8f;
                for(var pos : burstPos){
                    float x = this.x + Mathf.range(size * 10.0f), y = this.y + Mathf.range(size * 10.0f);
                    pos.set(x, y);

                    for(int i = 0; i < 4; i++)
                        Lightning.create(team, Pal.surge, 100.0f, x, y, Mathf.random(360.0f), 24);
                }

                Fx.dynamicWave.at(x, y, statusRadius, Pal.surge);
                Units.nearby(null, x, y, statusRadius, u -> u.apply(StatusEffects.burning, 300.0f));

                damage(health * 0.3f);
            }
        }

        @Override
        public float getPowerProduction(){
            float[] powerConsSum = {0.0f};
            indexer.eachBlock(this, detectRadius, other -> other.block.consPower != null && other != this, other -> {
                var cons = other.block.consPower;
                powerConsSum[0] += other.efficiency * cons.efficiency(other) * cons.requestedPower(other);
            });
            return super.getPowerProduction() + powerConsSum[0] * (healthf() > 0.5f ? 1.0f : 0.5f) * 0.8f;
        }

        @Override
        public void draw(){
            super.draw();
            if(burstAlpha <= 0.0f) return;

            for(var pos : burstPos)
                Fill.light(pos.x, pos.y, 24, statusRadius, Color.clear, Tmp.c4.set(Pal.surge).a(burstAlpha));
        }
    }
}
