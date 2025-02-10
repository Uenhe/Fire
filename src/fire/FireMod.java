package fire;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import fire.content.FBlocks;
import fire.content.FItems;
import fire.content.FLiquids;
import fire.content.FOverride;
import fire.content.FPlanets;
import fire.content.FSectorPresets;
import fire.content.FStatusEffects;
import fire.content.FUnitTypes;
import fire.content.FWeathers;
import fire.input.FBinding;
import fire.ui.dialogs.InfoDialog;
import fire.world.DEBUG;
import fire.world.meta.FAttribute;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.mod.Mods;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static mindustry.Vars.content;
import static mindustry.Vars.mods;
import static mindustry.Vars.ui;

@SuppressWarnings("unused")
public class FireMod extends mindustry.mod.Mod{

    private static final String
        linkRaindance = "https://space.bilibili.com/516898377",
        linkUeneh = "https://space.bilibili.com/327502129",
        linkGitHub = "https://github.com/Uenhe/Fire";

    private static Mods.LoadedMod FIRE;
    private static String displayName;
    private static boolean launched, multiplied;
    private static byte counter;

    @Override
    public void loadContent(){
        FIRE = mods.locateMod("fire");
        displayName = Core.bundle.get("modname");
        FIRE.meta.displayName = displayName;

        setRandTitle();

        FAttribute.load();
        FStatusEffects.load();
        FItems.load();
        FLiquids.load();
        FUnitTypes.load();
        FBlocks.load();
        FPlanets.load();
        FSectorPresets.load();
        FPlanets.loadTree();
        FWeathers.load();
    }

    @Override
    public void init(){
        FOverride.load();
        FBinding.load();
        loadSettings();
        loadDatabase();

        Events.on(EventType.ClientLoadEvent.class, e -> {
            showDialog();
            showNoMultipleMods();
            launched = true;
        });
    }

    static void loadSettings(){
        ui.settings.addCategory(Core.bundle.get("setting.fire"), "fire-setting", t -> {

            t.checkPref("allowSandMining", false, a ->
                Blocks.sand.playerUnmineable =
                Blocks.darksand.playerUnmineable =
                Blocks.sandWater.playerUnmineable =
                Blocks.darksandWater.playerUnmineable =
                Blocks.darksandTaintedWater.playerUnmineable = !a
            );

            t.checkPref("showBlockRange", true);
            t.checkPref("showLogs", true);
            t.checkPref("noMultipleMods", true);

            t.pref(new SettingsTable.Setting(Core.bundle.get("setting-showDialog")){
                @Override
                public void add(SettingsTable table){
                    table.button(name, () -> {
                        showDialog();

                        if(counter < 5){
                            counter++;
                            if(counter >= 5){
                                doSomethingPlayable();
                                ui.announce("Debug successfully.");
                            }
                        }

                    }).size(240.0f, 64.0f);
                    table.row();
                }
            });
        });
    }

    static void loadDatabase(){
        var table = ui.research.titleTable;

        table.row();
        table.button(b -> b.add("@fire.showDatabase"), () -> {
            InfoDialog.dialog.show();

            if(!"zh_CN".equals(Core.settings.getString("locale"))){

                var dialog = new BaseDialog("Warning");
                dialog.closeOnBack();
                dialog.buttons.button("@close", dialog::hide).size(210.0f, 64.0f);

                dialog.cont.pane(t ->
                    t.add("@AITranslationWarning").left().maxWidth(1280.0f).pad(4.0f)
                ).center();

                dialog.show();
            }
        }).visible(() -> ui.research.root.node == FPlanets.lysetta.techTree);
    }

    static void showDialog(){
        if(!(Core.settings.getBool("showLogs") || launched)) return;

        var version = FIRE.meta.version;

        var historyDialog = new BaseDialog(Core.bundle.format("historyTitle", displayName));
        setupDialog(historyDialog);
        historyDialog.cont.pane(t ->
            t.add("@history").left().maxWidth(1280.0f).pad(4.0f)
        );

        var mainDialog = new BaseDialog(Core.bundle.format("mainTitle", displayName, version));
        setupDialog(mainDialog);
        mainDialog.buttons.button(Core.bundle.format("historyTitle", ""), historyDialog::show).size(210.0f, 64.0f);
        mainDialog.cont.pane(t -> {

            t.image(Core.atlas.find("fire-logo")).height(107.0f).width(359.0f).pad(3.0f);
            t.row();

            t.add(Core.bundle.format("contentMain", version)).left().maxWidth(1024.0f).pad(4.0f);
            t.row();

            addContent(t,
                FBlocks.magneticDomain, FBlocks.aerolite,
                FBlocks.magneticRingPump, FBlocks.hardenedLiquidTank, FBlocks.hydroelectricGenerator,
                FBlocks.cryofluidMixerLarge, FBlocks.magnetismConcentratedRollingMill, FBlocks.magneticRingSynthesizer,
                FUnitTypes.hatchet, FUnitTypes.castle, FUnitTypes.mechanicalTide
            );
            t.row();

            t.add("@contentSecondary").left().maxWidth(1024.0f).pad(4.0f);
            t.row();

            if("zh_CN".equals(Core.settings.getString("locale"))){

                t.button(("@linkRaindance"), () -> {
                    if(!Core.app.openURI(linkRaindance)){
                        ui.showErrorMessage("@linkfail");
                        Core.app.setClipboardText(linkRaindance);
                    }
                }).size(256.0f, 64.0f);
                t.row();

                t.button(("@linkUenhe"), () -> {
                    if(!Core.app.openURI(linkUeneh)){
                        ui.showErrorMessage("@linkfail");
                        Core.app.setClipboardText(linkUeneh);
                    }
                }).size(256.0f, 64.0f);
                t.row();

            }else{

                t.button(("@linkGithub"), () -> {
                    if(!Core.app.openURI(linkGitHub)){
                        ui.showErrorMessage("@linkfail");
                        Core.app.setClipboardText(linkGitHub);
                    }
                }).size(256.0f, 64.0f);
                t.row();
            }
        }).maxWidth(1024.0f);

        mainDialog.show();
    }

    static void showNoMultipleMods(){
        if(mods.orderedMods().contains(mod -> !"fire".equals(mod.meta.name) && !mod.meta.hidden) && Core.settings.getBool("noMultipleMods")){

            // see https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod also
            new BaseDialog("o_o?"){
                float time = 300.0f;
                boolean canClose;

                {
                    update(() -> canClose = (time -= Time.delta) <= 0.0f);
                    cont.add("@noMultipleMods");
                    buttons.button("", this::hide).update(b -> {
                        b.setDisabled(!canClose);
                        b.setText(canClose ? "@close" : String.format("%s(%ss)", Core.bundle.get("close"), Strings.fixed(time / 60.0f, 1)));
                    }).size(210.0f, 64.0f);

                    multiplied = true;
                    doSomethingUnplayable();
                    show();
                }
            };
        }
    }

    /** See <a href="https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod">Extra Utilities</a> also. */
    static void setRandTitle(){
        if(!Core.app.isDesktop()) return;

        var titles = Core.bundle.get("fire.randTitle").split("\\|");
        var index = Mathf.random(titles.length - 1);
        var title = titles[index];

        // "Today is the _th Day of God's Creation of the Lysetta" picked
        if(index == 0)
            title = String.format(title, ChronoUnit.DAYS.between(LocalDate.of(2022, 11, 19), LocalDate.now()));

        Core.graphics.setTitle("Mindustry: " + title);
    }

    static void setupDialog(BaseDialog dialog){
        dialog.closeOnBack();
        dialog.buttons.button("@close", dialog::hide).size(210.0f, 64.0f);
    }

    static void addContent(Table table, UnlockableContent... content){
        for(var c : content){

            table.table(Styles.grayPanel, t -> {

                t.left().button(new TextureRegionDrawable(c.uiIcon), Styles.emptyi, 40.0f, () -> ui.content.show(c)).size(40.0f).pad(10.0f).scaling(Scaling.fit);
                t.left().table(info -> {

                    info.left().add("[accent]" + c.localizedName).left();
                    info.row();

                    try{
                        info.left().add(c.description.substring(0, c.description.indexOf(Core.bundle.get("stringEnd")))).left();
                    }catch(Throwable ignored){
                        info.left().add(c.description).left();
                    }
                });

            }).growX().pad(5.0f);
            table.row();
        }
    }

    /** ??? */
    static void doSomethingUnplayable(){
        for(var u : content.units())
            u.health -= 10000.0f;
    }

    /** !!! */
    static void doSomethingPlayable(){
        for(var e : DEBUG.DEBUGS){
            if(e instanceof Block)
                ((Block)e).buildVisibility = BuildVisibility.shown;
        }

        if(multiplied){
            multiplied = false;
            for(var u : content.units())
                u.health += 10000.0f;
        }
    }
}
