package TaskA;

public class Winnie implements Runnable{
    private Boolean is_asleep;
    private HoneyPot pot;
    public Winnie(HoneyPot pot){
        this.pot = pot;
        is_asleep = true;
    }

    @Override
    public void run(){
        while(true){
            synchronized(this){
                while(is_asleep){
                    try{
                        wait();
                    }catch(InterruptedException exception){
                        System.out.println("\n I cannot wait for the honey!\n");
                    }
                }
                pot.empty_pot();
                is_asleep = true;
                notifyAll();
            }
        }
    }

    public synchronized void wakeup(){
        is_asleep = false;
        notify();
    }
}
