package TaskA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Manager {
    private final String file_path = "D:\\Java\\DS_Labs\\Lab4\\java\\lab4\\src\\TaskA\\list.txt";
    private final String[] names_list = { "John", "Kate", "Billy", "Susan", "Anna", "Meg", "Ted", "Greg", "Mike",
            "Mark" };
    private List<Integer> number_list = new ArrayList<>();
    private final SecureRandom rnd = new SecureRandom();
    private final int reader_count;
    private final int writer_count;

    public Manager(int N, int readers, int writers) {
        init_db(N);
        reader_count = readers;
        writer_count = writers;
    }

    private void init_db(int N) {
        try {
            File file = new File(file_path);
            FileWriter file_writer = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(file_writer);
            for (int i = 0; i < N; i++) {
                int num = rnd.nextInt(1000) + 1;
                number_list.add(num);
                Entry entry = new Entry(names_list[rnd.nextInt(names_list.length)], num);
                writer.write(entry.toString());
            }
            writer.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public String get_rand_name(){
        int i = rnd.nextInt(names_list.length);
        return names_list[i];
    }

    public int get_rand_number(){
        int i = rnd.nextInt(number_list.size());
        return number_list.get(i);
    }

    public File get_file(){
        return new File(file_path);
    }

    public void remove_number_from_list(int number){
        for(int i = 0; i < number_list.size(); i++){
            if(number_list.get(i) == number){
                number_list.remove(i);
                return;
            }
        }
        System.out.println("\nNumber " + number + " is not present in databse!");
    }

    public void add_number_to_list(int number){
        number_list.add(number);
    }
}
