var gnj = extend(UnitType, "gnj", {})
gnj.defaultController = prov(() => new BuilderAI())
gnj.constructor = prov(() => extend(UnitTypes.poly.constructor.get().class, {}))
exports.gnj = gnj
