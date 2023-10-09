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
        barrier = new Barrier(N);
    }

    public void start() {
        for (int i = 0; i < N; i++) {
            Recruits group = new Recruits(n, rnd, barrier);
            group.setName("Recruits in Group " + i);
            recruitGroups.add(group);
        }
        while (true) {
            barrier.waitForCompletion();
            if(checkGroups()){
                break;
            }else{
                barrier.reset();
            }
        }
    }

    public boolean checkGroups(){
        boolean allRight = true;
        for(int i = 0; i < N; i++){
            if(!recruitGroups.get(i).first().getDirection()){
                if(i != 0){
                    if(recruitGroups.get(i-1).last().getDirection()){
                        recruitGroups.get(i).first().changeDirection();
                        recruitGroups.get(i-1).last().changeDirection();
                        allRight = false;
                    }
                }
            }
        }
        return allRight;
    }
}
