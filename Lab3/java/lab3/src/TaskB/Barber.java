package TaskB;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Queue;

public class Barber implements Runnable{
    private final Queue<Client> queue = new ConcurrentLinkedDeque<>();
    private final Semaphore semaphore = new Semaphore(1);
    private final Integer work_time = 600;

    public synchronized void add_to_queue(Client client){
        System.out.println("\n NOTIFIED");
        System.out.println("\n" + client.name + " has entered the queue.\n");
        queue.add(client);
        if(queue.size() == 1){
            System.out.println("\n NOTIFIED");
            this.notify();
        }
    }

    private synchronized void make_haircut(){
        try{
            semaphore.acquire();
            Client client = queue.poll();
            System.out.println("\n Barber is making a haircut for " + client.name + "\n");
            Thread.sleep(work_time);
            System.out.println("\n Barber has finished workind with " + client.name + "\n");
            semaphore.release();
        }catch(InterruptedException exception){
            System.out.println("\n Barber must not be interrupted during his work!\n");
        }
    }

    @Override
    public void run(){
        while(queue.size() == 0){
            try{
                wait();
            }catch(InterruptedException exception){
                System.out.println("\n Barber's sleep was interrupted :( \n");
            }
        }
        make_haircut();
    }
}
