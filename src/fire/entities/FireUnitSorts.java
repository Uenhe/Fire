package fire.entities;

import arc.math.Mathf;

public class FireUnitSorts{

    public static final mindustry.entities.Units.Sortf
        a = (u, x, y) -> Mathf.sqrt(u.maxHealth) + Mathf.dst2(u.x, u.y, x, y) / 6400f;
}
