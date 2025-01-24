package fire.ai;

import fire.ai.types.BuilderDashAI;
import mindustry.ai.UnitCommand;

/** TODO these are buggy but too lazy to fix. */
public class FUnitCommand{

    public static final UnitCommand
        repairDashCommand = new UnitCommand("repair", "modeSurvival", u -> new fire.ai.types.RepairDashAI()),
        rebuildDashCommand = new UnitCommand("rebuild", "hammer", u -> new BuilderDashAI()),
        assistDashCommand = new UnitCommand("assist", "players", u -> {
            var ai = new BuilderDashAI();
            ai.onlyAssist = true;
            return ai;
        }),
        mineDashCommand = new UnitCommand("mine", "production", u -> new fire.ai.types.MinerDashAI());
}
