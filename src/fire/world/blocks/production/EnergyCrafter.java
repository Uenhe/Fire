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
import mindustry.ui.Bar;

import static mindustry.Vars.*;
import static fire.FireLib.*;

public class EnergyCrafter extends mindustry.world.blocks.production.GenericCrafter{
    /** {@link mindustry.world.blocks.power.PowerGenerator} */
    public float
        explosionRadius = 40f,
        explosionDamage = 100f,
        explosionShake = 0f,
        explosionShakeDuration = 6f;
    /** {@link mindustry.world.blocks.power.PowerGenerator} */
    public Effect explodeEffect = Fx.none;
    /** {@link mindustry.world.blocks.power.PowerGenerator} */
    public Sound explodeSound = Sounds.none;
    public float maxInstability = 360f;
    public float stabilizeInterval = 60f;
    public float lightningDamage = 80f;
    public int lightningAmount = 8;
    public Sound craftSound = Sounds.none;
    public Color baseColor = Color.valueOf("faaaaa");
    public Color[] circleColor = {Pal.plastanium, Pal.lightishOrange};
    private final int timerStabilize = timers++;

    public EnergyCrafter(String name){
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
        public float instability, fraction, flash;
        public int rotation, colorCounter, realLightningAmount = lightningAmount;
        public Color color = circleColor[0];

        @Override
        public void updateTile(){
            super.updateTile();
            if(instability > maxInstability) kill();

            boolean wasUnstable = instability > 0f;
            instability = Math.max(instability, 0f);
            fraction = Interp.smoother.apply(1f - progress);

            if(consValid(this)){
                indexer.eachBlock(this, explosionRadius, b -> true, build -> {
                    if(build != null && build.block == this.block && consValid(build) && build != this){
                        instability += Time.delta;
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
            colorCounter = colorCounter >= circleColor.length - 1 ? 0 : colorCounter + 1;
            color = circleColor[colorCounter];
            rotation = Mathf.random(360);
            realLightningAmount = (int)(lightningAmount * (1 + instability / maxInstability));
            createLightning();
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
            if(state.rules.reactorExplosions && consValid(this)){
                //create explosion
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

        protected float time(){
            return (timeScale - 1f) * 0.5f + 1f;
        }

        protected void createLightning(){
            craftSound.at(this, Mathf.random(0.45f, 0.55f));
            for(int i = 0; i < realLightningAmount; i++){
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
                Lines.arc(x, y, size * 5f, fraction, rotation);
            }
            if(instability > maxInstability / 3f && Core.settings.getBool("showBlockRange")){
                float alpha = Mathf.absin(2.4f, 0.6f);
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
