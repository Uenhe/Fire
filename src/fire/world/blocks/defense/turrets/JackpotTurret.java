package fire.world.blocks.defense.turrets;

import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import fire.world.meta.FRStat;
import fire.world.meta.FRStatValues;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.pattern.ShootPattern;
import mindustry.type.Item;

public class JackpotTurret extends mindustry.world.blocks.defense.turrets.ItemTurret{

    protected final Seq<JackpotAmmo> jackpotAmmo = new Seq<>();

    protected JackpotTurret(String name){
        super(name);
        buildType = JackpotTurretBuild::new;
    }

    @Override
    public void init(){
        super.init();
        var jackpotAmmo = this.jackpotAmmo;
        for(var ammo : jackpotAmmo){
            ammo.type.ammoMultiplier = 1.0f;
            ammoTypes.put(ammo.item, ammo.type);
        }
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(FRStat.ammoDetails, FRStatValues.ammoDetails(jackpotAmmo));
    }

    public class JackpotTurretBuild extends ItemTurretBuild{

        @Override
        protected void updateShooting(){
            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup){
                JackpotAmmo type = null;

                var jackpotAmmo = JackpotTurret.this.jackpotAmmo;
                if(peekAmmo() == jackpotAmmo.peek().type){
                    type = jackpotAmmo.peek();
                }else{
                    for(var a : jackpotAmmo)
                        if(a.type == peekAmmo())
                            type = jackpotAmmo.get(jackpotAmmo.indexOf(a) + Mathf.num(Mathf.chance(a.chancePercentage * 0.01)));
                }

                shoot(type);
                reloadCounter -= reload;
            }
        }

        private void shoot(JackpotAmmo ammo){
            float bx = x + Angles.trnsx(rotation - 90.0f, shootX, shootY),
                by = y + Angles.trnsy(rotation - 90.0f, shootX, shootY);

            if(shoot.firstShotDelay > 0.0f){
                chargeSound.at(bx, by, Mathf.random(soundPitchMin, soundPitchMax));
                ammo.type.chargeEffect.at(bx, by, rotation);
            }

            ammo.shoot.shoot(barrelCounter, (xOffset, yOffset, angle, delay, mover) -> {
                queuedBullets++;

                if(delay > 0.0f)
                    Time.run(delay, () -> bullet(ammo.type, xOffset, yOffset, angle, mover));
                else
                    bullet(ammo.type, xOffset, yOffset, angle, mover);

            }, () -> barrelCounter++);

            if(consumeAmmoOnce) useAmmo();
        }
    }

    public static class JackpotAmmo{
        public final Item item;
        public final byte chancePercentage;
        public final ShootPattern shoot;
        public final BulletType type;
        public JackpotAmmo(Item item, int chancePercentage, ShootPattern shoot, BulletType type){
            this.item = item;
            this.chancePercentage = (byte)chancePercentage;
            this.shoot = shoot;
            this.type = type;
        }
    }
}
