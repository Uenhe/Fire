package fire.test;

import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.blocks.defense.ShieldWall;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;

import java.util.Arrays;

import static mindustry.Vars.*;

/** @see mindustry.world.blocks.power.BeamNode BeamNode
 * @see mindustry.world.blocks.defense.BaseShield BaseShield */
public class ChainShield extends mindustry.world.Block{
    public final int range = 64;

    public ChainShield(){
        super("chain-shield");
        requirements(Category.effect, BuildVisibility.sandboxOnly, ItemStack.with());
        size = 1;
        clipSize = 800;
        solid = true;
        destructible = true;
        update = true;
        canOverdrive = false;
        targetable = false;
        drawTeamOverlay = false;
        group = BlockGroup.walls;
        buildType = ChainShieldBuild::new;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        for(int i = 0; i < 4; i++){
            int d = size/2;
            int maxLen = d + range;
            Building dest = null;
            var dir = Geometry.d4[i];
            int dx = dir.x, dy = dir.y;
            for(int j = d + 1; j <= d + range; j++){
                var tile = world.tile(x + j * dx, y + j * dy);
                if(tile == null) continue;
                if(tile.block() == Blocks.metalWall3) break;

                var other = tile.build;
                if(other != null && other.team == player.team() && other.block == this){
                    maxLen = j;
                    dest = other;
                    break;
                }
            }

            Drawf.dashLine(Pal.placing,
                x * tilesize + offset + dx * (tilesize * d + 2),
                y * tilesize + offset + dy * (tilesize * d + 2),
                x * tilesize + offset + dx * maxLen * tilesize,
                y * tilesize + offset + dy * maxLen * tilesize
            );

            if(dest != null)
                Drawf.square(dest.x, dest.y, dest.block.size * tilesize/2f + 2.5f, 0f);
        }
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation, boolean diagonal){
        if(!diagonal)
            Placement.calculateNodes(points, this, rotation, (point, other) ->
                Math.max(Math.abs(point.x - other.x), Math.abs(point.y - other.y)) <= range + size - 1);
    }

    public class ChainShieldBuild extends Building{
        public Building[] links = new Building[4];

        @Override
        public void updateTile(){
            float len = size * tilesize;
            for(int i = 0; i < 4; i++){
                var dir = Geometry.d4[i];
                links[i] = null;
                int offset = size/2;
                for(int j = 1 + offset; j <= range + offset; j++){
                    var tile = world.tile(this.tile.x + j * dir.x, this.tile.y + j * dir.y);
                    if(tile == null) continue;
                    if(tile.block() == Blocks.metalWall3) break;

                    var other = tile.build;
                    if(other != null && other.team == team && other.block == block){
                        links[i] = other;
                        if(other.id <= id) break;

                        boolean horizontal = i % 2 == 0;
                        float fullDst = (horizontal ? Math.abs(other.x - x) : Math.abs(other.y - y)) + len,
                        x0 = (other.x + x)/2,
                        y0 = (other.y + y)/2,
                        w = horizontal ? fullDst : len,
                        h = horizontal ? len : fullDst;

                        Groups.bullet.intersect(x0 - w/2, y0 - h/2, w, h, bullet -> {
                            if(bullet.team != team && bullet.type.absorbable && Rect.contains(x0 - w/2, y0 - h/2, w, h, bullet.x, bullet.y))
                                bullet.absorb();
                        });

                        final float epsilon = 0.01f,
                            d = len/2 + epsilon,
                            x1 = (i == 0 ? other.x : x), y1 = (i == 1 ? other.y : y),
                            x2 = (i == 2 ? other.x : x), y2 = (i == 3 ? other.y : y);

                        Units.nearbyEnemies(team, x0 - w/2, y0 - h/2, w, h, unit -> {
                            if(!Rect.contains(x0 - w/2, y0 - h/2, w, h, unit.x, unit.y)) return;

                            if(Time.delta > 1.5f){
                                //if it's too laggy, I have to do this
                                unit.set(unit.lastX, unit.lastY);
                            }else{
                                float
                                    dstRight = x1 - unit.x,
                                    dstTop = y1 - unit.y,
                                    dstLeft = unit.x - x2,
                                    dstBottom = unit.y - y2,
                                    minDst = Math.min(Math.min(dstRight, dstTop), Math.min(dstLeft, dstBottom));

                                if(minDst == dstRight)
                                    unit.x = x1 + d;
                                else if(minDst == dstTop)
                                    unit.y = y1 + d;
                                else if(minDst == dstLeft)
                                    unit.x = x2 - d;
                                else if(minDst == dstBottom)
                                    unit.y = y2 - d;
                            }
                        });

                        break;
                    }
                }
            }
        }

        @Override
        public void draw(){
            super.draw();
            if(team == Team.derelict) return;

            float len = size * tilesize;
            for(int i = 0; i < 4; i++){
                var other = links[i];
                if(other != null && other.wasVisible && (other.block != block ||
                    (other.tileX() != tileX() && other.tileY() != tileY()) ||
                    (other.id > id))
                ){
                    boolean horizontal = i % 2 == 0;
                    float fullDst = (horizontal ? Math.abs(other.x - x) : Math.abs(other.y - y)) + len,
                    x0 = (other.x + x)/2,
                    y0 = (other.y + y)/2,
                    w = horizontal ? fullDst : len,
                    h = horizontal ? len : fullDst;

                    Draw.z(Layer.shields);
                    Draw.color(team.color);
                    Fill.rect(x0, y0, w, h);

                    final float glowMag = 0.2f, glowScl = 6f,
                        a = Mathf.absin(glowScl, glowMag) + glowMag + 0.001f;
                    var color = ((ShieldWall)Blocks.shieldedWall).glowColor;
                    Draw.z(Layer.blockAdditive);
                    Draw.blend(Blending.additive);
                    Draw.color(color, a * color.a);
                    Fill.rect(x0, y0, w, h);
                    Draw.blend();
                }
            }
            Draw.reset();
        }

        @Override
        public void pickedUp(){
            Arrays.fill(links, null);
        }

        @Override
        public boolean collide(Bullet other){
            return false;
        }

        @Override
        public void damage(float damage){}
    }
}
