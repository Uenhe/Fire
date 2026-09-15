package fire.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.scene.ui.layout.Table;
import fire.FRVars;
/** @author fy */
public class FRTable extends Table{
    static float x;
    static float y;
    static float w;
    static float h;
    FRTable(){
        x = 0.5f;
        y = 0.5f;
        w = 0f;
        h = 0f;
    }

    static Color backCol = FRVars.find("333333AA");

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

    public static void moveTo(float tx, float ty, float tw, float th, float time){

    }
}
