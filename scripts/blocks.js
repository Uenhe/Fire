//const AssemblerUnitPlan = Packages.mindustry.world.blocks.units.UnitAssembler.assemblerUnitPlan
const lib = require('misc/lib')
const FireItems = require('items')
const FireUnits = require('units')

//工厂

//工厂-木材焚烧厂
/*
* @author <guiY>
代码来自Extra Utilities mod
*/
const craftTime = 30
const itemDuration = 30
const input = FireItems.mc
const output = Items.coal
const mcfsc = extend(ConsumeGenerator, 'mcfsc', {
	setStats(){
		this.super$setStats()
		this.stats.add(Stat.productionTime, craftTime / 60, StatUnit.seconds)
		this.stats.add(Stat.output, output)
	},
	outputsItems() {return true},
})
mcfsc.buildType = prov(() => {
	var p = 0
	var gp = 0
	var full = false
	var block = mcfsc
	return new JavaAdapter(ConsumeGenerator.ConsumeGeneratorBuild, {
		updateTile(){
			const cons = this.consValid()
			full = this.items.get(output) >= block.itemCapacity
			if(cons && !full){
				p += this.getProgressIncrease(craftTime)
				gp += this.getProgressIncrease(itemDuration)
			}
			if(p > 1 && !full){
				this.items.add(output, 1)
				p %= 1
			}
			if(gp > 1 && !full){
				this.consume()
				gp %= 1
				block.generateEffect.at(this.x + Mathf.range(3), this.y + Mathf.range(3))
			}
			this.productionEfficiency = Mathf.num(cons) * Mathf.num(!full)
			this.dump(output)
			this.produced(output)
			this.warmup = Mathf.lerpDelta(this.warmup, cons && !full ? 1 : 0, 0.05)
		},
		consValid(){
			return this.efficiency > 0
		},
		getPowerProduction(){
			return Mathf.num(this.consValid()) * block.powerProduction * Mathf.num(!full)
		},
		status(){
			if(this.consValid() && !full) return BlockStatus.active
			if(full && this.consValid()) return BlockStatus.noOutput
			return BlockStatus.noInput
		},
		write(write){
			this.super$write(write)
			write.f(p)
			write.f(gp)
		},
		read(read, revision){
			this.super$read(read, revision)
			p = read.f()
			gp = read.f()
		},
	}, mcfsc)
})
mcfsc.category = Category.crafting
mcfsc.buildVisibility = BuildVisibility.shown
mcfsc.requirements = ItemStack.with(
	Items.copper, 50,
	Items.lead, 25,
	Items.metaglass, 15,
	Items.graphite, 20,
)
mcfsc.size = 2
mcfsc.hasPower = true
mcfsc.hasItems = true
mcfsc.hasLiquids = false
mcfsc.powerProduction = 3.5
mcfsc.itemDuration = itemDuration
mcfsc.generateEffect = Fx.generatespark
mcfsc.drawer = new DrawMulti(new DrawDefault(), new DrawWarmupRegion())
mcfsc.ambientSound = Sounds.steam
mcfsc.ambientSoundVolume = 0.01
mcfsc.consumeItem(input)
exports.mcfsc = mcfsc

//效果

//效果-装甲核心
/*
* @author <Uenhe>
实际上就是把原版的力墙抄了过来, 再根据js作相应调整, 不过还是花了我一天时间...我好蔡, 悲
*/
const radius = 96
const range = 96
const shieldHealth = 400
const cooldown = 0.8
const cooldownBrokenBase = 0.8 //定义力墙接口
const zjhx = extend(CoreBlock, 'zjhx', {
	canBreak(tile) {return Vars.state.teams.cores(tile.team()).size > 1}, //当核心数大于1时可被拆除
	canReplace(other) {return other.alwaysReplace}, //可被放置
	canPlaceOn(tile, team) {return true}, //可被放置(?)
	init(){
		this.updateClipRadius(radius + 3)
		this.super$init()
	},
	setBars(){
		this.super$setBars()
		this.addBar('shield', func(e => new Bar(
			prov(() => Core.bundle.format('stat.shieldhealth') + ' ' + Math.floor(shieldHealth - e.buildup()) + ' / ' + shieldHealth),
			prov(() => Pal.accent),
			floatp(() => e.broken() ? 0 : 1 - e.buildup() / shieldHealth)
		).blink(Color.white)))
	}, //设置方块力墙状态栏
	setStats(){
		this.super$setStats()
		this.stats.add(Stat.shieldHealth, shieldHealth, StatUnit.none)
		this.stats.add(Stat.cooldownTime, Math.floor(shieldHealth / cooldownBrokenBase / 60), StatUnit.seconds)
		this.stats.add(Stat.range, range / Vars.tilesize, StatUnit.blocks)
	}, //设置方块详细界面力墙属性
	drawPlace(x, y, roration, vaild){
		this.super$drawPlace(x, y, roration, vaild)
		Draw.color(Pal.gray)
		Lines.stroke(3)
		Lines.poly(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, 6, radius)
		Draw.color(Vars.player.team().color)
		Lines.stroke(1)
		Lines.poly(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, 6, radius)
		Draw.color()
	}, //方块放置预览力墙绘制
})
zjhx.buildType = prov(() => {
	var broken = true
	var buildup = 0
	var radscl = 0
	var warmup = 0
	var hit = 1 //初始化值, 其中buildup是当前力墙累计受到的伤害, 超过盾容(buildup>=shieldHealth)力墙就会破碎(broken=true)
	return new JavaAdapter(CoreBlock.CoreBuild, {
		onRemoved(){
			if(!broken && radius * radscl > 1) Fx.forceShrink.at(this.x, this.y, radius * radscl, this.team.color)
			this.super$onRemoved()
		}, //方块被移除时力墙收缩效果(?)
		pickedUp(){
			this.super$pickedUp()
			radscl = warmup = 0
		}, 
		inFogTo(viewer) {return false},
		updateTile(){
			radscl = Mathf.lerpDelta(radscl, broken ? 0 : warmup, 0.05)
			if(Mathf.chanceDelta(buildup / shieldHealth * 0.1)) {Fx.reactorsmoke.at(this.x + Mathf.range(Vars.tilesize / 2), this.y + Mathf.range(Vars.tilesize / 2))}
			warmup = Mathf.lerpDelta(warmup, 1, 0.1)
			if(buildup > 0) {
				var scale = !broken ? cooldown : cooldownBrokenBase
				buildup -= this.delta() * scale
			}
			if(broken && buildup <= 0) {broken = false}
			if(buildup >= shieldHealth && !broken) {
				broken = true
				buildup = shieldHealth
				Fx.shieldBreak.at(this.x, this.y, radius * radscl, this.team.color)
				if (this.team != Vars.state.rules.defaultTeam) {Events.fire(Trigger.forceProjectorBreak)}
			}
			if(hit > 0) {hit -= 1 / 5 * Time.delta}
			if(radius * radscl > 0 && !broken) {Groups.bullet.intersect(this.x - radius * radscl, this.y - radius * radscl, radius * radscl * 2, radius * radscl * 2, cons(bullet => {
				if(bullet.team != this.team && bullet.type.absorbable && Intersector.isInsideHexagon(this.x, this.y, radius * radscl * 2, bullet.x, bullet.y)) {
					bullet.absorb()
					Fx.absorb.at(bullet)
					hit = 1
					buildup += bullet.damage
				}
			}))}
		},
		broken() {return broken},
		buildup() {return buildup},
		sense(sensor){
			if(sensor == LAccess.heat) return buildup
			return this.super$sense(sensor)
		}, //当逻辑sensor这个块的heat属性时, 返回buildup的值
		draw(){
			this.super$draw()
			if (buildup > 0) {
				Draw.alpha(buildup / shieldHealth * 0.75)
				Draw.z(Layer.blockAdditive)
				Draw.blend(Blending.additive)
				Draw.blend() //去掉这个有惊喜
				Draw.z(Layer.block)
				Draw.reset()
			}
			if(!broken) {
				Draw.z(Layer.shields)
				Draw.color(this.team.color, Color.white, Mathf.clamp(hit))
				if(Core.settings.getBool('animatedshields')) {
					Fill.poly(this.x, this.y, 6, radius * radscl)
				}else {
					Lines.stroke(1.5)
					Draw.alpha(0.09 + Mathf.clamp(0.08 * hit))
					Fill.poly(this.x, this.y, 6, radius * radscl)
					Draw.alpha(1)
					Lines.poly(this.x, this.y, 6, radius * radscl)
					Draw.reset()
				}
			}
			Draw.reset()
		},
		write(write){
			this.super$write(write)
			write.bool(broken)
			write.f(buildup)
			write.f(radscl)
			write.f(warmup)
		},
		read(read, revision){
			this.super$read(read, revision)
			broken = read.bool()
			buildup = read.f()
			radscl = read.f()
			warmup = read.f()
		}, //write和read部分可以让值被存档&读取
	}, zjhx)
})
zjhx.category = Category.effect
zjhx.buildVisibility = BuildVisibility.shown
zjhx.researchCostMultiplier = 0.4
zjhx.requirements = ItemStack.with(
	Items.copper, 9000,
	Items.lead, 8500,
	Items.metaglass, 2500,
	Items.titanium, 4000,
	Items.thorium, 3500,
	Items.silicon, 6000,
	Items.plastanium, 1000,
)
zjhx.health = 11200
zjhx.armor = 8
zjhx.size = 5
zjhx.itemCapacity = 10500
zjhx.unitType = FireUnits.gnj
zjhx.unitCapModifier = 12
exports.zjhx = zjhx

//效果-Javelin机甲平台
//部分代码来自创世神mod
const javelinPad = extend(CoreBlock, 'javelinPad', {
	canBreak(tile){return Vars.state.teams.cores(tile.team()).size > 1},
	canReplace(other){return other.alwaysReplace},
	canPlaceOn(tile, team){return true},
})
javelinPad.buildType = prov(() => {
	return new JavaAdapter(CoreBlock.CoreBuild, {
		onRemoved() {Vars.state.teams.unregisterCore(this)}
	}, javelinPad)
})
javelinPad.category = Category.effect
javelinPad.buildVisibility = BuildVisibility.shown
javelinPad.alwaysReplace = false
javelinPad.replaceable = false
javelinPad.requirements = ItemStack.with(
	Items.lead, 350,
	Items.titanium, 500,
	Items.silicon, 450,
	Items.plastanium, 400,
	Items.phaseFabric, 200,
)
javelinPad.health = 1200
javelinPad.size = 2
javelinPad.itemCapacity = 0
javelinPad.unloadable = false
javelinPad.unitType = FireUnits.javelin
javelinPad.unitCapModifier = 0
exports.javelinPad = javelinPad

/*
这里本来是想用装配厂的装配机来做单位的...但是做出来的单位只能用装配机的AI, 不然会报错, 故搁置, 转移到普通空军厂里.
const limit = lib.createBuildLimit(5)
const fireflyAssembler = extend(UnitAssembler, 'fireflyAssembler', {
	canPlaceOn(tile, team, rotation) {
		if (limit.canBuild(Vars.player.team())) {return true}
		if (Vars.state.isEditor()) return true
		return this.super$canPlaceOn(tile, team, rotation)
	},
	drawPlace(x, y, rotation, valid){
		if (!limit.canBuild(Vars.player.team())) {this.drawPlaceText(Core.bundle.format('maxPlacementMessage') + ' 5', x, y, valid)}
	},
})
fireflyAssembler.buildType = prov(() => {
	return new JavaAdapter(UnitAssembler.UnitAssemblerBuild, {
		add(){
			this.super$add()
			if (this.team != Team.derelict) {limit.addBuild(this.team)}
		},
		readBase(read){
			this.super$readBase(read)
			if (this.team != Team.derelict) {limit.addBuild(this.team)}
		},
		remove(){
			if (this.added) limit.removeBuild(this.team)
			this.super$remove()
		},
	}, fireflyAssembler)
})
fireflyAssembler.category = Category.units
fireflyAssembler.buildVisibility = BuildVisibility.shown
fireflyAssembler.requirements = ItemStack.with(
	Items.copper, 160,
	Items.lead, 180,
	Items.graphite, 125,
	Items.silicon, 275,
)
fireflyAssembler.health = 420
fireflyAssembler.size = 3
fireflyAssembler.areaSize = 3
fireflyAssembler.droneType = FireUnits.firefly
fireflyAssembler.dronesCreated = 6
fireflyAssembler.droneConstructTime = 2400
fireflyAssembler.plans.add(new AssemblerUnitPlan(FireUnits.firefly, 60, PayloadStack.list(FireUnits.firefly, 1, Blocks.copperWallLarge, 1)))
exports.fireflyAssembler = fireflyAssembler

const limit = lib.createBuildLimit(2)
const javelinPad = extend(CoreBlock, 'javelinPad', {
	setStats(){
		this.super$setStats()
		this.stats.remove(Stat.buildTime)
	},
	canBreak(tile){return Vars.state.teams.cores(tile.team()).size > 1},
	canPlaceOn(tile, team, rotation) {
		if (limit.canBuild(Vars.player.team())) {return true}
		//if (tile == null) return false
		if (Vars.state.isEditor()) return true
		//if (team.core() == null || (!Vars.state.rules.infiniteResources && !team.core().items.has(this.requirements, Vars.state.rules.buildCostMultiplier))) return false
		return this.super$canPlaceOn(tile, team, rotation)
	},
	drawPlace(x, y, rotation, valid){
		const player = Vars.player
		const rules = Vars.state.rules
		const team = player.team()
		if ((team.core() != null && !team.core().items.has(this.requirements, rules.buildCostMultiplier)) && !rules.infiniteResources) {this.drawPlaceText(Core.bundle.get('bar.noresources'), x, y, false)}
		if (!Vars.world.tile(x, y)) {return}
		if (!limit.canBuild(Vars.player.team())) {this.drawPlaceText(Core.bundle.format('maxPlacementMessage') + ' 2', x, y, valid)}
	},
})
javelinPad.buildType = prov(() => {
	return new JavaAdapter(CoreBlock.CoreBuild, {
		add(){
			this.super$add()
			if (this.team != Team.derelict) {limit.addBuild(this.team)}
		},
		readBase(read){
			this.super$readBase(read)
			if (this.team != Team.derelict) {limit.addBuild(this.team)}
		},
		remove(){
			if (this.added) limit.removeBuild(this.team)
			this.super$remove()
		},
		onRemoved() {Vars.state.teams.unregisterCore(this)}
	}, javelinPad)
})
*/
