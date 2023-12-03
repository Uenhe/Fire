//why are you still here...?

var block = Blocks.distributor
var build = block.buildType.get().class
block.buildType = () => extend(build, block, {
    canControl(){
        return true
    }
})
