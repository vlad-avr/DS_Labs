package TaskB;

import java.security.SecureRandom;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

public class BarberShop {
    private SecureRandom rnd = new SecureRandom();
    private Queue<String> queue = new ConcurrentLinkedDeque<>();
    private Semaphore semaphore = new Semaphore(1);
    private final Integer max_client_span;
    private final Integer min_client_span;
    private final Integer work_time;
    public BarberShop(Integer max, Integer min, Integer work_time){
        max_client_span = max;
        min_client_span = min;
        this.work_time = work_time;
    }

    public void work(){
        Thread barber = new Thread(new Runnable() {
            @Override
            public void run(){
                make_haircut();
            }
        });

        Thread clients = new Thread(new Runnable() {
            @Override
            public void run(){
                add_clients();
            }
        });

        barber.start();
        clients.start();
        try{
            barber.join();
            clients.join();
        }catch(InterruptedException exception){
            exception.printStackTrace();
        }
    }

    private void make_haircut(){
        while(true){
            while(queue.size() > 0){
                try{
                    semaphore.acquire();
                    String client = queue.poll();
                    semaphore.release();
                    System.out.println("\n Barber is making a haircut for " + client + "\n");
                    Thread.sleep(work_time);
                    System.out.println("\n Barber has finished workind with " + client + "\n");
                }catch(InterruptedException exception){
                    System.out.println("\n Barber must not be interrupted during his work!\n");
                }
            }
            synchronized(this){
                try{
                    wait();
                }catch(InterruptedException exception){
                    System.out.println("\n Barber was interrupted >:(\n");
                }
            }
        }
    }

    private void add_clients(){
        int counter = 0;
        while(true){
            try{
                Thread.sleep(rnd.nextInt(max_client_span) + min_client_span);
            }catch(InterruptedException exception){
                System.out.println("\n Cannot wait for new clients :D \n");
            }
            counter++;
            String client = "Client " + counter;
            try{
                semaphore.acquire();
                System.out.println("\n" + client + " has entered the queue\n");
                queue.add(client);
                semaphore.release();
            }catch(InterruptedException exception){
                System.out.println("\n Client couldn`t join the queue :(");
            }
            synchronized(this){
                notify();
            }
        }
    }
}
