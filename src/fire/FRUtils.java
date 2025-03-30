package fire;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import mindustry.game.EventType;

public final class FRUtils{

    private static final String[] rnumerals = new String[10];

    static{{
        Events.on(EventType.ClientLoadEvent.class, e ->
            System.arraycopy(Core.bundle.get("fire.rnumerals").split("\\|"), 0, rnumerals, 0, 10));
    }}

    public static void colors(Color[] colors, Color... color){
        System.arraycopy(color, 0, colors, 0, 3);
    }

    public static Color color(int... component){
        return new Color(component[0] / 255.0f, component[1] / 255.0f, component[2] / 255.0f);
    }

    public static Color colora(int... component){
        return new Color(component[0] / 255.0f, component[1] / 255.0f, component[2] / 255.0f, component[3] / 255.0f);
    }

    /** 1 to 10, that should be enough. */
    public static String toRomanNumeral(int n){
        return rnumerals[n - 1];
    }

    public static final class TimeNode{

        private final int[] nodes;

        public TimeNode(int... nodes){
            this.nodes = nodes;
        }

        public int get(int index){
            return nodes[index];
        }

        public int first(){
            return nodes[0];
        }

        public int last(){
            return nodes[nodes.length - 1];
        }

        public boolean checkBelonging(float time, int phase){
            return checkBelonging(time, phase, phase);
        }

        public boolean checkBelonging(float time, int phaseStart, int phaseEnd){
            return time >= (phaseStart == 0 ? 0.0f : nodes[phaseStart - 1])
                && time <= nodes[phaseEnd];
        }
    }
}
