package fire.content;

import arc.audio.Sound;
import fire.FireMod;

@SuppressWarnings("SameParameterValue")
public class FRSounds{
    public static final Sound graze;

    static{
        graze = loadSound("graze.wav");
    }

    public static void load(){}

    static Sound loadSound(String name){
        return new Sound(FireMod.FIRE.root.child("sounds").child(name));
    }
}
