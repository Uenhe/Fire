const { gnj } = require('units/gnj')
var sh = extend(UnitType, "sh", {})
sh.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}))
var ky = extend(UnitType, "ky", {})
ky.constructor = prov(() => extend(UnitTypes.atrax.constructor.get().class, {}))
var ws = extend(UnitType, "ws", {})
ws.constructor = prov(() => extend(UnitTypes.spiroct.constructor.get().class, {}))
/*爬辅系列仍未完成
var bh = extend(UnitType, "bh", {})
bh.constructor = prov(() => extend(UnitTypes.arkyid.constructor.get().class, {}))
var sy = extend(UnitType, "sy", {})
sy.constructor = prov(() => extend(UnitTypes.toxopid.constructor.get().class, {}))
*/

Blocks.groundFactory.plans.add(
	new UnitFactory.UnitPlan(sh, 1500, ItemStack.with(Items.lead, 20, Items.titanium, 25, Items.silicon, 30)),
)
Blocks.airFactory.plans.add(
	new UnitFactory.UnitPlan(UnitTypes.alpha, 2400, ItemStack.with(Items.copper, 30, Items.lead, 40, Items.silicon, 30)),
)
    
Blocks.additiveReconstructor.addUpgrade(UnitTypes.alpha, UnitTypes.beta)
Blocks.additiveReconstructor.addUpgrade(sh, ky)
Blocks.multiplicativeReconstructor.addUpgrade(UnitTypes.beta, gnj)
Blocks.multiplicativeReconstructor.addUpgrade(ky, ws)
//Blocks.exponentialReconstructor.addUpgrade(ws, bh)
//Blocks.tetrativeReconstructor.addUpgrade(bh, sy)
