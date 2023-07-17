const
    nodeRoot = TechTree.nodeRoot,
    node = TechTree.node,
    nodeProduce = TechTree.nodeProduce,
    SectorComplete = Objectives.SectorComplete,
    OnSector = Objectives.OnSector,
    Research = Objectives.Research,
    Produce = Objectives.Produce,
    blocks = require("blocks"),
    items = require("items"),
    liquids = require("liquids"),
    planets = require("planets"),
    units = require("units");



planets.lisertar.techTree = nodeRoot("hzgs", blocks.fireCompany, () => {

    node(blocks.compositeConveyor, Seq.with(new OnSector(planets.cornerOfZero)), () => {
        node(blocks.compositeUnloader, Seq.with(new SectorComplete(planets.darkWorkshop)), () => {});
        node(blocks.compositeBridgeConveyor, Seq.with(new SectorComplete(planets.darkWorkshop)), () => {});
        node(blocks.compositeLiquidRouter, Seq.with(new SectorComplete(planets.darkWorkshop)), () => {
            node(blocks.compositeBridgeConduit, Seq.with(new SectorComplete(planets.darkWorkshop)), () => {})
        })
    });

    node(blocks.coreArmored, Seq.with(new OnSector(planets.darkWorkshop)), () => {
        node(units.omicron, () => {})
    });

    node(blocks.thermalKiln, Seq.with(new OnSector(planets.landingBase)), () => {
        node(blocks.chopper, Seq.with(new OnSector(planets.beachLanding)), () => {
            node(blocks.timberBurner, () => {});
            node(blocks.treeFarm, Seq.with(new SectorComplete(planets.beachLanding)), () => {})
        });
        node(blocks.biomassCultivator, Seq.with(new OnSector(planets.sporeFiord)), () => {
            node(blocks.vapourCondenser, Seq.with(new SectorComplete(planets.scorchingVolcano)), () => {});
            node(blocks.fissionDrill, Seq.with(new OnSector(planets.darkWorkshop)), () => {})
        });
        node(blocks.metaglassPlater, () => {
            node(blocks.mirrorglassPolisher, () => {
                node(blocks.conductorFormer, () => {})
            })
        });
        node(blocks.electrothermalSiliconFurnace, Seq.with(new OnSector(planets.cornerOfZero)), () => {
            node(blocks.logicAlloyProcessor, Seq.with(new SectorComplete(planets.sporeFiord)), () => {
                node(blocks.detonationMixer, () => {})
            })
        });
        node(blocks.impurityKindlingExtractor, Seq.with(new OnSector(planets.cornerOfZero)), () => {
            node(blocks.crusher, Seq.with(new SectorComplete(planets.sporeFiord)), () => {
                node(blocks.slagCooler, Seq.with(new SectorComplete(planets.scorchingVolcano)), () => {})
            });
            node(blocks.kindlingExtractor, () => {
                node(blocks.liquidNitrogenCompressor, Seq.with(new SectorComplete(planets.scorchingVolcano)), () => {});
                node(blocks.hardenedAlloySmelter, Seq.with(new SectorComplete(planets.eternalRiverStronghold)), () => {})
            })
        })
    });

    node(blocks.conductorPowerNode, Seq.with(new SectorComplete(planets.cornerOfZero)), () => {
        node(blocks.campfire, Seq.with(new SectorComplete(planets.beachLanding)), () => {
            node(blocks.buildingHealer, Seq.with(new OnSector(planets.darkWorkshop)), () => {
                node(blocks.buildIndicator, Seq.with(new SectorComplete(planets.darkWorkshop)), () => {})
            });
            node(blocks.flameGenerator, Seq.with(new SectorComplete(planets.chillyMountains)), () => {})
        })
    });

    node(blocks.smasher, Seq.with(new OnSector(planets.landingBase)), () => {
        node(blocks.damWall, Seq.with(new OnSector(planets.beachLanding)), () => {
            node(blocks.damWallLarge, () => {});
            node(blocks.hardenedWall, Seq.with(new SectorComplete(planets.darkWorkshop)), () => {})
        });
        node(blocks.nightmare, () => {
            node(blocks.distance, Seq.with(new SectorComplete(planets.eternalRiverStronghold)), () => {})
        });
        node(blocks.seaquake, Seq.with(new SectorComplete(planets.scorchingVolcano)), () => {});
        node(blocks.ignite, Seq.with(new OnSector(planets.scorchingVolcano)), () => {})
    });

    node(units.guarding, Seq.with(new SectorComplete(planets.darksandPlain)), () => {
        node(units.resisting, Seq.with(new SectorComplete(planets.cornerOfZero)), () => {
            node(units.garrison, Seq.with(new SectorComplete(planets.sporeFiord)), () => {
                node(units.shelter, Seq.with(new OnSector(planets.darkWorkshop)), () => {})
            })
        });
        node(units.firefly, () => {
            node(units.candlelight, Seq.with(new SectorComplete(planets.cornerOfZero)), () => {})
        })
    });

    node(planets.landingBase, Seq.with(new SectorComplete(SectorPresets.planetaryTerminal)), () => {
        node(planets.darksandPlain, Seq.with(new SectorComplete(planets.landingBase), new Research(blocks.nightmare)), () => {
            node(planets.cornerOfZero, Seq.with(new SectorComplete(planets.darksandPlain)), () => {
                node(planets.beachLanding, Seq.with(new SectorComplete(planets.cornerOfZero)), () => {
                    node(planets.darkWorkshop, Seq.with(new SectorComplete(planets.beachLanding), new Research(units.garrison)), () => {})
                })
            });
            node(planets.sporeFiord, Seq.with(new SectorComplete(planets.darksandPlain)), () => {
                node(planets.scorchingVolcano, Seq.with(new SectorComplete(planets.sporeFiord), new Research(blocks.compositeConveyor)), () => {})
            });
            node(planets.eternalRiverStronghold, Seq.with(new SectorComplete(planets.darksandPlain), new Research(units.guarding)), () => {
                node(planets.chillyMountains, Seq.with(new SectorComplete(planets.eternalRiverStronghold), new Produce(items.conductor)), () => {})
            })
        })
    });

    node(items.glass, () => {
        node(items.mirrorglass, () => {
            node(items.conductor, () => {})
        });
        node(items.timber, () => {});
        node(items.flamefluidCrystal, () => {});
        node(items.logicAlloy, () => {
            node(items.detonationCompound, () => {})
        });
        node(items.impurityKindlingAlloy, () => {
            node(items.kindlingAlloy, () => {
                node(liquids.liquidNitrogen, () => {});
                node(items.hardenedAlloy, () => {})
            })
        })
    })
})
