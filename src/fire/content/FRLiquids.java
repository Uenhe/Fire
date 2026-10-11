package fire.content;

import mindustry.type.Liquid;

import static fire.FRVars.findColor;

public class FRLiquids{

    public static final Liquid
        liquidNitrogen;

    static{
         liquidNitrogen = new Liquid("liquid-nitrogen", findColor("f0ffff")){{
            heatCapacity = 2.5f;
            temperature = -2.1f;
            viscosity = 0.4f;
            boilPoint = -1.7f;
            effect = FRStatusEffects.frostbite;
            barColor = color;
            gasColor = findColor("c1e8f5");
            lightColor = findColor("0097f532");
        }};
    }

    public static void load(){}
}
