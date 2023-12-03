package fire.entities.bullets;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;

public class VariableBulletType extends mindustry.entities.bullet.BasicBulletType{
    public float velInitial;
    public float velTerminal;
    /** Related to lifetime, should be less than or equal to lifetime. In **ticks**. */
    public float accelTime;
    public boolean drawPlasma;
    public float plasmaSize;
    public Color plasmaColor;

    public VariableBulletType(float velInitial, float velTerminal, float accelTime, float speed, float damage){
        super(speed, damage);
        this.velInitial = velInitial;
        this.velTerminal = velTerminal;
        this.accelTime = accelTime;
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        float vel = velInitial + accel() * b.time;
        if(b.time > accelTime){

            /*
             * if variable motion ends and bullet has homing ability, check if there's any target.
             * remove it if no target.
             */
            /** {@link mindustry.entities.bullet.BulletType#updateHoming(Bullet)} */
            if(homingRange > 0.0001f && b.time >= homingDelay){
                float realAimX = b.aimX < 0f ? b.x : b.aimX, realAimY = b.aimY < 0f ? b.y : b.aimY;
                Teamc target;
                if(b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)){
                    target = b.aimTile.build;
                }else{
                    target = Units.closestTarget(
                        b.team, realAimX, realAimY, homingRange,
                        e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                        t -> t != null && collidesGround && !b.hasCollided(t.id)
                    );
                }
                if(target == null){
                    b.hit = true;    //doesn't create frag bullet
                    hitEffect.at(b); //but keep effect
                    b.remove();
                }
            }

            vel = speed;
        }
        b.vel.setLength(vel);
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);
        if(drawPlasma){
            //special thanks to Extra Utilities mod (https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod)
            //this is really lazy
            byte plasmas = 4;
            for(byte i = 0; i < plasmas; i++){
                Draw.color(plasmaColor);
                Draw.alpha((0.3f + Mathf.absin(Time.time, 2f + i * 2f, 0.3f + i * 0.05f)));
                Draw.blend(Blending.additive);
                Draw.rect(Core.atlas.find("impact-reactor-plasma-" + i), b.x, b.y, plasmaSize, plasmaSize, Time.time * (12f + i * 6f));
                Draw.blend();
            }
        }
    }

    @Override
    protected float calculateRange(){
        //ignores spawn unit as no one would do that
        if(rangeOverride > 0) return rangeOverride;
        return (velInitial + velTerminal) * (lifetime - accelTime) / 2f;
    }

    protected float accel(){
        return (velTerminal - velInitial) / accelTime;
    }
}
