package fire.test;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.IntIntMap;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import fire.FRUtils;
import fire.annotation.Modified;
import fire.content.FRSounds;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.bullet.LaserBoltBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

import static mindustry.Vars.player;
import static mindustry.Vars.world;

public class PlayerCharacter extends UnitType{
    static final TextureRegion hitbox = new TextureRegion();

    public PlayerCharacter(String name, int spd, Weapon wp){
        super(name);
        STGPlayer.classId = EntityMapping.register(name, constructor = STGPlayer::new);

        //The width of game layout is 384px in TH07. Meanwhile, the width of custom STG scene is 216 world units.
        speed = spd * (216f / 384f);

        engineOffset = 4f;
        hitSize = 2f;
        rotateSpeed = 0;
        accel = drag = 0.9f;

        alwaysShootWhenMoving = true;
        canBoost = true;
        boostMultiplier = 0.5f;
        riseSpeed = descentSpeed = 1f;

        weapons.add(wp);
    }

    @Override
    public void load(){
        super.load();
        hitbox.set(FRUtils.findSprite("hitbox"));
        hitbox.scale = 2;
    }

    @Override
    public void init(){
        super.init();
        engines.set(0, new ConstantEngine(0, -engineOffset, engineSize));
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);
        if(unit != player.unit()) return;

        var u = (STGPlayer)unit;
        u.hitboxAlpha = Mathf.lerpDelta(u.hitboxAlpha, u.elevation > 0 ? 0.6f : 0f, 0.15f);
        if(u.hitboxAlpha > 0){
            Draw.color(Color.white, u.hitboxAlpha);
            Draw.rect(hitbox, u.x, u.y, Time.time * 2);
            Draw.rect(hitbox, u.x, u.y, -Time.time * 2);
        }
    }

    @Override
    @Modified
    public void drawShadow(Unit unit){
        var floor = world.floorWorld(unit.x + shadowTX, unit.y + shadowTY);

        float dest = floor.canShadow ? 1f : 0f;
        unit.shadowAlpha = unit.shadowAlpha < 0 ? dest : Mathf.approachDelta(unit.shadowAlpha, dest, 0.11f);
        Draw.color(Pal.shadow, Pal.shadow.a * unit.shadowAlpha);

        Draw.rect(shadowRegion, unit.x + shadowTX, unit.y + shadowTY, 0);
        Draw.color();
    }

    @Override
    public void drawEngines(Unit unit){
        engines.get(0).draw(unit);
        Draw.color();
    }

    @Override
    public <T extends Unit & Legsc> void drawLegs(T unit){}

    public static class STGPlayer extends UnitEntity{
        static int classId;
        static final IntIntMap grazeMap = new IntIntMap(); //key = bullet.id, value = 1 if grazed
        static final Effect grazeFx = new Effect(15, e ->
            Angles.randLenVectors(e.id, 8, 2f + e.fin() * 25f, (x, y) -> {
                float size = 1f + e.fout() * 3f;
                Draw.color(Color.lightGray, Color.darkGray, e.fin());
                Fill.square(e.x + x, e.y + y, size/2f, 45);
            })
        );

        float hitboxAlpha;

        static{
            Events.on(EventType.ResetEvent.class, e -> grazeMap.clear());
        }

        STGPlayer(){
            super();
        }

        @Override
        public int classId(){
            return classId;
        }

        @Override
        public boolean canShoot(){
            return !disarmed;
        }

        @Override
        public boolean isFlying(){
            return true;
        }

        @Override
        public void lookAt(float angle){
            rotation = 90;
        }

        @Override
        @Modified
        public void rotateMove(Vec2 vec){
            moveAt(Tmp.v2.trns(rotation, vec.len()));
            if(!vec.isZero()) rotation = 90;
        }

        @Override
        public void update(){
            super.update();
            if(this != player.unit()) return;

            final float rad = 16f;
            Groups.bullet.intersect(x - rad, y - rad, rad*2, rad*2, bullet -> {
                if(bullet.team != team && grazeMap.get(bullet.id) != 1 && bullet.within(this, rad)){
                    grazeMap.put(bullet.id, 1);
                    grazeFx.at(this);
                    FRSounds.graze.at(this, 1, 0.4f);
                }
            });
        }
    }

    public static class PlayerWeapon extends Weapon{

        public PlayerWeapon(String name){
            super(name);
            shootCone = 360;
            rotate = false;
            alternate = false;
        }

        @Override
        protected float bulletRotation(Unit unit, WeaponMount mount, float bulletX, float bulletY){
            return 90;
        }

        @Override
        @Modified
        protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){
            shoot.shoot(mount.barrelCounter, (xOffset, yOffset, angle, delay, mover) -> {
                mount.totalShots++;
                bullet(unit, mount, xOffset, yOffset, 0, mover);
            }, () -> mount.barrelCounter++);
        }
    }

    public static class PlayerBulletType extends LaserBoltBulletType{

        public PlayerBulletType(float speed, float damage){
            super(speed, damage);
        }

        @Override
        @Modified
        public @Nullable Bullet create(@Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY, @Nullable Teamc target){
            Bullet bullet = Bullet.create();
            bullet.type = this;
            bullet.owner = owner;
            bullet.shooter = (shooter == null ? owner : shooter);
            bullet.team = team;
            bullet.time = 0;
            bullet.originX = x;
            bullet.originY = y;
            bullet.aimX = aimX;
            bullet.aimY = aimY;
            bullet.initVel(90, speed * velocityScl);
            bullet.set(x, y);
            bullet.lastX = x;
            bullet.lastY = y;
            bullet.lifetime = lifetime;
            bullet.data = data;
            bullet.hitSize = hitSize;
            bullet.mover = mover;
            bullet.damage = (damage < 0 ? this.damage : damage) * bullet.damageMultiplier();
            bullet.buildingDamageMultiplier = buildingDamageMultiplier;

            if(bullet.trail != null)
                bullet.trail.clear();

            bullet.add();

            if(owner instanceof Velc v)
                bullet.vel.y += v.vel().y;

            return bullet;
        }
    }

    public static class ConstantEngine extends UnitEngine{

        public ConstantEngine(float x, float y, float radius){
            super(x, y, radius, -90);
        }

        @Override
        @Modified
        public void draw(Unit unit){
            UnitType type = unit.type;
            var col = color != null ? color : type.engineColor == null ? unit.team.color : type.engineColor;

            Tmp.v1.set(x, y).rotate(0);
            float ex = Tmp.v1.x, ey = Tmp.v1.y;
            float rad = (radius + Mathf.absin(Time.time, 2f, radius/4f));

            Draw.color(col);
            Fill.circle(unit.x + ex, unit.y + ey, rad);
            Draw.color(type.engineColorInner);
            Fill.circle(unit.x + ex - Angles.trnsx(-90, rad/4f), unit.y + ey - Angles.trnsy(-90, rad/4f), rad/2f);
        }
    }
}
