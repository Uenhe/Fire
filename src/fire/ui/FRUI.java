package fire.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Time;
import fire.content.FRMath;
import mindustry.ui.Styles;

import static fire.FRVars.find;
import static mindustry.Vars.state;

/** @author fy */
public class FRUI{

    public static void numberDisplay(int number){
        Table t = new Table(Styles.none);
        t.touchable = Touchable.disabled;
        if(number <= 9)
            t.margin(18f).add("[red]" + number + "[white]").style(Styles.techLabel).labelAlign(Align.center).fontScale(4);
        else
            t.margin(18f).add("[red]X[white]").style(Styles.techLabel).labelAlign(Align.center).fontScale(4);
        t.update(() -> {
            t.setPosition(Core.graphics.getWidth() / 2f, Core.graphics.getHeight() / 2f, Align.center);
            t.toFront();

            if(state.isMenu()){
                t.remove();
            }
        });
        t.actions(Actions.fadeOut(2.5f + number, Interp.pow4In), Actions.remove());
        t.pack();
        t.act(0.1f);
        Core.scene.add(t);
    }

    public static void announce(String text, float duration){
        freeShow(text, duration, 0.5f, 0.5f);
    }

    public static void bottom(String text, float duration){
        Table t = new Table(Styles.none);
        t.touchable = Touchable.disabled;
        t.margin(8f).add(text).style(Styles.outlineLabel).labelAlign(Align.center);
        t.update(() -> {
            t.setPosition(Core.graphics.getWidth() / 2f, Core.graphics.getHeight() / 32f, Align.bottom);
            t.toFront();

            if(state.isMenu()){
                t.remove();
            }
        });
        t.actions(Actions.delay(duration * 0.9f), Actions.fadeOut(duration * 0.1f, Interp.fade), Actions.remove());
        t.pack();
        t.act(0.1f);
        Core.scene.add(t);
    }

    public static void freeShow(String text, float duration, float x, float y){
        freeShow(text, duration, x, y, 0, 0);
    }

    public static void freeShow(String text, float duration, float x, float y, float dstx,float dsty){
        Table t = new Table(Styles.none);
        t.touchable = Touchable.disabled;

        t.margin(8f).add(text).style(Styles.outlineLabel).labelAlign(Align.center);
        t.update(() -> {
            t.setPosition(Core.graphics.getWidth() * x + dstx, Core.graphics.getHeight() * y + dsty, Align.center);
            t.toFront();

            if(state.isMenu()){
                t.remove();
            }
        });
        t.actions(Actions.delay(duration * 0.9f), Actions.fadeOut(duration * 0.1f, Interp.fade), Actions.remove());
        t.pack();
        t.act(0.1f);
        Core.scene.add(t);
    }

    public static void smoothShow(String text, float duration, float x, float y){
        Table t = new Table(Styles.none);
        t.touchable = Touchable.disabled;

        float tme = 0;
        float tme2 = 0;

        StringBuilder col = new StringBuilder();

        col.append("[white]");

        for(int i = 0; i < text.length(); i++){
            char character = text.charAt(i);
            switch(character){
                //FIXME a char can't contain Chinese character?
                case ',', '，':
                    tme += 20.0f;
                    break;
                case '。', '！', '？':
                    tme += 30.0f;
                    break;
                case '\n' :
                    t.row();
                    break;
                case '[':
                    col = new StringBuilder();
                    while(character != ']'){
                        character = text.charAt(i);
                        col.append(character);
                        i++;
                    }
                    i--;
                    break;
                default:
                    tme += 3.0f;
            }
            if(character == ']') continue;
            char finalCharacter = character;
            StringBuilder finalCol = col;
            Time.run(tme2, () -> {
                t.add(finalCol.toString() + finalCharacter).style(Styles.outlineLabel).labelAlign(Align.left);
            });
            tme2 = tme;
        }

        t.update(() -> {
            t.setPosition(Core.graphics.getWidth() * x, Core.graphics.getHeight() * y, Align.left);
            t.toFront();

            if(state.isMenu()){
                t.remove();
            }
        });
        t.actions(Actions.delay(duration * 0.9f), Actions.fadeOut(duration * 0.1f, Interp.fade), Actions.remove());
        t.pack();
        t.act(0.1f);
        Core.scene.add(t);
    }

    public static void tableShow(String info, float duration){
        float stTime = Time.time;
        Table t = new Table(Styles.none);
        t.touchable = Touchable.disabled;

        t.update(() -> {
            float cx = Core.camera.position.x,cy = Core.camera.position.y;
            float cw = Core.camera.width, ch = Core.camera.height;
            cy -= ch * 0.3f;
            float xy = cy - ch * 0.05f;
            float sy = cy + ch * 0.05f;
            float zx = cx - cw * 0.4f;
            float yx = cx + cw * 0.4f;
            float time = Time.time - stTime;

            float stP1x = cx, stP1y = cy - ch * 0.3f;
            float stP2x = cx, stP2y = cy - ch * 0.3f;

            float midP1x = cx, midP1y = xy;
            float midP2x = cx, midP2y = sy;

            float lftP1x = zx, lftP1y = xy;
            float lftP2x = zx + cw * 0.2f, lftP2y = sy;

            float edP1x = zx, edP1y = xy;
            float edP2x = yx, edP2y = sy;

            if(time >= duration * 60.0f)t.remove();
            if(time <= 30.0f){
                rectPlus(FRMath.mix(stP1x, midP1x, time / 30.0f),FRMath.mix(stP1y, midP1y, time / 30.0f),
                    FRMath.mix(stP2x, midP2x, time / 30.0f),FRMath.mix(stP2y, midP2y, time / 30.0f));
            }else if(time <= 45.0f){
                rectPlus(FRMath.mix(midP1x, lftP1x, (time - 30.0f) / 15.0f),FRMath.mix(midP1y, lftP1y, (time - 30.0f) / 15.0f),
                    FRMath.mix(midP2x, lftP2x, (time - 30.0f) / 15.0f),FRMath.mix(midP2y, lftP2y, (time - 30.0f) / 15.0f));
            }else if(time <= 65.0f){
                rectPlus(FRMath.mix(lftP1x, edP1x, (time - 45.0f) / 20.0f),FRMath.mix(lftP1y, edP1y, (time - 45.0f) / 20.0f),
                    FRMath.mix(lftP2x, edP2x, (time - 45.0f) / 20.0f),FRMath.mix(lftP2y, edP2y, (time - 45.0f) / 20.0f));
            }else if(time >= duration * 60.0f - 40.0f){
                rectPlus(FRMath.mix(edP1x, stP1x, (time - duration * 60.0f + 40.0f) / 20.0f),FRMath.mix(edP1y, stP1y, (time - duration * 60.0f + 40.0f) / 20.0f),
                    FRMath.mix(edP2x, stP2x, (time - duration * 60.0f + 40.0f) / 20.0f),FRMath.mix(edP2y, stP2y, (time - duration * 60.0f + 40.0f) / 20.0f));
            }else rectPlus(edP1x, edP1y, edP2x, edP2y);

            if(state.isMenu()){
                t.remove();
            }
        });
        t.pack();
        t.act(0.1f);
        Core.scene.add(t);
    }

    static Color backCol = find("333333AA");

    public static void rectPlus(float x1, float y1, float x2, float y2){
        rectPlus(x1, y1, x2, y2, backCol, Color.white);
    }

    public static void rectPlus(float x1, float y1, float x2, float y2, Color color){
        rectPlus(x1, y1, x2, y2, color, color);
    }

    public static void rectPlus(float x1, float y1, float x2, float y2, Color fillColor, Color edge){
        rectPlus(x1, y1, x2, y2, fillColor, fillColor, edge);
    }

    public static void rectPlus(float x1, float y1, float x2, float y2, Color topColor, Color buttonColor, Color edge){
        rectPlus(x1, y1, x2, y2, buttonColor, topColor, topColor, buttonColor, edge);
    }

    public static void rectPlus(float x1, float y1, float x2, float y2, Color color1, Color color2, Color color3, Color color4, Color edge){
        Lines.stroke(Core.camera.height * 0.005f);
        Fill.quad(x1, y1, color1.toFloatBits(), x1, y2, color2.toFloatBits(), x2, y2, color3.toFloatBits(), x2, y1, color4.toFloatBits());
        Draw.color(edge);
        Lines.quad(x1, y1, x1, y2, x2, y2, x2, y1);
        Draw.color();
    }
}
