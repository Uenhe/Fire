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
            Groups.bullet.intersect(unit.x - realRad(), unit.y - realRad(), realRad() * 2, realRad() * 2, b -> {
                if(b.team != unit.team && b.type.absorbable && Intersector.isInRegularPolygon(sides, unit.x, unit.y, realRad(), rotation, b.x, b.y) && unit.shield > 0){
                    boolean collision = true;
                    Fx.absorb.at(b);
                    if(b.vel.len() > 0.1f && b.type.reflectable && Mathf.chance(chanceDeflect / b.damage)){
                        b.trns(-b.vel.x, -b.vel.y);
                        float penX = Math.abs(unit.x - b.x);
                        float penY = Math.abs(unit.y - b.y);
                        if(penX > penY){
                            b.vel.x *= -1f;
                        }else{
                            b.vel.y *= -1f;
                        }
                        b.owner = unit;
                        b.team = unit.team;
                        b.time += 1f;
                        collision = false;
                    }
                    if(collision) b.absorb();
                    if(unit.shield <= b.damage){
                        unit.shield -= cooldown * regen;
                        Fx.shieldBreak.at(unit.x, unit.y, radius, unit.team.color, unit);
                        Sounds.spark.at(unit, Mathf.random(0.1f * lightningAmount, 0.15f * lightningAmount));
                        for(int i = 0; i < lightningAmount; i += 1){
                            Lightning.create(unit.team, lightningColor, lightningDamage, unit.x, unit.y, Mathf.random(360), lightningLength);
                        }
                    }
                    float dmg = collision ? b.damage : b.damage * 2f;
                    unit.shield -= dmg;
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
