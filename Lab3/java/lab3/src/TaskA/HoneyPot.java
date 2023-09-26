package TaskA;

public class HoneyPot {
    private final Integer max_honey;
    private Integer cur_honey = 0;

    public HoneyPot(Integer max_honey){
        this.max_honey = max_honey;
    }

    public synchronized void fill_pot(String bee_name){
        while(is_full()){
            try{
                wait();
            }catch(InterruptedException exception){
                System.out.println("\n The time for waiting is over\n");
            }
        }
        cur_honey++;
        System.out.println("\n" + bee_name + " has brought 1 point of honey to the Pot, it`s now " + cur_honey + "\\" + max_honey + " full\n");
    }

    public synchronized void empty_pot(){
        cur_honey = 0;
        System.out.println("\n Winnie ate all the honey from the pot!\n");
        notifyAll();
    }

    public synchronized boolean is_full(){
        return cur_honey >= max_honey;
    }
}
