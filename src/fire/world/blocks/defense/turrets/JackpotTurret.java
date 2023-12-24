package fire.world.blocks.defense.turrets;

import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import fire.world.meta.FireStatValues;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.pattern.ShootPattern;
import mindustry.type.Item;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.Stat;

public class JackpotTurret extends ItemTurret{
    public Seq<JackpotAmmo> jackpotAmmo = new Seq<>();
    public boolean centerChargeEffect = false;

    public JackpotTurret(String name){
        super(name);
    }

    @Override
    public void init(){
        super.init();
        for(var ammo : jackpotAmmo){
            ammo.type.ammoMultiplier = 1;
            ammoTypes.put(ammo.item, ammo.type);
        }
    }



    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.ammo);
        stats.add(Stat.ammo, FireStatValues.ammo(jackpotAmmo, 0));
    }

    public class JackpotTurretBuild extends ItemTurretBuild{

        @Override
        protected void updateShooting(){
            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup){
                JackpotAmmo type = new JackpotAmmo();

                if(peekAmmo() == jackpotAmmo.peek().type){
                    type = jackpotAmmo.peek();
                }else{
                    for(JackpotAmmo e : jackpotAmmo) if(e.type == peekAmmo()){
                        int i = Mathf.chance(e.chance) ? 1 : 0;
                        type = jackpotAmmo.get(jackpotAmmo.indexOf(e) + i);
                    }
                }

                shoot(type);
                reloadCounter %= reload;
            }
        }

        protected void shoot(JackpotAmmo ammo){
            float
                bulletX = centerChargeEffect ? x : x + Angles.trnsx(rotation - 90f, shootX, shootY),
                bulletY = centerChargeEffect ? y : y + Angles.trnsy(rotation - 90f, shootX, shootY);

            if(shoot.firstShotDelay > 0f){
                chargeSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
                ammo.type.chargeEffect.at(bulletX, bulletY, rotation);
            }

            ammo.shoot.shoot(barrelCounter, (xOffset, yOffset, angle, delay, mover) -> {
                queuedBullets++;
                if(delay > 0f){
                    Time.run(delay, () -> bullet(ammo.type, xOffset, yOffset, angle, mover));
                }else{
                    bullet(ammo.type, xOffset, yOffset, angle, mover);
                }
            }, () -> barrelCounter++);

            if(consumeAmmoOnce){
                useAmmo();
            }
        }
    }

    public static class JackpotAmmo{
        public Item item;
        public float chance;
        public ShootPattern shoot;
        public BulletType type;

        public JackpotAmmo(Item item, float chance, ShootPattern shoot, BulletType type){
            this.item = item;
            this.chance = chance;
            this.shoot = shoot;
            this.type = type;
        }

        JackpotAmmo(){}
    }
}
