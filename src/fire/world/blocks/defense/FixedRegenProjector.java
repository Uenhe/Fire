//TODO remove this if anuke updates new ver above v145.1.

package fire.world.blocks.defense;

import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.graphics.Drawf;

import static mindustry.Vars.*;

public class FixedRegenProjector extends mindustry.world.blocks.defense.RegenProjector{
    public FixedRegenProjector(String name){
        super(name);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        drawPotentialLinks(x, y);
        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;
        drawOverlay(x, y, rotation);
        Drawf.dashSquare(baseColor, x, y, range * tilesize);
        indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, range * tilesize), b -> true, t -> {
            Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));
        });
    }
}
