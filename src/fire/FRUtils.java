package fire;

import arc.Core;
import arc.graphics.Color;

public final class FRUtils{

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
    public static String toNumeral(int n){
        return Core.bundle.get("fire.numerals").split("\\|")[n - 1];
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

        public int getQuantum(int quantum){
            return getQuantum(quantum, quantum);
        }

        public int getQuantum(int quantumFrom, int quantumTo){
            return nodes[quantumTo] - (quantumFrom == 0 ? 0 : nodes[quantumFrom - 1]);
        }

        public int lastQuantum(){
            return nodes[nodes.length - 1] - nodes[nodes.length - 2];
        }

        public boolean checkBelonging(float time, int quantum){
            return checkBelonging(time, quantum, quantum);
        }

        public boolean checkBelonging(float time, int quantumFrom, int quantumTo){
            return time >= (quantumFrom == 0 ? 0.0f : nodes[quantumFrom - 1])
                && time <= nodes[quantumTo];
        }
    }
}
