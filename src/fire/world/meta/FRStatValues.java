package fire.world.meta;

import arc.Core;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Scaling;
import fire.FRUtils;
import fire.world.blocks.defense.turrets.JackpotTurret;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootMulti;
import mindustry.ui.Styles;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;

public class FRStatValues{

    /** @see StatValues#ammo(ObjectMap, boolean, boolean)  */
    public static StatValue ammoDetails(Seq<JackpotTurret.JackpotAmmo> ammo){
        return table -> {
            table.row();

            for(byte i = 0; i < ammo.size; i++){
                final byte j = i;
                var entry = ammo.get(j);

                table.table(Styles.grayPanel, bt -> {
                    bt.left().top().defaults().padRight(3.0f).left();
                    bt.table(title -> {
                        title.image(entry.item.uiIcon).size(24.0f).padRight(4.0f).right().scaling(Scaling.fit).top().with(e -> StatValues.withTooltip(e, entry.item, false));
                        title.add(entry.item.localizedName).padRight(10.0f).left().top();
                    });

                    bt.row().add(Core.bundle.format("bullet.level", FRUtils.toNumeral(j + 1)));

                    if(entry.chancePercentage != 0) bt.row().add(Core.bundle.format("bullet.chance", entry.chancePercentage));

                    int a = 1, n = 1;
                    if(entry.shoot instanceof ShootAlternate s){
                        a = s.shots;
                    }else if(entry.shoot instanceof ShootMulti s){
                        a = s.dest[0].shots;
                        n = s.source.shots;
                    }
                    bt.row().add(Core.bundle.format("bullet.pattern", a, n));
                }).padLeft(5.0f).padTop(5.0f).padBottom(5.0f).growX().margin(10.0f);
                if(j != ammo.size - 1) table.row();
            }
        };
    }
}
