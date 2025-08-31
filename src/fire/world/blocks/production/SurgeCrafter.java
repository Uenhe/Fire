package fire.world.blocks.production;

import arc.audio.Sound;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;

public class SurgeCrafter extends mindustry.world.blocks.production.GenericCrafter{

    protected byte fragBullets;
    protected @Nullable BulletType fragBullet;
    protected @Nullable SignPattern signs;
    protected @Nullable BulletPattern bullets;
    protected Sound craftSound = Sounds.none;

    private byte len;

    public SurgeCrafter(String name){
        super(name);
        baseExplosiveness = 5.0f;
        buildType = SurgeCrafterBuild::new;
    }

    @Override
    public void init(){
        super.init();
        len = (byte)signs.apply((byte)0).length;
    }

    public class SurgeCrafterBuild extends GenericCrafterBuild{

        private byte counter;

        @Override
        public void craft(){
            super.craft();
            craftSound.at(tile, Mathf.random(0.9f, 1.1f));
            byte n = fragBullets;
            if(n == 0) return;

            for(byte i = 0; i < n; i++){
                var bullet = fragBullet.create(this, x, y, 360.0f * i / n);
                if(bullet == null) continue; //???

                boolean s = signs.apply(i)[counter];
                bullet.lifetime /= Mathf.pow(timeScale, 0.99f);
                bullet.vel.scl(timeScale);
                bullet.mover = b -> bullets.accept(b, (byte)Mathf.sign(s));
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
