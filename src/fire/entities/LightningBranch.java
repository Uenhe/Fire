package fire.entities;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.*;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Unitc;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/** @see mindustry.entities.Lightning */
public class LightningBranch{

    private static final Rand random = new Rand();
    private static final Rect rect = new Rect();
    private static final Seq<Unitc> entities = new Seq<>();
    private static final IntSet hit = new IntSet();
    private static final int maxChain = 8;
    private static final float hitRange = 30.0f;
    private static boolean bhit;
    private static int lastSeed;

    public static void create(@Nullable Bullet hitter, Team team, Color color, float damage, float x, float y, float angle, int length, int branchAmount, int branchTimes){
        createLightningInternal(hitter, lastSeed++, team, color, damage, x, y, angle, length, branchAmount, branchTimes);
    }

    private static void createLightningInternal(@Nullable Bullet hitter, int seed, Team team, Color color, float damage, float x, float y, float angle, int length, int branchAmount, int branchLeft){
        random.setSeed(seed);
        hit.clear();
        bhit = false;
        var hitCreate = hitter == null || hitter.type.lightningType == null ? Bullets.damageLightning : hitter.type.lightningType;
        var lines = new Seq<Vec2>();
        for(short i = 0; i < length / 2; i++){
            hitCreate.create(null, team, x, y, angle, damage * (hitter == null ? 1.0f : hitter.damageMultiplier()), 1.0f, 1.0f, hitter);
            lines.add(new Vec2(x + Mathf.range(3.0f), y + Mathf.range(3.0f)));
            if(lines.size > 1){
                bhit = false;
                Vec2 from = lines.get(lines.size - 2), to = lines.get(lines.size - 1);
                World.raycastEach(World.toTile(from.getX()), World.toTile(from.getY()), World.toTile(to.getX()), World.toTile(to.getY()), (wx, wy) -> {
                    var tile = world.tile(wx, wy);
                    if(tile != null && (tile.build != null && tile.build.isInsulated()) && tile.team() != team){
                        bhit = true;
                        lines.get(lines.size - 1).set(wx * tilesize, wy * tilesize);
                        return true;
                    }
                    return false;
                });
                if(bhit) break;
            }
            rect.setSize(hitRange).setCenter(x, y);
            entities.clear();
            if(hit.size < maxChain)
                Units.nearbyEnemies(team, rect, u -> {
                    if(!hit.contains(u.id()) && (hitter == null || u.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround)))
                        entities.add(u);
                });
            var furthest = Geometry.findFurthest(x, y, entities);
            if(furthest != null){
                hit.add(furthest.id());
                x = furthest.x();
                y = furthest.y();
            }else if(i + 1 < length / 2){ //make it uninterrupted
                angle += random.range(15.0f);
                x += Angles.trnsx(angle, hitRange * 0.5f);
                y += Angles.trnsy(angle, hitRange * 0.5f);
            }
        }
        Fx.lightning.at(x, y, angle, color, lines);

        if(branchLeft <= 0) return;
        float xx = x, yy = y, aangle = angle;
        Time.run(1.0f, () -> {
            for(byte i = 0; i < branchAmount; i++)
                createLightningInternal(null, lastSeed++, team, color, damage, xx, yy, aangle + random.range(15.0f), length, branchAmount, branchLeft - 1);
        });
    }
}
