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
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;

import static mindustry.Vars.*;
import static fire.FireLib.*;

public class EnergyCrafter extends mindustry.world.blocks.production.GenericCrafter{

    /** {@link mindustry.world.blocks.power.PowerGenerator} */
    protected float
        explosionRadius = 40f,
        explosionDamage = 100f,
        explosionShake = 0f,
        explosionShakeDuration = 6f;
    /** {@link mindustry.world.blocks.power.PowerGenerator} */
    protected Effect explodeEffect = Fx.none;
    /** {@link mindustry.world.blocks.power.PowerGenerator} */
    protected Sound explodeSound = Sounds.none;
    protected float maxInstability = 360f;
    protected float stabilizeInterval = 60f;
    protected float lightningDamage = 80f;
    protected byte lightningAmount = 8;
    protected Sound craftSound = Sounds.none;
    protected Color baseColor = Color.valueOf("ffffff");
    protected Color[] circleColor = {Pal.plastanium, Pal.lightishOrange};

    private final short timerStabilize = (short)timers++;

    protected EnergyCrafter(String name){
        super(name);
        baseExplosiveness = 5f;
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

    public class EnergyCrafterBuild extends GenericCrafterBuild{

        private float instability, fraction, flash;
        private short angle, colorCounter;
        private byte realLightningAmount;
        private Color color = circleColor[0];

        @Override
        public final void updateTile(){
            super.updateTile();
            if(instability > maxInstability) kill();

            final boolean wasUnstable = instability > 0f;
            instability = Math.max(instability, 0f);
            fraction = Interp.smoother.apply(1f - progress);

            if(consValid(this)){
                indexer.eachBlock(this, explosionRadius, b -> true, build -> {
                    if(build != null && build.block == this.block && consValid(build) && build != this){
                        instability += delta();
                    }
                });
            }

            if(wasUnstable && efficiency > 0f && Mathf.chanceDelta((instability / maxInstability) * 0.05f)){
                createLightning();
            }

            if(wasUnstable && timer(timerStabilize, stabilizeInterval)){
                Fx.healBlockFull.at(x, y, size, color, block);
                instability -= maxInstability * 0.1f;
            }
        }

        @Override
        public void craft(){
            super.craft();
            colorCounter = (short)(colorCounter >= circleColor.length - 1 ? 0 : colorCounter + 1);
            color = circleColor[colorCounter];
            angle = (short)Mathf.random(360);
            realLightningAmount = (byte)(lightningAmount * (1 + instability / maxInstability));
            createLightning();
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
            if(state.rules.reactorExplosions && consValid(this)){
                Damage.damage(x, y, explosionRadius * time(), explosionDamage * time());
                explodeEffect.at(this);
                explodeSound.at(this);
                Effect.shake(explosionShake * time(), explosionShakeDuration * time(), this);
            }
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Drawf.dashCircle(x, y, explosionRadius, team.color);
        }

        private float time(){
            return (timeScale - 1f) * 0.5f + 1f;
        }

        private void createLightning(){
            craftSound.at(this, Mathf.random(0.45f, 0.55f));
            for(byte i = 0; i < realLightningAmount; i++){
                Lightning.create(team, color, lightningDamage, x, y, (i - 1) * (360f / realLightningAmount), (int)(size * 2f + instability * 0.03f));
            }
        }

        @Override
        public void draw(){
            super.draw();

            if(consValid(this)){
                flash += (1f + instability / maxInstability * 6f) * Time.delta;
                Draw.z(Layer.effect);
                Lines.stroke(2.5f, color);
                Lines.arc(x, y, size * 5f, fraction, angle);
            }

            if(instability > maxInstability / 3f && Core.settings.getBool("showBlockRange")){
                final float alpha = Mathf.absin(2.4f, 0.6f);
                Draw.color(Pal.health, alpha);
                Lines.stroke(1f);
                Lines.circle(x, y, explosionRadius * time());
                Draw.alpha(alpha / 3f);
                Fill.circle(x, y, explosionRadius * time());
            }

            Draw.color(color, baseColor, Mathf.absin(flash, 9f, 1f));
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
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            instability = read.f();
        }
    }
}
