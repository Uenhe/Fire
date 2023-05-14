require('overrides')
require('blocks')
require('items')
require('liquids')
require('planets')
require('statuses')
require('techtree')
require('units')

/*========Startup Dialog========*/
const lib = require('misc/lib')
const mod = Vars.mods.getMod('fire')
const version = mod.meta.version
const modname = Core.bundle.format('modname')
const title = Core.bundle.format('title')
const urlRaindance = 'https://space.bilibili.com/516898377'
const urlUenhe = 'https://space.bilibili.com/327502129'

lib.mod.meta.displayName = lib.getMessage('mod', 'displayName')
Events.on(EventType.ClientLoadEvent, cons(e => {
	var dialog = new BaseDialog(Core.bundle.format('modname') + ' v' + mod.meta.version + ' ' + Core.bundle.format('title'))
	dialog.addCloseListener() //按esc关闭
	dialog.buttons.defaults().size(210, 64)
	dialog.buttons.button('@close', run(() => {
		dialog.hide()
	})).size(256, 64)
	dialog.cont.pane((() => {
		var table = new Table()
		table.image(Core.atlas.find('fire-logo', Core.atlas.find('clear'))).height(107).width(359).pad(3) //LOGO
		table.row()

		table.add(Core.bundle.format('contentMain')).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left) //更新内容显示
		table.row()

		table.add(Core.bundle.format('contentSecondary')).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left) //补充内容显示
		table.row()

		table.button(Core.bundle.format('linkRaindance'), run(() => { //显示跳转按钮
			if(!Core.app.openURI(urlRaindance)){
				Vars.ui.showErrorMessage('@linkfail')
				Core.app.setClipboardText(urlRaindance) //设置跳转目的地
			}
		})).size(256, 64)
		table.row()

		table.button(Core.bundle.format('linkUenhe'), run(() => { //显示跳转按钮
			if(!Core.app.openURI(urlUenhe)){
				Vars.ui.showErrorMessage('@linkfail')
				Core.app.setClipboardText(urlUenhe) //设置跳转目的地
			}
		})).size(256, 64)
		table.row()

		return table
	})()).grow().center().maxWidth(770)
	dialog.show()
}))
