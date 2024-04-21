package fire.world.blocks.defense.turrets;

import arc.struct.OrderedMap;
import mindustry.entities.bullet.BulletType;
import mindustry.type.Item;

public class ItemBulletStackTurret extends mindustry.world.blocks.defense.turrets.ItemTurret{

    /** The first bullet to shoot should be placed in {@code ammoTypes}. */
    protected OrderedMap<Item, arc.struct.Seq<BulletStack>> bulletStack;

    protected ItemBulletStackTurret(String name){
        super(name);
        consumeAmmoOnce = true;
    }

    protected void stack(Object... objects){
        bulletStack = OrderedMap.of(objects);
    }

    public class ItemBulletStackTurretBuild extends ItemTurretBuild{

        private float reloadTimer;
        private byte index;
        private boolean shooting;

        @Override
        protected void updateShooting(){
            if(ammo.isEmpty()) return;
            if(unavailable()){
                super.updateShooting();
                return;
            }

            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup){
                shoot(peekAmmo());
                shooting = true;
            }
        }

        @Override
        public void updateTile(){
            if(totalAmmo == 0) return;
            if(bulletStack.get(item()) == null || !shooting){
                super.updateTile();
            }else{
                reloadTimer += delta();

                final var stack = bulletStack.get(item()).get(index);
                if(reloadTimer >= stack.delay){
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
            if(totalAmmo == 0) return true;
            else return ammoTypes.get(item) != null && item() == item && totalAmmo + ammoTypes.get(item).ammoMultiplier <= maxAmmo;
        }

        private Item item(){
            return ((ItemEntry)ammo.peek()).item;
        }

        /** For items which have no BulletStack (WiP or not intended). */
        private boolean unavailable(){
            return bulletStack.get(item()) == null;
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
