package fire.world.blocks.production;

import arc.audio.Sound;
import arc.math.Mathf;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Sounds;

public class SurgeCrafter extends mindustry.world.blocks.production.GenericCrafter{

    public int fragBullets = 6;
    public BulletType fragBullet;
    public Sound craftSound = Sounds.none;

    public SurgeCrafter(String name){
        super(name);
        baseExplosiveness = 5f;
    }

    public class SurgeCrafterBuild extends GenericCrafterBuild {

        @Override
        public void craft() {
            super.craft();
            craftSound.at(this, Mathf.random(0.45f, 0.55f));

            //create bullet
            float rand = Mathf.random(360f);
            for (int i = 0; i < fragBullets; i++) {
                fragBullet.create(this, x, y, 360f / fragBullets * i + rand);
            }
        }
    }
}
