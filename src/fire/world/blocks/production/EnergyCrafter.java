package fire.world.blocks.production;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;

import static mindustry.Vars.*;

public class EnergyCrafter extends mindustry.world.blocks.production.GenericCrafter{

    /** see {@link mindustry.world.blocks.power.PowerGenerator} */
    protected float
        explosionRadius,
        explosionDamage,
        explosionShake,
        explosionShakeDuration;
    protected float
        maxInstability,
        stabilizeInterval,
        lightningDamage;
    protected byte
        lightningAmount,
        fragBullets,
        fragRound,
        fragDelay;
    protected boolean
        fragBulletsRand,
        fragRoundRand,
        fragDelayRand,
        fragBulletVelRand;
    /** see {@link mindustry.world.blocks.power.PowerGenerator} */
    protected Effect explodeEffect = Fx.none;
    /** see {@link mindustry.world.blocks.power.PowerGenerator} */
    protected Sound
        craftSound = Sounds.none,
        explodeSound = Sounds.none;
    protected Color baseColor = Color.clear;
    protected Color[] circleColor = {Color.clear};
    protected BulletType fragBullet = Bullets.placeholder;

    private final short timerStabilize = (short)timers++;

    protected EnergyCrafter(String name){
        super(name);
        baseExplosiveness = 5.0f;
        buildType = EnergyCrafterBuild::new;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, explosionRadius, Pal.placing);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("instability", (EnergyCrafterBuild e) -> new Bar("bar.instability", Pal.sap, () -> e.instability / maxInstability));
    }

    public class EnergyCrafterBuild extends GenericCrafterBuild implements mindustry.logic.Ranged{

        private float instability, fraction, angle, flash;
        /** Of color. */
        private byte index;

        @Override
        public void updateTile(){
            super.updateTile();
            fraction = Interp.smoother.apply(1.0f - progress);

            if(efficiency > 0.0f)
                indexer.eachBlock(this, explosionRadius, b -> true, build -> {
                    if(build != null && build.block == block && efficiency > 0.0f && build != this){

                        instability += delta();
                        if(instability > maxInstability) kill();
                    }
                });

            if(instability > 0.0f){

                if(efficiency > 0.0f && Mathf.chanceDelta((instability / maxInstability) * 0.05f))
                    createLightning();

                if(timer(timerStabilize, stabilizeInterval)){
                    Fx.healBlockFull.at(x, y, size, circleColor[index], block);
                    instability = Math.max(instability - maxInstability * 0.1f, 0.0f);
                }
            }
        }

        @Override
        public void craft(){
            super.craft();
            index = (byte)((index + 1) % circleColor.length);
            angle = Mathf.random(360.0f);

            createLightning();
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
            if(!(state.rules.reactorExplosions && efficiency > 0.0f)) return;

            Damage.damage(x, y, range(), explosionDamage * scale());
            Effect.shake(explosionShake * scale(), explosionShakeDuration * scale(), this);
            explodeSound.at(this);
            explodeEffect.at(this);

            // ewe, this is truly horrible, no ref
            final float min = scale() * 0.7f, max = scale() * 1.3f;
            for(byte i = 0; i < (fragRoundRand ?
                Mathf.random(Mathf.ceil(fragRound * min), Mathf.floor(fragRound * max)) : fragRound
            ); i++)
                Time.run(fragDelayRand ?
                    i * fragDelay * Mathf.random(0.7f, 1.3f) / scale() : i * fragDelay,
                () -> {
                    for(byte j = 0; j < (fragBulletsRand ?
                        Mathf.random(Mathf.ceil(fragBullets * min), Mathf.floor(fragBullets * max)) : fragBullets
                    ); j++)
                        fragBullet.create(this, Team.derelict, x, y, Mathf.random(360.0f), fragBulletVelRand ?
                            Mathf.random(0.9f, 1.1f) : 1.0f
                        );
                });
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Drawf.dashCircle(x, y, range(), team.color);
        }

        @Override
        public void draw(){
            super.draw();

            if(efficiency > 0.0f){
                flash += (1.0f + instability / maxInstability * 6.0f) * delta();
                Draw.z(Layer.effect);
                Lines.stroke(2.5f, circleColor[index]);
                Lines.arc(x, y, size * 5.0f, fraction, angle);
            }

            if(instability > maxInstability * 0.3f && Core.settings.getBool("showBlockRange")){
                final float alpha = Mathf.absin(2.4f, 0.6f);

                Draw.color(Pal.health, alpha);
                Lines.stroke(1.0f);
                Lines.circle(x, y, range());
                Draw.alpha(alpha * 0.3f);
                Fill.circle(x, y, range());
            }

            Draw.color(circleColor[index], baseColor, Mathf.absin(flash, 9.0f, 1.0f));
            Draw.alpha(0.5f);
            Draw.z(Layer.blockOver);
            Draw.rect(name + "-lights", x, y);

            Draw.reset();
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.heat) return instability / maxInstability;
            return super.sense(sensor);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(instability);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            instability = read.f();
        }

        @Override
        public final float range(){
            return explosionRadius * scale();
        }

        private void createLightning(){
            craftSound.at(this, Mathf.random(0.45f, 0.55f));
            final byte realLightningAmount = (byte)(lightningAmount * (1 + instability / maxInstability));

            for(byte i = 0; i < realLightningAmount; i++)
                Lightning.create(team, circleColor[index], lightningDamage, x, y, i * (360.0f / realLightningAmount), (int)(size * 2.0f + instability * 0.03f));
        }

        private float scale(){
            return (timeScale - 1.0f) * 0.25f + 1.0f;
        }
    }
}
