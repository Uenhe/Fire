package fire;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Time;
import fire.content.*;
import fire.input.FBinding;
import fire.world.meta.FAttribute;
import mindustry.content.Blocks;
import mindustry.mod.Mods.LoadedMod;
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

    private static LoadedMod FIRE;
    private static String displayName, close;

    @Override
    public void init(){
        Log.info("init");
        FIRE = mods.locateMod("fire");
        displayName = Core.bundle.get("modname");
        close = Core.bundle.get("close");
        FIRE.meta.displayName = displayName;

        setRandTitle();
        loadSettings();
        FBinding.load();

        Events.on(mindustry.game.EventType.ClientLoadEvent.class, e -> {
            showNoMultipleMods();
            showDialog();

              Blocks.sand.playerUnmineable
            = Blocks.darksand.playerUnmineable
            = Blocks.sandWater.playerUnmineable
            = Blocks.darksandWater.playerUnmineable
            = Blocks.darksandTaintedWater.playerUnmineable
            = Core.settings.getBool("allowSandMining");
        });
    }

    @Override
    public void loadContent(){
        Log.info("content");
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

            t.checkPref("allowSandMining", false, a -> {
                Blocks.sand.playerUnmineable = !a;
                Blocks.darksand.playerUnmineable = !a;
                Blocks.sandWater.playerUnmineable = !a;
                Blocks.darksandWater.playerUnmineable = !a;
                Blocks.darksandTaintedWater.playerUnmineable = !a;
            });

            t.checkPref("showBlockRange", true);

            t.checkPref("showLogs", true);

            t.checkPref("noMultipleMods", true);

            t.pref(new SettingsTable.Setting(Core.bundle.get("setting-showDialog")){
                @Override
                public void add(SettingsTable table){
                    table.button(name, FireMod::showDialog).size(210f, 64f);
                    table.row();
                }
            });
        });
    }

    private static void showDialog(){
        if(!Core.settings.getBool("showLogs")) return;

        final var version = FIRE.meta.version;
        final var historyDialog = new BaseDialog(Core.bundle.format("historyTitle", displayName)){{
            setupDialog(this);
            cont.pane(t ->
                t.add("@history").left().width(1024f).maxWidth(1280f).pad(4f)
            ).maxWidth(1024f);
        }};

        new BaseDialog(Core.bundle.format("mainTitle", displayName, version)){{

            setupDialog(this);
            buttons.button(Core.bundle.format("historyTitle", ""), historyDialog::show).size(210f, 64f);
            cont.pane(t -> {

                t.image(Core.atlas.find("fire-logo", Core.atlas.find("clear"))).height(107f).width(359f).pad(3f);
                t.row();

                t.add(Core.bundle.format("contentMain", version)).left().width(800f).maxWidth(1024f).pad(4f);
                t.row();

                addContent(t, FBlocks.adaptiveSource);
                addContent(t, FBlocks.blossom);
                addContent(t, FBlocks.gambler);
                addContent(t, FBlocks.magneticSphere);
                addContent(t, FBlocks.hardenedAlloyConveyor);
                addContent(t, FUnitTypes.pioneer);
                addContent(t, FStatusEffects.overgrown);
                addContent(t, FStatusEffects.disintegrated);
                t.row();

                t.add("@contentSecondary").left().width(800f).maxWidth(1024f).pad(4f);
                t.row();

                if("zh_CN".equals(Core.settings.getString("locale"))){

                    t.button(("@linkRaindance"), () -> {
                        if(!Core.app.openURI(linkRaindance)){
                            ui.showErrorMessage("@linkfail");
                            Core.app.setClipboardText(linkRaindance);
                        }
                    }).size(256f, 64f);
                    t.row();

                    t.button(("@linkUenhe"), () -> {
                        if(!Core.app.openURI(linkUeneh)){
                            ui.showErrorMessage("@linkfail");
                            Core.app.setClipboardText(linkUeneh);
                        }
                    }).size(256f, 64f);
                    t.row();

                }else{

                    t.button(("@linkGithub"), () -> {
                        if(!Core.app.openURI(linkGitHub)){
                            ui.showErrorMessage("@linkfail");
                            Core.app.setClipboardText(linkGitHub);
                        }
                    }).size(256f, 64f);
                    t.row();
                }
            }).maxWidth(1024f);

            show();
        }};
    }

    private static void showNoMultipleMods(){
        boolean announces = false;

        for(final var mod : mods.orderedMods())
            if(!"fire".equals(mod.meta.name) && !mod.meta.hidden){
                announces = true;
                break;
            }

        if(announces && Core.settings.getBool("noMultipleMods"))

            // see https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod also
            new BaseDialog("o_o?"){
                private float time = 300f;
                private boolean canClose;

                {
                    update(() -> canClose = (time -= Time.delta) <= 0f);
                    cont.add("@noMultipleMods");
                    buttons.button("", this::hide).update(b -> {
                        b.setDisabled(!canClose);
                        b.setText(canClose ? close : String.format("%s(%ss)", close, Strings.fixed(time / 60f, 1)));
                    }).size(210f, 64f);
                }
                {
                    show();
                }
            };

    }

    /** See <a href="https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod">Extra Utilities</a> also. */
    private static void setRandTitle(){
        if (!Core.app.isDesktop()) return;

        final var titles = Core.bundle.get("fire.randTitle").split("\\|");
        final int index = Mathf.random(titles.length - 1);
        String title = titles[index];

        if(index == 0) // "Today is the {0}th Day of God's Creation of the Risetar" picked
            title = Core.bundle.format(
                title,
                java.time.temporal.ChronoUnit.DAYS.between(LocalDate.of(2022, 11, 19), LocalDate.now())
            );

        Core.graphics.setTitle("Mindustry: " + title);
    }

    private static void setupDialog(BaseDialog dialog){
        dialog.closeOnBack();
        dialog.buttons.button(close, dialog::hide).size(210f, 64f);
    }

    private static void addContent(Table table, mindustry.ctype.UnlockableContent content){
        table.table(Styles.grayPanel, t -> {

            t.left().button(new arc.scene.style.TextureRegionDrawable(content.uiIcon), Styles.emptyi, 40f, () -> ui.content.show(content)).size(40f).pad(10f).scaling(arc.util.Scaling.fit);
            t.left().table(info -> {

                info.left().add("[accent]" + content.localizedName).left();
                info.row();
                info.left().add(
                    content.description.substring(0, content.description.indexOf(Core.bundle.get("stringEnd")))
                ).left();
            });

        }).growX().pad(5f);
        table.row();
    }
}
