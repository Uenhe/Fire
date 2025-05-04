package fire;

import arc.Core;
import arc.Events;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Scaling;
import fire.content.*;
import fire.input.FRBinding;
import fire.ui.dialogs.DelayClosableDialog;
import fire.ui.dialogs.InfoDialog;
import fire.world.meta.FRAttribute;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.mod.Mods;
import mindustry.type.SectorPreset;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.meta.BuildVisibility;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static fire.FRVars.*;
import static mindustry.Vars.*;

@SuppressWarnings("unused")
public class FireMod extends mindustry.mod.Mod{

    private static Mods.LoadedMod FIRE;
    private static boolean multiplied;
    private static byte counter;
    private static BaseDialog mainDialog;

    public FireMod(){
        if(!headless) FRBinding.init();
    }

    @Override
    public void loadContent(){
        FIRE = mods.getMod(getClass());
        FIRE.meta.displayName = Core.bundle.get("fire.name");

        setRandTitle();

        FRAttribute.load();
        FRStatusEffects.load();
        FRItems.load();
        FRLiquids.load();
        FRUnitTypes.load();
        FRBlocks.load();
        FRPlanets.load();
        FRSectorPresets.load();
        FRPlanets.loadTree();
        FRWeathers.load();
    }

    @Override
    public void init(){
        if(headless) return;

        loadSetting();
        loadDatabase();
        FROverride.load();
        Events.on(EventType.ClientLoadEvent.class, e -> {
            showLog(false);
            showUpdate();
            showNoMultipleMods();
        });
    }

    private static void loadSetting(){
        ui.settings.addCategory("@setting.fire", "fire-setting", t -> {

            t.checkPref("minesand", false, b -> mineSand = b);
            t.checkPref("displayrange", true, b -> displayRange = b);
            t.checkPref("specialcontent", true, b -> specialContent = b);
            t.checkPref("showlog", true, b -> showLog = b);
            t.checkPref("nomultimods", true, b -> noMultiMods = b);

            t.rebuild(); //adapts to MindustryX
            t.row().button("@setting.fire-showlog", () -> {
                showLog(true);
                if(++counter == 5){
                    doSomethingPlayable();
                    ui.announce("Debug successfully.");
                }
            }).size(240.0f, 80.0f);
        });
        getSettings();
    }

    private static void loadDatabase(){
        ui.research.titleTable.row().button(b -> b.add("@fire.showdatabase"), InfoDialog.dialog::show).visible(() -> ui.research.root.node == FRPlanets.lysetta.techTree);
    }

    private static void showLog(boolean forces){
        if(!FRVars.showLog && !forces) return;

        var historyDialog = new BaseDialog("@fire.historytitle");
        setupDialog(historyDialog);
        historyDialog.cont.pane(t ->
            t.add("@fire.history").left().maxWidth(width()).pad(4.0f)
        );

        var main = mainDialog = new BaseDialog(Core.bundle.format("fire.maintitle", FIRE.meta.version));
        setupDialog(main);
        main.buttons.button(Core.bundle.format("fire.historytitle", ""), historyDialog::show).size(210.0f, 64.0f);
        main.cont.pane(t -> {

            t.image(FRUtils.find("logo")).size(438.0f, 136.0f).pad(3.0f);
            t.row();

            t.add(Core.bundle.format("fire.content1", FIRE.meta.version)).left().maxWidth(width()).pad(4.0f);
            t.row();

            addContent(t,
                FRSectorPresets.branchedRivers, FRSectorPresets.rubbleRidge, FRSectorPresets.taintedEstuary,
                FRBlocks.magneticDomain, FRBlocks.aerolite,
                FRBlocks.stackedCultivator, FRBlocks.constraintExtractor,
                FRBlocks.magneticRingPump, FRBlocks.hardenedLiquidTank, FRBlocks.hydroelectricGenerator,
                FRBlocks.cryofluidMixerLarge, FRBlocks.magnetismConcentratedRollingMill, FRBlocks.magneticRingSynthesizer,
                FRBlocks.primaryInterplanetaryAccelerator,
                FRUnitTypes.hatchet, FRUnitTypes.castle, FRUnitTypes.mechanicalTide
            );
            t.row();

            t.add("@fire.content2").left().maxWidth(width()).pad(4.0f);
            t.row();

            if("zh_CN".equals(Core.settings.getString("locale"))){
                t.button(("@fire.linkfy"), () -> {
                    if(!Core.app.openURI(linkFy)){
                        ui.showErrorMessage("@linkfail");
                        Core.app.setClipboardText(linkFy);
                    }
                }).size(256.0f, 64.0f);
                t.row();

                t.button(("@fire.linkue"), () -> {
                    if(!Core.app.openURI(linkUe)){
                        ui.showErrorMessage("@linkfail");
                        Core.app.setClipboardText(linkUe);
                    }
                }).size(256.0f, 64.0f);
                t.row();

            }
            t.button(("@fire.linkgithub"), () -> {
                if(!Core.app.openURI(linkGit)){
                    ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(linkGit);
                }
            }).size(256.0f, 64.0f);
            t.row();

        }).maxWidth(width());

        main.show();
    }

    private static void showNoMultipleMods(){
        if(!noMultiMods || !mods.orderedMods().contains(mod -> !"fire".equals(mod.meta.name) && !mod.meta.hidden)) return;

        multiplied = true;
        doSomethingUnplayable();

        var dialog = new DelayClosableDialog("Warning", 300.0f);
        dialog.cont.add("@fire.nomultimods");
        dialog.show();
    }

    private static void showUpdate(){
        try{
            if(FIRE.meta.version.equals(Core.settings.getString("mod-fire-version"))) return;
            Core.settings.put("mod-fire-version", FIRE.meta.version);
        }catch(Throwable ignored){
            Core.settings.put("mod-fire-version", "");
        }

        if(mainDialog == null || !mainDialog.isShown()) showLog(true);
        var dialog = new DelayClosableDialog("Update Notice", 300.0f);
        dialog.cont.pane(t -> {
            t.table(tt -> {
                try{
                    tt.image(new TextureRegion(new Texture(FIRE.root.child("preview.png")))).size(Math.min(Core.graphics.getWidth(), Core.graphics.getHeight()) * 0.33f).padRight(120.0f);
                }catch(Throwable e){
                    Log.err("Failed to load preview for mod Fire", e);
                }
            });
            t.add(Core.bundle.format("fire.content2", FIRE.meta.version)).maxWidth(width()).padRight(200.0f);
        });
        dialog.show();
    }

    /** See ExtraUtilities also.<p></p>
     * Clashes with MindustryX. */
    private static void setRandTitle(){
        if(!Core.app.isDesktop()) return;

        String[] titles = Core.bundle.get("fire.titles").split("\\|");
        int index = Mathf.random(titles.length - 1);
        String title = titles[index];

        // "Today is the %th Day of God's Creation of Planet Lysetta" picked
        if(index == 0)
            title = String.format(title, ChronoUnit.DAYS.between(LocalDate.of(2022, 11, 19), LocalDate.now()));

        Core.graphics.setTitle("Mindustry: " + title);
    }

    private static void setupDialog(BaseDialog dialog){
        dialog.closeOnBack();
        dialog.buttons.button("@close", dialog::hide).size(210.0f, 64.0f);
    }

    private static void addContent(Table table, UnlockableContent... content){
        for(var c : content)
            table.table(Styles.grayPanel, t -> {

                t.left().button(new TextureRegionDrawable(c.uiIcon), Styles.emptyi, 40.0f, () -> ui.content.show(c)).size(40.0f).pad(10.0f).scaling(Scaling.fit).left();
                t.table(info -> {

                    info.left().add("[accent]" + c.localizedName).left().row();
                    try{
                        info.left().add(c.description.substring(0, c.description.indexOf(Core.bundle.get("fire.strend"))) + (c instanceof SectorPreset ? "..." : ""));
                    }catch(Throwable e){
                        info.left().add(c.description);
                    }finally{
                        info.left();
                    }
                });

            }).growX().pad(5.0f).row();
    }

    private static float width(){
        return Core.graphics.getWidth() * 0.8f;
    }

    /** ??? */
    private static void doSomethingUnplayable(){
        var units = content.units();
        for(var unit : units)
            unit.health -= 10000.0f;
    }

    /** !!! */
    private static void doSomethingPlayable(){
        var blocks = content.blocks();
        for(var block : blocks)
            if(block.buildVisibility == BuildVisibility.debugOnly)
                block.buildVisibility = BuildVisibility.shown;

        if(multiplied){
            multiplied = false;

            var units = content.units();
            for(var unit : units)
                unit.health += 10000.0f;
        }
    }
}
