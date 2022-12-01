//代码来自创世神mod

let mod = Vars.mods.getMod("fire");
let version = mod.meta.version;
let modname = Core.bundle.format("modname");
let title = Core.bundle.format("title");
let urlSszylfyzgmz = "https://space.bilibili.com/516898377";
let urlUenhe = "https://space.bilibili.com/327502129";

Events.on(EventType.ClientLoadEvent, cons(e => {
	var dialog = new BaseDialog(Core.bundle.format("modname") + " v" + mod.meta.version + " " + Core.bundle.format("title"));

	dialog.addCloseListener();//按esc关闭

	dialog.buttons.defaults().size(192, 64);

	dialog.buttons.button("@close", run(() => {
		dialog.hide();
	})).size(256, 64);

	dialog.cont.pane((() => {

		var table = new Table();

		table.image(Core.atlas.find("fire-logo", Core.atlas.find("clear"))).height(107).width(359).pad(3);//LOGO
		table.row();

		table.add(Core.bundle.format("contentMain")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);//更新内容显示
		table.row();

		table.add(Core.bundle.format("contentSecondary")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);//补充内容显示
		table.row();
		
		table.button(Core.bundle.format("linkSszylfyzgmz"), run(() => {//显示跳转飞雨的bilibili个人主页的按钮
			if (!Core.app.openURI(urlSszylfyzgmz)) {//设置跳转目的地
				Vars.ui.showErrorMessage("@linkfail");
				Core.app.setClipboardText(urlSszylfyzgmz);
			}
		})).size(256, 64);
		table.row();
		
		table.button(Core.bundle.format("linkUenhe"), run(() => {//显示跳转Uenhe的bilibili个人主页的按钮
			if (!Core.app.openURI(urlUenhe)) {//设置跳转目的地
				Vars.ui.showErrorMessage("@linkfail");
				Core.app.setClipboardText(urlUenhe);
			}
		})).size(256, 64);
		table.row();
		
		return table;

	})()).grow().center().maxWidth(770);
dialog.show();
}));
