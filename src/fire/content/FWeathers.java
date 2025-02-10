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

public class FWeathers{

    public static Weather
        rainstorm;

    public static void load(){

        rainstorm = new RainWeather("rainstorm"){

            final LightningCloudBulletType type = new LightningCloudBulletType(12.0f, 12, 0.11f, 20, 20, Pal.lancerLaser);

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

            @Override
            public void drawUnder(WeatherState state){
                // do nothing or a NullPointer will be thrown
            }
            {
                attrs.set(Attribute.light, -0.35f);
                attrs.set(Attribute.water, 0.35f);
                status = StatusEffects.wet;
                sound = Sounds.rain;
                soundVol = 0.3f;
            }
        };
    }
}
