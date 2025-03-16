package fire;

public final class FRUtils{

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
