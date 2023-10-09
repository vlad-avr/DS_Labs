package TaskA;

public class Barrier{
    private final int finalThreadCount;
    private int currentThreadCount;

    public Barrier(int N){
        finalThreadCount = N;
        currentThreadCount = 0;
    }

    synchronized public void reportCompletion(String threadName){
        currentThreadCount++;
    }

    public void waitForCompletion(){
        while(true){
            if(currentThreadCount == finalThreadCount){
                break;
            }
        }
    }

    public void reset(){
        currentThreadCount = 0;
    }
}
