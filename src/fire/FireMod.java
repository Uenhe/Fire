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
import arc.util.Strings;
import arc.util.Time;
import fire.content.*;
import fire.input.FRBinding;
import fire.ui.dialogs.InfoDialog;
import fire.world.DEBUG;
import fire.world.meta.FRAttribute;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.mod.Mods;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
public class FireMod extends mindustry.mod.Mod{

    private static final int version = 1401;
    private static final String
        linkFy = "https://space.bilibili.com/516898377",
        linkUe = "https://space.bilibili.com/327502129",
        linkGit = "https://github.com/Uenhe/Fire";

    private static Mods.LoadedMod FIRE;
    private static boolean multiplied;
    private static byte counter;

    @Override
    public void loadContent(){
        FIRE = mods.locateMod("fire");
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
        FROverride.load();
        FRBinding.load();
        loadSetting();
        loadDatabase();

        Events.on(EventType.ClientLoadEvent.class, e -> {
            showLog();
            showNoMultipleMods();
            showUpdate();
        });
    }

    private static void loadSetting(){
        ui.settings.addCategory(Core.bundle.get("setting.fire"), "fire-setting", t -> {

            t.checkPref("minesand", false, a ->
                Blocks.sand.playerUnmineable =
                Blocks.darksand.playerUnmineable =
                Blocks.sandWater.playerUnmineable =
                Blocks.darksandWater.playerUnmineable =
                Blocks.darksandTaintedWater.playerUnmineable = !a
            );
            t.checkPref("displayrange", true);
            t.checkPref("showlogs", true);
            t.checkPref("nomultimods", true);

            t.rebuild(); //to adapt MindustryX
            t.row().button("@setting.fire-showlog", () -> {
                showLog();
                if(++counter >= 5){
                    doSomethingPlayable();
                    ui.announce("Debug successfully.");
                }
            }).size(240.0f, 80.0f);
        });
    }

    private static void loadDatabase(){
        ui.research.titleTable.row().button(b -> b.add("@fire.showdatabase"), InfoDialog.dialog::show).visible(() -> ui.research.root.node == FRPlanets.lysetta.techTree);
    }

    private static void showLog(){
        if(!FRVars.showLogs) return;

        var historyDialog = new BaseDialog("@fire.historytitle");
        setupDialog(historyDialog);
        historyDialog.cont.pane(t ->
            t.add("@fire.history").left().maxWidth(1280.0f).pad(4.0f)
        );

        var mainDialog = new BaseDialog(Core.bundle.format("fire.maintitle", FIRE.meta.version));
        setupDialog(mainDialog);
        mainDialog.buttons.button(Core.bundle.format("fire.historytitle", ""), historyDialog::show).size(210.0f, 64.0f);
        mainDialog.cont.pane(t -> {

            t.image(Core.atlas.find("fire-logo")).height(107.0f).width(359.0f).pad(3.0f);
            t.row();

            t.add(Core.bundle.format("fire.content1", FIRE.meta.version)).left().maxWidth(1024.0f).pad(4.0f);
            t.row();

            addContent(t,
                FRBlocks.magneticDomain, FRBlocks.aerolite,
                FRBlocks.magneticRingPump, FRBlocks.hardenedLiquidTank, FRBlocks.hydroelectricGenerator,
                FRBlocks.constraintExtractor,
                FRBlocks.cryofluidMixerLarge, FRBlocks.magnetismConcentratedRollingMill, FRBlocks.magneticRingSynthesizer,
                FRBlocks.primaryInterplanetaryAccelerator,
                FRUnitTypes.hatchet, FRUnitTypes.castle, FRUnitTypes.mechanicalTide
            );
            t.row();

            t.add("@fire.content2").left().maxWidth(1024.0f).pad(4.0f);
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

            }else{
                t.button(("@fire.linkgithub"), () -> {
                    if(!Core.app.openURI(linkGit)){
                        ui.showErrorMessage("@linkfail");
                        Core.app.setClipboardText(linkGit);
                    }
                }).size(256.0f, 64.0f);
                t.row();
            }
        }).maxWidth(1024.0f);

        mainDialog.show();
    }

    private static void showNoMultipleMods(){
        if(mods.orderedMods().contains(mod -> !"fire".equals(mod.meta.name) && !mod.meta.hidden) && FRVars.noMultiMods){

            // see https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod also
            new BaseDialog("Warning"){
                float time = 300.0f;
                boolean closable;
                {
                    update(() -> closable = (time -= Time.delta) <= 0.0f);
                    cont.add("@fire.nomultimods");
                    buttons.button("", this::hide).update(b -> {
                        b.setDisabled(!closable);
                        b.setText(closable ? "@close" : String.format("%s(%ss)", Core.bundle.get("close"), Strings.fixed(time / 60.0f, 1)));
                    }).size(210.0f, 64.0f);

                    multiplied = true;
                    doSomethingUnplayable();
                    show();
                }
            };
        }
    }

    private static void showUpdate(){
        if(Core.settings.getInt("mod-fire-version") == version) return;
        Core.settings.put("mod-fire-version", version);

        new BaseDialog("Update"){
            float time = 300.0f;
            boolean closable;
            {
                update(() -> closable = (time -= Time.delta) <= 0.0f);
                cont.pane(t -> {
                    t.table(tt -> {
                        try{
                            tt.image(new TextureRegion(new Texture(FIRE.root.child("preview.png")))).height(800.0f).width(800.0f).padRight(120.0f);
                        }catch(Throwable e){
                            Log.err("Failed to load preview for mod Fire", e);
                        }
                    });
                    t.add(Core.bundle.format("fire.content2", version)).maxWidth(1024.0f).padRight(200.0f);
                });
                buttons.button("", this::hide).update(b -> {
                    b.setDisabled(!closable);
                    b.setText(closable ? "@close" : String.format("%s(%ss)", Core.bundle.get("close"), Strings.fixed(time / 60.0f, 1)));
                }).size(210.0f, 64.0f);

                show();
            }
        };
    }

    /** See <a href="https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod">Extra Utilities</a> also. */
    private static void setRandTitle(){
        if(!Core.app.isDesktop()) return;

        var titles = Core.bundle.get("fire.titles").split("\\|");
        var index = Mathf.random(titles.length - 1);
        var title = titles[index];

        // "Today is the %th Day of God's Creation of the Lysetta" picked
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

                t.left().button(new TextureRegionDrawable(c.uiIcon), Styles.emptyi, 40.0f, () -> ui.content.show(c)).size(40.0f).pad(10.0f).scaling(Scaling.fit);
                t.left().table(info -> {

                    info.left().add("[accent]" + c.localizedName).left();
                    info.row();

                    try{
                        info.left().add(c.description.substring(0, c.description.indexOf(Core.bundle.get("fire.strend")))).left();
                    }catch(Throwable ignored){
                        info.left().add(c.description).left();
                    }
                });

            }).growX().pad(5.0f).row();
    }

    /** ??? */
    private static void doSomethingUnplayable(){
        for(var u : content.units())
            u.health -= 10000.0f;
    }

    /** !!! */
    private static void doSomethingPlayable(){
        for(var e : DEBUG.DEBUGS)
            if(e instanceof Block)
                ((Block)e).buildVisibility = BuildVisibility.shown;

        if(multiplied){
            multiplied = false;
            for(var u : content.units())
                u.health += 10000.0f;
        }
    }
}
