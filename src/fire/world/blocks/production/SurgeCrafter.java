package fire.world.blocks.production;

import arc.audio.Sound;
import arc.math.Mathf;
import mindustry.content.Bullets;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;

import static fire.FRVars.blockSpecial;

public class SurgeCrafter extends mindustry.world.blocks.production.GenericCrafter{

    protected byte fragBullets;
    protected BulletType fragBullet = Bullets.placeholder;
    /** Used for custom bullets behavior. */
    protected Pattern pattern = (bullet, index, sign, scale) -> {};
    protected Sound craftSound = Sounds.none;

    public SurgeCrafter(String name){
        super(name);
        baseExplosiveness = 5.0f;
        buildType = SurgeCrafterBuild::new;
    }

    public class SurgeCrafterBuild extends GenericCrafterBuild{

        private byte counter;

        @Override
        public void craft(){
            super.craft();
            craftSound.at(this, Mathf.random(0.9f, 1.1f));
            if(!blockSpecial) return;

            for(byte i = 0; i < fragBullets; i++){
                final byte j = i;
                byte[] sign = {1, -1, (byte)Mathf.sign(Mathf.range(1)), (byte)Mathf.sign(j % 2 == 0)};

                var bullet = fragBullet.create(this, x, y, 360.0f / fragBullets * j);
                bullet.lifetime /= Mathf.pow(timeScale, 0.99f);
                bullet.vel.scl(timeScale);
                bullet.mover = b -> pattern.accept(b, j, sign[counter], timeScale);
            }

            if(++counter >= 4) counter -= 4;
        }
    }

    @FunctionalInterface
    public interface Pattern{
        void accept(Bullet bullet, byte index, byte sign, float scale);
    }
}
