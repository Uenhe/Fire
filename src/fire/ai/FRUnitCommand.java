package fire.ai;

import fire.ai.types.BuilderDashAI;
import fire.ai.types.RepairDashAI;
import fire.entities.abilities.DashAbility;
import mindustry.ai.UnitCommand;
import mindustry.entities.abilities.Ability;
import mindustry.input.Binding;

public class FRUnitCommand{

    public static final UnitCommand
        repairDashCommand = new UnitCommand("repair", "modeSurvival", Binding.unit_command_repair, u -> new RepairDashAI(find(u.abilities))),
        rebuildDashCommand = new UnitCommand("rebuild", "hammer", Binding.unit_command_rebuild, u -> new BuilderDashAI(find(u.abilities))),
        assistDashCommand = new UnitCommand("assist", "players", Binding.unit_command_assist, u -> {
            var ai = new BuilderDashAI(find(u.abilities));
            ai.onlyAssist = true;
            return ai;
        });
        //commented: there's no unit that can both dash and mine, currently
        //mineDashCommand = new UnitCommand("mine", "production", u -> new MinerDashAI(find(u.abilities)));

    private static DashAbility find(Ability[] abs){
        for(var ab : abs)
            if(ab instanceof DashAbility)
                return (DashAbility)ab;

        return null;
    }
}
