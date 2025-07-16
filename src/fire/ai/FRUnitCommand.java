package fire.ai;

import arc.Core;
import arc.func.Func;
import arc.input.KeyBind;
import fire.ai.types.DashBuilderAI;
import fire.ai.types.DashRepairAI;
import mindustry.ai.UnitCommand;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;
import mindustry.input.Binding;

public class FRUnitCommand extends UnitCommand{

    public FRUnitCommand(String name, String icon, KeyBind keybind, Func<Unit, AIController> controller){
        super(name, icon, keybind, controller);
    }

    public static void loadAll(){
        repairCommand = new FRUnitCommand("repair", "modeSurvival", Binding.unitCommandRepair, u -> new DashRepairAI());
        rebuildCommand = new FRUnitCommand("rebuild", "hammer", Binding.unitCommandRebuild, u -> new DashBuilderAI(false));
        assistCommand = new FRUnitCommand("assist", "players", Binding.unitCommandAssist, u -> new DashBuilderAI(true));
    }

    @Override
    public String localized(){
        return Core.bundle.get("command." + name.replace("fire-", ""));
    }
}
