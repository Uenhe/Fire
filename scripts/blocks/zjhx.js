const gnj = require('units/gnj')
const zjhx = extend(CoreBlock, "zjhx", {
	canBreak() {return true},
	canReplace(other) {return other.alwaysReplace},
	canPlaceOn(tile, team) {return true},
})
zjhx.category = Category.effect
zjhx.buildVisibility = BuildVisibility.shown
zjhx.requirements = ItemStack.with(
	Items.copper, 9000,
	Items.lead, 8500,
	Items.metaglass, 2500
	Items.titanium, 4000,
	Items.thorium, 3500
	Items.silicon, 6000,
	Items.plastanium, 1000,
)

zjhx.health = 11200
zjhx.armor = 8
zjhx.size = 5
zjhx.itemCapacity = 10500
zjhx.unitType = gnj
zjhx.unitCapModifier = 12

exports.zjhx = zjhx
