package TaskB;

import java.security.SecureRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Nature extends Thread {

    private Lock write_lock;
    private Garden garden;
    private final SecureRandom rnd;

    public Nature(ReentrantReadWriteLock lock, Garden garden, SecureRandom rnd) {
        write_lock = lock.writeLock();
        this.garden = garden;
        this.rnd = rnd;
        setName("Nature");
    }

    @Override
    public void run() {
        while(true){
            try{
                sleep(rnd.nextInt(1000) + 500);
            }catch(InterruptedException exception){
                System.out.println(exception.getMessage());
            }

            try{
                write_lock.lock();
                System.out.println("\n The lady Nature is doing her thing shuffling all the flowers in the garden.");
                shuffle_states();
            }finally{
                write_lock.unlock();
            }
        }
    }

    private void shuffle_states(){
        for(int i = 0; i < garden.garden_map.length; i++){
            for(int j = 0; j < garden.garden_map[i].length; j++){
                garden.garden_map[i][j] = rnd.nextInt(3);
            }
        }
    }
}
