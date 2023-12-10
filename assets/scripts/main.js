//why are you still here...?

var build = Blocks.distributor.buildType.get().class;
Blocks.distributor.buildType = () => extend(build, Blocks.distributor, {
    canControl(){
        return true
    }
})
