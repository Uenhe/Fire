package fire.entities.abilities;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

import static mindustry.Vars.tilesize;
import static fire.FireLib.format;

public class EnergyForceFieldAbility extends ForceFieldAbility{
    public float chanceDeflect;
    public float lightningDamage;
    public int lightningLength;
    public int lightningAmount;
    public Color lightningColor = Pal.surge;

    public EnergyForceFieldAbility(float radius, float regen, float max, float cooldown, float chance, float damage, int length, int amount){
        super(radius, regen, max, cooldown);
        chanceDeflect = chance;
        lightningDamage = damage;
        lightningLength = length;
        lightningAmount = amount;
    }

    @Override
    public String localized(){
        return format("ability.fire-energyforcefield", radius / tilesize, regen * 60, max, cooldown / 60, sides, chanceDeflect, lightningDamage, lightningLength / tilesize, lightningAmount);
    }

    @Override
    public void update(Unit unit){
        if(rotation >= 361f) rotation = rotation > 720f ? 361f : rotation + Time.delta;
        alpha = Math.max(alpha - Time.delta / 10f, 0f);
        if(unit.shield < max) unit.shield += Time.delta * regen;
        if(unit.shield > 0f){
            radiusScale = Mathf.lerpDelta(radiusScale, 1f, 0.06f);
            Groups.bullet.intersect(unit.x - realRad(), unit.y - realRad(), realRad() * 2f, realRad() * 2f, bullet -> {
                if(bullet.team != unit.team && bullet.type.absorbable && Intersector.isInRegularPolygon(sides, unit.x, unit.y, realRad(), rotation, bullet.x, bullet.y) && unit.shield > 0f){
                    boolean collision = true;
                    Fx.absorb.at(bullet);
                    if(bullet.vel.len() > 0.1f && bullet.type.reflectable && Mathf.chance(chanceDeflect / bullet.damage)){
                        bullet.trns(-bullet.vel.x, -bullet.vel.y);
                        if(Math.abs(unit.x - bullet.x) > Math.abs(unit.y - bullet.y)){
                            bullet.vel.x *= -1f;
                        }else{
                            bullet.vel.y *= -1f;
                        }
                        bullet.owner = unit;
                        bullet.team = unit.team;
                        bullet.time += 1f;
                        collision = false;
                    }
                    if(collision) bullet.absorb();
                    if(unit.shield <= bullet.damage){
                        unit.shield -= cooldown * regen;
                        Fx.shieldBreak.at(unit.x, unit.y, radius, unit.team.color, unit);
                        Sounds.spark.at(unit, Mathf.random(0.1f * lightningAmount, 0.15f * lightningAmount));
                        for(int i = 0; i < lightningAmount; i += 1){
                            Lightning.create(unit.team, lightningColor, lightningDamage, unit.x, unit.y, Mathf.random(360), lightningLength);
                        }
                    }
                    unit.shield -= collision ? bullet.damage : bullet.damage * 2f;
                    alpha = 1f;
                }
            });
        }else{
            radiusScale = 0f;
        }
    }

    protected float realRad(){
        return radius * radiusScale;
    }
}
