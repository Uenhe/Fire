package fire.content;

import mindustry.gen.WeatherState;
import mindustry.type.Weather;
import mindustry.type.weather.RainWeather;

public class FWeathers{

    public static Weather
        rainstorm;

    public static void load(){

        rainstorm = new RainWeather("rainstorm"){

            /** TODO... */
            @Override
            public void update(WeatherState state){
                super.update(state);

            }
        };
    }
}
