package fire;

import arc.Events;
import arc.math.Mathf;
import fire.input.FireBinding;
import mindustry.game.EventType;
import mindustry.mod.Mod;

import static fire.FireLib.desktop;

public class FireMod extends Mod{
    public FireMod(){
        var a = Mathf.pow(2, 1.5f);
        Events.on(EventType.ClientLoadEvent.class, e -> {
            if(desktop()) FireBinding.load();
        });
    }
    @Override
    public void loadContent(){

    }
}
