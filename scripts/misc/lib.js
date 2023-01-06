// Copyright (C) 2020 abomb4
//
// This file is part of Dimension Shard.
//
// Dimension Shard is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Dimension Shard is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Dimension Shard.  If not, see <http://www.gnu.org/licenses/>.

//---------------------@滞人编写

//代码来自创世神mod

exports.modName = "fire"

exports.newEffectt = (lifetime, renderer) => new Effect(lifetime, cons(renderer));
exports.techDsAvailable = () =>
    Vars.state == null
    || Vars.state.rules.infiniteResources
    || !Vars.player
    //|| Vars.player.team().cores().find(boolf(v => v.block.name == lib.modName + "-dimension-technology-core"))
    //|| Vars.player.team().cores().find(boolf(v => v.block.name == lib.modName + "-dimension-technology-core-3"))
exports.mod = Vars.mods.locateMod(exports.modName);
exports.raycaster = (func) => new Geometry.Raycaster({ accept: func });
exports.isDev = () => exports.mod.meta.version.indexOf("dev") >= 0;
exports.addToResearch = (content, research) => {
    if (!content) {
        throw new Error('子内容为空!,');
    }
    if (!research.parent) {
        throw new Error('研究。父母为空!');
    }
    var researchName = research.parent;
    var customRequirements = research.requirements;
    var objectives = research.objectives;

    var lastNode = TechTree.all.find(boolf(t => t.content == content));
    if (lastNode != null) {
        lastNode.remove();
    }

    var node = new TechTree.TechNode(null, content, customRequirements !== undefined ? customRequirements : content.researchRequirements());
    var currentMod = exports.mod;
    if (objectives) {
        node.objectives.addAll(objectives);
    }

    if (node.parent != null) {
        node.parent.children.remove(node);
    }

    // find parent node.
    var parent = TechTree.all.find(boolf(t => t.content.name.equals(researchName) || t.content.name.equals(currentMod.name + "-" + researchName)));

    // if (parent == null) {
    //     throw new Error("Content '" + researchName + "' isn't in the tech tree, but '" + content.name + "' requires it to be researched.");
    // }
    if (parent == null) {
        throw new Error("'内容 '" + researchName + "' 不在科技树上, 但 '" + content.name + "'需要对其研究.");
    }
    // add this node to the parent
    if (!parent.children.contains(node)) {
        parent.children.add(node);
    }
    // reparent the node
    node.parent = parent;
};
exports.setBuildingSimple = function (blockType, buildingType, overrides) {
    blockType.buildType = prov(() => new JavaAdapter(buildingType, overrides, blockType));
};
exports.setBuildingSimple2 = function (blockType, buildingType, overridesGetter) {
    blockType.buildType = prov(() => new JavaAdapter(buildingType, overridesGetter(blockType), blockType));
};

// exports.modName = "dimension-shard";

// exports.mod = Vars.mods.locateMod(exports.modName);
exports.newEffect = (lifetime, renderer) => new Effect(lifetime, cons(renderer));
exports.cons2 = (func) => new Cons2({ get: (v1, v2) => func(v1, v2) });
exports.floatc2 = (func) => new Floatc2({ get: (v1, v2) => func(v1, v2) });
exports.boolf2 = (func) => new Boolf2({ get: (v1, v2) => func(v1, v2) });
exports.func = (getter) => new Func({ get: getter });
exports.raycaster = (func) => new Geometry.Raycaster({ accept: func });
exports.intc = (func) => new Intc({ get: func });
exports.intc2 = (func) => new Intc2({ get: func });
exports.floatf = (func) => new Floatf({ get: func });
exports.cons2 = (func) => new Cons2({ get: (v1, v2) => func(v1, v2) });
exports.loadRegion = (name) => Vars.headless ? null : Core.atlas.find(exports.modName + '-' + name, Core.atlas.find("clear"));
exports.int = (v) => new java.lang.Integer(v);
const loadRegionCache = {};
exports.loadRegion2 = (name) => {
    if (Vars.headless === true) {
        return null
    }
    var c = loadRegionCache[name]
    if (c) {
        return c
    }
    c = Core.atlas.find(exports.modName + '-' + name, Core.atlas.find("error"))
    print('find ' + exports.modName + '-' + name + ' result: ' + c)
    loadRegionCache[name] = c
    return c
};
/**
 * Get message from bundle, 'type.mod-name.key'
 * @param {string} type the prefix such as block, unit, mech
 * @param {string} key  the suffix
 * @param {string} msgs  messages
 */
exports.getMessage = (type, key, msgs) =>
    Vars.headless
        ? ''
        : Core.bundle.format(type + "." + exports.modName + "." + key, msgs || []);

/** Cannot use java.lang.reflect.Array, but Arrays.copyOf available! Lucky! */
exports.createUnitPlan = (unitFrom, unitTo) => {
    var a = java.util.Arrays.copyOf(Blocks.tetrativeReconstructor.upgrades.get(0), 2);
    a[0] = unitFrom;
    a[1] = unitTo;
    return a;
}

/**
 * Add content to research tree
 *
 * @param {Content} content Such as Block, Turret
 * @param {{parent: string, requirements: ItemStack[], objectives: Seq<Objective>}} research
 *        Research Info is an object with parent and requirements
 */

exports.objectivePlanetaryTerminalActivated = (() => {
    const SETTING_KEY = 'planetary-terminal-activated';
    const objective = new Objectives.Objective({
        complete() { return Core.settings.getBool(SETTING_KEY, false); },
        display() { return exports.getMessage("msg", "planetaryTerminalActivated"); }
    });
    function contentUnlocked(content) {
        if (typeof content.unlocked === "boolean") {
            return content.unlocked;
        } else {
            return content.unlocked();
        }
    }
    Events.run(Trigger.acceleratorUse, run(() => {
        // Control.java void checkAutoUnlcoks
        if (Vars.net.client()) return;
        Core.settings.put(SETTING_KEY, true)
        TechTree.all.each(cons(node => {
            if (!contentUnlocked(node.content) && node.requirements.length == 0 && !node.objectives.contains(boolf(o => !o.complete()))) {
                node.content.unlock();
            }
        }));
    }));
    return objective;
})();

exports.cons2 = (func) => new Cons2({
    get: (v1, v2) => func(v1, v2)
});



/**
 * lib.setBuilding(extend(CoreBlock, "my-core", {}), () => extend(CoreBlock.CoreBuilding, {}))
 *
 * @param {Block} blockType The block type
 * @param {(block: Block) => Building} buildingCreator
 *        A function receives block type, return Building instance;
 *        don't use prov (this function will use prov once)
 */
exports.setBuilding = function (blockType, buildingCreator) {
    blockType.buildType = prov(() => buildingCreator(blockType));
}

/**
 * lib.setBuildingSimple(extend(CoreBlock, "my-core", {}), CoreBlock.CoreBuilding, {})
 *
 * @param {Block} blockType The block type
 * @param {Class<Building>} buildingType The building type
 * @param {Object} overrides Object that as second parameter of extend()
 */


/** Random item picker, use add() to add items with integer probability */
exports.createProbabilitySelector = function () {
    const objects = [];
    const probabilities = [];
    var maxProbabilitySum = 0;

    return {
        showProbabilities() {
            const p = [];
            var previous = 0;
            for (var i = 0; i < probabilities.length; i++) {
                var current = probabilities[i];
                p.push(parseFloat(((current - previous) / maxProbabilitySum).toFixed(5)))
                previous = current;
            }
            return p;
        },
        add(obj, probability) {
            if (!Number.isInteger(probability)) {
                throw "'probability' have to be integer."
            }
            maxProbabilitySum += probability;
            objects.push(obj);
            probabilities.push(maxProbabilitySum);
        },
        random: function () {
            const random = Math.floor(Math.random() * maxProbabilitySum);
            // Can use binary search
            for (var i = 0; i < probabilities.length; i++) {
                var max = probabilities[i];
                if (random < max) {
                    return objects[i];
                }
            }
            throw "IMPOSSIBLE!!! THIS IS A BUG"
        }
    }
}

//----------------------------------------------
function createBuildLimit(limit) {
    const built = {};
    function _init_built_(team) {
        if (!built[team.id]) {
            built[team.id] = 0;
        }
    }
    function canBuild(team) {
        _init_built_(team);
        return built[team.id] < limit;
    }
    function addBuild(team) {
        _init_built_(team);
        return built[team.id]++;
    }
    function removeBuild(team) {
        _init_built_(team);
        return built[team.id]--;
    }
    return {
        canBuild: canBuild,
        addBuild: addBuild,
        removeBuild: removeBuild,
    }
};
exports.createBuildLimit = createBuildLimit;
const Health = (block) => {
    block.addBar("", func(e =>
        new Bar(
            prov(() => Math.floor(e.health) + " / " + e.maxHealth),
            prov(() => Color.valueOf("#ff0000"), 1),
            floatp(() => e.healthf()))));
    block.super$setBars();
    block.removeBar("health");
}
exports.Health = Health;

// module.exports = {
// 	c(string) {
// 		return Color.valueOf(string);
// 	},
// };