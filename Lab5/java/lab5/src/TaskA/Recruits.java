package TaskA;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Recruits extends Thread {
    private List<Recruit> recruits = new ArrayList<>();
    private Barrier barrier;
    // private final SecureRandom rnd;

    public Recruits(int N, SecureRandom rnd, Barrier barrier) {
        // this.rnd = rnd;
        this.barrier = barrier;
        for (int i = 0; i < N; i++) {
            recruits.add(new Recruit(rnd.nextBoolean()));
        }
    }

    public void shuffle() {
        System.out.println("\n" + getName() + "started turning around \n");
        while (true) {
            boolean allRight = true;
            for (int i = 0; i < recruits.size(); i++) {
                if (!recruits.get(i).getDirection()) {
                    if (i != 0) {
                        if (recruits.get(i - 1).getDirection()) {
                            recruits.get(i - 1).changeDirection();
                            recruits.get(i).changeDirection();
                            allRight = false;
                        }
                    }
                } else {
                    if (i != recruits.size() - 1) {
                        if (!recruits.get(i + 1).getDirection()) {
                            recruits.get(i + 1).changeDirection();
                            recruits.get(i).changeDirection();
                            allRight = false;
                        }
                    }
                }
            }
            if (allRight) {
                break;
            }
        }
        barrier.reportCompletion(getName());
    }

    public Recruit first(){
        return recruits.get(0);
    }

    public Recruit last(){
        return recruits.get(recruits.size()-1);
    }

    @Override
    public void run() {
        while (true) {
            shuffle();
            try {
                wait();
            } catch (InterruptedException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}
