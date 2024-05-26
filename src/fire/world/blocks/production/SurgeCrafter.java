package fire.world.blocks.production;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.gen.Bullet;

public class SurgeCrafter extends mindustry.world.blocks.production.GenericCrafter{

    protected byte fragBullets;
    protected mindustry.entities.bullet.BulletType fragBullet = mindustry.content.Bullets.placeholder;
    /** Used for custom bullets behavior. */
    protected Pattern pattern = (bullet, index, sign) -> {};
    protected arc.audio.Sound craftSound = mindustry.gen.Sounds.none;

    public SurgeCrafter(String name){
        super(name);
        baseExplosiveness = 5.0f;
        buildType = SurgeCrafterBuild::new;
    }

    public class SurgeCrafterBuild extends GenericCrafterBuild{

        protected final Seq<mindustry.gen.Bullet> bullets = new Seq<>(fragBullets);
        private byte sign;

        @Override
        public void craft(){
            super.craft();
            craftSound.at(this, Mathf.random(0.45f, 0.55f));
            bullets.clear();

            //create bullet
            final float rand = Mathf.random(360.0f);
            for(byte i = 0; i < fragBullets; i++){
                bullets.add(fragBullet.create(this, x, y, 360.0f / fragBullets * i + rand));
            }
            sign = (byte)Mathf.sign(bullets.get(0).id % 2 == 0);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(bullets.isEmpty()) return;

            bullets.each(b -> {
                // use replace() so that the origin pattern won't be messed up
                if(!b.isAdded()) bullets.replace(b, Bullet.create());

                pattern.get(b, bullets.indexOf(b), sign);
            });
        }
    }

    public interface Pattern{
        void get(Bullet bullet, int index, byte sign);
    }
}
