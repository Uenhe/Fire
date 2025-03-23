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

import static mindustry.Vars.*;

public class AcceleratorScene extends Accelerator{

    private Color originColor;
    private final String[] texts;
    private final String sourceText;
    /** Switch; Mask; Detect; Text; Unmask.<p>
     * Have to set manually according to text number. */
    private final FRUtils.TimeNode node;
    /** Set manually, length = 5 */
    private final short[] sectors;

    private static final Image[] masks = new Image[2];

    static{{
        for(byte i = 0; i < masks.length; i++)
            masks[i] = new Image();
    }}

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
        launchCandidates = Seq.with(FRPlanets.lysetta); //byd Anuke

        if(launchCandidates.size == 1) originColor = launchCandidates.first().atmosphereColor.cpy();
        super.init();
    }

    public class AcceleratorSceneBuild extends AcceleratorBuild{

        private boolean scene, messaged, detected;
        private float sceneTimer;

        @Override
        public void buildConfiguration(Table table){
            if(launchCandidates.size > 1){
                super.buildConfiguration(table);
                return;
            }

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
            if(launchCandidates.size > 1) return;

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

                //avoid side effect, doesn't work entirely
                //is this necessary?
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
                    final float tr = 0.9f, tg = 0.1f, tb = 0.0f;
                    ui.planet.state.planet.atmosphereColor.set(
                        originColor.r + Mathf.absin(sceneTimer - node.first(), 10.0f, tr - originColor.r),
                        originColor.g + Mathf.absin(sceneTimer - node.first(), 10.0f, tg - originColor.g),
                        originColor.b + Mathf.absin(sceneTimer - node.first(), 10.0f, tb - originColor.b)
                    );
                }

                if(!messaged && node.checkBelonging(sceneTimer, 2)){
                    messaged = true;

                    for(byte i = 0; i < texts.length; i++){
                        final byte j = i;
                        final float delay = j != 0
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
                        ui.announce(sourceText, 5.0f));
                }
            }
        }

        @Override
        public void remove(){
            if(launchCandidates.size == 1) reset();
            super.remove();
        }

        private void setup(){
            Reflect.set(BaseDialog.class, ui.planet, "shouldPause", false);

            // without announcement since it will be covered by PlanetDialog
            Core.settings.put("skipcoreanimation", false);
            Core.settings.put("atmosphere", true);

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
