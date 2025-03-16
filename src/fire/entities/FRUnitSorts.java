package fire.entities;

import arc.math.Mathf;

public class FRUnitSorts{

    public static final mindustry.entities.Units.Sortf
        strongerPlus = (u, x, y) -> Mathf.sqrt(u.maxHealth) + Mathf.dst2(u.x, u.y, x, y) / 6400.0f;
}
