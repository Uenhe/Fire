package fire.world.blocks.production;

import arc.audio.Sound;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;

import static fire.FRVars.specialContent;

public class SurgeCrafter extends mindustry.world.blocks.production.GenericCrafter{

    protected byte fragBullets;
    protected @Nullable BulletType fragBullet;
    protected @Nullable SignPattern signs;
    protected @Nullable BulletPattern bullets;
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
            if(!specialContent || fragBullets == 0) return;

            int len = 0;
            for(byte i = 0; i < fragBullets; i++){
                var bullet = fragBullet.create(this, x, y, 360.0f / fragBullets * i);
                var ss = signs.apply(i);
                if(i == 0) len = ss.length;

                bullet.lifetime /= Mathf.pow(timeScale, 0.99f);
                bullet.vel.scl(timeScale);
                bullet.mover = b -> bullets.accept(b, (byte)Mathf.sign(ss[counter]));
            }

            if(++counter >= len) counter = 0;
        }
    }

    @FunctionalInterface
    public interface SignPattern{
        boolean[] apply(byte index); //true -> "+", false -> "-"
    }

    @FunctionalInterface
    public interface BulletPattern{
        void accept(Bullet bullet, byte sign);
    }
}
