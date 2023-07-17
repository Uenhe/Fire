//special thanks to Extra Utilities mod
exports.railChargeEffect = (lifetime, color, width, range, spacing) => {
    return new Effect(lifetime, range * 2, e => {
        var data = e.data ? e.data : {length: range};
        Draw.color(color);
        var track = Mathf.curve(e.fin(Interp.pow2Out), 0, 0.25) * Mathf.curve(e.fout(Interp.pow4Out), 0, 0.3) * e.fin();
        for(var i = 0; i <= data.length / spacing; i += 1){
            Tmp.v1.trns(e.rotation, i * spacing);
            var f = Interp.pow3Out.apply(Mathf.clamp((e.fin() * data.length - i * spacing) / spacing)) * (0.6 + track * 0.4);
            Draw.rect(Core.atlas.find("fire-aim-shoot"), e.x + Tmp.v1.x, e.y + Tmp.v1.y, 144 * Draw.scl * f, 144 * Draw.scl * f, e.rotation - 90)
        };
        Tmp.v1.trns(e.rotation, 0, (2 - track) * Vars.tilesize * width);
        Lines.stroke(track * 2);
        for(var i of Mathf.signs){
            Lines.lineAngle(e.x + Tmp.v1.x * i, e.y + Tmp.v1.y * i, e.rotation, data.length * (0.75 + track / 4) * Mathf.curve(e.fout(Interp.pow5Out), 0, 0.1))
        }
    })
}
