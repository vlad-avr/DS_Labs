package TaskB;

import java.security.SecureRandom;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Line extends Thread {
    private String line;
    private int aCount = 0;
    private int bCount = 0;
    private Line nextLine;
    private boolean isEqualized = false;
    private CyclicBarrier barrier;

    public Line(int N, SecureRandom rnd, CyclicBarrier barrier) {
        line = "";
        this.barrier = barrier;
        for (int i = 0; i < N / 2; i++) {
            if (rnd.nextBoolean()) {
                line += "A";
                aCount++;
            } else {
                line += "C";
            }
        }
        for (int i = 0; i < (N / 2 + N % 2); i++) {
            if (rnd.nextBoolean()) {
                line += "B";
                bCount++;
            } else {
                line += "D";
            }
        }
    }

    public void setNextLine(Line nextLine) {
        this.nextLine = nextLine;
    }

    public synchronized int countA() {
        return aCount;
    }

    public synchronized int countB() {
        return bCount;
    }

    private void equalise() {
        while (true) {
            boolean equalized = true;
            if (nextLine.countA() > aCount) {
                int ind = line.indexOf("C");
                if (ind != -1) {
                    line = line.substring(0, ind) + "A" + line.substring(ind + 1);
                    aCount++;
                }
                equalized = false;
            } else if (nextLine.countA() < aCount) {
                int ind = line.indexOf("A");
                if (ind != -1) {
                    line = line.substring(0, ind) + "C" + line.substring(ind + 1);
                    aCount--;
                }
                equalized = false;
            }
            if (nextLine.countB() > bCount) {
                int ind = line.indexOf("D");
                if (ind != -1) {
                    line = line.substring(0, ind) + "B" + line.substring(ind + 1);
                    bCount++;
                }
                equalized = false;
            } else if (nextLine.countB() < bCount) {
                int ind = line.indexOf("B");
                if (ind != -1) {
                    line = line.substring(0, ind) + "D" + line.substring(ind + 1);
                    bCount--;
                }
                equalized = false;
            }
            print();
            if (equalized) {
                isEqualized = true;
            }
            if (nextLine.equalized() && equalized) {
                break;
            }
        }
    }

    public synchronized Boolean equalized() {
        return isEqualized;
    }

    @Override
    public void run() {
        try {
            System.out.println("\n" + getName() + " started equalising!\n");
            equalise();
            barrier.await();
            System.out.println("\n" + getName() + " finished!\n");
            print();
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println(e.getMessage());
        }
    }

    public void print() {
        System.out.println("\n" + getName() + " : " + line);
    }
}
