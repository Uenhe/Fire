package fire.content;

import mindustry.gen.WeatherState;
import mindustry.type.Weather;
import mindustry.type.weather.RainWeather;

/** TODO: stormy coast with real rainstorm. */
public class FWeathers{

    public static Weather
        rainstorm;

    public static void load(){

        rainstorm = new RainWeather("rainstorm"){

            @Override
            public void update(WeatherState state){
                super.update(state);
            }
        };
    }
}
