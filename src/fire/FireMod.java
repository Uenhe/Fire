package fire;

import arc.Core;
import arc.Events;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import fire.content.*;
import fire.input.FireBinding;
import fire.world.meta.FireAttribute;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.mod.Mods.LoadedMod;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static mindustry.Vars.mods;
import static mindustry.Vars.ui;

public class FireMod extends mindustry.mod.Mod{
    public static final String

        linkRaindance = "https://space.bilibili.com/516898377",
        linkUeneh = "https://space.bilibili.com/327502129",
        linkGitHub = "https://github.com/Uenhe/Fire";

<<<<<<< Updated upstream
    public static LoadedMod fire;
    public static String name;
    public static String close;
=======
    private static mindustry.mod.Mods.LoadedMod FIRE;
    private static String displayName;
    private static boolean launched;
    private static byte counter;
>>>>>>> Stashed changes

    public FireMod(){

        Events.on(EventType.ClientLoadEvent.class, e -> {

            FireBinding.load();
            showDialog();
            showNoMultipleMods();
        });
    }

    @Override
    public void init(){

        fire = mods.locateMod("fire");
        name = Core.bundle.get("modname");
        close = Core.bundle.get("close");
        fire.meta.displayName = name;

        loadSettings();

          Blocks.sand.playerUnmineable
        = Blocks.darksand.playerUnmineable
        = Blocks.sandWater.playerUnmineable
        = Blocks.darksandWater.playerUnmineable
        = Blocks.darksandTaintedWater.playerUnmineable
        = Core.settings.getBool("allowSandMining");
    }

    @Override
    public void loadContent(){

        FireAttribute.load();
        FireStatusEffects.load();
        FireItems.load();
        FireLiquids.load();
        FireUnitTypes.load();
        FireBlocks.load();
        FirePlanets.load();
        FireSectorPresets.load();
        RisetarTechTree.load();
        FireOverride.load();
    }

    public static void loadSettings(){

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

            t.pref(new SettingsMenuDialog.SettingsTable.Setting(Core.bundle.get("setting-showDialog")){

                @Override
<<<<<<< Updated upstream
                public void add(SettingsMenuDialog.SettingsTable table){
                    table.button(name, FireMod::showDialog).size(210f, 64f);
=======
                public void add(SettingsTable table){
                    table.button(name, () -> {
                        showDialog();

                        //???
                        if(counter < 5){
                            counter++;
                            if(counter >= 5)
                                FPlanets.risetar.accessible = true;
                        }

                    }).size(240.0f, 64.0f);
>>>>>>> Stashed changes
                    table.row();
                }
            });
        });
    }

    public static void showDialog(){

        String version = fire.meta.version;

        var historyDialog = new BaseDialog(Core.bundle.format("historyTitle", name));
        setupDialog(historyDialog);
        historyDialog.cont.pane(t ->
            t.add("@history").left().width(1024f).maxWidth(1280f).pad(4f)
        ).maxWidth(1024f);

        var mainDialog = new BaseDialog(Core.bundle.format("mainTitle", name, version));
        setupDialog(mainDialog);
        mainDialog.buttons.button(Core.bundle.format("historyTitle", ""), historyDialog::show).size(210f, 64f);

        mainDialog.cont.pane(t -> {

<<<<<<< Updated upstream
            t.image(Core.atlas.find("fire-logo", Core.atlas.find("clear"))).height(107f).width(359f).pad(3f);
=======
            t.image(Core.atlas.find("fire-logo")).height(107.0f).width(359.0f).pad(3.0f);
>>>>>>> Stashed changes
            t.row();

            t.add(Core.bundle.format("contentMain", version)).left().width(800f).maxWidth(1024f).pad(4f);
            t.row();

            t.row();
            addContent(t, FireBlocks.adaptiveSource);
            addContent(t, FireBlocks.blossom);
            addContent(t, FireBlocks.gambler);
            addContent(t, FireBlocks.magneticSphere);
            addContent(t, FireBlocks.hardenedAlloyConveyor);
            addContent(t, FireUnitTypes.pioneer);
            addContent(t, FireStatusEffects.overgrown);
            addContent(t, FireStatusEffects.disintegrated);
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
        mainDialog.show();
    }

<<<<<<< Updated upstream
    public static void showNoMultipleMods(){

        boolean announces = false;

        for(var mod : mods.orderedMods()){
            if(!"fire".equals(mod.meta.name) && !mod.meta.hidden){
                announces = true;
                break;
            }
        }

        if(announces && Core.settings.getBool("noMultipleMods")){
=======
    private static void showNoMultipleMods(){

        if(mods.orderedMods().contains(mod -> !"fire".equals(mod.meta.name) && !mod.meta.hidden) && Core.settings.getBool("noMultipleMods")){
>>>>>>> Stashed changes

            var dialog = new BaseDialog("o_o?"){

                float time = 300f;
                boolean canClose;

                {
                    update(() -> canClose = (time -= Time.delta) <= 0f);
                    cont.add("@noMultipleMods");

                    buttons.button("", this::hide).update(b -> {
                        b.setDisabled(!canClose);
<<<<<<< Updated upstream
                        b.setText(canClose ? close : String.format("%s(%ss)", close, Strings.fixed(time / 60f, 1)));
                    }).size(210f, 64f);
=======
                        b.setText(canClose ? "@close" : String.format("%s(%ss)", Core.bundle.get("close"), Strings.fixed(time / 60.0f, 1)));
                    }).size(210.0f, 64.0f);

                    FPlanets.risetar.accessible = false;

                    show();
>>>>>>> Stashed changes
                }
            };

            dialog.show();
        }
    }

    public static void setupDialog(BaseDialog dialog){

<<<<<<< Updated upstream
=======
        var titles = Core.bundle.get("fire.randTitle").split("\\|");
        var index = Mathf.random(titles.length - 1);
        var title = titles[index];

        // "Today is the _th Day of God's Creation of the Risetar" picked
        if(index == 0)
            title = String.format(title, ChronoUnit.DAYS.between(LocalDate.of(2022, 11, 19), LocalDate.now()));

        Core.graphics.setTitle("Mindustry: " + title);
    }

    private static void setupDialog(BaseDialog dialog){
>>>>>>> Stashed changes
        dialog.closeOnBack();
        dialog.buttons.button(close, dialog::hide).size(210f, 64f);
    }

    public static void addContent(Table table, UnlockableContent content){

        table.table(Styles.grayPanel, t -> {

            t.left().button(new TextureRegionDrawable(content.uiIcon), Styles.emptyi, 40f, () -> ui.content.show(content)).size(40f).pad(10f).scaling(Scaling.fit);
            t.left().table(info -> {

                var detail = content.description.substring(0, content.description.indexOf(Core.bundle.get("stringEnd")));
                info.left().add("[accent]" + content.localizedName).left();
                info.row();
                info.left().add(detail).left();
            });

        }).growX().pad(5f);
        table.row();
    }
}
