package TaskA;

import java.security.SecureRandom;

public class Bee implements Runnable{
    private HoneyPot pot;
    private Winnie winnie;
    private Integer wait_time = 600;
    private final String name;

    private SecureRandom rnd = new SecureRandom();
    public Bee(HoneyPot pot, Winnie winnie, String name){
        this.pot = pot;
        this.winnie = winnie;
        this.name = name;
    }

    @Override
    public void run(){
        while(true){
            try{
                Thread.sleep(rnd.nextInt(wait_time) + 100);
            }catch(Exception exception){
                System.out.println("\n Bees cannot sleep! At least this one couldn`t!\n");
            }
            pot.fill_pot(this.name);
            if(pot.is_full()){
                winnie.wakeup();
            }
        }
    }
}
