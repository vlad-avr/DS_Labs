
import TaskA.HoneyPotFeast;
import TaskB.BarberShop;

public class App {
    public static void main(String[] args) throws Exception {
        // HoneyPotFeast feast = new HoneyPotFeast(50, 400);
        // feast.start_feast();
        //System.out.println("null");
        BarberShop shop = new BarberShop(1000, 2000, 100);
        shop.work();
    }
}
