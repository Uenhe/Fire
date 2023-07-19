package fire.world.blocks.storage;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static fire.FireLib.*;
import static mindustry.Vars.*;

public class ForceCoreBlock extends BuildableCoreBlock{
    /** {@link mindustry.world.blocks.defense.ForceProjector} */
    public float
        radius = 101.7f,
        shieldHealth = 750f,
        cooldownNormal = 1.2f,
        cooldownBroken = 1.5f,
        shieldRotation = 0f;
    /** {@link mindustry.world.blocks.defense.ForceProjector} */
    public int sides = 6;

    public ForceCoreBlock(String name){
        super(name);
    }

    @Override
    public void init(){
        updateClipRadius(radius + 3f);
        super.init();
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.shieldHealth, shieldHealth);
        stats.add(Stat.cooldownTime, (int)(shieldHealth / cooldownBroken / 60f), StatUnit.seconds);
        stats.add(Stat.range, radius / tilesize, StatUnit.blocks);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("shield", (ForceCoreBuild e) -> new Bar(
                () -> format("bar.detailedshield", (int)(shieldHealth - e.buildup), shieldHealth),
                () -> Pal.accent,
                () -> e.broken ? 0f : 1f - e.buildup / shieldHealth
        ));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Draw.color(Pal.gray);
        Lines.stroke(3);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, 6, radius);
        Draw.color(player.team().color);
        Lines.stroke(1f);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, 6, radius);
        Draw.color();
    }

    public class ForceCoreBuild extends CoreBuild{
        protected float buildup, radscl, hit, warmup;
        protected boolean broken = true;

        @Override
        public void onRemoved(){
            super.onRemoved();
            if(!broken && realRad() > 1f) Fx.forceShrink.at(x, y, realRad(), team.color);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            radscl = Mathf.lerpDelta(radscl, broken ? 0f : warmup, 0.05f);
            if(Mathf.chanceDelta(buildup / shieldHealth * 0.1)){
                Fx.reactorsmoke.at(x + Mathf.range(tilesize / 2), y + Mathf.range(tilesize / 2));
            }
            warmup = Mathf.lerpDelta(warmup, 1f, 0.1f);
            if(buildup > 0){
                float scale = !broken ? cooldownNormal : cooldownBroken;
                buildup -= delta() * scale;
            }
            if(broken && buildup <= 0) broken = false;
            if(buildup >= shieldHealth && !broken){
                broken = true;
                buildup = shieldHealth;
                Fx.shieldBreak.at(x, y, realRad(), team.color);
            }
            if(hit > 0) hit -= 0.2f * Time.delta;
            if(realRad() > 0 && !broken){
                Groups.bullet.intersect(x - realRad(), y - realRad(), realRad() * 2, realRad() * 2, bullet -> {
                    if(bullet.team != this.team && bullet.type.absorbable && Intersector.isInRegularPolygon(sides, x, y, realRad(), shieldRotation, bullet.x, bullet.y)){
                        bullet.absorb();
                        Fx.absorb.at(bullet);
                        hit = 1f;
                        buildup += bullet.damage;
                    }
                });
            }
        }

        protected float realRad(){
            return radius * radscl;
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.heat) return buildup;
            return super.sense(sensor);
        }

        @Override
        public void draw(){
            super.draw();
            if(team == Team.derelict) return;
            if(buildup > 0){
                Draw.alpha(buildup / shieldHealth * 0.75f);
                Draw.z(Layer.blockAdditive);
                Draw.blend(Blending.additive);
                Draw.blend();
                Draw.z(Layer.block);
                Draw.reset();
            }
            if(!broken){
                Draw.color(team.color, Color.white, Mathf.clamp(hit));
                if(renderer.animateShields){
                    Draw.z(Layer.shields + 0.001f * hit);
                    Fill.poly(x, y, sides, realRad(), shieldRotation);
                }else{
                    Draw.z(Layer.shields);
                    Lines.stroke(1.5f);
                    Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                    Fill.poly(x, y, sides, realRad(), shieldRotation);
                    Draw.alpha(1f);
                    Lines.poly(x, y, sides, realRad(), shieldRotation);
                    Draw.reset();
                }
            }
            Draw.reset();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.bool(broken);
            write.f(buildup);
            write.f(radscl);
            write.f(warmup);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            broken = read.bool();
            buildup = read.f();
            radscl = read.f();
            warmup = read.f();
        }
    }
}
