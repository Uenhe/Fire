package fire.world.blocks.campaign;

import arc.Core;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import fire.FRUtils;
import fire.content.FRPlanets;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Iconc;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;

public class AcceleratorScene extends mindustry.world.blocks.campaign.Accelerator{

    private final Color originColor = new Color();
    private final String[] texts;
    private final String sourceText;
    /** Switch; Mask; Detect; Text; Unmask.<p></p>
     * Have to set manually according to text number. */
    private final FRUtils.TimeNode node;
    /** Set manually, length = 5 */
    private final short[] sectors;

    public AcceleratorScene(String name, String textKey, String sourceKey, int[] nodes, short[] sects){
        super(name);
        texts = Core.bundle.get(textKey).split("\\|");
        sourceText = Core.bundle.get(sourceKey);
        node = new FRUtils.TimeNode(nodes);
        sectors = sects;
        buildType = AcceleratorSceneBuild::new;
    }

    @Override
    public void init(){
        super.init();

        launchCandidates = Seq.with(FRPlanets.lysetta); //byd Anuke
        if(launchCandidates.size == 1) originColor.set(launchCandidates.first().atmosphereColor);
    }

    public class AcceleratorSceneBuild extends AcceleratorBuild{

        private boolean scene;
        private float sceneTimer;

        @Override
        public void buildConfiguration(Table table){
            if(launchCandidates.size > 1){
                super.buildConfiguration(table);
                return;
            }

            deselect();
            if(!canLaunch()) return;

            reset(false);
            setup();
            ui.planet.showPlanetLaunch(state.rules.sector, launchCandidates, sector -> {
                if(canLaunch()){
                    consume();
                    power.graph.useBatteries(powerBufferRequirement);
                    progress = 0.0f;
                    renderer.showLaunch(this);
                    Time.runTask(launchDuration() - 6.0f, () -> {
                        launching = false;
                        sector.planet.unlockedOnLand.each(UnlockableContent::unlock);
                        universe.clearLoadoutInfo();
                        universe.updateLoadout((CoreBlock)launchBlock);
                        control.playSector(sector);
                    });
                }
            });

            ui.planet.lookAt(ui.planet.state.planet.getStartSector());
        }

        @Override
        @SuppressWarnings("unchecked")
        public void updateTile(){
            super.updateTile();
            if(launchCandidates.size != 1 || !scene) return;

            if(ui.hasAnnouncement()){
                Table t = Reflect.get(ui, "lastAnnouncement");
                if(t.getCells().any())((Cell<Label>)t.getCells().first()).with(label -> {
                    final var texts = label.getText();
                    if(texts.indexOf(String.valueOf(Iconc.lockOpen)) != -1){ // avoids side effect but doesn't work entirely
                        t.visible = false;
                    }else if(texts.indexOf(String.valueOf(Iconc.infoCircle)) != -1){
                        t.update(() -> {
                            t.setPosition(Core.graphics.getWidth() * 0.5f, Core.graphics.getHeight() * 0.14f, Align.center);
                            t.toFront();
                            if(!ui.planet.isShown()) t.remove();
                        });
                    }
                });
            }

            sceneTimer += Time.delta;
            if(node.checkBelonging(sceneTimer, 1, 2)){
                final float tr = 0.9f, tg = 0.1f, tb = 0.0f,
                    scl = node.getQuantum(1, 2) / Mathf.PI / 40, time = sceneTimer - node.first() - scl * Mathf.PI;
                ui.planet.state.planet.atmosphereColor.set(
                    //absin: initial phase = PI, period = 6PI
                    originColor.r + Mathf.absin(time, scl, tr - originColor.r),
                    originColor.g + Mathf.absin(time, scl, tg - originColor.g),
                    originColor.b + Mathf.absin(time, scl, tb - originColor.b)
                );
            }
        }

        @Override
        public void remove(){
            if(launchCandidates.size == 1) reset(true);
            super.remove();
        }

        private void setup(){
            Reflect.set(BaseDialog.class, ui.planet, "shouldPause", false);

            // without announcement since it will be covered by PlanetDialog
            Core.settings.put("skipcoreanimation", false);
            Core.settings.put("atmosphere", true);

            ui.planet.state.planet.sectors.get(ui.planet.state.planet.startSector).preset.clearUnlock();
            launchCandidates.first().unlock();
            ui.planet.launchSector = null; //cancels the weird line

            for(byte i = 0; i < 2; i++){
                final var mask = new Image();
                final float p = 0.2f, height = Core.graphics.getHeight();

                mask.color.set(Color.black);
                mask.touchable = Touchable.disabled;
                mask.setSize(Core.graphics.getWidth(), height * p);
                mask.setScaling(Scaling.stretch);
                mask.y = i == 0 ? height * -p : height * (1.0f + p);

                mask.actions(
                    Actions.delay(node.first() / 60.0f), Actions.moveTo(0.0f, i == 0 ? 0.0f : height * (1.0f - p), node.getQuantum(1) / 60.0f, Interp.smoother),
                    Actions.delay(node.getQuantum(2, 3) / 60.0f), Actions.moveTo(0.0f, i == 0 ? height * -p : height * (1.0f + p), node.lastQuantum() / 60.0f, Interp.smoother),
                    Actions.delay(node.lastQuantum() / 60.0f), Actions.remove()
                );
                mask.update(() -> {
                    mask.toFront();
                    if(!ui.planet.isShown()) mask.remove();
                });

                Core.scene.add(mask);
            }

            Time.run(60.0f, () -> {
                for(short id : sectors)
                    ui.planet.newPresets.add(ui.planet.state.planet.sectors.get(id));
            });

            for(byte i = 0; i < texts.length; i++){
                final byte j = i;
                final float delay = j != 0
                    ? node.getQuantum(2) + 150.0f * (j - 1)
                    : 0.0f;
                Time.run(delay + node.get(1), () -> ui.announce(texts[j], 2.4f));
            }

            Time.run(node.get(2) - 30.0f, () ->
                ui.announce(sourceText, 5.0f));

            scene = true;
        }

        private void reset(boolean color){
            Reflect.set(BaseDialog.class, ui.planet, "shouldPause", true);
            if(color) launchCandidates.first().atmosphereColor = originColor;
            scene = false;
            sceneTimer = 0.0f;
        }
    }
}
