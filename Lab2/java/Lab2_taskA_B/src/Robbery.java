import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

public class Robbery {
    private final Integer number_of_parts;
    private List<Part> parts_list;
    private final Integer max_sleep_time = 500;

    //Rand Generator
    SecureRandom rnd = new SecureRandom();

    Robbery(int num){
        number_of_parts = num;
        init_parts();
    }

    private void init_parts(){
        parts_list = new ArrayList<>();
        for(int i = 0; i < number_of_parts; i++){
            parts_list.add(new Part("Part " + (i + 1), rnd.nextInt((i+1)*10)));
        }
    }

    public void start_heist() throws InterruptedException{
        Robbers robbers = new Robbers();
        Thread ivanov = new Thread(()->{
            try{
                robbers.steal();
            }catch(InterruptedException exception){
                System.out.println("Oops, looks like someone got caught!");
            }
        });
        Thread petrov = new Thread(()->{
            try{
                robbers.pack();
            }catch(InterruptedException exception){
                System.out.println("Oops, looks like someone got caught!");
            }
        });
        Thread nechiporuk = new Thread(()->{
            try{
                robbers.count();
            }catch(InterruptedException exception){
                System.out.println("Oops, looks like someone got caught!");
            }
        });
        ivanov.start();
        petrov.start();
        nechiporuk.start();

        ivanov.join();
        petrov.join();
        nechiporuk.join();
    }

    private class Part{
        private final String name;
        private final Integer value;
        Part(String name, int val){
            this.name = name;
            this.value = val;
        }
    }

    private class Robbers{
        private final SynchronousQueue<Part> pack_steal_connect = new SynchronousQueue<>();
        private final SynchronousQueue<Part> count_pack_connect = new SynchronousQueue<>();

        public void steal() throws InterruptedException{
            int cur_part_id = 0;
            while(cur_part_id < parts_list.size()){
                System.out.println("Ivanov is stealing " + parts_list.get(cur_part_id).name);
                pack_steal_connect.put(parts_list.get(cur_part_id));
                cur_part_id++;
                Thread.sleep(rnd.nextInt(max_sleep_time) + 10);
            }
        }

        public void pack() throws InterruptedException{
            int cur_part_id = 0;
            while(cur_part_id < parts_list.size()){
                Part part_stolen = pack_steal_connect.take();
                System.out.println("Petrov is packing " + part_stolen.name);
                count_pack_connect.put(part_stolen);
                cur_part_id++;
                Thread.sleep(rnd.nextInt(max_sleep_time) + 10);
            }
        }

        public void count() throws InterruptedException{
            int cur_part_id = 0;
            int sum = 0;
            while(cur_part_id < parts_list.size()){
                Part part_packed = count_pack_connect.take();
                System.out.println("Nechiporuk is counting " + part_packed.name + " whih is worth " + part_packed.value + " $ \n");
                sum += part_packed.value;
                cur_part_id++;
                Thread.sleep(rnd.nextInt(max_sleep_time) + 10);
            }
            System.out.println("\n HEIST RESULTS: \nValue of stolen goodies : " + sum + " $ ");
        }

    }
}
