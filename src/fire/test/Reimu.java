package fire.test;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import fire.annotation.Modified;
import fire.content.FRBlocks;
import mindustry.ai.types.LogicAI;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;
import mindustry.world.blocks.defense.turrets.ItemTurret;

import static mindustry.Vars.player;

public class Reimu extends UnitType{

    final TextureRegion[] regions = new TextureRegion[4];
    final float frameTime = 5f;

    public Reimu(){
        super("_0_");
        constructor = UnitEntity::create;
        flying = true;
        outlines = false;
        health = 1000;
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

    //area size = 216 * 264
    //timer format: timerM_N, where M = phase, N = index
    static class ReimuAbility extends Ability{

        float centerX, centerY;
        float timerMove;
        byte phase;

        //phase1
        float timer1_1;
        final BulletType phase1Bullet1 = ((ItemTurret)Blocks.duo).ammoTypes.get(Items.graphite).copy();

        float timer1_2;
        final BulletType phase1Bullet2;

        //phase2
        float timer2_1 = 120, timer2_2;
        final float timer2_2CD = 5;
        byte counter2_1;
        boolean clockwise = true;
        final BulletType phase2Bullet1;

        {
            phase1Bullet1.keepVelocity = false;
            phase1Bullet1.speed *= 0.7f;
            phase1Bullet1.lifetime *= 1.8f;
            ((BasicBulletType)phase1Bullet1).shrinkY = 0;

            phase1Bullet2 = phase1Bullet1.copy();
            phase1Bullet2.speed *= 1.2f;

            phase2Bullet1 = phase1Bullet1.copy();
            phase2Bullet1.speed = 6f;
            phase2Bullet1.lifetime = 240f;
        }

        @Override
        public void update(Unit unit){
            unit.rotation = 90;

            if(centerX == 0){
                var center = unit.team.data().buildingTypes.get(FRBlocks.envStgCenter);
                if(center != null && center.size == 1){
                    centerX = center.get(0).x;
                    centerY = center.get(0).y;
                }
            }

            if(unit.health < 50){
                unit.heal();
                phase++;
            }

            if(centerX == 0 || unit.controller() instanceof LogicAI) return;

            //random move
            float dt = Time.delta;
            if(timerMove != -1 && (phase != 1 || timer2_1 > timer2_2CD * 12 + 90 && timer2_1 < 240) &&(timerMove += dt) >= 120){
                timerMove = -1;
                float moveX = player.dead() ? centerX : player.x;
                float radian = Mathf.atan2(Mathf.sign(moveX - unit.x), Mathf.sign(unit.y < y(64))) + Mathf.range(Mathf.PI / 6);
                unit.vel.set(Mathf.cos(radian), Mathf.sin(radian)).scl(4);
            }
            if(timerMove == -1 && !unit.moving()){
                timerMove = 0;
            }

            //attack pattern
            float angle = unit.angleTo(player);
            switch(phase){
                case 0 -> {
                    if((timer1_1 += dt) >= 50){
                        timer1_1 = 0;
                        createNWay(3, 12, phase1Bullet1, angle, unit);
                    }

                    if((timer1_2 += dt) >= 115){
                        timer1_2 = 0;
                        createNWay(4, 15, phase1Bullet2, angle, unit);
                    }
                }

                case 1 -> {
                    if((timer2_1 += dt) >= 240){
                        if((timer2_2 += dt) >= timer2_2CD){
                            timer2_2 -= timer2_2CD;
                            int sign = Mathf.sign(clockwise);
                            var bullet = phase2Bullet1.create(unit, unit.x, unit.y, counter2_1 / 12f * 360f * sign);
                            bullet.mover = b -> {
                                final float r = 32f, spd = phase2Bullet1.speed * Mathf.clamp(Mathf.sqrt(b.time / 45f + 0.01f));
                                Tmp.v1.set(unit).sub(b).rotate(90f * Mathf.lerp(0f, 1f, 1f - Mathf.clamp((Tmp.v1.len() - r) / r)) * sign);
                                b.vel.add(Tmp.v1.limit(spd * 0.2f * Time.delta)).limit(spd);
                                if(b.time > 90){
                                    Mathf.randomSeed(b.id);
                                    float ang = b.angleTo(player) + Mathf.range(15f);
                                    if(b.time > b.lifetime * 0.9f || Angles.near(b.vel.angle(), ang, 9f * Time.delta)){
                                        b.vel.setAngle(ang);
                                        b.mover = null;
                                    }
                                }
                            };
                            if(++counter2_1 >= 12){
                                timer2_1 = counter2_1 = 0;
                                clockwise = !clockwise;
                            }
                        }
                    }
                }
            }
        }

        //stg coords -> real coords
        float x(float x){
            return x + centerX;
        }
        float y(float y){
            return y + centerY;
        }

        void createNWay(int n, float step, BulletType type, float base, Unit unit){
            for(int i = 0; i < n; i++)
                type.create(unit, unit.team, unit.x, unit.y, base + (i - (n - 1) * 0.5f) * step);
        }
    }
}
