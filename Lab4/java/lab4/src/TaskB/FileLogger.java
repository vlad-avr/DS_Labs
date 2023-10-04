package TaskB;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileLogger extends Thread {
    private final String file_path = "D:\\Java\\DS_Labs\\Lab4\\java\\lab4\\src\\TaskB\\logger.txt";
    private Lock read_lock;
    private final SecureRandom rnd;
    private Garden garden;

    public FileLogger(ReentrantReadWriteLock lock, Garden garden, SecureRandom rnd) {
        this.garden = garden;
        this.rnd = rnd;
        read_lock = lock.readLock();
        setName("File Logger");
        try{
            File file = new File(file_path);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.close();
        }catch(IOException exception){
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(rnd.nextInt(1000) + 1000);
            } catch (InterruptedException exception) {
                System.out.println(exception.getMessage());
            }

            try {
                read_lock.lock();
                System.out.println("\n File Logger is logging log to the logs file");
                log();
            } finally {
                read_lock.unlock();
            }
        }
    }

    private void log() {
        File file = new File(file_path);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            for (int i = 0; i < garden.garden_map.length; i++) {
                for (int j = 0; j < garden.garden_map[i].length; j++) {
                    if (garden.garden_map[i][j] == 0) {
                        writer.write("\tdehydrated");
                    } else if (garden.garden_map[i][j] == 1) {
                        writer.write("\tgrowing");
                    } else if (garden.garden_map[i][j] == 2) {
                        writer.write("\tblooming");
                    } else {
                        writer.write("\tUnknown");
                    }
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
