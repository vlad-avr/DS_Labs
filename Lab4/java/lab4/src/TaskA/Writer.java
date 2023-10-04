package TaskA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Writer extends Worker {
    public Writer(int lock_cap, int max_wait_time, FileLocker locker, Manager manager) {
        super(lock_cap, max_wait_time, locker, manager);
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(rnd.nextInt(max_wait_time) + 200);
            } catch (InterruptedException exception) {
                System.out.println(exception.getMessage());
            }
            int flip = rnd.nextInt(3);
            while (!locker.is_free()) {
                continue;
            }
            try {
                locker.acquire_lock(lock_cap);
                File file = manager.get_file();
                if (flip == 0) {
                    delete_entry(manager.get_rand_number(), file);
                } else {
                    add_entry(file);
                }
                locker.release_lock(lock_cap);
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private void delete_entry(int number, File file) {
        List<Entry> list = new ArrayList<>();
        System.out.println("\n" + getName() + " is trying to delete a person with number " + number);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                Entry entry = new Entry(line);
                if (entry.number != number) {
                    list.add(entry);
                } else {
                    System.out.println("\n" + getName() + " has found and deleted " + entry.toString());
                    manager.remove_number_from_list(number);
                }
            }
            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < list.size(); i++) {
                writer.write(list.get(i).toString());
            }
            writer.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void add_entry(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            Entry entry = new Entry(manager.get_rand_name(), rnd.nextInt(1000) + 1);
            System.out.println("\n" + getName() + " added to the databse person : " + entry.toString());
            writer.write(entry.toString());
            manager.add_number_to_list(entry.number);
            writer.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
