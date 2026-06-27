package fire.entities.weapons;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import fire.content.FRStatusEffects;
import fire.world.meta.FRStat;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.PointBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static fire.FRVars.find;

public class MinerLikedWeapon extends Weapon{
    public final float dps;
    public float armorAffection = 5.0f;
    public StatusEffect attackEffect = StatusEffects.overclock;
    public float attackEffectDuration = 180.0f;
    public float healingPercent = 100.0f;
    public float extraShield = 1.0f;
    public boolean showShield = true;
    public Color baseColor = find("f9a27a");
    public Color boostColor = find("ffd8e8");

    public MinerLikedWeapon(String name, float dps){
        super(name);
        this.dps = dps / 60.0f;
        inaccuracy = 0;
        predictTarget = false;
        minWarmup = 0.9f;
        shootWarmupSpeed = 0.08f;
        reload = 10.0f;

        bullet = new PointBulletType(){
            @Override
            public void removed(Bullet b){
                if(b.hit){
                    var owner = (Unit)b.owner;
                    if(b.aimTile != null){
                        owner.mounts[0].target = b.aimTile.build;
                    }else{
                        Unit[] result = {null};
                        float[] cdist = {0.0f};
                        Units.nearbyEnemies(b.team, b.x - 1.0f, b.y - 1.0f, 2.0F, 2.0F, (e) -> {
                            if(!e.dead() && e.checkTarget(this.collidesAir, this.collidesGround) && e.hittable()){
                                e.hitbox(Tmp.r1);
                                if(Tmp.r1.contains(b.x, b.y)){
                                    float dst = e.dst(b.x, b.y) - e.hitSize;
                                    if(result[0] == null || dst < cdist[0]){
                                        result[0] = e;
                                        cdist[0] = dst;
                                    }
                                }
                            }
                        });
                        owner.mounts[0].target = result[0];
                    }
                    if(owner.health >= owner.maxHealth){
                        Fx.itemTransfer.at(b.x, b.y, 0.0f, Pal.shield, owner);
                    }else{
                        Fx.itemTransfer.at(b.x, b.y, 0.0f, Pal.heal, owner);
                    }
                }
                super.removed(b);
            }
            {
                damage = 0.0f;
                speed = 80.0f;
                lifetime = 2.0f;
                hitEffect = despawnEffect = trailEffect = shootEffect = smokeEffect = Fx.none;
            }
        };
    }

    @Override
    public void update(Unit unit, WeaponMount mount){
        super.update(unit, mount);
        if(mount.target == null)
            return;
        if(!Mathf.within(unit.x, unit.y, mount.target.x(), mount.target.y(), 165.0f) || !Angles.within(Angles.angle(unit.x, unit.y, mount.target.x(), mount.target.y()), unit.rotation(), 5.0f) || !Mathf.within(unit.aimX, unit.aimY, mount.target.x(), mount.target.y(), 8.0f)){
            mount.target = null;
            return;
        }
        float dmg = dps * unit.damageMultiplier();
        if(mount.target instanceof Unit target){
            dmg = (dmg - target.armor() * armorAffection / 60.0f) * Time.delta;
            target.damagePierce(dmg);
            if(unit.health >= unit.maxHealth && unit.shield > unit.maxHealth){
                target.damagePierce(dmg * 0.5f);
            }
        }else{
            var target = (Building)mount.target;
            dmg = (dmg - target.block.armor * armorAffection / 60.0f) * Time.delta;
            target.damagePierce(dmg);
            if(unit.health >= unit.maxHealth && unit.shield > unit.maxHealth){
                target.damagePierce(dmg * 0.5f);
            }
        }
        if(unit.health >= unit.maxHealth){
            if(unit.shield < unit.maxHealth * extraShield){
                unit.shield(Math.min(unit.shield + dmg * healingPercent * 0.01f, unit.maxHealth));
            }
        }else{
            unit.heal(dmg * healingPercent * 0.01f);
        }
        unit.apply(attackEffect, attackEffectDuration);
    }

    @Override
    public void draw(Unit unit, WeaponMount mount){
        super.draw(unit, mount);
        if(showShield){
            Draw.z(Layer.turretHeat + 0.5f);
            Draw.blend(Blending.additive);
            if(unit.shield > 0.0f){
                float phase = unit.shield / unit.maxHealth;
                if(phase > 1.0f)
                    phase = 1.0f;
                Lines.stroke(1.0f, unit.team.color);
                Draw.alpha(phase);
                Lines.poly(unit.x, unit.y, 4, unit.hitSize * 1.5f * Mathf.sqrt2 * phase, Time.time * 2.0f);
            }
            Draw.blend();
            Draw.color();
        }

        float xx = 0, yy = 0;
        if(mount.target != null){
            xx = mount.target.x();
            yy = mount.target.y();
        }
        if((!Mathf.within(unit.x, unit.y, xx, yy, 165.0f) || !Angles.within(Angles.angle(unit.x, unit.y, xx, yy), unit.rotation(), 5.0f) || !Mathf.within(unit.aimX, unit.aimY, xx, yy, 8.0f)) || mount.target == null){
            mount.target = null;
            float realX = mount.aimX, realY = mount.aimY;
            final float sx = unit.x;
            final float sy = unit.y;
            final float ps = Mathf.sqrt((realX - sx) * (realX - sx) + (realY - sy) * (realY - sy)) / 160.0f;
            if(ps > 1f){
                realX = sx + (realX - sx) / ps;
                realY = sy + (realY - sy) / ps;
            }
            Draw.color(Tmp.c1.set(unit.hasEffect(FRStatusEffects.overgrown) ? boostColor : baseColor), mount.warmup * (baseColor.a * (0.5f + Mathf.absin(7.0f, 0.3f))));
            Drawf.laser(UnitTypes.mono.mineLaserRegion, UnitTypes.mono.mineLaserEndRegion, unit.x + Angles.trnsx(mount.rotation, 0.0f, 0.0f), unit.y + Angles.trnsy(mount.rotation, 0.0f, 0.0f), realX + Mathf.sin(Time.time, 12.0f, 1.0f), realY + Mathf.sin(Time.time, 14.0f, 1.0f), 0.75f);
            return;
        }
        float realX = mount.target.x(), realY = mount.target.y();
        final float sx = unit.x;
        final float sy = unit.y;
        final float ps = Mathf.sqrt((realX - sx) * (realX - sx) + (realY - sy) * (realY - sy)) / 160.0f;
        if(ps > 1f){
            realX = sx + (realX - sx) / ps;
            realY = sy + (realY - sy) / ps;
        }

        Draw.color(Tmp.c1.set(unit.hasEffect(FRStatusEffects.overgrown) ? boostColor : baseColor), mount.warmup * (baseColor.a * (0.5f + Mathf.absin(7.0f, 0.3f))));
        Drawf.laser(UnitTypes.mono.mineLaserRegion, UnitTypes.mono.mineLaserEndRegion, unit.x + Angles.trnsx(mount.rotation, 0.0f, 0.0f), unit.y + Angles.trnsy(mount.rotation, 0.0f, 0.0f), realX + Mathf.sin(Time.time, 12.0f, 1.0f), realY + Mathf.sin(Time.time, 14.0f, 1.0f), 0.75f);

        if(mount.target instanceof Building){
            Building target;
            target = (Building)mount.target;
            final float size = target.hitSize();

            Lines.stroke(1.0f, Pal.accent);
            Draw.alpha(mount.warmup);
            Lines.poly(target.x, target.y, 4, size * 0.5f * Mathf.sqrt2, Time.time);
        }else if(mount.target instanceof Unit){
            Unit target;
            target = (Unit)mount.target;
            final float size = target.hitSize();

            Lines.stroke(1.0f, Pal.accent);
            Draw.alpha(mount.warmup);
            Lines.poly(target.x, target.y, 4, size * 0.5f * Mathf.sqrt2, Time.time * 2.0f);
        }
        Draw.blend();
        Draw.color();
    }

    @Override
    public void addStats(UnitType u, Table t){
        t.row();
        t.add(FRStat.minerweapon1.localized() + dps * 60.0f + "[red] - " + armorAffection + FRStat.minerweapon2.localized() + "\n" + FRStat.minerweapon3.localized() + healingPercent + "%\n" + FRStat.minerweapon4.localized() + extraShield + "x\n" + FRStat.minerweapon5.localized());

        StatValues.ammo(ObjectMap.of(new Object[]{u, this.bullet})).display(t);
    }

    @Override
    public float dps(){
        return dps * 90.0f; //dps * 1.5, for no reason :)
    }
}
