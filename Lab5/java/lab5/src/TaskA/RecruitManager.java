package TaskA;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class RecruitManager {
    private final int n;
    private final int N;
    private final SecureRandom rnd = new SecureRandom();
    private final Barrier barrier;
    private List<Recruits> recruitGroups = new ArrayList<>();

    public RecruitManager(int n, int N) {
        this.n = n;
        this.N = N;
        barrier = new Barrier(N + 1);
    }

    public void start() {
        for (int i = 0; i < N; i++) {
            Recruits group = new Recruits(n, rnd, barrier);
            group.setName("Recruits in Group " + i);
            recruitGroups.add(group);
        }
        for (int i = 0; i < N; i++) {
            recruitGroups.get(i).start();
        }
        while (true) {
            while (!barrier.isCompleted()) {
                continue;
            }
            if (checkGroups()) {
                System.out.println("\nAll groups finished turning around!\n");
                printGroups();
                stopAllGroups();
                try {
                    barrier.await();
                } catch (InterruptedException exception) {
                    // TO DO
                }
                break;
            } else {
                System.out.println("\nNot all groups are turned the right way\n");
                try {
                    barrier.await();
                } catch (InterruptedException exception) {
                    // TO DO
                }
            }
        }
    }

    private void stopAllGroups() {
        Recruits.turningCompleted = true;
    }

    public boolean checkGroups() {
        boolean allRight = true;
        for (int i = 0; i < N; i++) {
            if (!recruitGroups.get(i).first().getDirection()) {
                if (i != 0) {
                    if (recruitGroups.get(i - 1).last().getDirection()) {
                        recruitGroups.get(i).first().changeDirection();
                        recruitGroups.get(i - 1).last().changeDirection();
                        allRight = false;
                    }
                }
            }
        }
        return allRight;
    }

    private void printGroups() {
        for (int i = 0; i < N; i++) {
            recruitGroups.get(i).print();
        }
    }
}
