package fire.world.blocks.defense.turrets;

import arc.struct.ObjectMap;
import mindustry.entities.bullet.BulletType;
import mindustry.type.Item;

public class ItemBulletStackTurret extends mindustry.world.blocks.defense.turrets.ItemTurret{

    /** The first bullet to shoot should be placed in {@code ammoTypes}. */
    protected ObjectMap<Item, arc.struct.Seq<BulletStack>> bulletStack;

    protected ItemBulletStackTurret(String name){
        super(name);
        consumeAmmoOnce = true;
        buildType = ItemBulletStackTurretBuild::new;
    }

    protected void stack(Object... objects){
        bulletStack = ObjectMap.of(objects);
    }

    public class ItemBulletStackTurretBuild extends ItemTurretBuild{

        private float reloadTimer;
        private byte index;
        private boolean shooting;

        @Override
        protected void updateShooting(){
            if(ammo.isEmpty()) return;

            if(unavailable())
                super.updateShooting();

            else if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup){
                shoot(peekAmmo());
                shooting = true;
            }
        }

        @Override
        public void updateTile(){
            if(totalAmmo == 0) return;

            if(unavailable() || !shooting){
                super.updateTile();

            }else{
                var stack = bulletStack.get(item()).get(index);

                if((reloadTimer += delta()) >= stack.delay){
                    reloadTimer %= stack.delay;

                    shoot(stack.type);
                    index++;
                }

                if(index > bulletStack.size){
                    index = 0;
                    reloadCounter %= reload;
                    shooting = false;
                }
            }
        }

        /** Do not support using multiple ammoTypes. */
        @Override
        public boolean acceptItem(mindustry.gen.Building source, Item item){
            return (totalAmmo == 0 || item() == item) && super.acceptItem(source, item);
        }

        /** Prevent units supplying multiple ammoTypes. */
        @Override
        public int acceptStack(Item item, int amount, mindustry.gen.Teamc source){
            return (totalAmmo == 0 || item() == item) ? super.acceptStack(item, amount, source) : 0;
        }

        /** For items that have no BulletStack (WiP or not intended). */
        private boolean unavailable(){
            return bulletStack.get(item()) == null;
        }

        private Item item(){
            return ((ItemEntry)ammo.peek()).item;
        }
    }

    public static class BulletStack{

        private final float delay;
        private final BulletType type;

        public BulletStack(float delay, BulletType type){
            this.delay = delay;
            this.type = type;
        }
    }
}
