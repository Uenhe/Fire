package fire.content;

import mindustry.type.Liquid;

import static fire.FRVars.find;

public class FRLiquids{

    public static final Liquid
        liquidNitrogen;

    static{
         liquidNitrogen = new Liquid("liquid-nitrogen", find("f0ffff")){{
            heatCapacity = 2.5f;
            temperature = -2.1f;
            viscosity = 0.4f;
            boilPoint = -1.7f;
            effect = FRStatusEffects.frostbite;
            barColor = color;
            gasColor = find("c1e8f5");
            lightColor = find("0097f532");
        }};
    }

    public static void load(){}
}
