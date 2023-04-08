//注: 此处仅负责方块的特殊效果, 一般参数请移步json部分.

//const AssemblerUnitPlan = Packages.mindustry.world.blocks.units.UnitAssembler.assemblerUnitPlan
const lib = require('misc/lib')
const FireItems = require('items')
const FireStatuses = require('statuses')
const FireUnits = require('units')

function newBlock(type, name) {
	exports[type, name] = (() => {
		const block = extend(type, name, {})
		return block
	})()
} //json方块初始化函数...用于其他js文件的引用

//炮塔

newBlock(ItemTurret, 'yg') //魇光

//运输

newBlock(Conveyor, 'fhcsd') //复合传送带

//工厂

newBlock(GenericCrafter, 'dmj') //钢化玻璃打磨机

//工厂-木材焚烧厂
/*
* @author <guiY>
* 代码来自Extra Utilities mod
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
exports.mcfsc = mcfsc

//效果

//效果-装甲核心
/*
* @author <Uenhe>
* 实际上就是把原版的力墙抄了过来, 再根据js作相应调整, 不过还是花了我一天时间...我好蔡, 悲
*/
const radius = 96
const range = 96
const shieldHealth = 400
const cooldown = 0.8
const cooldownBrokenBase = 0.8 //定义力墙接口
const zjhx = extend(CoreBlock, 'zjhx', {
	canBreak(tile){
		if(this.team = Team.derelict) return true //灰队? 不就是用来回收资源的吗(
		return Vars.state.teams.cores(tile.team()).size > 1 //当核心数大于1时可被拆除
	},
	canPlaceOn(tile, team) {return true}, //可被放置
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
	} //方块放置预览力墙绘制
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
			if(buildup > 0) {
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
				if(Core.settings.getBool('animatedshields')) {Fill.poly(this.x, this.y, 6, radius * radscl)}
				else{
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
		} //write和read部分可以让值被存档&读取
	}, zjhx)
})
exports.zjhx = zjhx

//效果-Javelin机甲平台
//移除核心不清物品代码部分来自创世神mod
const javelinPad = extend(CoreBlock, 'javelinPad', {
	canBreak(tile){
		return (Vars.state.teams.cores(tile.team()).size > 1 || Vars.state.isEditor())
	},
	canPlaceOn(tile, team, rotation){
		return true
	}
})
javelinPad.buildType = () => {
	return extend(CoreBlock.CoreBuild, javelinPad, {
		onRemoved(){
			Vars.state.teams.unregisterCore(this)
		}
	})
}	
exports.javelinPad = javelinPad

//效果-复合装卸器
//低帧装卸代码部分来自创世神mod
const compositeUnloader = extend(DirectionalUnloader, 'composite-unloader', {
	setStats(){
		this.super$setStats()
		this.stats.remove(Stat.speed)
		this.stats.add(Stat.speed, 25, StatUnit.itemsSecond)
	}
})
compositeUnloader.speed = 25
compositeUnloader.buildType = () => {
	var counter = 0
	return extend(DirectionalUnloader.DirectionalUnloaderBuild, compositeUnloader, {
		updateTile(){
			counter += this.edelta()
			while(counter >= 60 / 25){
				this.unloadTimer = 25
				this.super$updateTile()
				counter -= 60 / 25
			}
		}
	})
}
exports.compositeUnloader = compositeUnloader

//效果-篝火
const gh = new OverdriveProjector('gh')
gh.buildType = () => {
	return extend(OverdriveProjector.OverdriveBuild, gh, {
		updateTile(){
			this.super$updateTile()
			if(this.efficiency > 0){
				Groups.unit.intersect(this.x - 512, this.y - 512, 1024, 1024, cons(unit => {
					if(unit.team == this.team && Intersector.isInRegularPolygon(24, this.x, this.y, 512, 0, unit.x, unit.y)) {unit.apply(FireStatuses.inspired, 60)}
					if(unit.team != this.team && Intersector.isInRegularPolygon(24, this.x, this.y, 512, 0, unit.x, unit.y)) {unit.apply(StatusEffects.sapped, 60)}
				}))
				if(Mathf.chanceDelta(0.02)) {Fx.blastsmoke.at(this.x + Mathf.range(20), this.y + Mathf.range(20))}
				if(Mathf.chanceDelta(0.02)) {Fx.generatespark.at(this.x + Mathf.range(20), this.y + Mathf.range(20))}
			}
		},
		draw(){
			this.super$draw()
			if(this.efficiency > 0 & Core.settings.getBool('showBlockRange')){
				Draw.color(Color.valueOf('feb380'), 1)
				Lines.stroke(2)
				Lines.circle(this.x, this.y, 512)
				Draw.alpha(0.25)
				Fill.circle(this.x, this.y, 512)
			}
		}
	})
}
exports.gh = gh
