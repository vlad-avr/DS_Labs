package TaskB;

public class Client implements Runnable{
    private final Barber barber;
    public final String name;

    public Client(Barber barber, String name){
        this.barber = barber;
        this.name = name;
    }

    @Override
    public void run(){
        barber.add_to_queue(this);
    }
}
