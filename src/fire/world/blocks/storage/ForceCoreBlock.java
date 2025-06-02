package fire.world.blocks.storage;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import fire.world.consumers.ConsumePowerCustom;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import java.util.Arrays;

import static mindustry.Vars.*;

/** @see mindustry.world.blocks.defense.ForceProjector ForceProjector */
public class ForceCoreBlock extends mindustry.world.blocks.storage.CoreBlock{

    protected float
        radius = 101.7f,
        shieldHealth = 750.0f,
        cooldownNormal = 1.2f,
        cooldownBroken = 1.5f,
        shieldRotation;
    protected byte sides = 6;

    /** Number of registered cores, according to {@link mindustry.game.Team#all}.<p>
     * Team ID -> Core number<p>
     * Also see those JS files in mod Creator. */
    public final short[] cores = new short[256];

    protected ForceCoreBlock(String name){
        super(name);
        hasPower = true;
        buildType = ForceCoreBuild::new;
    }

    @Override
    public void init(){
        Events.on(EventType.ResetEvent.class, e -> Arrays.fill(cores, (short)0));
        updateClipRadius(radius + 3.0f);
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
        stats.add(Stat.cooldownTime, (int)(shieldHealth / cooldownBroken / 60.0f), StatUnit.seconds);
        stats.add(Stat.range, radius / tilesize, StatUnit.blocks);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("shield", (ForceCoreBuild e) -> new Bar(
            () -> Core.bundle.format("bar.detailedshield", (int)(shieldHealth - e.buildup), shieldHealth),
            () -> e.broken ? Color.gray : Pal.accent,
            () -> 1.0f - (e.buildup / shieldHealth)
        ));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Draw.color(Pal.gray);
        Lines.stroke(3.0f);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, sides, radius, shieldRotation);
        Draw.color(player.team().color);
        Lines.stroke(1.0f);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, sides, radius, shieldRotation);
        Draw.color();

        Draw.reset();
    }

    public class ForceCoreBuild extends CoreBuild implements mindustry.logic.Ranged{

        private float buildup, scl, hit, warmup;
        private boolean broken;

        @Override
        public float range(){
            return radius * scl;
        }

        @Override
        public void onRemoved(){
            if(!broken && range() > 1.0f) Fx.forceShrink.at(x, y, range(), team.color);
            super.onRemoved();
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(team == state.rules.waveTeam){
                ConsumePowerCustom.scaleMap.put(this, 0.0f); //won't damage enemy cores
            }else{
                ConsumePowerCustom.scaleMap.put(this, cores[team.id]);
                if(power.graph.getPowerBalance() < 0.0f && power.graph.getBatteryStored() <= 0.0f && cores[team.id] > 2) //allowing to place 2 cores without damage
                    damage(Mathf.sqrt(-power.graph.getPowerBalance() + consPower.requestedPower(this)) * delta());
            }

            scl = Mathf.lerpDelta(scl, broken ? 0.0f : warmup, 0.08f);
            warmup = Mathf.lerpDelta(warmup, 1.0f, 0.14f);

            if(Mathf.chanceDelta(buildup / shieldHealth * 0.1f))
                Fx.reactorsmoke.at(x + Mathf.range(tilesize / 2), y + Mathf.range(tilesize / 2));

            if(buildup > 0.0f)
                buildup -= delta() * (broken ? cooldownBroken : cooldownNormal);

            if(broken && buildup <= 0.0f) broken = false;

            if(buildup >= shieldHealth && !broken){
                broken = true;
                buildup = shieldHealth;
                Fx.shieldBreak.at(x, y, range(), team.color);
            }

            if(hit > 0.0f)
                hit -= 0.2f * Time.delta;

            if(range() > 0.0f && !broken)
                Groups.bullet.intersect(x - range(), y - range(), range() * 2.0f, range() * 2.0f, bullet -> {
                    if(bullet.team != team && bullet.type.absorbable && Intersector.isInRegularPolygon(sides, x, y, range(), rotation, bullet.x, bullet.y)){
                        bullet.absorb();
                        Fx.absorb.at(bullet);
                        hit = 1.0f;
                        buildup += bullet.type.shieldDamage(bullet);
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
                    Fill.poly(x, y, sides, range(), shieldRotation);

                }else{
                    Draw.z(Layer.shields);
                    Lines.stroke(1.5f);
                    Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                    Fill.poly(x, y, sides, range(), shieldRotation);
                    Draw.alpha(1.0f);
                    Lines.poly(x, y, sides, range(), shieldRotation);
                }
            }

            Draw.reset();
        }

        @Override
        public void add(){
            super.add();
            if(team == Team.derelict) return;
            cores[team.id]++;
        }

        @Override
        public void readBase(Reads read){
            super.readBase(read);
            if(team == Team.derelict) return;
            cores[team.id]++;
        }

        @Override
        public void remove(){
            if(added) cores[team.id]--;
            ConsumePowerCustom.scaleMap.remove(this, 0.0f);
            super.remove();
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
