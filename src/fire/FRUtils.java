package fire;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureAtlas;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public final class FRUtils{

    public static void colors(Color[] colors, Color... color){
        System.arraycopy(color, 0, colors, 0, colors.length);
    }

    public static TextureAtlas.AtlasRegion find(String name){
        return Core.atlas.find("fire-" + name);
    }

    /** 1 to 10, that should be enough. */
    public static String toNumeral(int n){
        return Core.bundle.get("fire.numerals").split("\\|")[n - 1];
    }

    public static int round(float v, int step){
        return Math.round(v / step) * step;
    }

    public static Field field(Class<?> type, String name) throws NoSuchFieldException{
        var field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    public static class TimeNode{

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
                && time < nodes[quantumTo];
        }
    }

    public static class AtomicFloat{

        private final AtomicInteger bits;

        public AtomicFloat(float initialValue){
            bits = new AtomicInteger(Float.floatToIntBits(initialValue));
        }

        public float get(){
            return Float.intBitsToFloat(bits.get());
        }

        public void set(float newValue){
            bits.set(Float.floatToIntBits(newValue));
        }

        public boolean compareAndSet(float expectValue, float newValue){
            return bits.compareAndSet(Float.floatToIntBits(expectValue), Float.floatToIntBits(newValue));
        }

        public float addAndGet(float delta){
            float prev, next;
            do{
                prev = get();
                next = prev + delta;
            }while(!compareAndSet(prev, next));
            return next;
        }
    }
}
