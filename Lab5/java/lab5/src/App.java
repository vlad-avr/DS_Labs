import TaskA.RecruitManager;
import TaskB.LineManager;

public class App {
    public static void main(String[] args) throws Exception {
        RecruitManager manager = new RecruitManager(200, 4);
        manager.start();
        //LineManager lineManager = new LineManager(20);
        // lineManager.start();
    }
}
