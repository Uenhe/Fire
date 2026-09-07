package fire.ui.dialogs;

import mindustry.ctype.UnlockableContent;

public class FRContentInfoDialog extends mindustry.ui.dialogs.ContentInfoDialog{
    public boolean shown;
    public UnlockableContent current;

    @Override
    public void show(UnlockableContent content){
        super.show(content);
        shown = true;
        current = content;
    }

    @Override
    public void hide(){
        super.hide();
        shown = false;
    }
}
