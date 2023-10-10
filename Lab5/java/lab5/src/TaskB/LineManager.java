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
    private CyclicBarrier barrier1 = new CyclicBarrier(5);
    private CyclicBarrier barrier2 = new CyclicBarrier(5);

    public LineManager(int N) {
        // this.N = N;
        for (int i = 0; i < 4; i++) {
            lines[i] = new Line(N, rnd, barrier1, barrier2);
            lines[i].setName("Line " + i);
        }
    }

    public void start() {
        work();
    }

    private void work() {
        for (int i = 0; i < 4; i++) {
            lines[i].start();
        }

        while (true) {
            try {
                barrier1.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println(e.getMessage());
            }

            boolean isEqualized = false;
            for (int i = 0; i < 2; i++) {
                for (int j = i + 1; j < 3; j++) {
                    for (int k = j + 1; k < 4; k++) {
                        if (((lines[k].countA() == lines[j].countA()) && (lines[j].countA() == lines[i].countA()))
                                && ((lines[k].countB() == lines[j].countB())
                                        && (lines[j].countB() == lines[i].countB()))) {
                            isEqualized = true;
                            System.out.println("\n At least 3 lines have equalised : " + lines[k].getName() + " and " + lines[j].getName() + " and " + lines[i].getName() + "\n");
                        }
                    }
                }
            }

            if (isEqualized) {
                for (int i = 0; i < 4; i++) {
                    lines[i].setEqualized();
                }
                try {
                    barrier2.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    System.out.println(e.getMessage());
                }
                break;
            } else {
                System.out.println("\n Lines haven't equalised yet!\n");
                try {
                    barrier1.reset();
                    barrier2.await();
                    barrier2.reset();
                } catch (InterruptedException | BrokenBarrierException e) {
                    System.out.println(e.getMessage());
                }
            }

        }
    }
}
