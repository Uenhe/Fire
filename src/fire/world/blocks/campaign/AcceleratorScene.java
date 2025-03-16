package fire.world.blocks.campaign;

import arc.Core;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Reflect;
import arc.util.Scaling;
import arc.util.Time;
import fire.FRUtils;
import fire.content.FRPlanets;
import mindustry.ctype.UnlockableContent;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.blocks.campaign.Accelerator;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.control;
import static mindustry.Vars.renderer;
import static mindustry.Vars.state;
import static mindustry.Vars.ui;
import static mindustry.Vars.universe;

public class AcceleratorScene extends Accelerator{

    private Color originColor;

    /** Set manually. */
    private final short[] sectors = {0, 41, 70, 185, 79};
    private final String[] texts = Core.bundle.get("fire.planetarySceneTexts").split("\\|");
    /** Switch; Mask; Detect; Text; Unmask.<p>
     * Have to set manually according to text number. */
    private final FRUtils.TimeNode node = new FRUtils.TimeNode(90, 210, 810, 2010, 2130);

    public AcceleratorScene(String name){
        super(name);
        buildType = AcceleratorPlusBuild::new;
    }

    @Override
    public void init(){
        launchCandidates = Seq.with(FRPlanets.lysetta);
        originColor = launchCandidates.first().atmosphereColor.cpy();
        super.init();
    }

    public class AcceleratorPlusBuild extends AcceleratorBuild{

        private boolean scene, messaged, detected;
        private float sceneTimer;
        private final Image[] masks = {new Image(), new Image()};

        @Override
        public void buildConfiguration(Table table){
            deselect();
            if(!canLaunch()) return;

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

            if(sceneTimer > node.last()){
                ui.planet.hide();
                reset();
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(scene){
                sceneTimer += Time.delta;

                if(!detected){
                    ui.planet.launchSector = null; //cancel the weird line
                    detected = true;
                    Time.runTask(60.0f, () -> {
                        for(short id : sectors)
                            ui.planet.newPresets.add(ui.planet.state.planet.sectors.get(id));
                    });
                }
                // avoid side effect, doesn't work entirely
                // is this necessary?
                //if(ui.hasAnnouncement())
                //    ((Element)Reflect.get(ui, "lastAnnouncement")).visible = false;

                if(node.checkBelonging(sceneTimer, 1, 4))
                    for(var mask : masks){
                        mask.color.set(Color.black);
                        mask.touchable = Touchable.disabled;
                        mask.visibility = () -> node.checkBelonging(sceneTimer, 1, 4);
                        mask.setWidth(Core.graphics.getWidth());
                        mask.setScaling(Scaling.stretch);
                        if(mask == masks[1]) mask.y = Core.graphics.getHeight();
                        mask.update(() -> {
                            mask.setHeight(Core.graphics.getHeight() * 0.2f * maskScale() * Mathf.sign(mask == masks[0]));
                            mask.toFront();
                        });

                        Core.scene.add(mask);
                    }

                if(node.checkBelonging(sceneTimer, 1, 2)){
                    float tr = 0.9f, tg = 0.1f, tb = 0.0f;
                    ui.planet.state.planet.atmosphereColor.set(
                        originColor.r + Mathf.absin(sceneTimer - node.first(), 10.0f, tr - originColor.r),
                        originColor.g + Mathf.absin(sceneTimer - node.first(), 10.0f, tg - originColor.g),
                        originColor.b + Mathf.absin(sceneTimer - node.first(), 10.0f, tb - originColor.b)
                    );
                }

                if(!messaged && node.checkBelonging(sceneTimer, 2)){
                    messaged = true;

                    for(byte i = 0; i < texts.length; i++){
                        byte j = i;
                        float delay = j != 0
                            ? node.get(2) - node.get(1) + 150.0f * (j - 1)
                            : 0.0f;

                        Time.runTask(delay, () -> {
                            // see mindustry.core.UI.announce() (#593)
                            var t = new Table(Styles.black3);
                            t.touchable = Touchable.disabled;
                            t.margin(8.0f).add(texts[j]).style(Styles.outlineLabel).labelAlign(Align.center);
                            t.update(() -> {
                                t.setPosition(Core.graphics.getWidth() / 2.0f, Core.graphics.getHeight() / 7.0f, Align.center);
                                t.toFront();
                            });
                            t.actions(Actions.fadeOut(2.4f, Interp.pow4In), Actions.remove());
                            t.pack();
                            t.act(0.1f);
                            Core.scene.add(t);
                        });
                    }

                    Time.runTask(node.get(2) - node.get(1), () ->
                        ui.announce("@fire.planetarySceneSource", 5.0f));
                }
            }
        }

        @Override
        public void remove(){
            reset();
            super.remove();
        }

        private void setup(){
            Reflect.set(BaseDialog.class, ui.planet, "shouldPause", false);
            Core.settings.put("skipcoreanimation", false); //without announcement since it will be covered by PlanetDialog
            ui.planet.state.planet.sectors.get(ui.planet.state.planet.startSector).preset.clearUnlock();
            launchCandidates.first().unlock();
            scene = true;
        }

        private void reset(){
            Reflect.set(BaseDialog.class, ui.planet, "shouldPause", true);
            launchCandidates.first().atmosphereColor = originColor;
            scene = messaged = detected = false;
            sceneTimer = 0.0f;
        }

        private float maskScale(){
            final float val;

            if(node.checkBelonging(sceneTimer, 0))
                val = 0.0f;

            else if(node.checkBelonging(sceneTimer, 1))
                val = (sceneTimer - node.first()) / (node.get(1) - node.first());

            else if(node.checkBelonging(sceneTimer, 2, 3))
                val = 1.0f;

            else if(node.checkBelonging(sceneTimer, 4))
                val = 1.0f - (sceneTimer - node.get(3)) / (node.last() - node.get(3));

            else
                val = 0.0f;

            return Mathf.clamp(Interp.smoother.apply(val), 0.0f, 1.0f);
        }
    }
}
