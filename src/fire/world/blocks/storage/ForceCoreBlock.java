package fire.world.blocks.storage;

import arc.Core;
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
import mindustry.logic.Ranged;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.*;

public class ForceCoreBlock extends mindustry.world.blocks.storage.CoreBlock{

    /** {@link mindustry.world.blocks.defense.ForceProjector} */
    protected float
        radius = 101.7f,
        shieldHealth = 750f,
        cooldownNormal = 1.2f,
        cooldownBroken = 1.5f,
        rot;

    /** {@link mindustry.world.blocks.defense.ForceProjector} */
    protected byte sides = 6;

    protected ForceCoreBlock(String name){
        super(name);
        buildType = ForceCoreBuild::new;
    }

    @Override
    public void init(){
        updateClipRadius(radius + 3f);
        super.init();
    }

    @Override
    public boolean canBreak(Tile tile){
        return tile.team() == Team.derelict || state.teams.cores(tile.team()).size > 1 || state.isEditor();
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return true;
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
            () -> Core.bundle.format("bar.detailedshield", (int)(shieldHealth - e.buildup), shieldHealth),
            () -> e.broken ? Color.gray : Pal.accent,
            () -> 1f - (e.buildup / shieldHealth)
        ));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Draw.color(Pal.gray);
        Lines.stroke(3f);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, sides, radius, rot);
        Draw.color(player.team().color);
        Lines.stroke(1f);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, sides, radius, rot);
        Draw.color();

        Draw.reset();
    }

    public class ForceCoreBuild extends CoreBuild implements Ranged{

        private float buildup, scl, hit, warmup;
        private boolean broken;

        @Override
        public float range(){
            return radius * scl;
        }

        @Override
        public void onRemoved(){
            if(!broken && range() > 1f) Fx.forceShrink.at(x, y, range(), team.color);
            super.onRemoved();
        }

        @Override
        public void updateTile(){
            super.updateTile();
            scl = Mathf.lerpDelta(scl, broken ? 0f : warmup, 0.06f);
            warmup = Mathf.lerpDelta(warmup, 1f, 0.1f);

            if(Mathf.chanceDelta(buildup / shieldHealth * 0.1f))
                Fx.reactorsmoke.at(x + Mathf.range(tilesize / 2), y + Mathf.range(tilesize / 2));

            if(buildup > 0f)
                buildup -= delta() * (!broken ? cooldownNormal : cooldownBroken);

            if(broken && buildup <= 0f) broken = false;

            if(buildup >= shieldHealth && !broken){
                broken = true;
                buildup = shieldHealth;
                Fx.shieldBreak.at(x, y, range(), team.color);
            }

            if(hit > 0f)
                hit -= 0.2f * Time.delta;

            if(range() > 0 && !broken)
                Groups.bullet.intersect(x - range(), y - range(), range() * 2f, range() * 2f, bullet -> {
                    if(bullet.team != this.team && bullet.type.absorbable && Intersector.isInRegularPolygon(sides, x, y, range(), rotation, bullet.x, bullet.y)){
                        bullet.absorb();
                        Fx.absorb.at(bullet);
                        hit = 1f;
                        buildup += bullet.damage;
                    }
                });
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.heat) return buildup;
            return super.sense(sensor);
        }

        @Override
        public void draw(){
            super.draw();

            if(!broken){
                Draw.color(team.color, Color.white, Mathf.clamp(hit));

                if(renderer.animateShields){
                    Draw.z(Layer.shields + 0.001f * hit);
                    Fill.poly(x, y, sides, range(), rot);

                }else{
                    Draw.z(Layer.shields);
                    Lines.stroke(1.5f);
                    Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                    Fill.poly(x, y, sides, range(), rot);
                    Draw.alpha(1f);
                    Lines.poly(x, y, sides, range(), rot);
                }
            }

            Draw.reset();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.bool(broken);
            write.f(buildup);
            write.f(scl);
            write.f(warmup);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            broken = read.bool();
            buildup = read.f();
            scl = read.f();
            warmup = read.f();
        }
    }
}
