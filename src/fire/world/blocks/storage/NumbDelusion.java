package fire.world.blocks.storage;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.ObjectMap;
import arc.struct.Queue;
import arc.util.Time;
import arc.util.Tmp;
import fire.content.FRFx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.*;

public class NumbDelusion extends StorageBlock{

    private final short delay = 150;
    private final short range = 33 * tilesize;
    private final Effect destroyFx = FRFx.scanEffect(delay, Pal.accent, range);
    private final Boolf<Block> wasComplex = block -> block instanceof StorageBlock || block instanceof LogicBlock;

    private final BasicBulletType[] bullets = new BasicBulletType[content.items().size];

    public NumbDelusion(String name){
        super(name);
        buildType = NumbDelusionBuild::new;

        //all items should have been loaded when loading blocks... right?
        var rand = new Rand();
        Color[] colBacks = new Color[]{Pal.copperAmmoBack, Pal.graphiteAmmoBack, Pal.surgeAmmoBack, Pal.blastAmmoBack, Pal.thoriumAmmoBack, Pal.plastaniumBack},
            colFronts = new Color[]{Pal.copperAmmoFront, Pal.graphiteAmmoFront, Pal.surgeAmmoFront, Pal.blastAmmoFront, Pal.thoriumAmmoFront, Pal.plastaniumFront};

        for(int i = 0, n = content.items().size; i < n; i++){
            var it = content.items().get(i);
            if(it.explosiveness == 0.0f && it.flammability == 0.0f && it.radioactivity == 0.0f && it.charge == 0.0f && it.healthScaling == 0.0f){
                bullets[i] = null;
            }else{
                float exp = it.explosiveness, flm = it.flammability, rad = it.radioactivity, chr = it.charge,
                dmg = exp * 10.0f + flm * 20.0f + rad * 120.0f + chr * 80.0f + it.healthScaling * 10.0f,
                splashDmg = exp * 60.0f + flm * 80.0f + rad * 50.0f + chr * 30.0f;
                bullets[i] = dmg + splashDmg < 50.0f
                    ? null
                    : new BasicBulletType(0.5f * Mathf.sqrt(dmg + splashDmg + 20.0f), dmg){{
                        lifetime = (float)(60.0f / Math.log10(7.5 * speed + 3.0));
                        drag = 0.02f;
                        width = (float)(3.0f * Math.log10(damage + splashDamage * 0.2 + 20.0) + 5.0f);
                        height = width * 1.5f;
                        knockback = (int)(height * 0.6f);
                        splashDamage = splashDmg;
                        splashDamageRadius = exp * 40.0f + flm * 32.0f + rad * 8.0f + chr * 24.0f;
                        pierce = pierceBuilding = true;
                        pierceCap = (int)Math.log10(damage + 10.0);
                        trailWidth = width * 0.21f;
                        trailLength = (int)(width * 0.5f);

                        if(chr > 0.0f){
                            lightning = (int)(chr * 3);
                            lightningDamage = chr * 20.0f;
                            lightningLength = (int)(chr * 8);
                            lightningLengthRand = 1;
                        }

                        if(flm > 0.0f && rad > 0.0f){
                            status = StatusEffects.melting;
                            statusDuration = flm * 90.0f + rad * 180.0f;
                        }else if(flm > 0.0f){
                            status = StatusEffects.burning;
                            statusDuration = flm * 180.0f;
                        }else if(rad > 0.0f){
                            status = StatusEffects.corroded;
                            statusDuration = rad * 360.0f;
                        }else if(exp > 0.0f){
                            status = StatusEffects.blasted;
                        }

                        rand.setSeed(it.id);
                        int i = rand.random(0, colBacks.length - 1);
                        backColor = trailColor = colBacks[i];
                        frontColor = colFronts[i];
                    }};
            }
        }
    }

    @Override
    public void setStats(){
        super.setStats();
        var map = new ObjectMap<Item, BulletType>();
        var bullets = this.bullets;
        var items = content.items();
        for(int i = 0, n = bullets.length; i < n; i++){
            var type = bullets[i];
            if(type != null) map.put(items.get(i), type);
        }

        stats.add(Stat.ammo, StatValues.ammo(map));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        float wx = x * tilesize + offset, wy = y * tilesize + offset;
        Drawf.dashCircle(wx, wy, range, Pal.placing);
        indexer.eachBlock(player.team(), wx, wy, range, other -> wasComplex.get(other.block) && !(other instanceof NumbDelusionBuild), other -> Drawf.selected(other, Tmp.c1.set(Pal.redSpark).a(Mathf.absin(4.0f, 1.0f))));
    }

    public class NumbDelusionBuild extends StorageBuild{

        @Override
        public void drawSelect(){
            super.drawSelect();
            Drawf.dashCircle(x, y, range, Pal.placing);
            indexer.eachBlock(this, range, other -> wasComplex.get(other.block) && !(other instanceof NumbDelusionBuild), other -> Drawf.selected(other, Tmp.c1.set(Pal.redSpark).a(Mathf.absin(4.0f, 1.0f))));
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
            var items = this.items;
            if(!items.any()) return;

            Core.app.post(() -> {
                final float threshold = 0.1f;
                int n = content.items().size,
                tx = tileX(), ty = tileY();

                float[] amounts = new float[n]; //actually it's more like an int[]
                Queue<Teams.BlockPlan> plansFrom = team.data().plans, plansTo = new Queue<>();

                boolean complex = false;
                for(var plan : plansFrom){
                    float dst = Mathf.dst(tx, ty, plan.x, plan.y);
                    if(dst > range || plan.block instanceof NumbDelusion || !Build.validPlace(plan.block, team, plan.x, plan.y, plan.rotation)) continue;

                    complex |= wasComplex.get(plan.block);

                    //TODO iterates twice here would be bad
                    boolean enough = true;
                    for(var stack : plan.block.requirements){
                        if(items.get(stack.item) - amounts[stack.item.id] < stack.amount * threshold){
                            enough = false;
                            break;
                        }
                    }
                    if(enough){
                        plansTo.add(plan);
                        for(var stack : plan.block.requirements)
                            amounts[stack.item.id] += stack.amount * threshold;
                    }
                }

                boolean Complex = complex;
                Time.run(delay, () -> {
                    for(var plan : plansTo){
                        var tile = world.tile(plan.x, plan.y);
                        tile.setNet(plan.block, team, plan.rotation);
                        if(tile.build != null) tile.build.configured(null, plan.config); //sometimes build will be null if immediately destroyed
                        plansFrom.remove(plan);
                        plan.block.placeEffect.at(tile.drawx(), tile.drawy(), plan.block.size);
                    }

                    for(int i = 0; i < n; i++)
                        items.add(content.item(i), (int)-amounts[i]);

                    var bullets = NumbDelusion.this.bullets;
                    for(int i = 0; i < n; i++){
                        var type = bullets[i];
                        if(type == null) continue;
                        for(int j = 0, m = Math.min(items.get(i), itemCapacity) / 50; j < m; j++)
                            type.create(this, Complex ? Team.derelict : team, x, y, Mathf.random(360.0f), Mathf.random(0.75f, 1.25f) + (Complex ? 0.6f : 0.0f), Mathf.random(0.75f, 1.25f));
                    }
                });

                destroyFx.at(this);
            });
        }
    }
}
