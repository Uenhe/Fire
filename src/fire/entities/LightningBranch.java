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
import arc.util.Nullable;
import arc.util.Time;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;
import mindustry.gen.Unitc;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/** @see mindustry.entities.Lightning */
public class LightningBranch{

    private static final Rand random = new Rand();
    private static final Rect rect = new Rect();
    private static final Seq<Unitc> entities = new Seq<>();
    private static final IntSet hit = new IntSet();
    private static final byte maxChain = 8;
    private static final byte hitRange = 30;
    private static boolean bhit;
    private static int lastSeed;

    public static void create(Teamc owner, Color color, float damage, float angle, int length, int branchAmount, int branchLeft){
        createInternal(null, null, lastSeed++, owner.team(), color, damage, owner.x(), owner.y(), angle, length, branchAmount, branchLeft);
    }

    public static void create(Bullet hitter, BulletType type, Color color, float damage, float angle, int length, int branchAmount, int branchLeft){
        createInternal(hitter, type, lastSeed++, hitter.team, color, damage, hitter.x, hitter.y, angle, length, branchAmount, branchLeft);
    }

    private static void createInternal(@Nullable Bullet hitter, @Nullable BulletType type, int seed, Team team, Color color, float damage, float x, float y, float angle, int length, int branchAmount, int branchLeft){
        random.setSeed(seed);
        hit.clear();
        bhit = false;
        if(type == null) type = Bullets.damageLightning;
        var Type = type;
        var lines = new Seq<Vec2>();
        for(int i = 0; i < length / 2; i++){
            type.create(null, team, x, y, angle, damage * (hitter == null ? 1.0f : Type.damageMultiplier(hitter)), 1.0f, 1.0f, hitter);
            lines.add(new Vec2(x + Mathf.range(3.0f), y + Mathf.range(3.0f)));
            if(lines.size > 1){
                bhit = false;
                Vec2 from = lines.get(lines.size - 2), to = lines.peek();
                World.raycastEach(World.toTile(from.x), World.toTile(from.y), World.toTile(to.x), World.toTile(to.y), (tx, ty) -> {
                    var tile = world.tile(tx, ty);
                    if(tile != null && (tile.build != null && tile.build.isInsulated()) && tile.team() != team){
                        bhit = true;
                        lines.peek().set(tx * tilesize, ty * tilesize);
                        return true;
                    }
                    return false;
                });
                if(bhit) break;
            }
            rect.setSize(hitRange).setCenter(x, y);
            entities.clear();
            if(hit.size < maxChain){
                Units.nearbyEnemies(team, rect, u -> {
                    if(!hit.contains(u.id()) && (hitter == null || u.checkTarget(Type.collidesAir, Type.collidesGround)))
                        entities.add(u);
                });
            }
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
        float X = x, Y = y, Angle = angle;
        Time.run(1.5f, () -> {
            for(int i = 0; i < branchAmount; i++){
                createInternal(hitter, Type, lastSeed++, team, color, damage, X, Y, Angle + random.range(15.0f), length, branchAmount, branchLeft - 1);
            }
        });
    }
}
