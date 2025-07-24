package fire.world.kits;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import fire.world.meta.FRStat;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

import static mindustry.Vars.state;

/** @see mindustry.entities.abilities.EnergyFieldAbility EnergyFieldAbility */
public class EnergyField{

    public static class EnergyFieldPowerTurret extends mindustry.world.blocks.defense.turrets.PowerTurret{

        public EnergyFieldPowerTurret(String name){
            super(name);
            targetInterval = 1.0f;
            buildType = EnergyFieldPowerTurretBuild::new;
        }

        @Override
        public void setStats(){
            super.setStats();
            stats.add(FRStat.maxTargets, type().maxTargets);
        }

        private EnergyFieldBulletType type(){
            return (EnergyFieldBulletType)shootType;
        }

        public class EnergyFieldPowerTurretBuild extends PowerTurretBuild{

            @Override
            public void updateTile(){
                super.updateTile();
                unit.rotation(0.0f);
                rotation += shootWarmup * rotateSpeed * edelta();
            }

            @Override
            public void draw(){
                super.draw();
                if(shootWarmup < 0.01f) return;

                Draw.z(Layer.bullet - 0.001f);
                Draw.color(type().lightningColor);
                Lines.stroke(Lines.getStroke() * shootWarmup * 0.8f);
                Drawf.light(x, y, range * 1.2f, type().lightningColor, shootWarmup * 0.6f);
                for(int i = 0, n = 6; i < n; i++)
                    Lines.arc(x, y, range, 0.12f, i * 360.0f / n + Time.time * rotateSpeed * 0.5f);

                Draw.reset();
            }
        }
    }

    public static class EnergyFieldBulletType extends mindustry.entities.bullet.BulletType{

        public final byte maxTargets;

        public EnergyFieldBulletType(float damage, int max){
            super(0.0f, damage);
            maxTargets = (byte)max;
            shootEffect = Fx.none;
        }

        @Override
        public void init(Bullet b){
            super.init(b);
            var targets = new Seq<Healthc>();

            Units.nearby(null, b.x, b.y, homingRange, unit -> {
                if(unit.checkTarget(collidesAir, collidesGround) && unit.targetable(b.team) && unit.team != b.team)
                    targets.add(unit);
            });

            if(collidesGround) Units.nearbyBuildings(b.x, b.y, homingRange, build -> {
                if((build.team != Team.derelict || state.rules.coreCapture) && build.team != b.team)
                    targets.add(build);
            });

            if(targets.isEmpty()) return;

            if(b.owner instanceof Building build)
                b.damage *= Mathf.sqrt(build.timeScale());

            targets.sort(h -> h.dst2(b.x, b.y));
            for(int i = 0, len = Math.min(targets.size, maxTargets); i < len; i++){
                var target = targets.get(i);

                //lightning gets absorbed by plastanium
                var absorber = Damage.findAbsorber(b.team, b.x, b.y, target.x(), target.y());
                if(absorber != null) target = absorber;

                //target must be either unit or building
                if(target instanceof Unit u){
                    b.collision(u, u.x, u.y);
                }else{
                    assert target instanceof Building;
                    ((Building)target).collision(b);
                }

                hitEffect.at(b.x, b.y, 0.0f, lightningColor, target);
            }

            b.remove();
        }
    }
}
