package fire.ui.dialogs;

import arc.util.Time;

/** See ExtraUtilities also. */
public class DelayClosableDialog extends mindustry.ui.dialogs.BaseDialog{

    public float time;
    public boolean closable;

    public DelayClosableDialog(String title, float t){
        super(title);
        time = t;
        update(() -> closable = (time -= Time.delta) <= 0.0f);
    }
}
