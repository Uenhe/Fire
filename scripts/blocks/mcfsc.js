/*
@author <guiY>
代码来自Extra Utilities mod
*/

const { mc } = require('misc/items')
const craftTime = 30
const itemDuration = 30
const input = mc
const output = Items.coal
const mcfsc = extend(ConsumeGenerator, 'mcfsc', {
    setStats(){
        this.super$setStats()
        this.stats.add(Stat.productionTime, craftTime / 60, StatUnit.seconds)
        this.stats.add(Stat.output, output)
    },
    outputsItems(){
        return true
    },
})
mcfsc.buildType = prov(() => {
    var p = 0
    var gp = 0
    var full = false
    const block = mcfsc
    
    return new JavaAdapter(ConsumeGenerator.ConsumeGeneratorBuild, {
        updateTile(){
            var cons = this.consValid()
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
    },mcfsc)
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
