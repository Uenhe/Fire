package fire.world.blocks.defense;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Time;
import fire.world.meta.FireStat;
import mindustry.content.Fx;
import mindustry.entities.TargetPriority;
import mindustry.gen.Bullet;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;

import static fire.FireLib.atlas;
import static mindustry.Vars.tilesize;

public class RegenWall extends RegenProjector{
    /** Wall healing itself chance at collision. -1 to disable. */
    public float chanceHeal = -1f;
    /** {@link mindustry.world.blocks.defense.Wall} */
    public float chanceDeflect = -1f;
    /** How much should wall heal at collision. */
    public float regenPercent = 0.1f;
    /** {@link mindustry.type.Item} */
    public float frameTime = 3f;
    /** {@link mindustry.type.Item} */
    public int frames = 0;
    /** {@link mindustry.world.blocks.defense.Wall} */
    public boolean flashHit = false;
    /** {@link mindustry.world.blocks.defense.Wall} */
    public Color flashColor = Color.white;

    public RegenWall(String name){
        super(name);
        group = BlockGroup.walls;
        priority = TargetPriority.wall;
        buildCostMultiplier = 6f;
        crushDamageMultiplier = 5f;
        update = true;
        hasPower = false;
        hasItems = false;
        canOverdrive = false;
        range = 1;
        effect = Fx.none;
        envEnabled = Env.any;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.range);
        stats.addPercent(FireStat.baseHealChance, chanceHeal);
        if(chanceDeflect > 0f) stats.add(Stat.baseDeflectChance, chanceDeflect);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);
    }

    public class RegenWallBuild extends RegenProjectorBuild{
        protected float healAmount, hit, time;
        protected int count = 1;
        protected boolean heals = false;

        @Override
        public void updateTile(){
            super.updateTile();
            time += Time.delta;
            if(time >= frameTime){
                count += 1;
                time = 0f;
            }
            if(count > frames) count = 1;
            hit = Mathf.clamp(hit - Time.delta / 10f);
            if(damaged() && heals){
                heals = false;
                heal(healAmount);
            }
        }

        @Override
        public boolean collision(Bullet bullet){
            super.collision(bullet);
            hit = 1f;
            if(Mathf.chance(chanceHeal)){
                healAmount = bullet.damage * regenPercent;
                heals = true;
            }
            if(
                chanceDeflect > 0f && bullet.vel.len() > 0.1f &&
                bullet.type.reflectable && Mathf.chance(chanceDeflect / bullet.damage)
            ){
                bullet.trns(-bullet.vel.x, -bullet.vel.y);
                float penX = Math.abs(x - bullet.x), penY = Math.abs(y - bullet.y);
                if(penX > penY){
                    bullet.vel.x *= -1;
                }else{
                    bullet.vel.y *= -1;
                }
                bullet.owner = this;
                bullet.team = team;
                bullet.time += 1f;
                return false;
            }
            return true;
        }

        @Override
        public void drawSelect(){
            block.drawOverlay(x, y, rotation);
        }

        @Override
        public void draw(){
            super.draw();
            Draw.rect(atlas(name + count), x, y);
            if(flashHit){
                if(hit < 0.0001f) return;
                Draw.color(flashColor);
                Draw.alpha(hit * 0.5f);
                Draw.blend(Blending.additive);
                Fill.rect(x, y, tilesize * size, tilesize * size);
                Draw.blend();
                Draw.reset();
            }
        }
    }
}
