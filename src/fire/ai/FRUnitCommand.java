package fire.ai;

import arc.Core;
import arc.func.Func;
import arc.input.KeyBind;
import fire.ai.types.BuilderDashAI;
import fire.ai.types.RepairDashAI;
import fire.entities.abilities.DashAbility;
import mindustry.ai.UnitCommand;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;
import mindustry.input.Binding;

public class FRUnitCommand extends UnitCommand{

    public static final UnitCommand
        repairDashCommand = new FRUnitCommand("repair0", "modeSurvival", Binding.unitCommandRepair, u -> new RepairDashAI(find(u.abilities))),
        rebuildDashCommand = new FRUnitCommand("rebuild0", "hammer", Binding.unitCommandRebuild, u -> new BuilderDashAI(find(u.abilities))),
        assistDashCommand = new FRUnitCommand("assist0", "players", Binding.unitCommandAssist, u -> new BuilderDashAI(find(u.abilities), 0));

    public FRUnitCommand(String name, String icon, KeyBind keybind, Func<Unit, AIController> controller){
        super(name, icon, keybind, controller);
    }

    @Override
    public String localized(){
        return Core.bundle.get("command." + name.substring(0, name.length() - 1));
    }

    private static DashAbility find(Ability[] abs){
        for(var ab : abs)
            if(ab instanceof DashAbility dab)
                return dab;

        return null;
    }
}
