package TaskB;

import java.security.SecureRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Gardener extends Thread{
    
    private Lock write_lock;
    private final SecureRandom rnd;
    private Garden garden;

    public Gardener(ReentrantReadWriteLock lock, Garden garden, SecureRandom rnd){
        this.garden = garden;
        this.rnd = rnd;
        write_lock = lock.writeLock();
        setName("Gardener");
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
                System.out.println("\nThe Gardener is watering the flowers");;
                water();
            }finally{
                write_lock.unlock();
            }
        }
    }

    private void water(){
        for(int i = 0; i < garden.garden_map.length; i++){
            for(int j = 0; j < garden.garden_map[i].length; j++){
                if(garden.garden_map[i][j] == 0){
                    garden.garden_map[i][j] = 1;
                }
            }
        }
    }
}
