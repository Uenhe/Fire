package fire.world.meta;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import fire.world.blocks.defense.turrets.JackpotTurret;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootMulti;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.tilesize;

public class FStatValues{

    /** see {@link mindustry.world.meta.StatValues#ammo(ObjectMap, int, boolean)} */
    public static StatValue ammo(Seq<JackpotTurret.JackpotAmmo> map, int indent){

        return table -> {

            table.row();

            for(var t : map){
                BulletType type = t.type;

                table.table(Styles.grayPanel, bt -> {

                    bt.left().top().defaults().padRight(3f).left();
                    bt.table(title -> {
                        title.image(t.item.uiIcon).size(3 * 8).padRight(4).right().scaling(Scaling.fit).top();
                        title.add(t.item.localizedName).padRight(10).left().top();
                    });
                    bt.row();

                    if(type.damage > 0f && (type.collides || type.splashDamage <= 0f)){
                        if(type.continuousDamage() > 0f){
                            bt.add(Core.bundle.format("bullet.damage", type.continuousDamage()) + StatUnit.perSecond.localized());
                        }else{
                            bt.add(Core.bundle.format("bullet.damage", type.damage));
                        }
                    }

                    if(type.buildingDamageMultiplier != 1f){
                        int val = (short)(type.buildingDamageMultiplier * 100 - 100);
                        sep(bt, Core.bundle.format("bullet.buildingdamage", ammoStat(val)));
                    }

                    if(type.rangeChange != 0f){
                        sep(bt, Core.bundle.format("bullet.range", ammoStat(type.rangeChange / tilesize)));
                    }

                    if(type.splashDamage > 0f){
                        sep(bt, Core.bundle.format("bullet.splashdamage", (int)type.splashDamage, Strings.fixed(type.splashDamageRadius / tilesize, 1)));
                    }

                    if(!Mathf.equal(type.ammoMultiplier, 1f) && type.displayAmmoMultiplier){
                        sep(bt, Core.bundle.format("bullet.multiplier", (byte)type.ammoMultiplier));
                    }

                    if(!Mathf.equal(type.reloadMultiplier, 1f)){
                        short val = (short)(type.reloadMultiplier * 100 - 100);
                        sep(bt, Core.bundle.format("bullet.reload", ammoStat(val)));
                    }

                    if(type.knockback > 0f){
                        sep(bt, Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)));
                    }

                    if(type.healPercent > 0f){
                        sep(bt, Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)));
                    }

                    if(type.healAmount > 0f){
                        sep(bt, Core.bundle.format("bullet.healamount", Strings.autoFixed(type.healAmount, 2)));
                    }

                    if(type.pierce || type.pierceCap != -1){
                        sep(bt, type.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", type.pierceCap));
                    }

                    if(type.incendAmount > 0){
                        sep(bt, "@bullet.incendiary");
                    }

                    if(type.homingPower > 0.01f){
                        sep(bt, "@bullet.homing");
                    }

                    if(type.lightning > 0){
                        sep(bt, Core.bundle.format("bullet.lightning", type.lightning, type.lightningDamage < 0f ? type.damage : type.lightningDamage));
                    }

                    if(type.pierceArmor){
                        sep(bt, "@bullet.armorpierce");
                    }

                    if(type.suppressionRange > 0f){
                        sep(bt, Core.bundle.format("bullet.suppression", Strings.autoFixed(type.suppressionDuration / 60f, 2), Strings.fixed(type.suppressionRange / tilesize, 1)));
                    }

                    if(type.status != StatusEffects.none){
                        sep(bt, (type.status.minfo.mod == null ? type.status.emoji() : "") + "[stat]" + type.status.localizedName + (type.status.reactive ? "" : "[lightgray] ~ [stat]" + ((byte)(type.statusDuration / 60f)) + "[lightgray] " + StatUnit.seconds.localized()));
                    }

                    if(t.chance != 0f){
                        sep(bt, Core.bundle.format("bullet.chance", (byte)(t.chance * 100) + StatUnit.percent.localized()));
                    }

                    int a = 1, n = 1;
                    if(t.shoot instanceof ShootAlternate s){
                        a = s.shots;
                    }
                    if(t.shoot instanceof ShootMulti s){
                        a = s.dest[0].shots;
                        n = s.source.shots;
                    }
                    sep(bt, Core.bundle.format("bullet.pattern", a, n));

                    if(type.intervalBullet != null){
                        bt.row();

                        Table ic = new Table();
                        StatValues.ammo(ObjectMap.of(t, type.intervalBullet), indent + 1, false).display(ic);
                        Collapser coll = new Collapser(ic, true);
                        coll.setDuration(0.1f);

                        bt.table(it -> {
                            it.left().defaults().left();

                            it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(type.intervalBullets / type.bulletInterval * 60f, 2)));
                            it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8f).padLeft(16f).expandX();
                        });
                        bt.row();
                        bt.add(coll);
                    }

                    if(type.fragBullet != null){
                        bt.row();

                        Table fc = new Table();
                        StatValues.ammo(ObjectMap.of(t, type.fragBullet), indent + 1, false).display(fc);
                        Collapser coll = new Collapser(fc, true);
                        coll.setDuration(0.1f);

                        bt.table(ft -> {
                            ft.left().defaults().left();

                            ft.add(Core.bundle.format("bullet.frags", type.fragBullets));
                            ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8f).padLeft(16f).expandX();
                        });
                        bt.row();
                        bt.add(coll);
                    }
                }).padLeft(indent * 5f).padTop(5f).padBottom(5f).growX().margin(10f);
                table.row();
            }
        };
    }

    private static void sep(Table table, String text){
        table.row();
        table.add(text);
    }

    private static String ammoStat(float val){
        return (val > 0f ? "[stat]+" : "[negstat]") + Strings.autoFixed(val, 1);
    }
}
