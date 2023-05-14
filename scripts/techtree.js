const nodeRoot = TechTree.nodeRoot
const node = TechTree.node
const nodeProduce = TechTree.nodeProduce
const FireBlocks = require('blocks')
const FireItems = require('items')
const FireLiquids = require('liquids')
const FirePlanets = require('planets')
const FireUnits = require('units')

FirePlanets.lisertar.techTree = nodeRoot('hzgs', FireBlocks.fireCompany, () => {

	node(FireBlocks.compositeConveyor, Seq.with(new Objectives.OnSector(FirePlanets.cornerOfZero)), () => {
		node(FireBlocks.compositeUnloader, Seq.with(new Objectives.SectorComplete(FirePlanets.darkWorkshop)), () => {})
		node(FireBlocks.compositeBridgeConveyor, Seq.with(new Objectives.SectorComplete(FirePlanets.darkWorkshop)), () => {})
		node(FireBlocks.compositeLiquidRouter, Seq.with(new Objectives.SectorComplete(FirePlanets.darkWorkshop)), () => {
			node(FireBlocks.compositeBridgeConduit, Seq.with(new Objectives.SectorComplete(FirePlanets.darkWorkshop)), () => {})
		})
	})

	node(FireBlocks.coreArmored, Seq.with(new Objectives.OnSector(FirePlanets.darkWorkshop)), () => {
		node(FireUnits.gnj, () => {})
	})

	node(FireBlocks.thermalKiln, Seq.with(new Objectives.OnSector(FirePlanets.landingBase)), () => {
		node(FireBlocks.chopper, Seq.with(new Objectives.OnSector(FirePlanets.beachLanding)), () => {
			node(FireBlocks.timberBurner, () => {})
			node(FireBlocks.treeFarm, Seq.with(new Objectives.SectorComplete(FirePlanets.beachLanding)), () => {})
		})
		node(FireBlocks.biomassCultivator, Seq.with(new Objectives.OnSector(FirePlanets.sporeFiord)), () => {
			node(FireBlocks.vapourCondenser, Seq.with(new Objectives.SectorComplete(FirePlanets.scorchingVolcano)), () => {})
			node(FireBlocks.fissionDrill, Seq.with(new Objectives.OnSector(FirePlanets.darkWorkshop)), () => {})
		})
		node(FireBlocks.metaglassPlater, () => {
			node(FireBlocks.mirrorglassPolisher, () => {
				node(FireBlocks.conductorFormer, () => {})
			})
		})
		node(FireBlocks.electrothermalSiliconFurnace, Seq.with(new Objectives.OnSector(FirePlanets.cornerOfZero)), () => {
			node(FireBlocks.logicAlloyProcessor, Seq.with(new Objectives.SectorComplete(FirePlanets.sporeFiord)), () => {
				node(FireBlocks.detonationMixer, () => {})
			})
		})
		node(FireBlocks.impurityKindlingExtractor, Seq.with(new Objectives.OnSector(FirePlanets.cornerOfZero)), () => {
			node(FireBlocks.crusher, Seq.with(new Objectives.SectorComplete(FirePlanets.sporeFiord)), () => {
				node(FireBlocks.slagCooler, Seq.with(new Objectives.SectorComplete(FirePlanets.scorchingVolcano)), () => {})
			})
			node(FireBlocks.kindlingExtractor, () => {
				node(FireBlocks.liquidNitrogenCompressor, Seq.with(new Objectives.SectorComplete(FirePlanets.scorchingVolcano)), () => {})
				node(FireBlocks.hardenedAlloySmelter, Seq.with(new Objectives.SectorComplete(FirePlanets.eternalriverStronghold)), () => {})
			})
		})
	})

	node(FireBlocks.conductorPowerNode, Seq.with(new Objectives.SectorComplete(FirePlanets.cornerOfZero)), () => {
		node(FireBlocks.campfire, Seq.with(new Objectives.SectorComplete(FirePlanets.beachLanding)), () => {
			node(FireBlocks.buildingHealer, Seq.with(new Objectives.OnSector(FirePlanets.darkWorkshop)), () => {
				node(FireBlocks.buildIndicator, Seq.with(new Objectives.SectorComplete(FirePlanets.darkWorkshop)), () => {})
			})
			node(FireBlocks.flameGenerator, Seq.with(new Objectives.SectorComplete(FirePlanets.chillyMountains)), () => {})
		})
	})

	node(FireBlocks.smasher, Seq.with(new Objectives.OnSector(FirePlanets.landingBase)), () => {
		node(FireBlocks.damWall, Seq.with(new Objectives.OnSector(FirePlanets.beachLanding)), () => {
			node(FireBlocks.damWallLarge, () => {})
			node(FireBlocks.hardenedWall, Seq.with(new Objectives.SectorComplete(FirePlanets.darkWorkshop)), () => {})
		})
		node(FireBlocks.nightmare, () => {
			node(FireBlocks.distance, Seq.with(new Objectives.SectorComplete(FirePlanets.eternalriverStronghold)), () => {})
		})
		node(FireBlocks.seaquake, Seq.with(new Objectives.SectorComplete(FirePlanets.scorchingVolcano)), () => {})
		node(FireBlocks.ignite, Seq.with(new Objectives.OnSector(FirePlanets.scorchingVolcano)), () => {})
	})

	node(FireUnits.sh, Seq.with(new Objectives.SectorComplete(FirePlanets.darksandPlain)), () => {
		node(FireUnits.ky, Seq.with(new Objectives.SectorComplete(FirePlanets.cornerOfZero)), () => {
			node(FireUnits.ws, Seq.with(new Objectives.SectorComplete(FirePlanets.sporeFiord)), () => {
				node(FireUnits.bh, Seq.with(new Objectives.OnSector(FirePlanets.darkWorkshop)), () => {})
			})
		})
		node(FireUnits.firefly, () => {
			node(FireUnits.candlelight, Seq.with(new Objectives.SectorComplete(FirePlanets.cornerOfZero)), () => {})
		})
	})

	node(FirePlanets.landingBase, Seq.with(new Objectives.SectorComplete(SectorPresets.planetaryTerminal)), () => {
		node(FirePlanets.darksandPlain, Seq.with(new Objectives.SectorComplete(FirePlanets.landingBase), new Objectives.Research(FireBlocks.nightmare)), () => {
			node(FirePlanets.cornerOfZero, Seq.with(new Objectives.SectorComplete(FirePlanets.darksandPlain)), () => {
				node(FirePlanets.beachLanding, Seq.with(new Objectives.SectorComplete(FirePlanets.cornerOfZero)), () => {
					node(FirePlanets.darkWorkshop, Seq.with(new Objectives.SectorComplete(FirePlanets.beachLanding), new Objectives.Research(FireUnits.ws)), () => {})
				})
			})
			node(FirePlanets.sporeFiord, Seq.with(new Objectives.SectorComplete(FirePlanets.darksandPlain)), () => {
				node(FirePlanets.scorchingVolcano, Seq.with(new Objectives.SectorComplete(FirePlanets.sporeFiord), new Objectives.Research(FireBlocks.compositeConveyor)), () => {})
			})
			node(FirePlanets.eternalriverStronghold, Seq.with(new Objectives.SectorComplete(FirePlanets.darksandPlain), new Objectives.Research(FireUnits.sh)), () => {
				node(FirePlanets.chillyMountains, Seq.with(new Objectives.SectorComplete(FirePlanets.eternalriverStronghold), new Objectives.Produce(FireItems.conductor)), () => {})
			})
		})
	})

	node(FireItems.glass, () => {
		node(FireItems.mirrorglass, () => {
			node(FireItems.conductor, () => {})
		})
		node(FireItems.timber, () => {})
		node(FireItems.flamefluidCrystal, () => {})
		node(FireItems.logicAlloy, () => {
			node(FireItems.detonationCompound, () => {})
		})
		node(FireItems.impurityKindlingAlloy, () => {
			node(FireItems.kindlingAlloy, () => {
				node(FireLiquids.liquidNitrogen, () => {})
				node(FireItems.hardenedAlloy, () => {})
			})
		})
	})
})
