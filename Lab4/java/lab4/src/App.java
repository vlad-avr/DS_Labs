import TaskA.Manager;
import TaskB.Garden;

public class App {
    public static void main(String[] args) throws Exception {
        Manager manager = new Manager(10, 2, 2);
        manager.start();
        Garden garden = new Garden(5, 5);
        garden.start();
    }
}
