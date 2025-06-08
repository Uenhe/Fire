package fire.ui.dialogs;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.gen.Icon;
import mindustry.ui.Links;
import mindustry.ui.Styles;

import static mindustry.Vars.*;

/** @see mindustry.ui.dialogs.AboutDialog AboutDialog */
public class FRAboutDialog extends mindustry.ui.dialogs.BaseDialog{

    /** @see Links */
    private static final Links.LinkEntry[] links;

    public static final FRAboutDialog dialog = new FRAboutDialog();

    static{
        var vanilla = Links.getLinks();
        var changelog = vanilla[1];
        var github = vanilla[9];
        var biliicon = Core.atlas.drawable("fire-bilibili");
        var bilicol = Color.valueOf("31a9d0");

        links = new Links.LinkEntry[]{
            new Links.LinkEntry("changelog", "https://github.com/Uenhe/Fire/releases", changelog.icon, changelog.color),
            new Links.LinkEntry("githubmod", "https://github.com/Uenhe/Fire", github.icon, github.color),
            new Links.LinkEntry("bilify", "https://space.bilibili.com/516898377", biliicon, bilicol),
            new Links.LinkEntry("biliue", "https://space.bilibili.com/327502129", biliicon, bilicol)
        };
    }

    public FRAboutDialog(){
        super("@about.button");
        shown(() -> Core.app.post(this::setup));
        shown(this::setup);
        onResize(this::setup);
    }

    void setup(){
        cont.clear();
        buttons.clear();

        float h = Core.graphics.isPortrait() ? 90.0f : 80.0f,
        w = Core.graphics.isPortrait() ? 400.0f : 600.0f;

        var in = new Table();
        var pane = new ScrollPane(in);

        for(var link : links){
            var table = new Table(Styles.grayPanel);
            table.margin(0.0f);
            table.table(img -> {
                img.image().height(h - 5.0f).width(40.0f).color(link.color);
                img.row();
                img.image().height(5.0f).width(40.0f).color(link.color.cpy().mul(0.6f, 0.6f, 0.8f, 1.0f));
            }).expandY();

            table.table(i -> {
                i.background(Styles.grayPanel);
                i.image(link.icon);
            }).size(h - 5.0f, h);

            table.table(inset -> {
                inset.add("[accent]" + link.title).growX().left();
                inset.row();
                inset.labelWrap(link.description).width(w - 100.0f - h).color(Color.lightGray).growX();
            }).padLeft(8);

            table.button(Icon.link, Styles.clearNonei, () -> {
                if(!Core.app.openURI(link.link)){
                    ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(link.link);
                }
            }).size(h - 5.0f, h);

            in.add(table).size(w, h).padTop(5.0f).row();
        }

        shown(() -> Time.run(1.0f, () -> Core.scene.setScrollFocus(pane)));
        cont.add(pane).growX();

        addCloseButton();
    }
}
