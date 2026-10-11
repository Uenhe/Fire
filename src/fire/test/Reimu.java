package fire.test;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import fire.annotation.Modified;
import fire.content.FRBlocks;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;

import static mindustry.Vars.player;

public class Reimu extends UnitType{

    final TextureRegion[] regions = new TextureRegion[4];
    final float frameTime = 7f;

    public Reimu(){
        super("_0_");
        constructor = UnitEntity::create;
        flying = true;
        outlines = false;
        health = 100000;
        hitSize = 12;
        speed = 3.8f;
        drag = 0.1f;
        accel = 0.15f;
        rotateSpeed = 7;
        lowAltitude = true;
        buildRange += 40.0f;
        buildSpeed = 3.0f;
        abilities.add(new ReimuAbility());
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < regions.length;){
            var reg = Core.atlas.find(name + i);
            reg.scale = 2;
            regions[i++] = reg;
        }
    }

    @Override
    @Modified
    public void drawBody(Unit unit){
        applyColor(unit);
        Draw.rect(regions[(int)(Time.time / frameTime % regions.length)], unit.x, unit.y, 0);
        Draw.reset();
    }

    @Override
    public void drawEngines(Unit unit){}

    @Override
    public void drawCell(Unit unit){}

    //size = 216 * 264
    static class ReimuAbility extends Ability{

        float centerX, centerY;
        float timerMove;

        @Override
        public void init(UnitType type){
            super.init(type);
        }

        @Override
        public void update(Unit unit){
            unit.rotation = 0;

            if(centerX == 0){
                var center = unit.team.data().buildingTypes.get(FRBlocks.envStgCenter);
                if(center != null && center.size == 1){
                    centerX = center.get(0).x;
                    centerY = center.get(0).y;
                }
            }

            if(!unit.isCommandable()) return;

            if(timerMove != -1 && (timerMove += Time.delta) >= 120){
                timerMove = -1;
                float moveX = player.dead() ? centerX : player.x;
                float radian = Mathf.atan2(Mathf.sign(moveX - unit.x), Mathf.sign(unit.y < y(64))) + Mathf.range(Mathf.PI / 6);
                unit.vel.set(Mathf.cos(radian), Mathf.sin(radian)).scl(4);
            }
            if(timerMove == -1 && !unit.moving()){
                timerMove = 0;
            }
        }

        //stg coords -> real coords
        float x(float x){
            return x + centerX;
        }
        float y(float y){
            return y + centerY;
        }
    }
}
