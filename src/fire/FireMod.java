package fire;

import arc.Core;
import arc.Events;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import fire.content.*;
import fire.input.FireBinding;
import fire.world.meta.FireAttribute;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;

import java.util.Objects;

import static fire.FireLib.format;
import static mindustry.Vars.mods;
import static mindustry.Vars.ui;

public class FireMod extends mindustry.mod.Mod{
    public static final String
        linkRaindance = "https://space.bilibili.com/516898377",
        linkUeneh = "https://space.bilibili.com/327502129",
        linkGitHub = "https://github.com/Uenhe/Fire";

    public FireMod(){
        Events.on(EventType.ClientLoadEvent.class, e -> {
            FireBinding.load();
            showDialog();
        });
    }

    @Override
    public void init(){
        loadSettings();
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

    public void loadSettings(){
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

            t.pref(new SettingsMenuDialog.SettingsTable.Setting(Core.bundle.get("setting-showDialog")){

                @Override
                public void add(SettingsMenuDialog.SettingsTable table){
                    table.button(name, () -> showDialog()).size(210f, 64f);
                    table.row();
                }
            });
        });
    }

    public void showDialog(){
        var mod = mods.locateMod("fire");
        String name = Core.bundle.get("modname");
        String version = mod.meta.version;
        mod.meta.displayName = name;

        var historyDialog = new BaseDialog(format("historyTitle", name));
        setupDialog(historyDialog);
        historyDialog.cont.pane(t ->
            t.add("@history").left().width(1024f).maxWidth(1280f).pad(4f)
        ).maxWidth(1024f);

        var mainDialog = new BaseDialog(format("mainTitle", name, version));
        setupDialog(mainDialog);
        mainDialog.buttons.button(Core.bundle.format("historyTitle", ""), historyDialog::show).size(210f, 64f);

        mainDialog.cont.pane(t -> {

            t.image(Core.atlas.find("fire-logo", Core.atlas.find("clear"))).height(107f).width(359f).pad(3f);
            t.row();

            t.add(Core.bundle.format("contentMain", version)).left().width(800f).maxWidth(1024f).pad(4f);
            t.row();

            t.row();
            addContent(t, FireBlocks.adaptiveSource);
            addContent(t, FireBlocks.blossom);
            addContent(t, FireBlocks.gambler);
            addContent(t, FireBlocks.magneticSphere);
            addContent(t, FireBlocks.magneticAlloyFormer);
            addContent(t, FireBlocks.electromagnetismDiffuser);
            addContent(t, FireBlocks.fleshReconstructor);
            addContent(t, FireUnitTypes.hatchet);
            addContent(t, FireUnitTypes.pioneer);
            addContent(t, FireStatusEffects.overgrown);
            addContent(t, FireStatusEffects.disintegrated);
            t.row();

            t.add("@contentSecondary").left().width(800f).maxWidth(1024f).pad(4f);
            t.row();

            if(Objects.equals(Core.settings.getString("locale"), "zh_CN")){

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

    private static void setupDialog(BaseDialog dialog){
        dialog.addCloseListener();
        dialog.buttons.button("@close", dialog::hide).size(210f, 64f);
    }



    private static void addContent(Table table, UnlockableContent content){
        table.table(Styles.grayPanel, t -> {

            t.left().button(new TextureRegionDrawable(content.uiIcon), Styles.emptyi, 40f, () -> ui.content.show(content)).size(40f).pad(10f).scaling(Scaling.fit);
            t.left().table(info -> {

                var end = Objects.equals(Core.settings.getString("locale"), "zh_CN") ? Core.bundle.get("stringEnd") : ".";
                var detail = content.description.substring(0, content.description.indexOf(end));
                info.left().add("[accent]" + content.localizedName).left();
                info.row();
                info.left().add(detail).left();
            });

        }).growX().pad(5f);
        table.row();
    }
}
