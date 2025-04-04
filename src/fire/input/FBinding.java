package fire.input;

import arc.Core;
import arc.input.KeyCode;
import arc.util.Reflect;

import static arc.KeyBinds.KeyBind;
import static arc.KeyBinds.KeybindValue;
import static mindustry.Vars.headless;

public enum FRBinding implements KeyBind{

    unit_ability(KeyCode.g, "fire");

    private final KeybindValue defaultValue;
    private final String category;

    FRBinding(KeybindValue defaultValue, String cat){
        this.defaultValue = defaultValue;
        this.category = cat;
    }

    @Override
    public KeybindValue defaultValue(arc.input.InputDevice.DeviceType type){
        return defaultValue;
    }

    @Override
    public String category(){
        return category;
    }

    /** See <a href="https://github.com/RICCJ/MinerTools">MinerTools</a> also. */
    public static void load(){
        if(headless) return;
        
        KeyBind[] bindings = Reflect.get(Core.keybinds, "definitions");
        KeyBind[] newBindings = new KeyBind[bindings.length + values().length];
        System.arraycopy(bindings, 0, newBindings, 0, bindings.length);
        System.arraycopy(values(), 0, newBindings, bindings.length, values().length);
        Core.keybinds.setDefaults(newBindings);
        Reflect.invoke(Core.keybinds, "load");
        Reflect.invoke(mindustry.Vars.ui.controls, "setup");
    }
}
