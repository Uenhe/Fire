package fire.input;

import arc.Core;
import arc.input.KeyCode;
import arc.util.Reflect;

import static arc.KeyBinds.*;
import static arc.input.InputDevice.DeviceType;
import static mindustry.Vars.ui;

public enum FireBinding implements KeyBind{
    unit_ability(KeyCode.g, "fire");

    private final KeybindValue defaultValue;
    private final String category;

    FireBinding(KeybindValue defaultValue, String cat){
        this.defaultValue = defaultValue;
        this.category = cat;
    }

    @Override
    public KeybindValue defaultValue(DeviceType type){
        return defaultValue;
    }

    @Override
    public String category(){
        return category;
    }

    //special thanks to MinerTools mod (https://github.com/RICCJ/MinerTools)
    public static void load(){
        KeyBind[] bindings = Reflect.get(Core.keybinds, "definitions");
        KeyBind[] modBindings = values();
        KeyBind[] newBindings = new KeyBind[bindings.length + modBindings.length];
        System.arraycopy(bindings, 0, newBindings, 0, bindings.length);
        System.arraycopy(modBindings, 0, newBindings, bindings.length, modBindings.length);
        Core.keybinds.setDefaults(newBindings);
        Reflect.invoke(Core.keybinds, "load");
        Reflect.invoke(ui.controls, "setup");
    }
}
