package fire.content;

import arc.graphics.Color;
import fire.world.planets.RisetarPlanetGenerator;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Planet;

import static arc.struct.Seq.with;
import static fire.content.FBlocks.*;
import static fire.content.FItems.*;
import static fire.content.FLiquids.*;
import static fire.content.FSectorPresets.*;
import static fire.content.FUnitTypes.*;
import static fire.ui.dialogs.InfoDialog.InfoNode.dnode;
import static mindustry.content.TechTree.*;
import static mindustry.game.Objectives.*;

public class FPlanets{

    public static Planet risetar;

    public static void load(){
        risetar = new Planet("lst", Planets.sun, 1.0f, 3){
            {
                meshLoader = () -> new HexMesh(this, 8);
                cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, Color.valueOf("5279f0bb"), 2, 0.45f, 0.9f, 0.38f),
                    new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(Color.valueOf("5279f0bb"), 0.55f), 2, 0.45f, 1.0f, 0.41f)
                );
                generator = new RisetarPlanetGenerator();
                rotateTime = 7200.0f;
                clearSectorOnLose = true;
                prebuildBase = false;
                sectorSeed = 3;
                atmosphereColor = Color.valueOf("1a3db1");
                atmosphereRadIn = 0.05f;
                atmosphereRadOut = 0.5f;
                iconColor = Color.valueOf("5b6fff");
                accessible = false;
                hiddenItems.addAll(Items.erekirItems).removeAll(Items.serpuloItems);
            }
        };
    }

    public static void loadTree(){

        risetar.techTree = nodeRoot("hzgs", fireCompany, () -> {

            node(compositeConveyor, with(new OnSector(frozenGround)), () -> {
                node(compositeUnloader, with(new SectorComplete(darkWorkshop)), () -> {
                });
                node(compositeBridgeConveyor, with(new SectorComplete(darkWorkshop)), () -> {
                });
                node(hardenedAlloyConveyor, with(new SectorComplete(desolateFortification)), () -> {
                });
                node(compositeLiquidRouter, with(new SectorComplete(darkWorkshop)), () ->
                    node(compositeBridgeConduit, with(new SectorComplete(darkWorkshop)), () -> {
                    })
                );
            });

            node(coreArmored, with(new OnSector(darkWorkshop)), () -> {
                node(omicron, () ->
                    node(pioneer, with(new OnSector(desolateFortification)), () -> {
                    })
                );
                node(javelinPad, with(new SectorComplete(lavaStronghold)), () ->
                    node(javelin, () -> {
                    })
                );
            });

            node(thermalKiln, with(new OnSector(landingBase)), () -> {
                node(chopper, with(new OnSector(beachLanding)), () -> {
                    node(timberBurner, () -> {
                    });
                    node(treeFarm, with(new SectorComplete(beachLanding)), () -> {
                    });
                });
                node(biomassCultivator, with(new OnSector(sporeFiord)), () -> {
                    node(vapourCondenser, with(new SectorComplete(scorchingVolcano)), () -> {
                    });
                    node(fissionDrill, with(new OnSector(darkWorkshop)), () -> {
                    });
                    node(fleshSynthesizer, with(new SectorComplete(stormyCoast)), () -> {
                    });
                });
                node(metaglassPlater, () ->
                    node(mirrorglassPolisher, () ->
                        node(conductorFormer, () -> {
                        })
                    )
                );
                node(electrothermalSiliconFurnace, with(new OnSector(frozenGround)), () ->
                    node(logicAlloyProcessor, with(new SectorComplete(sporeFiord)), () ->
                        node(detonationMixer, () -> {
                        })
                    )
                );
                node(sulflameExtractor, with(new OnSector(frozenGround)), () -> {
                    node(crusher, with(new SectorComplete(sporeFiord)), () ->
                        node(slagCooler, with(new SectorComplete(scorchingVolcano)), () -> {
                        })
                    );
                    node(kindlingExtractor, () -> {
                        node(liquidNitrogenCompressor, with(new SectorComplete(scorchingVolcano)), () -> {
                        });
                        node(hardenedAlloySmelter, with(new SectorComplete(eteriverStronghold)), () -> {
                            node(hardenedAlloyCrucible, with(new SectorComplete(desolateFortification)), () -> {
                            });
                            node(magneticAlloyFormer, with(new SectorComplete(glaciatedPeaks)), () ->
                                node(electromagnetismDiffuser, () -> {
                                })
                            );
                        });
                    });
                });
            });

            node(conductorPowerNode, with(new SectorComplete(frozenGround)), () -> {
                node(flameGenerator, with(new SectorComplete(chillyMountains)), () -> {
                });
                node(campfire, with(new SectorComplete(beachLanding)), () ->
                    node(buildingHealer, with(new OnSector(darkWorkshop)), () ->
                        node(buildIndicator, with(new SectorComplete(darkWorkshop)), () -> {
                        })
                    )
                );
                node(skyDome, with(new SectorComplete(desolateFortification)), () -> {
                });
            });

            node(smasher, with(new OnSector(landingBase)), () -> {
                node(damWall, with(new OnSector(beachLanding)), () ->
                    node(damWallLarge, () -> {
                        node(hardenedWall, with(new SectorComplete(darkWorkshop)), () ->
                            node(hardenedWallLarge, with(new OnSector(desolateFortification)), () -> {
                            })
                        );
                        node(fleshWall, with(new Produce(flesh), new OnSector(stormyCoast)), () -> {
                        });
                    })
                );

                node(nightmare, with(new OnSector(landingBase)), () -> {
                    node(blossom, with(new SectorComplete(beachLanding)), () ->
                        node(gambler, with(new SectorComplete(darkWorkshop)), () -> {
                        })
                    );
                    node(distance, with(new SectorComplete(eteriverStronghold)), () ->
                        node(grudge, with(new SectorComplete(desolateFortification)), () -> {
                        })
                    );
                });

                node(ignition, with(new OnSector(scorchingVolcano)), () ->
                    node(seaquake, with(new SectorComplete(scorchingVolcano)), () -> {
                    })
                );

                node(magneticSphere, with(new SectorComplete(glaciatedPeaks)), () -> {
                });
            });

            node(guarding, with(new SectorComplete(darksandPlain)), () -> {
                node(resisting, with(new SectorComplete(frozenGround)), () ->
                    node(garrison, with(new SectorComplete(sporeFiord)), () ->
                        node(shelter, with(new OnSector(darkWorkshop)), () ->
                            node(blessing, with(new OnSector(glaciatedPeaks)), () -> {
                            }))
                    )
                );

                node(firefly, () ->
                    node(candlelight, with(new SectorComplete(frozenGround)), () ->
                        node(lampflame, with(new SectorComplete(chillyMountains)), () -> {
                        }))
                );

                node(fleshReconstructor, with(new SectorComplete(stormyCoast), new Produce(flesh)), () ->
                    node(blade, () -> {
                    })
                );
            });

            node(landingBase, with(new SectorComplete(SectorPresets.planetaryTerminal)), () ->
                node(darksandPlain, with(new SectorComplete(landingBase), new Research(nightmare)), () -> {
                    node(frozenGround, with(new SectorComplete(darksandPlain)), () ->
                        node(beachLanding, with(new SectorComplete(frozenGround)), () ->
                            node(darkWorkshop, with(new SectorComplete(beachLanding), new Research(garrison)), () ->
                                node(desolateFortification, with(new SectorComplete(darkWorkshop), new Research(distance)), () ->
                                    node(glaciatedPeaks, () -> {
                                    })
                                )
                            )
                        )
                    );

                    node(sporeFiord, with(new SectorComplete(darksandPlain)), () ->
                        node(scorchingVolcano, with(new SectorComplete(sporeFiord), new Research(compositeConveyor)), () ->
                            node(lavaStronghold, with(new SectorComplete(scorchingVolcano), new SectorComplete(desolateFortification), new Research(skyDome)), () -> {
                            })
                        )
                    );

                    node(eteriverStronghold, with(new SectorComplete(darksandPlain), new Research(guarding)), () -> {
                        node(chillyMountains, with(new SectorComplete(eteriverStronghold), new Produce(conductor)), () -> {
                        });
                        node(stormyCoast, with(new SectorComplete(eteriverStronghold), new Research(biomassCultivator), new Research(seaquake), new Research(distance), new Research(grudge)), () -> {
                        });
                    });
                })
            );

            nodeProduce(glass, () -> {
                nodeProduce(mirrorglass, () ->
                    nodeProduce(conductor, () -> {
                    })
                );
                nodeProduce(timber, () -> {
                });
                nodeProduce(flamefluidCrystal, () -> {
                });
                nodeProduce(logicAlloy, () ->
                    nodeProduce(detonationCompound, () -> {
                    })
                );
                nodeProduce(sulflameAlloy, () ->
                    nodeProduce(kindlingAlloy, () -> {
                        nodeProduce(liquidNitrogen, () -> {
                        });
                        nodeProduce(hardenedAlloy, () ->
                            nodeProduce(magneticAlloy, () -> {
                            })
                        );
                    })
                );
                nodeProduce(flesh, () -> {
                });
            });

            addResearch(UnitTypes.alpha, Blocks.airFactory);
            addResearch(UnitTypes.beta, UnitTypes.alpha);

            /* ======== InfoNodes below ======== */

            dnode(fireCompany, true, () ->

                dnode(landingBase, () -> {
                    dnode(smasher);
                    dnode(nightmare);

                    dnode(darksandPlain, () -> {

                        dnode(frozenGround, () -> {
                            dnode(compositeConveyor);
                            dnode(electrothermalSiliconFurnace);

                            dnode(beachLanding, () -> {
                                dnode(blossom);

                                dnode(darkWorkshop, () -> {
                                    dnode(compositeUnloader);
                                    dnode(compositeBridgeConveyor);
                                    dnode(compositeLiquidRouter);
                                    dnode(compositeBridgeConduit);
                                    dnode(gambler);

                                    dnode(desolateFortification, () -> {
                                        dnode(hardenedAlloyCrucible);
                                        dnode(grudge);
                                        dnode(pioneer);

                                        dnode(glaciatedPeaks, () -> {
                                        });
                                    });
                                });
                            });
                        });

                        dnode(sporeFiord, () -> {
                            dnode(biomassCultivator);

                            dnode(scorchingVolcano, () -> {
                                dnode(slagCooler);
                                dnode(seaquake);

                                dnode(lavaStronghold, () -> {
                                });
                            });
                        });

                        dnode(eteriverStronghold, () -> {
                            dnode(distance);

                            dnode(chillyMountains, () ->
                                dnode(flameGenerator)
                            );
                            dnode(stormyCoast, () ->
                                dnode(flesh)
                            );
                        });
                    });
                })
            );
        });

    }

    private static void addResearch(UnlockableContent content, UnlockableContent parent){

        var lastNode = TechTree.all.find(t -> t.content == content);
        if(lastNode != null) lastNode.remove();

        var node = new TechTree.TechNode(null, content, content.researchRequirements());
        if(node.parent != null) node.parent.children.remove(node);

        var p = TechTree.all.find(t -> t.content == parent);
        if(!p.children.contains(node)) p.children.add(node);

        node.parent = p;

    }
}
