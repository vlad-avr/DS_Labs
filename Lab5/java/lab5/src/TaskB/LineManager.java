package TaskB;

import java.security.SecureRandom;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;

public class LineManager {
    // private int N;
    private Line[] lines = new Line[4];
    private final SecureRandom rnd = new SecureRandom();
    private CyclicBarrier barrier = new CyclicBarrier(3);

    public LineManager(int N) {
        // this.N = N;
        for (int i = 0; i < 4; i++) {
            lines[i] = new Line(N, rnd, barrier);
            lines[i].setName("Line " + i);
        }
        for (int i = 0; i < 4; i++) {
            if (i != 3) {
                lines[i].setNextLine(lines[i + 1]);
            } else {
                lines[i].setNextLine(lines[0]);
            }
        }
    }

    public void start() {
        work();
    }

    private void work() {
        for (int i = 0; i < 4; i++) {
            lines[i].start();
        }
    }
}
