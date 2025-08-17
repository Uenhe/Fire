package fire.world.blocks.production;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import fire.entities.LightningBranch;
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

import static fire.FRVars.displayRange;
import static mindustry.Vars.*;

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
        fragDelayRand;
    protected Effect explodeEffect = Fx.none;
    protected Sound
        craftSound = Sounds.none,
        explodeSound = Sounds.none;
    protected final Color baseColor = new Color();
    protected Color[] circleColor = {};
    protected BulletType fragBullet = Bullets.placeholder;

    private final byte timerStabilize = (byte)timers++;
    private final TextureRegion lights = new TextureRegion();

    protected EnergyCrafter(String name){
        super(name);
        baseExplosiveness = 5.0f;
        buildType = EnergyCrafterBuild::new;
    }

    @Override
    public void load(){
        lights.set(Core.atlas.find(name + "-lights"));
        super.load();
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

    public class EnergyCrafterBuild extends GenericCrafterBuild implements mindustry.logic.Ranged, fire.world.draw.DrawArrows.SmoothCrafter{

        public float instability, fraction, flash, angle, smoothProgress;
        private byte counter;
        private short targetAngle;

        @Override
        public void updateTile(){
            super.updateTile();
            float spd = 0.2f * Mathf.sqrt(timeScale);
            fraction = Mathf.lerpDelta(fraction, Interp.smoother.apply(1.0f - progress), spd);
            angle = Mathf.lerpDelta(angle, targetAngle, spd);
            smoothProgress = Mathf.lerpDelta(smoothProgress, progress / (1.0f - 20.0f / craftTime), 0.1f);

            if(efficiency > 0.0f && indexer.eachBlock(this, explosionRadius, b -> b != this && b.block == block && b.efficiency > 0.0f, e -> {})){
                instability += delta();
                if(instability >= maxInstability) kill();
            }

            if(instability <= 0.0f) return;

            if(efficiency > 0.0f && Mathf.chanceDelta((instability / maxInstability) * 0.05f))
                createLightning();

            if(timer(timerStabilize, stabilizeInterval)){
                Fx.healBlockFull.at(x, y, size, circleColor[counter], block);
                instability = Math.max(instability - 0.1f * maxInstability, 0.0f);
            }
        }

        /** Modified from super's one. */
        @Override
        public void craft(){
            consume();
            var items = outputItems;
            if(items != null)
                for(var output : items)
                    for(int i = 0, n = output.amount; i < n; i++)
                        offload(output.item);
                        
            if(wasVisible) craftEffect.at(x, y, 0.0f, circleColor[counter]);
            progress -= 1.0f;

            if(++counter >= circleColor.length) counter = 0;
            targetAngle = (short)Mathf.random(360);
            createLightning();
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
            if(!state.rules.reactorExplosions || efficiency <= 0.0f) return;

            float scl = scale();
            Damage.damage(x, y, range(), explosionDamage * scl);
            Effect.shake(explosionShake * scl, explosionShakeDuration * scl, this);
            explodeSound.at(tile);
            explodeEffect.at(x, y, 0.0f, scl);

            float min = scl * 0.7f, max = scl * 1.2f;
            int round = fragRoundRand ? Mathf.random(Mathf.ceil(fragRound * min), Mathf.floor(fragRound * max)) : fragRound;
            int bullets = fragBulletsRand ? Mathf.random(Mathf.ceil(fragBullets * min), Mathf.floor(fragBullets * max)) : fragBullets;

            for(int i = 0; i < round; i++){
                float delay = fragDelayRand ? i * fragDelay * Mathf.random(0.8f, 1.25f) / scl : i * fragDelay;
                Time.run(delay, () -> {
                    for(int j = 0; j < bullets; j++)
                        fragBullet.create(this, Team.derelict, x, y, Mathf.random(360.0f),
                            Mathf.random(fragBullet.fragVelocityMin, fragBullet.fragVelocityMax)
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

            Draw.z(Layer.effect);
            Lines.stroke(2.4f * Interp.slope.apply(1.0f - fraction) * warmup(), circleColor[counter]);
            Draw.alpha(warmup());
            Lines.arc(x, y, size * 5.0f, fraction, angle);

            if(efficiency > 0.0f)
                flash += (1.0f + instability / maxInstability * 6.0f) * delta(); //update in draw() but NuclearReactor does the same

            if(instability > maxInstability * 0.3f && displayRange){
                float alpha = Mathf.absin(2.4f, 0.6f);
                Draw.color(Pal.health, alpha);
                Lines.stroke(1.0f);
                Lines.circle(x, y, range());
                Draw.alpha(alpha * 0.3f);
                Fill.circle(x, y, range());
            }

            Draw.color(circleColor[counter], baseColor, Mathf.absin(flash, 9.0f, 1.0f));
            Draw.alpha(0.5f);
            Draw.z(Layer.blockOver);
            Draw.rect(lights, x, y);

            Draw.reset();
        }

        @Override
        public double sense(LAccess sensor){
            return sensor == LAccess.heat ? instability / maxInstability : super.sense(sensor);
        }

        @Override
        public float range(){
            return explosionRadius * scale();
        }

        @Override
        public float smoothProgress(){
            return smoothProgress;
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

        private void createLightning(){
            craftSound.at(tile, Mathf.random(0.45f, 0.55f));

            int amount = (int)(lightningAmount * (1 + instability / maxInstability));
            if(instability <= maxInstability * 0.5f){
                for(int i = 0; i < amount; i++)
                    Lightning.create(team, circleColor[counter], lightningDamage, x, y, i * (360.0f / amount), (int)(size * 2.0f + instability * 0.03f));
            }else{
                for(int i = 0; i < amount; i++)
                    LightningBranch.create(this, circleColor[counter], lightningDamage, i * (360.0f / amount), (int)(size + instability * 0.024f), 2, 2);
            }
        }

        private float scale(){
            return 1.0f + 0.25f * (timeScale - 1.0f);
        }
    }
}
