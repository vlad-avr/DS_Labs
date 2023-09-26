package TaskA;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HoneyPotFeast {
    private final int n_bees;
    private final int N_honey;

    public HoneyPotFeast(int n_bees, int N_honey){
        this.n_bees = n_bees;
        this.N_honey = N_honey;
    }

    public void start_feast(){
        HoneyPot pot = new HoneyPot(N_honey);
        Winnie winnie = new Winnie(pot);
        new Thread(winnie).start();
        ExecutorService service = Executors.newFixedThreadPool(n_bees);
        for(int i = 0; i < n_bees; i++){
            Runnable bee = new Bee(pot, winnie,"Bee " + i);
            service.execute(bee);
        }
        service.shutdown();
    }
}
