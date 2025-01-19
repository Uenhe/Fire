package fire.content;

import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import arc.util.pooling.Pools;
import fire.entities.LightningCloud;
import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.gen.WeatherState;
import mindustry.type.Weather;
import mindustry.type.weather.RainWeather;
import mindustry.world.meta.Attribute;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;


public class FWeathers{

    public static Weather
        rainstorm;

    public static void load(){

        rainstorm = new RainWeather("rainstorm"){

            @Override
            public void update(WeatherState state){
                super.update(state);

                if(Mathf.chanceDelta(world.width() * world.height() * 0.000002f))
                    Pools.obtain(LightningCloud.class, () -> new LightningCloud(
                        Mathf.random(world.width() * tilesize),
                        Mathf.random(world.height() * tilesize)
                ));

                for(var c : LightningCloud.clouds)
                    c.update();
            }

            @Override
            public void drawOver(WeatherState state){
                super.drawOver(state);

                for(var c : LightningCloud.clouds)
                    c.draw();
            }

            @Override
            public void drawUnder(WeatherState state){
                //do nothing or a NullPointer will be thrown
            }
        {
            attrs.set(Attribute.light, -0.35f);
            attrs.set(Attribute.water, 0.35f);
            status = StatusEffects.wet;
            sound = Sounds.rain;
            soundVol = 0.3f;
        }};

        for(int i = 0; i < 1000; i++){
            Time.runTask(60 * i, () -> {
                Log.info(LightningCloud.clouds.size);
            });
        }
    }
}
