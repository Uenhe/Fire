package fire.world.blocks.production;

import arc.audio.Sound;
import arc.math.Mathf;
import mindustry.content.Bullets;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.world.blocks.production.GenericCrafter;

public class SurgeCrafter extends GenericCrafter{

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

            for(byte i = 0; i < fragBullets; i++){
                final byte j = i;
                byte[] sign = {1, -1, (byte)Mathf.sign(Mathf.range(1)), (byte)Mathf.sign(j % 2 == 0)};

                var bullet = fragBullet.create(this, x, y, 360.0f / fragBullets * j);
                bullet.lifetime /= timeScale;
                bullet.vel.scl(timeScale, timeScale);
                bullet.mover = b -> pattern.accept(b, j, sign[counter], timeScale);
            }

            counter++;
            if(counter >= 4) counter -= 4;
        }
    }

    @FunctionalInterface
    public interface Pattern{
        void accept(Bullet bullet, byte index, byte sign, float scale);
    }
}
