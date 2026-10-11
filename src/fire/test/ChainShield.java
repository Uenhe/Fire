package fire.test;

import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ShieldWall;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;

import java.util.Arrays;

import static mindustry.Vars.*;

/** @see mindustry.world.blocks.power.BeamNode BeamNode
 * @see mindustry.world.blocks.defense.BaseShield BaseShield */
public class ChainShield extends Block{

    public final int range = 64;

    public ChainShield(String name){
        super(name);
        requirements(Category.defense, BuildVisibility.sandboxOnly, ItemStack.with());
        size = 1;
        clipSize = 800;
        solid = true;
        destructible = true;
        update = true;
        canOverdrive = false;
        targetable = false;
        group = BlockGroup.walls;
        buildType = ChainShieldBuild::new;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        for(int i = 0; i < 4; i++){
            int maxLen = range + size/2;
            Building dest = null;
            var dir = Geometry.d4[i];
            int dx = dir.x, dy = dir.y;
            int offset = size/2;
            for(int j = 1 + offset; j <= range + offset; j++){
                var other = world.build(x + j * dir.x, y + j * dir.y);
                if(other != null && other.isInsulated())
                    break;

                if(other != null && other.team == player.team() && other.block == this){
                    maxLen = j;
                    dest = other;
                    break;
                }
            }

            Drawf.dashLine(Pal.placing,
                x * tilesize + this.offset + dx * (tilesize * size / 2f + 2),
                y * tilesize + this.offset + dy * (tilesize * size / 2f + 2),
                x * tilesize + this.offset + dx * (maxLen) * tilesize,
                y * tilesize + this.offset + dy * (maxLen) * tilesize
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
            for(int i = 0; i < 4; i++){
                var dir = Geometry.d4[i];
                links[i] = null;
                int offset = size/2;
                for(int j = 1 + offset; j <= range + offset; j++){
                    var other = world.build(tile.x + j * dir.x, tile.y + j * dir.y);
                    if(other != null && other.isInsulated())
                        break;

                    if(other != null && other.team == team && other.block == block){
                        links[i] = other;
                        if(other.id <= id) break;

                        boolean horizontal = i % 2 == 0;
                        float len = size * tilesize,
                            fullDst = (horizontal ? Math.abs(other.x - x) : Math.abs(other.y - y)) + len;
                        float x = (other.x + this.x)/2,
                            y = (other.y + this.y)/2,
                            w = horizontal ? fullDst : len,
                            h = horizontal ? len : fullDst;

                        Groups.bullet.intersect(x - w/2, y - h/2, w, h, bullet -> {
                            if(bullet.team != team && bullet.type.absorbable && Rect.contains(x - w/2, y - h/2, w, h, bullet.x, bullet.y))
                                bullet.absorb();
                        });

                        int k = i;
                        Units.nearbyEnemies(team, x - w/2, y - h/2, w, h, unit -> {
                            if(Rect.contains(x - w/2, y - h/2, w, h, unit.x, unit.y)){
                                final float epsilon = 0.01f;
                                float dstRight = (k == 0 ? other.x : this.x) - unit.x,
                                    dstUpper = (k == 1 ? other.y : this.y) - unit.y,
                                    dstLeft = unit.x - (k == 2 ? other.x : this.x),
                                    dstDown = unit.y - (k == 3 ? other.y : this.y);

                                float minDst = Math.min(Math.min(dstRight, dstUpper), Math.min(dstLeft, dstDown));
                                if(minDst == dstRight)
                                    unit.x = other.x + len/2 + epsilon;
                                else if(minDst == dstUpper)
                                    unit.y = other.y + len/2 + epsilon;
                                else if(minDst == dstLeft)
                                    unit.x = this.x - len/2 - epsilon;
                                else if(minDst == dstDown)
                                    unit.y = this.y - len/2 - epsilon;
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

            for(int i = 0; i < 4; i++){
                var other = links[i];
                if(other != null && other.wasVisible && (other.block != block ||
                    (other.tileX() != tileX() && other.tileY() != tileY()) ||
                    (other.id > id))
                ){
                    boolean horizontal = i % 2 == 0;
                    float len = size * tilesize,
                        fullDst = (horizontal ? Math.abs(other.x - x) : Math.abs(other.y - y)) + len;
                    float x = (other.x + this.x)/2,
                        y = (other.y + this.y)/2,
                        w = horizontal ? fullDst : len,
                        h = horizontal ? len : fullDst;

                    Draw.z(Layer.shields);
                    Draw.color(team.color);
                    Fill.rect(x, y, w, h);

                    var color = ((ShieldWall)Blocks.shieldedWall).glowColor;
                    final float glowMag = 0.2f, glowScl = 6f;
                    float a = Mathf.absin(glowScl, glowMag) + glowMag + 0.001f;
                    Draw.z(Layer.blockAdditive);
                    Draw.blend(Blending.additive);
                    Draw.color(color, a * color.a);
                    Fill.rect(x, y, w, h);
                    Draw.blend();
                }
            }
            Draw.reset();
        }

        @Override
        public void pickedUp(){
            Arrays.fill(links, null);
        }
    }
}
