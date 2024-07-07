package fire;

import arc.Core;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import fire.content.*;
import fire.world.meta.FAttribute;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable;

import java.time.LocalDate;

import static mindustry.Vars.mods;
import static mindustry.Vars.ui;

public class FireMod extends mindustry.mod.Mod{

    private static final String
        linkRaindance = "https://space.bilibili.com/516898377",
        linkUeneh = "https://space.bilibili.com/327502129",
        linkGitHub = "https://github.com/Uenhe/Fire";

    private static mindustry.mod.Mods.LoadedMod FIRE;
    private static String displayName, close;
    private static boolean launched;

    @Override
    public void init(){
        fire.input.FBinding.load();
        loadSettings();

        arc.Events.run(mindustry.game.EventType.ClientLoadEvent.class, () -> {
            showDialog();
            showNoMultipleMods();
            launched = true;
        });
    }

    @Override
    public void loadContent(){
        FIRE = mods.locateMod("fire");
        displayName = Core.bundle.get("modname");
        close = Core.bundle.get("close");
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
        FOverride.load();
    }

    private static void loadSettings(){
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
                    table.button(name, FireMod::showDialog).size(240.0f, 64.0f);
                    table.row();
                }
            });
        });
    }

    private static void showDialog(){
        if(!(Core.settings.getBool("showLogs") || launched)) return;

        var version = FIRE.meta.version;
        var historyDialog = new BaseDialog(Core.bundle.format("historyTitle", displayName)){{
            setupDialog(this);
            cont.pane(t ->
                t.add("@history").left().width(1024.0f).maxWidth(1280.0f).pad(4.0f)
            ).maxWidth(1024.0f);
        }};

        var mainDialog = new BaseDialog(Core.bundle.format("mainTitle", displayName, version));

        setupDialog(mainDialog);
        mainDialog.buttons.button(Core.bundle.format("historyTitle", ""), historyDialog::show).size(210.0f, 64.0f);
        mainDialog.cont.pane(t -> {

            t.image(Core.atlas.find("fire-logo", Core.atlas.find("clear"))).height(107.0f).width(359.0f).pad(3.0f);
            t.row();

            t.add(Core.bundle.format("contentMain", version)).left().width(800.0f).maxWidth(1024.0f).pad(4.0f);
            t.row();

            addContent(t,
                FBlocks.adaptiveSource, FBlocks.blossom, FBlocks.gambler, FBlocks.magneticSphere,
                FBlocks.hardenedAlloyConveyor, FUnitTypes.pioneer,
                FStatusEffects.overgrown, FStatusEffects.disintegrated
            );
            t.row();

            t.add("@contentSecondary").left().width(800.0f).maxWidth(1024.0f).pad(4.0f);
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

    private static void showNoMultipleMods(){
        boolean announces = false;

        for(var mod : mods.orderedMods())
            if(!"fire".equals(mod.meta.name) && !mod.meta.hidden){
                announces = true;
                break;
            }

        if(announces && Core.settings.getBool("noMultipleMods")){

            // see https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod also
            var dialog = new BaseDialog("o_o?"){
                private float time = 300.0f;
                private boolean canClose;

                {
                    update(() -> canClose = (time -= Time.delta) <= 0.0f);
                    cont.add("@noMultipleMods");
                    buttons.button("", this::hide).update(b -> {
                        b.setDisabled(!canClose);
                        b.setText(canClose ? close : String.format("%s(%ss)", close, Strings.fixed(time / 60.0f, 1)));
                    }).size(210.0f, 64.0f);
                }
            };

            dialog.show();
        }
    }

    /** See <a href="https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod">Extra Utilities</a> also. */
    private static void setRandTitle(){
        if(!Core.app.isDesktop()) return;

        var titles = Core.bundle.get("fire.randTitle").split("\\|");
        int index = Mathf.random(titles.length - 1);
        var title = titles[index];

        // "Today is the {0}th Day of God's Creation of the Risetar" picked
        if(index == 0)
            title = Core.bundle.format(
                title,
                java.time.temporal.ChronoUnit.DAYS.between(LocalDate.of(2022, 11, 19), LocalDate.now())
            );

        Core.graphics.setTitle("Mindustry: " + title);
    }

    private static void setupDialog(BaseDialog dialog){
        dialog.closeOnBack();
        dialog.buttons.button(close, dialog::hide).size(210.0f, 64.0f);
    }

    private static void addContent(Table table, UnlockableContent... content){
        for(var c : content){

            table.table(Styles.grayPanel, t -> {

                t.left().button(new TextureRegionDrawable(c.uiIcon), Styles.emptyi, 40.0f, () -> ui.content.show(c)).size(40.0f).pad(10.0f).scaling(Scaling.fit);
                t.left().table(info -> {

                    info.left().add("[accent]" + c.localizedName).left();
                    info.row();
                    info.left().add(
                        c.description.substring(0, c.description.indexOf(Core.bundle.get("stringEnd")))
                    ).left();
                });

            }).growX().pad(5.0f);
            table.row();
        }
    }
}
