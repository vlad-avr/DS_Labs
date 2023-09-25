import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

public class Robbery {
    private final Integer number_of_parts;
    private List<Part> parts_list;

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

    private class Part{
        private final String name;
        private final Integer value;
        Part(String name, int val){
            this.name = name;
            this.value = val;
        }
    }

    private class Robbers{
        private final SynchronousQueue<Part> petrov_ivanov_connect = new SynchronousQueue<>();
        private final SynchronousQueue<Part> nechiporuk_petrov_connect = new SynchronousQueue<>();

    }
}
