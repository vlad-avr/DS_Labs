package TaskA;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Recruits {
    private List<Recruit> recruits = new ArrayList<>();
    // private final SecureRandom rnd;

    public Recruits(int N, SecureRandom rnd){
        // this.rnd = rnd;
        for(int i = 0; i < N; i++){
            recruits.add(new Recruit(rnd.nextBoolean()));
        }
    }

    public void shuffle(){
        while(true){
            boolean allRight = true;
            for(int i = 0; i < recruits.size(); i++){
                if(!recruits.get(i).getDirection()){
                    if(i != 0){
                        if(recruits.get(i-1).getDirection()){
                            recruits.get(i-1).changeDirection();
                            recruits.get(i).changeDirection();
                            allRight = false;
                        }
                    }
                }else{
                    if(i != recruits.size()-1){
                        if(!recruits.get(i+1).getDirection()){
                            recruits.get(i+1).changeDirection();
                            recruits.get(i).changeDirection();
                            allRight = false;
                        }
                    }
                }
            }
            if(allRight){
                break;
            }
        }
    }
}
