package fire.world.blocks.power;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import fire.content.FRStatusEffects;
import fire.content.FRUnitTypes;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.*;

public class BurstReactor extends mindustry.world.blocks.power.ImpactReactor{

    protected final byte statusRadius = 108;
    protected final short detectRadius = 640;

    private final float
        warmupMin = Mathf.pow(1.4f, 0.2f), warmupMax = Mathf.pow(2.0f, 0.2f); //140% - 200% power gen

    public BurstReactor(String name){
        super(name);
        buildType = BurstReactorBuild::new;
    }

    @Override
    public void load(){
        super.load();
        BurstBullet.burstType.load();
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.range, detectRadius / tilesize, StatUnit.blocks);
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
        private final Vec2[] burstPos = new Vec2[6];

        {
            for(int i = 0, n = burstPos.length; i < n; i++)
                burstPos[i] = new Vec2();
        }

        @Override
        public void updateTile(){
            super.updateTile();
            burstAlpha -= Time.delta * 0.05f;
            if(burstAlpha < 0.0f) burstAlpha = 0.0f;

            if(warmup != 1.0f || healthf() <= 0.5f) return;

            if(Mathf.chanceDelta(0.005)){
                warmup = Mathf.random(warmupMin, warmupMax);
                burstAlpha = 0.8f;
                for(var pos : burstPos){
                    float x = this.x + Mathf.range(size * 30.0f), y = this.y + Mathf.range(size * 30.0f);
                    pos.set(x, y);
                    for(int i = 0; i < 5; i++)
                        Lightning.create(team, Pal.surge, 120.0f, x, y, Mathf.random(360.0f), 28);
                }

                Fx.dynamicWave.at(x, y, statusRadius, Pal.surge);
                Units.nearby(null, x, y, statusRadius, u -> u.apply(StatusEffects.burning, 300.0f));
                Units.nearby(null, x, y, statusRadius, u -> u.apply(FRStatusEffects.magnetized, 300.0f));

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
        public void drawSelect(){
            super.drawSelect();
            Drawf.dashCircle(x, y, detectRadius, Pal.surge);
            indexer.eachBlock(player.team(), x, y, detectRadius, other -> other.block.consPower != null && other.block != block, other ->
                Drawf.selected(other, Tmp.c4.set(Pal.surge).a(Mathf.absin(4.0f, 1.0f))));
        }

        @Override
        public void draw(){
            super.draw();
            if(burstAlpha <= 0.0f) return;

            for(var pos : burstPos)
                Fill.light(pos.x, pos.y, 24, statusRadius, Color.clear, Tmp.c4.set(Pal.surge).a(burstAlpha));
        }

        @Override
        public void createExplosion(){
            super.createExplosion();
            if(!shouldExplode()) return;

            var bullet = BurstBullet.create();
            bullet.type = new BulletType(0.0f, 0.0f);
            bullet.owner = this;
            bullet.shooter = this;
            bullet.team = team;
            bullet.originX = x;
            bullet.originY = y;
            bullet.set(x, y);
            bullet.lastX = x;
            bullet.lastY = y;
            bullet.lifetime = 120.0f;
            bullet.add();
        }
    }

    private static class BurstBullet extends mindustry.gen.Bullet {

        private float timer;
        private static final BasicBulletType burstType = new BasicBulletType(2.0f, 100.0f){{
            lifetime = 50.0f;
            width = 3.0f;
            height = 8.0f;
            pierceCap = 3;
            trailWidth = 2.4f;
            trailLength = 5;
            drag = -0.05f;
            hitEffect = despawnEffect = Fx.hitBulletColor;
            backColor = trailColor = Pal.surgeAmmoBack;
            frontColor = hitColor = Pal.surgeAmmoFront;
            lightning = 2;
            lightningColor = Pal.surge;
            lightningLength = 5;
            lightningDamage = 10.0f;
            lightningLengthRand = 2;
        }};

        public static BurstBullet create() {
            return Pools.obtain(BurstBullet.class, BurstBullet::new);
        }

        @Override
        public void update(){
            super.update();
            final int n = 12;
            final float interval = 2.4f;
            if((timer += Time.delta) < interval) return;

            timer -= interval;
            for(int i = 0; i < n; i++)
                burstType.create(this, x, y, ((float)i / n) * 360.0f + time * (time - 1.0f) * 1.2f);
        }

        @Override
        public void remove(){
            super.remove();
            FRUnitTypes.radiance.weapons.get(0).bullet.create(this, team, x, y, 0.0f, 0.0f);
        }

        @Override
        public void reset(){
            super.reset();
            timer = 0.0f;
        }
    }
}
