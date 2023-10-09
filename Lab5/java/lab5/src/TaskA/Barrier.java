package TaskA;

public class Barrier {
    private final int finalThreadCount;
    private int currentThreadCount;

    public Barrier(int N) {
        finalThreadCount = N;
        currentThreadCount = 0;
    }

    public synchronized void reportCompletion(String threadName) {
        System.out.println("\n" + threadName + " finished turning around\n");
        currentThreadCount++;
    }

    public synchronized boolean isCompleted() {
        if(currentThreadCount == finalThreadCount){
            return true;
        }else{
            return false;
        }
    }

    public synchronized boolean isReset(){
        if(currentThreadCount == 0){
            return true;
        }else{
            return false;
        }
    }

    public synchronized void reset() {
        // notifyAll();
        currentThreadCount = 0;
    }
}
