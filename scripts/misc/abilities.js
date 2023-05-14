/*
* @author <Uenhe>
* 原版力墙+自己琢磨缝合...出现了! 集力墙, 反弹子弹和破盾释放电弧于一身的单位能力场!
* 20230113: v141更新, anuke改了单位力墙 (加了 sides & rotation 接口), 此处重新适配. 新增旋转力墙, 将rotation = 361来开启!
*/
const EnergyForceFieldAbility = (radius, regen, max, cooldown, sides, rotation, chance, damage, length, amount) => { //力墙半径, 力墙每帧回复量, 力墙最大盾容, 力墙冷却所需帧, 力墙边数, 力墙旋转角度, 子弹反弹几率 (参考原版布墙), 闪电伤害, 闪电长度, 闪电数量; 将chance = -1, damage = 0, length = 0, amount = 0来将其当作正常力墙使用
	var realrotation = rotation
	var alpha = 1
	var radscl = 0
	var ability = new JavaAdapter(Ability, {
		localized(){
			return Core.bundle.format('ability.fire-energyforcefield', radius / 8, regen * 60, max, cooldown / 60, sides, chance, damage, length / 8, amount)
		},
		update(unit){
			if(realrotation >= 361 && !Vars.state.isPaused()){
				realrotation = realrotation > 720 ? 361 : realrotation + 1
			} //力墙旋转部分
			alpha = Math.max(alpha - Time.delta / 10, 0)
			if(unit.shield < max){
				unit.shield += Time.delta * regen
			}
			if(unit.shield > 0){
				radscl = Mathf.lerpDelta(radscl, 1, 0.06)
				Groups.bullet.intersect(unit.x - radscl * radius, unit.y - radscl * radius, radscl * radius * 2, radscl * radius * 2, cons(bullet => {
					if(bullet.team != unit.team && bullet.type.absorbable && Intersector.isInRegularPolygon(sides, unit.x, unit.y, radscl * radius, realrotation, bullet.x, bullet.y) && unit.shield > 0){
						var collision = true
						Fx.absorb.at(bullet)
						if(bullet.vel.len() > 0.1 && bullet.type.reflectable && Mathf.chance(chance / bullet.damage)){
							bullet.trns(-bullet.vel.x, -bullet.vel.y)
							var penX = Math.abs(unit.x - bullet.x)
							var penY = Math.abs(unit.y - bullet.y)
							if(penX > penY){
								bullet.vel.x *= -1
							}else{
								bullet.vel.y *= -1
							}
							bullet.owner = unit
							bullet.team = unit.team
							bullet.time += 1
							collision = false
						} //子弹反弹部分
						if(collision){
							bullet.absorb()
						}
						if(unit.shield <= bullet.damage){
							unit.shield -= cooldown * regen
							Fx.shieldBreak.at(unit.x, unit.y, radius, unit.team.color, unit)
							Sounds.spark.at(unit, Mathf.random(0.9, 1.1))
							for(var i = 0; i < amount; i += 1){
								Lightning.create(unit.team, Pal.surge, damage, unit.x, unit.y, Mathf.random(360), length)
							} //破盾放电部分
						}
						unit.shield -= bullet.damage
						alpha = 1
					}
				}))
			}else{
				radscl = 0
			}	
		},
		draw(unit){
			if(unit.shield > 0){
				Draw.z(Layer.shields)
				Draw.color(unit.team.color, Color.white, Mathf.clamp(alpha))
				if(Core.settings.getBool('animatedshields')){
					Fill.poly(unit.x, unit.y, sides, radscl * radius, realrotation)
				}else{
					Lines.stroke(1.5)
					Draw.alpha(0.09)
					Fill.poly(unit.x, unit.y, sides, radius, realrotation)
					Draw.alpha(1)
					Lines.poly(unit.x, unit.y, sides, radius, realrotation)
				}
			}
		},
		displayBars(unit, bars){
			bars.add(new Bar(Core.bundle.format("stat.shieldhealth"), Pal.accent, () => unit.shield / max)).row()
		}, //单位状态栏力墙条
		copy(){
			return EnergyForceFieldAbility(radius, regen, max, cooldown, sides, rotation, chance, damage, length, amount)
		}
	})
	return ability
}
exports.EnergyForceFieldAbility = EnergyForceFieldAbility

const RegenFieldAbility = (amount, range, color, speed, stroke) => { //帧回复量, 回复半径, 圆圈颜色, 扩散速度, 线条粗细
	var refreshInterval = 6
	var refresh = Mathf.random(refreshInterval)
	var warmup = 0
	var totalProgress = 0
	var ability = new JavaAdapter(Ability, {
		localized(){
			return Core.bundle.format('ability.fire-regenfield', amount * 60, range / 8)
		},
		update(unit){
			var draws = false //bad name in vanilla src??
			if((refresh += Time.delta) >= refreshInterval){
				refresh = 0
				Units.nearby(unit.team, unit.x, unit.y, range, u => {
					if(u.damaged()){
						u.heal(amount)
						draws = true
					}
				})
			}
			warmup = Mathf.lerpDelta(warmup, draws ? 1 : 0, 0.08)
			totalProgress += Time.delta / speed
		},
		draw(unit){
			if(warmup > 0.001){
				Draw.z(Layer.effect)
				var mod = totalProgress % 1
				Draw.color(color)
				Lines.stroke(stroke * (1 - mod) * warmup)
				Lines.circle(unit.x, unit.y, range * mod)
				Draw.reset()
			}
		},
		copy(){
			return RegenFieldAbility(amount, range, color, speed, stroke)
		}
	})
	return ability
}
exports.RegenFieldAbility = RegenFieldAbility
