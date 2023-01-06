const FXds = new Effect(40, e => {
	Draw.color(Color.valueOf('f0ffff'))
	Angles.randLenVectors(e.id, 2, 1 + e.fin() * 2, (x, y) => {
		Fill.circle(e.x + x, e.y + y, e.fout() * 1.2)
	})
})
exports.FXds = FXds
