package fire.ai;

import arc.func.Func;
import fire.ai.types.DashBuilderAI;
import fire.ai.types.RepairDashAI;
import mindustry.ai.UnitCommand;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;
import mindustry.input.Binding;

public class FRUnitCommand extends UnitCommand{

    public FRUnitCommand(String name, String icon, Func<Unit, AIController> controller){
        super(name, icon, controller);
    }

    public static void loadAll(){
        repairCommand = new UnitCommand("repair", "modeSurvival", Binding.unitCommandRepair, u -> new RepairDashAI());
        rebuildCommand = new UnitCommand("rebuild", "hammer", Binding.unitCommandRebuild, u -> new DashBuilderAI());
        assistCommand = new UnitCommand("assist", "players", Binding.unitCommandAssist, u -> new DashBuilderAI(0));
    }
}
