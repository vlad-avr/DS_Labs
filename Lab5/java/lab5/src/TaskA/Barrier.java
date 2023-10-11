package TaskA;

public class Barrier {
    private final int finalThreadCount;
    private volatile int currentThreadCount;

    public Barrier(int N) {
        finalThreadCount = N;
        currentThreadCount = 0;
    }

    public synchronized void await(String threadName) throws InterruptedException{
        System.out.println("\n" + threadName + " finished turning around\n");
        await();
    }

    public synchronized void await() throws InterruptedException{
        currentThreadCount++;
        if(currentThreadCount < finalThreadCount){
            this.wait();
        }
        currentThreadCount = 0;
        notifyAll();
    }

    public synchronized Boolean isCompleted(){
        if(currentThreadCount == finalThreadCount-1){
            return true;
        }else{
            return false;
        }
    }

    // public synchronized void reportCompletion(String threadName) {
    //     
    //     currentThreadCount++;
    // }

    // public synchronized boolean isCompleted() {
    //     if(currentThreadCount == finalThreadCount){
    //         return true;
    //     }else{
    //         return false;
    //     }
    // }

    // public synchronized boolean isReset(){
    //     if(currentThreadCount == 0){
    //         return true;
    //     }else{
    //         return false;
    //     }
    // }

    // public synchronized void reset() {
    //     currentThreadCount = 0;
    // }
}
