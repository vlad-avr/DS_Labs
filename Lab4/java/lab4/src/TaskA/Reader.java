package TaskA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Reader extends Worker {
    public Reader(int lock_cap, int max_wait_time, FileLocker locker, Manager manager) {
        super(lock_cap, max_wait_time, locker, manager);
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
        System.out.println("\n" + getName() + " looking for people with name " + name);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            boolean is_found = false;
            while((line = reader.readLine()) != null){
                Entry entry = new Entry(line);
                if(entry.name.equals(name)){
                    System.out.println("\n" + getName() + " has found : " + entry.toString());
                    is_found = true;
                }
            }
            if(!is_found){
                System.out.println("\n" + getName() + " didn`t find people with name " + name);
            }
            reader.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void find_by_number(int number, File file) {
        System.out.println("\n" + getName() + " looking for people with number " + number);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            boolean is_found = false;
            while((line = reader.readLine()) != null){
                Entry entry = new Entry(line);
                if(entry.number == number){
                    System.out.println("\n" + getName() + " has found : " + entry.toString());
                    is_found = true;
                }
            }
            if(!is_found){
                System.out.println("\n" + getName() + " didn`t find people with number " + number);
            }
            reader.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
