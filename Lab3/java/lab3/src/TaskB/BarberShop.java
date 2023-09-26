package TaskB;

import java.security.SecureRandom;

public class BarberShop {
    private Barber barber = new Barber();
    private SecureRandom rnd = new SecureRandom();
    private final Integer max_client_span;
    private final Integer min_client_span;
    public BarberShop(Integer max, Integer min){
        max_client_span = max;
        min_client_span = min;
    }

    public void work(){
        int counter = 0;
        while(true){
            try{
                Thread.sleep(rnd.nextInt(max_client_span) + min_client_span);
            }catch(InterruptedException exception){
                System.out.println("\n Cannot wait for new clients :D \n");
            }
            counter++;
            Thread client = new Thread(new Client(barber, "Client " + counter));
            client.start();
            try{
                client.join();
            }catch(InterruptedException exception){
                System.out.println("\n Client was interrupted while joining the queue :( \n");
            }
        }
    }
}
