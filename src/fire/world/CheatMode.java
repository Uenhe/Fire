package fire.world;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import arc.util.Time;
import fire.content.FRBlocks;
import fire.content.FRFx;
import fire.content.FRItems;
import fire.content.FRStatusEffects;
import fire.type.FleshUnitType;
import fire.ui.FRUI;
import fire.world.blocks.sandbox.AdaptiveSource;
import fire.world.blocks.units.ElementUnitFactory;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;

import static arc.Core.bundle;
import static arc.Core.camera;
import static mindustry.Vars.*;

/** @author fy */
public class CheatMode{

    private static final ObjectIntMap<Block> blockThreats = new ObjectIntMap<>();
    private static final Seq<Block> threateningBlocks = new Seq<>();
    static int threat;

    static{
        Events.on(EventType.WaveEvent.class, e -> {
            Time.run(10, () -> {

                state.rules.waveTeam.data().units.each(unit -> {
                    switch(threat){
                        case 0, 1:
                            break;
                        case 2, 3, 4:
                            unit.apply(FRStatusEffects.informationalPerturbation, 3600);
                            break;
                        case 5:
                            unit.apply(FRStatusEffects.informationalPerturbation, 3600);
                            if(!unit.hasItem())
                                unit.addItem(Items.blastCompound, unit.itemCapacity());
                            break;
                        case 6:
                            unit.apply(FRStatusEffects.informationalPerturbation, 3600);
                            unit.apply(StatusEffects.overclock, 3600);
                            unit.apply(StatusEffects.overdrive, 3600);
                            if(!unit.hasItem())
                                unit.addItem(Items.blastCompound, unit.itemCapacity());
                            break;
                        case 7:
                            unit.apply(FRStatusEffects.informationalPerturbation, 3600);
                            unit.apply(StatusEffects.overclock, 3600);
                            unit.apply(StatusEffects.overdrive, 3600);
                            if(!unit.hasItem())
                                unit.addItem(FRItems.detonationCompound, unit.itemCapacity());
                            break;
                        case 8:
                            unit.apply(FRStatusEffects.informationalPerturbation, 3600);
                            unit.apply(StatusEffects.overclock, 3600);
                            unit.apply(StatusEffects.overdrive, 3600);
                            unit.apply(StatusEffects.shielded, 3600);
                            if(!unit.hasItem())
                                unit.addItem(FRItems.detonationCompound, unit.itemCapacity());
                            if(unit.type instanceof FleshUnitType)
                                unit.apply(FRStatusEffects.overgrown);
                            break;
                        case 9:
                            unit.apply(FRStatusEffects.informationalPerturbation, 3600);
                            unit.apply(FRStatusEffects.inspired, 3600);
                            unit.apply(StatusEffects.overclock, 3600);
                            unit.apply(StatusEffects.overdrive, 3600);
                            unit.apply(StatusEffects.shielded, 3600);
                            if(!unit.hasItem())
                                unit.addItem(FRItems.detonationCompound, unit.itemCapacity());
                            if(unit.type instanceof FleshUnitType)
                                unit.apply(FRStatusEffects.overgrown);
                            break;
                    }
                });
            });
        });
    }

    public static void putThreat(Object... values){
        for(int i = 0; i < values.length; i += 2){
            blockThreats.put((Block)values[i], (int)values[i + 1]);
            threateningBlocks.add((Block)values[i]);
        }
    }

    public static void update(){
        var playerRules = player.team().rules();
        if(playerRules.cheat && !state.rules.editor && state.isCampaign()){
            if(threat == 0){
                threat = getThreatLevel(player.team());
                if(state.wave == 1){
                    FRFx.wordDisplay(bundle.get("fire.cheat1A"), player.team().color, 1f, 0.3f, 0.8f, 5);
                    Time.run(30, () -> {
                        FRFx.wordDisplay(bundle.get("fire.cheat1B"), player.team().color, 1f, 0.3f, 0.77f, 4.3f);
                    });
                    Time.run(60, () -> {
                        FRFx.wordDisplay(bundle.get("fire.cheat1C") + threat, player.team().color, 1f, 0.3f, 0.74f, 3.6f);
                    });
                    Time.run(90, () -> {
                        FRFx.wordDisplay(bundle.get("fire.cheat1D"), player.team().color, 1f, 0.3f, 0.71f, 2.9f);
                    });
                }
            }

            if(getThreatLevel(player.team()) > threat){
                threat = getThreatLevel(player.team());
                for(int i = 0; i < 80 + threat * threat * 2; i++){
                    FRFx.threatingEffect(threat, Mathf.random(-30.0f, 60.0f)).at(camera.position.x, camera.position.y);
                }
                FRUI.numberDisplay(threat);
                FRFx.wordDisplay(bundle.get("fire.cheat1E") + threat, Color.red, 1.3f, 0.5f, 0.3f, 10);
                FRFx.wordDisplay(bundle.get("fire.cheat" + threat), Color.red, 1f, 0.5f, 0.2f, 10);

                //ui.hudfrag.showToast(Icon.warning, iconLarge, bundle.get("fire.cheat2") + (threat >= 4?"[red]":"[accent]") + threat + "[white]");
            }

            switch(threat){
                case 2:
                    state.rules.buildCostMultiplier = 1.2f;
                    playerRules.buildSpeedMultiplier = 0.9f;
                    if(state.wave > 1 && state.wavetime > state.rules.waveSpacing * 0.85f)
                        state.wavetime = state.rules.waveSpacing * 0.85f;
                    break;
                case 3:
                    playerRules.buildSpeedMultiplier = 0.9f;
                    state.rules.buildCostMultiplier = 1.2f;
                    if(state.wave > 1 && state.wavetime > state.rules.waveSpacing * 0.8f)
                        state.wavetime = state.rules.waveSpacing * 0.8f;
                    break;
                case 4:
                    playerRules.buildSpeedMultiplier = 0.9f;
                    state.rules.buildCostMultiplier = 1.3f;
                    if(state.wave > 1 && state.wavetime > state.rules.waveSpacing * 0.8f)
                        state.wavetime = state.rules.waveSpacing * 0.8f;
                    break;
                case 5:
                    state.rules.buildCostMultiplier = 1.7f;
                    playerRules.buildSpeedMultiplier = 0.75f;
                    if(state.wave > 1 && state.wavetime > state.rules.waveSpacing * 0.7f)
                        state.wavetime = state.rules.waveSpacing * 0.7f;
                    break;
                case 6, 7:
                    state.rules.buildCostMultiplier = 2f;
                    playerRules.buildSpeedMultiplier = 0.5f;
                    if(state.wave > 1 && state.wavetime > state.rules.waveSpacing * 0.5f)
                        state.wavetime = state.rules.waveSpacing * 0.5f;
                    break;
                case 8, 9:
                    state.rules.buildCostMultiplier = 2f;
                    //state.rules.pauseDisabled = true;
                    playerRules.buildSpeedMultiplier = 0.5f;
                    if(state.wave > 1 && state.wavetime > state.rules.waveSpacing * 0.4f)
                        state.wavetime = state.rules.waveSpacing * 0.4f;
                    break;
            }
            if(state.wave % 2 == 0 && state.wavetime >= 2 && threat >= 4){
                logic.runWave();
            }
            if(state.wave == 1 && state.wavetime > state.rules.initialWaveSpacing * 0.6f)
                state.wavetime = state.rules.initialWaveSpacing == 0 ? state.rules.waveSpacing : state.rules.initialWaveSpacing * 0.6f;
            if(state.wave > 1 && state.wavetime > state.rules.waveSpacing * 0.9f)
                state.wavetime = state.rules.waveSpacing * 0.9f;

            if(Core.graphics.getFrameId() % 7 == 0){
                for(var build : player.team().data().buildings){
                    if(!(build instanceof ItemTurret.ItemTurretBuild))
                        continue;

                    var ammo = ((ItemTurret.ItemTurretBuild)build).ammo;
                    var item = content.item(AdaptiveSource.turretItemMap.get(build.block.id));
                    if(ammo.isEmpty())
                        build.handleItem(build, item);

                    var entry = (ItemTurret.ItemEntry)ammo.peek();
                    entry.amount = ((ItemTurret.ItemTurretBuild)build).totalAmmo = 100;
                    entry.item = item;
                }
            }
        }else{
            threat = 0;
        }
    }

    public static int getThreatLevel(Team team){
        int threat = getThreat(team);
        return threat <= 200 ? threat <= 150 ? threat <= 120 ? threat <= 90 ? threat <= 60 ? threat <= 35 ? threat <= 12 ? threat <= 5 ? 1 : 2 : 3 : 4 : 5 : 6 : 7 : 8 : 9;
    }

    public static int getThreat(Team team){
        int threat = 0;

        threat += (int)player.team().items().sum((i, a) -> {
            var value = ElementUnitFactory.itemValues.get(i);
            if(value == null) return 0.0f;
            return value.threat;
        });

        for(var block : threateningBlocks){
            var builds = team.data().buildingTypes.get(block);
            if(builds == null || builds.isEmpty()) continue;
            threat += blockThreats.get(block);
        }

        return threat;
    }

    public static void load(){
        putThreat(
            FRBlocks.metaglassPlater, 2,
            FRBlocks.electrothermalSiliconFurnace, 2,

            Blocks.overdriveProjector, 3,
            FRBlocks.smasher, 3,
            FRBlocks.grudge, 3,
            FRBlocks.vectorialUnitFactory, 3,

            Blocks.overdriveDome, 4,
            Blocks.exponentialReconstructor, 4,
            FRBlocks.nightmare, 4,
            FRBlocks.liquidNitrogenCompressor, 5,
            FRBlocks.electromagnetismDiffuser, 5,
            FRBlocks.hardenedAlloyConveyor, 5,
            Blocks.phaseConveyor, 5,
            FRBlocks.obstruction, 5,
            FRBlocks.distance, 5,

            FRBlocks.cumulonimbus, 6,
            FRBlocks.aerolite, 6,

            Blocks.tetrativeReconstructor, 7,

            FRBlocks.campfire, 15,

            FRBlocks.fractalUnitFactory, 18
        );
    }
}
