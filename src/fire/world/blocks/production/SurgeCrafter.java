package fire.world.blocks.production;

import arc.math.Mathf;
import arc.struct.Seq;

public class SurgeCrafter extends mindustry.world.blocks.production.GenericCrafter{

    protected byte fragBullets = 6;
    /** Remember to set {@code lifetime} to be less than {@code craftTime}. */
    protected mindustry.entities.bullet.BulletType fragBullet;
    protected arc.audio.Sound craftSound = mindustry.gen.Sounds.none;

    public SurgeCrafter(String name){
        super(name);
        baseExplosiveness = 5f;
    }

    public class SurgeCrafterBuild extends GenericCrafterBuild{

        /** Used for custom bullets behavior. */
        protected final Seq<mindustry.gen.Bullet> bullets = new Seq<>();

        @Override
        public void craft(){
            super.craft();
            craftSound.at(this, Mathf.random(0.45f, 0.55f));

            //create bullet
            final float rand = Mathf.random(360f);
            for(byte i = 0; i < fragBullets; i++){
                bullets.add(fragBullet.create(this, x, y, 360f / fragBullets * i + rand));
            }
        }

        /** Used for custom bullets behavior. */
        protected void updateBullet(){
            bullets.each(b -> {

                // some custom part
                b.update();

                // necessary
                if(b.time >= b.lifetime)
                    bullets.clear();
            });
        }

        @Override
        public void update(){
            super.update();

            // write this in update() instead of updateTile()
            // since if the factory is not working, bullets will stop updating

            // clear corrupted bullets
            if(bullets.size > fragBullets)
                bullets.clear();

            else if(bullets.size == fragBullets)
                updateBullet();
        }
    }
}
