package TaskA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;

public class Reader extends Thread {
    private final int lock_cap;
    private final int max_wait_time;
    private FileLocker locker;
    private Manager manager;
    private final SecureRandom rnd = new SecureRandom();

    public Reader(int lock_cap, int max_wait_time, FileLocker locker, Manager manager) {
        this.lock_cap = lock_cap;
        this.max_wait_time = max_wait_time;
        this.locker = locker;
        this.manager = manager;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(rnd.nextInt(max_wait_time) + 100);
            } catch (InterruptedException exception) {
                System.out.println(exception.getMessage());
            }
            int flip = rnd.nextInt(2);
            while (locker.is_full()) {
                continue;
            }
            try {
                locker.acquire_lock(lock_cap);
                File file = manager.get_file();
                if (flip == 0) {
                    find_by_name(manager.get_rand_name(), file);
                } else {
                    find_by_number(manager.get_rand_number(), file);
                }
                locker.release_lock(lock_cap);
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private void find_by_name(String name, File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                Entry entry = new Entry(line);
                if(entry.name.equals(name)){
                    System.out.println("\n" + getName() + " has found what he was looking for : " + entry.toString());
                    reader.close();
                    return;
                }
            }
            reader.close();
            System.out.println("\n" + getName() + " didn`t find person with name " + name);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void find_by_number(int number, File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                Entry entry = new Entry(line);
                if(entry.number == number){
                    System.out.println("\n" + getName() + " has found what he was looking for : " + entry.toString());
                    reader.close();
                    return;
                }
            }
            reader.close();
            System.out.println("\n" + getName() + " didn`t find person whose number is " + number);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
