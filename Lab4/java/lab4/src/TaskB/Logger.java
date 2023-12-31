package TaskB;

import java.security.SecureRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Logger extends Thread{
    
    private Lock read_lock;
    private Garden garden;
    private final SecureRandom rnd;

    public Logger(ReentrantReadWriteLock lock, Garden garden, SecureRandom rnd){
        read_lock = lock.readLock();
        this.garden = garden;
        this.rnd = rnd;
        setName("Logger");
    }

    @Override
    public void run(){
        while(true){
            try{
                sleep(rnd.nextInt(1000) + 1000);
            }catch(InterruptedException exception){
                System.out.println(exception.getMessage());
            }

            try{
                read_lock.lock();
                System.out.println("\nThe logger is logging logs in the console:");
                log();
            }finally{
                read_lock.unlock();
            }
        }
    }

    private void log(){
        System.out.println("\n");
        for(int i = 0; i < garden.garden_map.length; i++){
            for(int j = 0; j < garden.garden_map[i].length; j++){
                if(garden.garden_map[i][j] == 0){
                    System.out.print(String.format("%20s","dehydrated"));
                }else if(garden.garden_map[i][j] == 1){
                    System.out.print(String.format("%20s","growing"));
                }else if(garden.garden_map[i][j] == 2){
                    System.out.print(String.format("%20s","blooming"));
                }else{
                    System.out.print(String.format("%20s","unknown"));
                }
            }
            System.out.println("\n");
        }
    }
}
