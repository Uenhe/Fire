package fire.content;

import mindustry.content.SectorPresets;
import mindustry.content.TechTree;
import mindustry.content.UnitTypes;
import mindustry.ctype.UnlockableContent;

import static arc.struct.Seq.with;
import static fire.content.FireBlocks.*;
import static fire.content.FireItems.*;
import static fire.content.FireLiquids.*;
import static fire.content.FireSectorPresets.*;
import static fire.content.FireUnitTypes.*;
import static mindustry.content.TechTree.node;
import static mindustry.content.TechTree.nodeRoot;
import static mindustry.game.Objectives.*;

public class RisetarTechTree{

    //Copied from origin JS file, really messy and confusing.
    private static void add(UnlockableContent content, String parent){
        var lastNode = TechTree.all.find(t -> t.content == content);
        if(lastNode != null) lastNode.remove();
        var node = new TechTree.TechNode(null, content, content.researchRequirements());
        if(node.parent != null) node.parent.children.remove(node);
        var p = TechTree.all.find(t -> t.content.name.equals(parent) || t.content.name.equals("fire-" + parent));
        if(!p.children.contains(node)) p.children.add(node);
        node.parent = p;
    }

    public static void load(){
        
        FirePlanets.risetar.techTree = nodeRoot("hzgs", fireCompany, () -> {

            node(compositeConveyor, with(new OnSector(cornerOfZero)), () -> {
                node(compositeUnloader, with(new SectorComplete(darkWorkshop)), () -> {});
                node(compositeBridgeConveyor, with(new SectorComplete(darkWorkshop)), () -> {});
                node(compositeLiquidRouter, with(new SectorComplete(darkWorkshop)), () -> {
                    node(compositeBridgeConduit, with(new SectorComplete(darkWorkshop)), () -> {});
                });
            });

            node(coreArmored, with(new OnSector(darkWorkshop)), () -> {
                node(omicron, () -> {});
            });

            node(thermalKiln, with(new OnSector(landingBase)), () -> {
                node(chopper, with(new OnSector(beachLanding)), () -> {
                    node(timberBurner, () -> {});
                    node(treeFarm, with(new SectorComplete(beachLanding)), () -> {});
                });
                node(biomassCultivator, with(new OnSector(sporeFiord)), () -> {
                    node(vapourCondenser, with(new SectorComplete(scorchingVolcano)), () -> {});
                    node(fissionDrill, with(new OnSector(darkWorkshop)), () -> {});
                });
                node(metaglassPlater, () -> {
                    node(mirrorglassPolisher, () -> {
                        node(conductorFormer, () -> {});
                    });
                });
                node(electrothermalSiliconFurnace, with(new OnSector(cornerOfZero)), () -> {
                    node(logicAlloyProcessor, with(new SectorComplete(sporeFiord)), () -> {
                        node(detonationMixer, () -> {});
                    });
                });
                node(impurityKindlingExtractor, with(new OnSector(cornerOfZero)), () -> {
                    node(crusher, with(new SectorComplete(sporeFiord)), () -> {
                        node(slagCooler, with(new SectorComplete(scorchingVolcano)), () -> {});
                    });
                    node(kindlingExtractor, () -> {
                        node(liquidNitrogenCompressor, with(new SectorComplete(scorchingVolcano)), () -> {});
                        node(hardenedAlloySmelter, with(new SectorComplete(eternityRiverStronghold)), () -> {});
                    });
                });
            });

            node(conductorPowerNode, with(new SectorComplete(cornerOfZero)), () -> {
                node(campfire, with(new SectorComplete(beachLanding)), () -> {
                    node(buildingHealer, with(new OnSector(darkWorkshop)), () -> {
                        node(buildIndicator, with(new SectorComplete(darkWorkshop)), () -> {});
                    });
                    node(flameGenerator, with(new SectorComplete(chillyMountains)), () -> {});
                });
            });

            node(smasher, with(new OnSector(landingBase)), () -> {
                node(damWall, with(new OnSector(beachLanding)), () -> {
                    node(damWallLarge, () -> {});
                    node(hardenedWall, with(new SectorComplete(darkWorkshop)), () -> {});
                });
                node(nightmare, () -> {
                    node(distance, with(new SectorComplete(eternityRiverStronghold)), () -> {});
                });
                node(seaquake, with(new SectorComplete(scorchingVolcano)), () -> {});
                node(ignite, with(new OnSector(scorchingVolcano)), () -> {});
            });

            node(guarding, with(new SectorComplete(darksandPlain)), () -> {
                node(resisting, with(new SectorComplete(cornerOfZero)), () -> {
                    node(garrison, with(new SectorComplete(sporeFiord)), () -> {
                        node(shelter, with(new OnSector(darkWorkshop)), () -> {});
                    });
                });
                node(firefly, () -> {
                    node(candlelight, with(new SectorComplete(cornerOfZero)), () -> {});
                });
            });

            node(landingBase, with(new SectorComplete(SectorPresets.planetaryTerminal)), () -> {
                node(darksandPlain, with(new SectorComplete(landingBase), new Research(nightmare)), () -> {
                    node(cornerOfZero, with(new SectorComplete(darksandPlain)), () -> {
                        node(beachLanding, with(new SectorComplete(cornerOfZero)), () -> {
                            node(darkWorkshop, with(new SectorComplete(beachLanding), new Research(garrison)), () -> {});
                        });
                    });
                    node(sporeFiord, with(new SectorComplete(darksandPlain)), () -> {
                        node(scorchingVolcano, with(new SectorComplete(sporeFiord), new Research(compositeConveyor)), () -> {});
                    });
                    node(eternityRiverStronghold, with(new SectorComplete(darksandPlain), new Research(guarding)), () -> {
                        node(chillyMountains, with(new SectorComplete(eternityRiverStronghold), new Produce(conductor)), () -> {});
                    });
                });
            });

            node(glass, () -> {
                node(mirrorglass, () -> {
                    node(conductor, () -> {});
                });
                node(timber, () -> {});
                node(flamefluidCrystal, () -> {});
                node(logicAlloy, () -> {
                    node(detonationCompound, () -> {});
                });
                node(impurityKindlingAlloy, () -> {
                    node(kindlingAlloy, () -> {
                        node(liquidNitrogen, () -> {});
                        node(hardenedAlloy, () -> {});
                    });
                });
            });
        });

        add(UnitTypes.alpha, "air-factory");
        add(UnitTypes.beta, "alpha");
    }
}