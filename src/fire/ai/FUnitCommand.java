package fire.ai;

import fire.ai.types.BuilderDashAI;
import mindustry.ai.UnitCommand;

public class FUnitCommand{

    public static final UnitCommand
        repairDashCommand = new UnitCommand("repair", "modeSurvival", u -> new fire.ai.types.RepairDashAI()),
        rebuildDashCommand = new UnitCommand("rebuild", "hammer", u -> new BuilderDashAI()),
        assistDashCommand = new UnitCommand("assist", "players", u -> {
            final var ai = new BuilderDashAI();
            ai.onlyAssist = true;
            return ai;
        }),
        mineDashCommand = new UnitCommand("mine", "production", u -> new fire.ai.types.MinerDashAI());
}
