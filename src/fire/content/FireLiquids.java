package fire.content;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class FireLiquids{
    public static Liquid
        liquidNitrogen;
    
    public static void load(){
        
        liquidNitrogen = new Liquid("liquid-nitrogen", Color.valueOf("f0ffff")){{
            heatCapacity = 2.5f;
            temperature = -2.1f;
            viscosity = 0.4f;
            boilPoint = -1.7f;
            effect = FireStatusEffects.frostbite;
            barColor = Color.valueOf("f0ffff");
            gasColor = Color.valueOf("c1e8f5");
            lightColor = Color.valueOf("0097f532");
        }};
    }
}
