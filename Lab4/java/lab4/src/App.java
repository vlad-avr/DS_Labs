import TaskA.Manager;

public class App {
    public static void main(String[] args) throws Exception {
        Manager manager = new Manager(10, 2, 2);
        manager.start();
    }
}
