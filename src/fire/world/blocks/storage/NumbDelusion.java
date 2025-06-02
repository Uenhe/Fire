package fire.world.blocks.storage;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.Queue;
import arc.util.Time;
import fire.content.FRFx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.blocks.storage.StorageBlock;

import static mindustry.Vars.*;

public class NumbDelusion extends StorageBlock{

    private final short delay = 150;
    private final short range = 33 * tilesize;
    private final Effect destroyFx = FRFx.scanEffect(delay, Pal.accent, range);

    private final BasicBulletType[] bullets = new BasicBulletType[content.items().size];

    public NumbDelusion(String name){
        super(name);
        buildType = NumbDelusionBuild::new;

        //all items should have been loaded when loading blocks... right?
        var rand = new Rand();
        Color[] colBacks = new Color[]{Pal.copperAmmoBack, Pal.graphiteAmmoBack, Pal.surgeAmmoBack, Pal.blastAmmoBack, Pal.thoriumAmmoBack, Pal.plastaniumBack},
            colFronts = new Color[]{Pal.copperAmmoFront, Pal.graphiteAmmoFront, Pal.surgeAmmoFront, Pal.blastAmmoFront, Pal.thoriumAmmoFront, Pal.plastaniumFront};

        for(byte i = 0, n = (byte)content.items().size; i < n; i++){
            var it = content.items().get(i);
            if(it.explosiveness == 0.0f && it.flammability == 0.0f && it.radioactivity == 0.0f && it.charge == 0.0f && it.healthScaling == 0.0f){
                bullets[i] = null;
            }else{
                float exp = it.explosiveness, flm = it.flammability, rad = it.radioactivity, chr = it.charge,
                    dmg = exp * 10.0f + flm * 20.0f + rad * 120.0f + chr * 80.0f + it.healthScaling * 10.0f,
                    splashDmg = exp * 60.0f + flm * 80.0f + rad * 50.0f + chr * 30.0f,
                    splashRad = exp * 40.0f + flm * 32.0f + rad * 8.0f + chr * 24.0f,
                    spd = 0.5f * Mathf.sqrt(dmg + splashDmg + 20.0f);
                bullets[i] = new BasicBulletType(spd, dmg){{
                    lifetime = (float)(60.0f / Math.log10(7.5 * spd + 3.0));
                    drag = 0.02f;
                    width = (float)(3.0f * Math.log10(damage + splashDamage * 0.2 + 20.0) + 5.0f);
                    height = width * 1.5f;
                    knockback = height + 3.0f;
                    splashDamage = splashDmg;
                    splashDamageRadius = splashRad;
                    pierce = pierceBuilding = true;
                    pierceCap = (int)Math.log10(damage + 10.0);
                    trailWidth = width * 0.21f;
                    trailLength = (int)(width * 0.5f);

                    if(chr > 0.0f){
                        lightningDamage = chr * 20.0f;
                        lightningLength = (int)(chr * 5);
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
                    byte i = (byte)rand.random(0, colBacks.length - 1);
                    backColor = trailColor = colBacks[i];
                    frontColor = colFronts[i];
                }};
            }
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
    }

    public class NumbDelusionBuild extends StorageBuild{

        @Override
        public void onDestroyed(){
            super.onDestroyed();
            var items = this.items;
            if(!items.any()) return;

            final float threshold = 0.1f;
            byte n = (byte)content.items().size;
            float[] amounts = new float[n]; //actually it's more like an int[]
            Queue<Teams.BlockPlan> plansFrom = team.data().plans, plansTo = new Queue<>();

            boolean hasComplex = false;
            for(var plan : plansFrom){
                float dst = Mathf.dst(tileX(), tileY(), plan.x, plan.y);
                if(dst > range || plan.block instanceof NumbDelusion) continue;
                plansTo.add(plan);
                hasComplex |= plan.block instanceof StorageBlock || plan.block instanceof LogicBlock;
                for(var stack : plan.block.requirements) amounts[stack.item.id] += stack.amount;
            }

            boolean enough = true;
            for(byte i = 0; i < n; i++){
                if(items.get(i) < (amounts[i] *= threshold)){
                    enough = false;
                    break;
                }
            }

            boolean Enough = enough, HasComplex = hasComplex;
            Time.run(delay, () -> {
                if(Enough){
                    for(var plan : plansTo){
                        var tile = world.tile(plan.x, plan.y);
                        tile.setBlock(plan.block, team, plan.rotation);
                        tile.build.configured(null, plan.config);
                        plansFrom.remove(plan);
                        plan.block.placeEffect.at(tile.drawx(), tile.drawy(), plan.block.size);
                    }

                    for(byte i = 0; i < n; i++) items.add(content.item(i), (int)-amounts[i]);
                }

                for(byte i = 0; i < n; i++){
                    var type = bullets[i];
                    if(type == null) continue;
                    for(byte j = 0, m = (byte)(Math.min(items.get(i), itemCapacity) / 50); j < m; j++)
                        type.create(this, HasComplex ? Team.derelict : team, x, y, Mathf.random(360.0f), Mathf.random(0.75f, 1.25f) + (HasComplex ? 0.6f : 0.0f), Mathf.random(0.75f, 1.25f));
                }
            });

            destroyFx.at(this);
        }
    }
}
