package fire.world.blocks.defense;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Time;
import fire.world.meta.FRStat;
import mindustry.content.Fx;
import mindustry.entities.TargetPriority;
import mindustry.gen.Bullet;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;

import static mindustry.Vars.tilesize;

public class RegenWall extends mindustry.world.blocks.defense.RegenProjector{

    /** Chance of wall to heal itself on collision. */
    protected byte chanceHeal;
    protected byte chanceDeflect;
    /** How much wall heals at collision. Based on bullet damage. */
    protected float regenPercent = 0.1f;
    protected byte frameTime = 3;
    protected byte frames;
    protected boolean flashHit;
    protected final Color flashColor = new Color();

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
        buildType = RegenWallBuild::new;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.range);
        stats.addPercent(FRStat.baseHealChance, chanceHeal * 0.01f);
        if(chanceDeflect > 0) stats.add(Stat.baseDeflectChance, chanceDeflect);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);
    }

    public class RegenWallBuild extends RegenProjectorBuild{

        private float healAmount, hit;
        private boolean heals;

        @Override
        public void updateTile(){
            super.updateTile();

            hit = Mathf.clamp(hit - Time.delta / 10.0f);
            if(damaged() && heals){
                heals = false;
                heal(healAmount);
            }
        }

        @Override
        public boolean collision(Bullet bullet){
            super.collision(bullet);
            hit = 1.0f;

            if(Mathf.chance(chanceHeal * 0.01)){
                healAmount = bullet.damage * regenPercent;
                heals = true;
            }

            if(chanceDeflect > 0 && bullet.vel.len() > 0.1f && bullet.type.reflectable && Mathf.chance(chanceDeflect / bullet.damage)){
                bullet.trns(-bullet.vel.x, -bullet.vel.y);

                if(Math.abs(x - bullet.x) > Math.abs(y - bullet.y)){
                    bullet.vel.x *= -1.0f;
                }else{
                    bullet.vel.y *= -1.0f;
                }

                bullet.owner = this;
                bullet.team = team;
                bullet.time += 1.0f;
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
            Draw.rect(name + (int)((Time.time / frameTime % frames) + 1), x, y);

            if(flashHit && hit >= 0.0001f){
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
