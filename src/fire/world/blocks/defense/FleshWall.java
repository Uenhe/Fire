package fire.world.blocks.defense;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Damage;
import mindustry.entities.Lightning;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.type.Liquid;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

public class FleshWall extends mindustry.world.blocks.defense.Wall{

    protected float healPercent;
    protected float optionalMultiplier;
    protected byte frames;
    protected byte frameTime;

    public FleshWall(String name){
        super(name);
        update = true;
        buildType = FleshWallBuild::new;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.repairTime, (int)(1.0f / 60.0f / healPercent), StatUnit.seconds);
        ConsumeLiquid cons = findConsumer(c -> c instanceof ConsumeLiquid);
        if(cons != null){
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(), cons.amount, optionalMultiplier, false, cons::consumes));
        }
    }

    public class FleshWallBuild extends WallBuild{

        /** Modified from super's one. */
        @Override
        public boolean collision(Bullet bullet){
            boolean wasDead = health <= 0.0f;

            float dmg = bullet.type.buildingDamage(bullet);
            if(!bullet.type.pierceArmor) dmg = Damage.applyArmor(dmg, armor);
            damage(bullet, bullet.team, dmg);

            if(health <= 0.0f && !wasDead) Events.fire(new EventType.BuildingBulletDestroyEvent(this, bullet));

            hit = 1.0f;

            if(lightningChance > 0.0f && Mathf.chance(lightningChance)){
                Lightning.create(team, lightningColor, lightningDamage, x, y, bullet.rotation() + 180.0f, lightningLength);
                lightningSound.at(tile, Mathf.random(0.9f, 1.1f));
            }

            if(chanceDeflect > 0.0f && bullet.type.reflectable && bullet.vel.len() > 0.1f && Mathf.chance(chanceDeflect / bullet.damage())){
                deflectSound.at(tile, Mathf.random(0.9f, 1.1f));

                bullet.trns(-bullet.vel.x, -bullet.vel.y);
                if(Math.abs(x - bullet.x) > Math.abs(y - bullet.y))
                    bullet.vel.x *= -1.0f;
                else
                    bullet.vel.y *= -1.0f;

                bullet.owner = this;
                bullet.team = team;
                bullet.time += 1.0f;

                return false;
            }

            return true;
        }

        @Override
        public void updateTile(){
            dumpLiquid(liquids.current(), 1.5f);
            if(damaged()){
                healFract(Mathf.lerp(1.0f, optionalMultiplier, optionalEfficiency) * healPercent * edelta());
                recentlyHealed();
            }
        }

        @Override
        public boolean shouldConsume(){
            return super.shouldConsume() && damaged();
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return consumesLiquid(liquid) && (liquids.current() == liquid || liquids.currentAmount() < 0.2f);
        }

        @Override
        public boolean canDumpLiquid(Building to, Liquid liquid){
            return block == to.block;
        }

        @Override
        public void splashLiquid(Liquid liquid, float amount){
            //doesn't splash liquids if destroyed
        }

        @Override
        public void draw(){
            super.draw();
            if(frames > 0) Draw.rect(name + (byte)((Time.time / frameTime % frames) + 1), x, y);
        }
    }
}
