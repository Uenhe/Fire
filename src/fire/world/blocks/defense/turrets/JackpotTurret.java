package fire.world.blocks.defense.turrets;

import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import fire.world.meta.FireStatValues;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.pattern.ShootPattern;
import mindustry.type.Item;
import mindustry.world.meta.Stat;

public class JackpotTurret extends mindustry.world.blocks.defense.turrets.ItemTurret{

    protected final Seq<JackpotAmmo> jackpotAmmo = new Seq<>();
    protected boolean centerChargeEffect;

    protected JackpotTurret(String name){
        super(name);
    }

    @Override
    public void init(){
        super.init();
        for(final var ammo : jackpotAmmo){
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
                JackpotAmmo type = null;

                if(peekAmmo() == jackpotAmmo.peek().type)
                    type = jackpotAmmo.peek();
                else{
                    for(final var a : jackpotAmmo)
                        if(a.type == peekAmmo()){
                            final byte i = (byte)(Mathf.chance(a.chance) ? 1 : 0);
                            type = jackpotAmmo.get(jackpotAmmo.indexOf(a) + i);
                        }
                }

                shoot(type);
                reloadCounter %= reload;
            }
        }

        private void shoot(JackpotAmmo ammo){
            final float
                bx = centerChargeEffect ? x : x + Angles.trnsx(rotation - 90f, shootX, shootY),
                by = centerChargeEffect ? y : y + Angles.trnsy(rotation - 90f, shootX, shootY);

            if(shoot.firstShotDelay > 0f){
                chargeSound.at(bx, by, Mathf.random(soundPitchMin, soundPitchMax));
                ammo.type.chargeEffect.at(bx, by, rotation);
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
    }
}
