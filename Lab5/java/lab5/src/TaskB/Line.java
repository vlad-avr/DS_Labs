package TaskB;

import java.security.SecureRandom;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Line extends Thread {
    private String line;
    private int aCount = 0;
    private int bCount = 0;
    private boolean isEqualized = false;
    private CyclicBarrier barrier1;
    private CyclicBarrier barrier2;
    private SecureRandom rnd;

    public Line(int N, SecureRandom rnd, CyclicBarrier barrier1, CyclicBarrier barrier2) {
        this.rnd = rnd;
        line = "";
        this.barrier1 = barrier1;
        this.barrier2 = barrier2;
        int Acounter = 0;
        int Bcounter = 0;
        for (int i = 0; i < N; i++) {
            if (Acounter < N / 2 && Bcounter < N / 2) {
                int rndInd = rnd.nextInt(4);
                switch (rndInd) {
                    case 0:
                        line += "A";
                        aCount++;
                        Acounter++;
                        break;
                    case 1:
                        line += "C";
                        Acounter++;
                        break;
                    case 2:
                        line += "B";
                        bCount++;
                        Bcounter++;
                        break;
                    case 3:
                        line += "D";
                        Bcounter++;
                        break;
                }
            } else {
                if (Acounter >= N / 2) {
                    if (rnd.nextBoolean()) {
                        line += "B";
                        bCount++;
                    } else {
                        line += "D";
                    }
                    Bcounter++;
                } else if (Bcounter >= N / 2) {
                    if (rnd.nextBoolean()) {
                        line += "A";
                        aCount++;
                    } else {
                        line += "C";
                    }
                    Acounter++;
                }
            }
        }
    }

    public synchronized int countA() {
        return aCount;
    }

    public synchronized int countB() {
        return bCount;
    }

    private void shuffle() {
        int rndInd = rnd.nextInt(line.length());
        switch (line.charAt(rndInd)) {
            case 'A':
                if (rnd.nextBoolean()) {
                    line = line.substring(0, rndInd) + 'C' + line.substring(rndInd + 1);
                    aCount--;
                }
                break;
            case 'C':
                if (rnd.nextBoolean()) {
                    line = line.substring(0, rndInd) + 'A' + line.substring(rndInd + 1);
                    aCount++;
                }
                break;
            case 'B':
                if (rnd.nextBoolean()) {
                    line = line.substring(0, rndInd) + 'D' + line.substring(rndInd + 1);
                    bCount--;
                }
                break;
            case 'D':
                if (rnd.nextBoolean()) {
                    line = line.substring(0, rndInd) + 'B' + line.substring(rndInd + 1);
                    bCount++;
                }
                break;
        }
    }

    public void setEqualized() {
        this.isEqualized = true;
    }

    @Override
    public void run() {
        while (true) {
            try {
                print("\n" + getName() + " started shuffling!\n");
                shuffle();
                barrier1.await();
                barrier2.await();
                print("\n" + getName() + " finished!\n");
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println(e.getMessage());
            }
            if (isEqualized) {
                break;
            }
        }
    }

    public void print(String prompt) {
        System.out.println(prompt + "\n" + getName() + " : " + line);
    }
}
