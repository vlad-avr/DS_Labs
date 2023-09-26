package TaskA;

public class Bee implements Runnable{
    private HoneyPot pot;
    private Winnie winnie;
    private Integer wait_time = 600;
    private final String name;

    public Bee(HoneyPot pot, Winnie winnie, String name){
        this.pot = pot;
        this.winnie = winnie;
        this.name = name;
    }

    @Override
    public void run(){
        while(true){
            try{
                Thread.sleep(wait_time);
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
