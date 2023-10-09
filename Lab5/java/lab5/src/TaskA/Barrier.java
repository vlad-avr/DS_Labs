package TaskA;

public class Barrier {
    private final int finalThreadCount;
    private int currentThreadCount;

    public Barrier(int N) {
        finalThreadCount = N;
        currentThreadCount = 0;
    }

    synchronized public void reportCompletion(String threadName) {
        System.out.println("\n" + threadName + " finished turning around\n");
        currentThreadCount++;
    }

    synchronized public void waitForCompletion() {
        while (true) {
            if (currentThreadCount == finalThreadCount) {
                break;
            }
        }
    }

    synchronized public void reset() {
        notifyAll();
        currentThreadCount = 0;
    }
}
