package fire.world.blocks.production;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;

import static mindustry.Vars.*;
import static fire.FireLib.*;

public class EnergyCrafter extends GenericCrafter{
    /** {@link mindustry.world.blocks.power.PowerGenerator} */
    public float
        explosionRadius = 240f,
        explosionDamage = 2880f,
        explosionShake = 0f,
        explosionShakeDuration = 6f;
    /** {@link mindustry.world.blocks.power.PowerGenerator} */
    public Effect explodeEffect = Fx.none;
    /** {@link mindustry.world.blocks.power.PowerGenerator} */
    public Sound explodeSound = Sounds.none;
    public float maxInstability = 360f;
    public float lightningDamage = 80f;
    public int lightningAmount = 8;
    public Color baseColor = color("faaaaa");
    public Color[] circleColor = {Pal.plastanium, Pal.lightishOrange};

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
        protected float instability, fraction, flash;
        protected int rotation, colorCount;
        protected Color color = circleColor[0];

        @Override
        public void updateTile(){
            super.updateTile();
            if(consValid(this)){
                fraction = Mathf.lerpDelta(fraction, 0f, craftTime / 4000f * timeScale);
                indexer.eachBlock(this, explosionRadius, b -> true, build -> {
                    if(build != null && build.block == this.block && consValid(build) && build != this) instability += Time.delta;
                });
            }
            if(instability > maxInstability) kill();
        }

        @Override
        public void craft(){
            super.craft();
            colorCount = colorCount >= circleColor.length - 1 ? 0 : colorCount + 1;
            color = circleColor[colorCount];
            rotation = Mathf.random(360);
            Sounds.release.at(this, Mathf.random(0.45f, 0.55f));
            for(int i = 0; i < (instability > maxInstability / 2f ? lightningAmount * 2 : lightningAmount); i += 1){
                Lightning.create(team, color, lightningDamage, x, y, (i - 1) * 45f, (int)(size * 2 + instability * 0.03));
            }
            fraction = 1.05f;
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
            if(state.rules.reactorExplosions && consValid(this)){
                //create explosion.
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

        @Override
        public void draw(){
            super.draw();
            if(consValid(this)){
                flash += (1f + instability / maxInstability * 6f) * Time.delta;
                Draw.z(Layer.effect);
                Lines.stroke(2.5f, color);
                Draw.alpha(1f - progress);
                Lines.arc(x, y, size * 5f, fraction, rotation);
            }
            if(instability > maxInstability / 3f && getSetting("showBlockRange")){
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
            Draw.rect(atlas(name + "-lights"), x, y);
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
