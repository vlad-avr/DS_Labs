
import TaskA.HoneyPotFeast;
import TaskB.BarberShop;

public class App {
    public static void main(String[] args) throws Exception {
        HoneyPotFeast feast = new HoneyPotFeast(50, 400);
        feast.start_feast();
        BarberShop shop = new BarberShop(800, 100, 50);
        shop.work();
    }
}
