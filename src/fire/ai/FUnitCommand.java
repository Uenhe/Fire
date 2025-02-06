package fire.ai;

import fire.ai.types.BuilderDashAI;
import fire.ai.types.RepairDashAI;
import fire.entities.abilities.DashAbility;
import mindustry.ai.UnitCommand;
import mindustry.entities.abilities.Ability;

/** TODO adapt these to v147 if released. */
public class FUnitCommand{

    public static final UnitCommand
        repairDashCommand = new UnitCommand("repair", "modeSurvival", u -> new RepairDashAI(find(u.abilities))),
        rebuildDashCommand = new UnitCommand("rebuild", "hammer", u -> new BuilderDashAI(find(u.abilities))),
        assistDashCommand = new UnitCommand("assist", "players", u -> {
            var ai = new BuilderDashAI(find(u.abilities));
            ai.onlyAssist = true;
            return ai;
        });
        //commented: there's no unit that can both dash and mine currently
        //mineDashCommand = new UnitCommand("mine", "production", u -> new MinerDashAI(find(u.abilities)));

    private static DashAbility find(Ability[] abs){
        for(var ab : abs)
            if(ab instanceof DashAbility)
                return (DashAbility)ab;

        return null;
    }
}
