package fire.content;

import arc.math.Mathf;
import fire.entities.bullets.LightningCloudBulletType;
import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.gen.WeatherState;
import mindustry.graphics.Pal;
import mindustry.type.Weather;
import mindustry.type.weather.RainWeather;
import mindustry.world.meta.Attribute;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class FRWeathers{

    public static Weather
        rainstorm;

    public static void load(){

        rainstorm = new RainWeather("rainstorm"){

            final LightningCloudBulletType type = new LightningCloudBulletType(12.0f, 12, 11, 20, 20, Pal.lancerLaser);

            @Override
            public void update(WeatherState state){
                super.update(state);

                if(Mathf.chanceDelta(world.width() * world.height() * 0.000002f)){
                    type.create(
                        state, //????
                        Mathf.random(world.width() * tilesize),
                        Mathf.random(world.height() * tilesize)
                    );
                }
            }
            {
                attrs.set(Attribute.light, -0.35f);
                attrs.set(Attribute.water, 0.35f);
                status = StatusEffects.wet;
                sound = Sounds.rain;
                soundVol = 0.3f;

                padding = 20.0f;
                density = 1500.0f;
                stroke = 0.8f;
                sizeMin = 10.0f;
                sizeMax = 50.0f;
            }
        };
    }
}
