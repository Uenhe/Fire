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
import fire.world.draw.DrawArrows.SmoothCrafter;
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

import static mindustry.Vars.indexer;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

/** @see mindustry.world.blocks.power.PowerGenerator */
public class EnergyCrafter extends mindustry.world.blocks.production.GenericCrafter{

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
    protected Effect explodeEffect = Fx.none;
    protected Sound
        craftSound = Sounds.none,
        explodeSound = Sounds.none;
    protected Color baseColor = Color.clear;
    protected Color[] circleColor = {Color.clear};
    protected BulletType fragBullet = Bullets.placeholder;

    final int timerStabilize = timers++;

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

    public class EnergyCrafterBuild extends GenericCrafterBuild implements mindustry.logic.Ranged, SmoothCrafter{

        float instability, fraction, flash, smoothProgress;
        byte index;
        short angle;

        @Override
        public void updateTile(){
            super.updateTile();
            fraction = Interp.smoother.apply(1.0f - progress);
            smoothProgress = Mathf.lerpDelta(smoothProgress, progress / (1.0f - 20.0f / craftTime), 0.1f);

            if(efficiency > 0.0f)
                indexer.eachBlock(this, explosionRadius, b -> true, build -> {
                    if(build != null && build.block == block && build.efficiency > 0.0f && build != this){

                        instability += delta();
                        if(instability > maxInstability) kill();
                    }
                });

            if(instability <= 0.0f) return;

            if(efficiency > 0.0f && Mathf.chanceDelta((instability / maxInstability) * 0.05f))
                createLightning();

            if(timer(timerStabilize, stabilizeInterval)){
                Fx.healBlockFull.at(x, y, size, circleColor[index], block);
                instability = Math.max(instability - maxInstability * 0.1f, 0.0f);
            }
        }

        @Override
        public void craft(){
            // equals to super.craft() except for craftEffect part
            consume();
            if(outputItems != null)
                for(var output : outputItems)
                    for(int i = 0; i < output.amount; i++)
                        offload(output.item);
                        
            if(wasVisible) craftEffect.at(x, y, 0.0f, circleColor[index]);
            progress %= 1.0f;

            // customized below
            index = (byte)((index + 1) % circleColor.length);
            angle = (short)Mathf.random(360);
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

            float min = scale() * 0.6f, max = scale() * 1.2f;
            int round = fragRoundRand ? Mathf.random(Mathf.ceil(fragRound * min), Mathf.floor(fragRound * max)) : fragRound;
            int bullets = fragBulletsRand ? Mathf.random(Mathf.ceil(fragBullets * min), Mathf.floor(fragBullets * max)) : fragBullets;

            for(byte i = 0; i < round; i++){
                float delay = fragDelayRand ? i * fragDelay * Mathf.random(0.7f, 1.3f) / scale() : i * fragDelay;
                Time.run(delay, () -> {
                    for(byte j = 0; j < bullets; j++)
                        fragBullet.create(this, Team.derelict, x, y, Mathf.random(360.0f),
                            fragBulletVelRand ? Mathf.random(0.9f, 1.1f) : 1.0f
                        );
                });
            }
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
                Lines.stroke(2.4f, circleColor[index]);
                Lines.arc(x, y, size * 5.0f, fraction, angle);
            }

            if(instability > maxInstability * 0.3f && Core.settings.getBool("showBlockRange")){
                float alpha = Mathf.absin(2.4f, 0.6f);

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
            return sensor == LAccess.heat ? instability / maxInstability : super.sense(sensor);
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
        public float range(){
            return explosionRadius * scale();
        }

        @Override
        public float smoothProgress(){
            return smoothProgress;
        }

        void createLightning(){
            craftSound.at(this, Mathf.random(0.45f, 0.55f));
            byte realLightningAmount = (byte)(lightningAmount * (1 + instability / maxInstability));

            for(byte i = 0; i < realLightningAmount; i++)
                Lightning.create(team, circleColor[index], lightningDamage, x, y, i * (360.0f / realLightningAmount), (int)(size * 2.0f + instability * 0.03f));
        }

        float scale(){
            return (timeScale - 1.0f) * 0.25f + 1.0f;
        }
    }
}
