package fire.ui.dialogs;

import arc.Core;
import arc.util.Strings;
import arc.util.Time;

/** See Extra Utilities also. */
public class DelayClosableDialog extends mindustry.ui.dialogs.BaseDialog{

    public float time;
    public boolean closable;

    public DelayClosableDialog(String title, float t){
        super(title);
        time = t;
        update(() -> closable = (time -= Time.delta) <= 0.0f);
        buttons.button("", this::hide).update(b -> {
            b.setDisabled(!closable);
            b.setText(closable ? "@close" : String.format("%s(%ss)", Core.bundle.get("close"), Strings.fixed(time / 60.0f, 1)));
        }).size(210.0f, 64.0f);
    }
}
