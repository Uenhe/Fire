package fire.world.blocks.defense.turrets;

import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

public class ItemBulletAdaptingTurret extends ItemTurret{
    public static IntMap<Seq<BulletStack>> bulletStack;

    public ItemBulletAdaptingTurret(String name){
        super(name);
        buildType = ItemBulletAdaptingTurret.ItemBulletAdaptingTurretBuild::new;
    }

    public void stack(Object... objects){

        bulletStack = IntMap.of(objects);
    }

    public class ItemBulletAdaptingTurretBuild extends ItemTurretBuild{
        public float adaptTimer, timer2;

        public BulletType findAmmo(int x){
            for(int i = 0; i < bulletStack.get(x).size - 1; i++){
                if(adaptTimer < bulletStack.get(x).get(i).req){
                    return bulletStack.get(x).get(i).type;
                }
            }
            BulletStack a = bulletStack.get(x).get(bulletStack.get(x).size - 1), b = bulletStack.get(x).get(bulletStack.get(x).size - 2);
            adaptTimer -= a.req;
            shoot(a.type);
            return b.type;
        }

        @Override
        public BulletType peekAmmo() {
            return this.ammo.size == 0 ? null : bulletStack.get(item().id).size < 3 ? bulletStack.get(item().id).get(0).type : findAmmo(item().id);
        }


        @Override
        public void updateTile(){
            if(!wasShooting){
                timer2 = Math.min(5.0f, timer2 + delta() * 0.001f);
                adaptTimer = Math.max(0.0f,adaptTimer - delta() * timer2);
            }
            else {
                timer2 = 0.0f;
                adaptTimer += delta();
            }
            super.updateTile();
        }
        /** Do not support using multiple ammoTypes. */
        @Override
        public boolean acceptItem(Building source, Item item){
            return (totalAmmo == 0 || item() == item) && super.acceptItem(source, item);
        }

        /** Prevent units supplying multiple ammoTypes. */
        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            return (totalAmmo == 0 || item() == item) ? super.acceptStack(item, amount, source) : 0;
        }

        private Item item(){
            return ((ItemEntry)ammo.peek()).item;
        }
    }

    public static class BulletStack{
        public final int req;
        public final Item item;
        public final BulletType type;
        public BulletStack(int req, BulletType type, Item item){
            this.req = req;
            this.item = item;
            this.type = type;
        }
    }
}
