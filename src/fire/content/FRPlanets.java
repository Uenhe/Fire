package fire.content;

import arc.graphics.Color;
import fire.maps.LysettaPlanetGenerator;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Planet;

import static arc.struct.Seq.with;
import static fire.FRVars.find;
import static fire.content.FRBlocks.*;
import static fire.content.FRItems.*;
import static fire.content.FRLiquids.liquidNitrogen;
import static fire.content.FRSectorPresets.*;
import static fire.content.FRUnitTypes.*;
import static fire.ui.dialogs.InfoDialog.InfoNode.dnode;
import static mindustry.content.TechTree.*;
import static mindustry.game.Objectives.*;

public class FRPlanets{

    public static final Planet lysetta;

    static{
        lysetta = new Planet("lst", Planets.sun, 1.0f, 3){{
            meshLoader = () -> new HexMesh(this, 8);
            cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, find("5279f0bb"), 2, 0.45f, 0.9f, 0.38f),
                new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(find("5279f0bb"), 0.55f), 2, 0.45f, 1.0f, 0.41f)
            );
            generator = new LysettaPlanetGenerator();
            sectorSeed = 3;
            rotateTime = 6600.0f;
            clearSectorOnLose = true;
            prebuildBase = false;
            allowCampaignRules = true;
            atmosphereColor = find("1a3db1");
            atmosphereRadIn = 0.05f;
            atmosphereRadOut = 0.5f;
            iconColor = find("5b6fff");

            unlockedOnLand.add(fireCompany);
            ruleSetter = r -> {
                r.hideBannedBlocks = true;
                r.bannedBlocks.addAll(Blocks.launchPad, Blocks.advancedLaunchPad, Blocks.landingPad, Blocks.interplanetaryAccelerator, primaryInterplanetaryAccelerator);
            };
        }};
    }

    public static void load(){}

    public static void loadTree(){
        lysetta.techTree = nodeRoot("lysetta", lysetta, () -> {

            node(compositeConveyor, with(new OnSector(frozenGround)), () -> {
                node(compositeUnloader, with(new SectorComplete(darkWorkshop)), () -> {});
                node(compositeBridgeConveyor, with(new SectorComplete(darkWorkshop)), () ->
                    node(compositeRouter, with(new SectorComplete(darkWorkshop)), () -> {})
                );
                node(hardenedAlloyConveyor, with(new SectorComplete(desolateFortification)), () -> {});
                node(compositeLiquidRouter, with(new SectorComplete(darkWorkshop)), () ->
                    node(compositeBridgeConduit, with(new SectorComplete(darkWorkshop)), () -> {
                        node(magneticRingPump, with(new SectorComplete(branchedRivers)), () -> {});
                        node(hardenedLiquidTank, with(new SectorComplete(branchedRivers)), () -> {});
                    })
                );
            });

            node(coreBulwark, with(new OnSector(darkWorkshop)), () -> {
                node(omicron, () ->
                    node(pioneer, with(new OnSector(desolateFortification)), () -> {})
                );
                node(javelinPad, with(new SectorComplete(lavaStronghold)), () ->
                    node(javelin, () -> {})
                );
            });

            node(thermalKiln, with(new OnSector(landingBase)), () -> {
                node(chopper, with(new OnSector(beachLanding)), () -> {
                    node(timberBurner, () -> {});
                    node(treeFarm, with(new OnSector(beachLanding)), () -> {});
                });
                node(fissionDrill, with(new OnSector(darkWorkshop)), () ->
                    node(constraintExtractor, with(new OnSector(rubbleRidge)), () ->
                        node(focusingExtractor, with(new SectorComplete(rubbleRidge)), () -> {})
                ));
                node(biomassCultivator, with(new OnSector(sporeFiord)), () -> {
                    node(vapourCondenser, with(new SectorComplete(scorchingVolcano)), () -> {});
                    node(fleshSynthesizer, with(new OnSector(stormyCoast)), () -> {});
                    node(stackedCultivator, with(new OnSector(rubbleRidge)), () -> {});
                });
                node(metaglassPlater, () ->
                    node(mirrorglassPolisher, () ->
                        node(conductorFormer, with(new SectorComplete(frozenGround)), () -> {})
                    )
                );
                node(electrothermalSiliconFurnace, with(new OnSector(frozenGround)), () ->
                    node(logicAlloyProcessor, with(new SectorComplete(sporeFiord)), () ->
                        node(detonationMixer, () -> {})
                    )
                );
                node(sulflameExtractor, with(new OnSector(frozenGround)), () -> {
                    node(crusher, with(new SectorComplete(sporeFiord)), () ->
                        node(slagCooler, with(new SectorComplete(scorchingVolcano)), () -> {})
                    );
                    node(kindlingExtractor, () -> {
                        node(liquidNitrogenCompressor, with(new SectorComplete(scorchingVolcano)), () ->
                            node(cryofluidMixerLarge, with(new SectorComplete(desolateFortification)), () -> {})
                        );
                        node(hardenedAlloySmelter, with(new SectorComplete(eteriverStronghold)), () -> {
                            node(hardenedAlloyCrucible, with(new SectorComplete(desolateFortification)), () -> {});
                            node(magnetismConcentratedRollingMill, with(new SectorComplete(branchedRivers)), () -> {});
                            node(magneticAlloyFormer, with(new SectorComplete(glaciatedPeaks)), () -> {
                                node(magneticRingSynthesizer, with(new SectorComplete(taintedEstuary)), () -> {});
                                node(electromagnetismDiffuser, () -> {});
                            });
                        });
                    });
                });
            });

            node(conductorPowerNode, with(new SectorComplete(frozenGround)), () -> {
                node(hydroelectricGenerator, with(new SectorComplete(beachLanding)), () ->
                    node(hydroelectricGeneratorLarge, () -> {})
                );
                
                node(flameGenerator, with(new OnSector(chillyMountains)), () -> {});
                
                node(campfire, with(new SectorComplete(beachLanding)), () ->
                    node(buildingHealer, with(new OnSector(darkWorkshop)), () ->
                        node(buildIndicator, with(new SectorComplete(darkWorkshop)), () -> {})
                    )
                );
                node(skyDome, with(new SectorComplete(desolateFortification)), () -> {});
            });

            node(smasher, with(new OnSector(landingBase)), () -> {
                node(damWall, with(new OnSector(beachLanding)), () ->
                    node(damWallLarge, () -> {
                        node(hardenedWall, with(new SectorComplete(darkWorkshop)), () ->
                            node(hardenedWallLarge, with(new OnSector(desolateFortification)), () -> {})
                        );
                        node(fleshWall, with(new OnSector(stormyCoast)), () -> {});
                    })
                );

                node(nightmare, with(new OnSector(landingBase)), () -> {
                    node(gambler, with(new SectorComplete(darkWorkshop)), () -> {
                        node(distance, with(new SectorComplete(eteriverStronghold)), () ->
                            node(aerolite, with(new SectorComplete(desolateFortification)), () -> {})
                        );
                        node(grudge, with(new SectorComplete(desolateFortification)), () -> {});
                    });

                    node(fulmination, with(new OnSector(darksandPeakforest)), () ->
                        node(blossom, with(new SectorComplete(beachLanding)), () -> {
                            node(magneticSphere, with(new SectorComplete(glaciatedPeaks)), () -> {});
                            node(magneticDomain, with(new OnSector(taintedEstuary)), () -> {});
                        })
                    );

                    node(ignition, with(new OnSector(scorchingVolcano)), () ->
                        node(seaquake, with(new SectorComplete(scorchingVolcano)), () -> {})
                    );

                    node(scab, with(new SectorComplete(stormyCoast)), () -> {});
                });
            });

            node(unitHealer, with(new OnSector(landingBase)), () -> {
                node(guarding, with(new SectorComplete(darksandPeakforest)), () ->
                    node(resisting, with(new SectorComplete(frozenGround)), () ->
                        node(garrison, with(new SectorComplete(sporeFiord)), () ->
                            node(shelter, with(new OnSector(darkWorkshop)), () ->
                                node(blessing, with(new OnSector(glaciatedPeaks)), () -> {})
                            )
                        )
                    )
                );

                node(firefly, () ->
                    node(candlight, with(new SectorComplete(frozenGround)), () ->
                        node(lampryo, with(new SectorComplete(chillyMountains)), () ->
                            node(lumiflame, with(new SectorComplete(branchedRivers)), () -> {})
                        )
                    )
                );

                node(fleshReconstructor, with(new SectorComplete(stormyCoast)), () ->
                    node(blade, () ->
                        node(hatchet, with(new SectorComplete(branchedRivers)), () ->
                            node(castle, with(new SectorComplete(taintedEstuary)), () -> {})
                        )
                    )
                );

                node(payloadConveyorLarge, with(new SectorComplete(scorchingVolcano)), () ->
                    node(payloadRouterLarge)
                );
            });

            node(landingBase, with(new SectorComplete(SectorPresets.planetaryTerminal), new Research(primaryInterplanetaryAccelerator), new Research(fireCompany)), () ->
                node(darksandPeakforest, with(new SectorComplete(landingBase), new Research(nightmare)), () -> {
                    node(frozenGround, with(new SectorComplete(darksandPeakforest)), () ->
                        node(beachLanding, with(new SectorComplete(frozenGround)), () ->
                            node(darkWorkshop, with(new SectorComplete(beachLanding), new Research(garrison)), () ->
                                node(desolateFortification, with(new SectorComplete(darkWorkshop), new Research(distance), new Research(flameGenerator)), () ->
                                    node(glaciatedPeaks, () -> {
                                    })
                                )
                            )
                        )
                    );

                    node(sporeFiord, with(new SectorComplete(darksandPeakforest)), () ->
                        node(scorchingVolcano, with(new SectorComplete(sporeFiord), new Research(compositeConveyor)), () ->
                            node(lavaStronghold, with(new SectorComplete(scorchingVolcano), new SectorComplete(desolateFortification), new Research(skyDome)), () -> {
                            })
                        )
                    );

                    node(eteriverStronghold, with(new SectorComplete(darksandPeakforest), new Research(guarding)), () -> {
                        node(chillyMountains, with(new SectorComplete(eteriverStronghold), new Produce(conductor)), () -> {
                        });
                        node(stormyCoast, with(new SectorComplete(eteriverStronghold), new Research(biomassCultivator), new Research(seaquake), new Research(distance), new Research(grudge)), () -> {
                        });
                        node(branchedRivers, with(new SectorComplete(eteriverStronghold), new SectorComplete(stormyCoast), new Research(hydroelectricGenerator), new Research(hydroelectricGeneratorLarge)), () -> {
                            node(rubbleRidge, with(
                                new SectorComplete(branchedRivers), new Research(aerolite), new Research(magneticSphere), new Research(magneticRingPump), new Research(hardenedLiquidTank),
                                new Research(cryofluidMixerLarge), new Research(magnetismConcentratedRollingMill), new Research(javelinPad), new Research(javelin)
                            ), () -> {});
                            node(taintedEstuary, with(new SectorComplete(branchedRivers), new Research(aerolite), new Research(magneticRingPump), new Research(hardenedLiquidTank), new Research(cryofluidMixerLarge), new Research(magnetismConcentratedRollingMill)), () -> {
                            });
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
            addResearch(Liquids.neoplasm, flesh);
            addResearch(primaryInterplanetaryAccelerator, Blocks.landingPad);

            /* ======== InfoNodes below ======== */

            dnode(fireCompany, true, () ->

                dnode(landingBase, () -> {
                    dnode(smasher);
                    dnode(nightmare);

                    dnode(darksandPeakforest, () -> {

                        dnode(frozenGround, () -> {
                            dnode(compositeConveyor);
                            dnode(electrothermalSiliconFurnace);

                            dnode(beachLanding, () -> {
                                dnode(blossom);
                                dnode(aerolite);

                                dnode(darkWorkshop, () -> {
                                    dnode(compositeUnloader);
                                    dnode(compositeBridgeConveyor);
                                    dnode(compositeRouter);
                                    dnode(compositeLiquidRouter);
                                    dnode(compositeBridgeConduit);
                                    dnode(gambler);

                                    dnode(desolateFortification, () -> {
                                        dnode(hardenedAlloyCrucible);
                                        dnode(grudge);
                                        dnode(pioneer);

                                        dnode(glaciatedPeaks, () ->
                                            dnode(magneticSphere));
                                    });
                                });
                            });
                        });

                        dnode(sporeFiord, () -> {
                            dnode(biomassCultivator);

                            dnode(scorchingVolcano, () -> {
                                dnode(slagCooler);
                                dnode(seaquake);

                                dnode(lavaStronghold, () -> {});
                            });
                        });

                        dnode(eteriverStronghold, () -> {
                            dnode(hardenedAlloy);
                            dnode(distance);

                            dnode(chillyMountains, () ->
                                dnode(flameGenerator)
                            );

                            dnode(stormyCoast, () -> {
                                dnode(flesh);
                                dnode(scab);
                            });
                        });
                    });
                })
            );
        });

        Planets.serpulo.techTree.addPlanet(lysetta);
        lysetta.techTree.addPlanet(Planets.serpulo);
    }

    /** From Creator Mod. */
    private static void addResearch(UnlockableContent added, UnlockableContent parent){
        var lastNode = TechTree.all.find(t -> t.content == added);
        if(lastNode != null) lastNode.remove();

        var node = new TechTree.TechNode(null, added, added.researchRequirements());
        if(node.parent != null) node.parent.children.remove(node);

        var p = TechTree.all.find(t -> t.content == parent);
        if(!p.children.contains(node)) p.children.add(node);

        node.parent = p;
    }
}
