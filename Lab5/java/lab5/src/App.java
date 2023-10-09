import TaskA.RecruitManager;

public class App {
    public static void main(String[] args) throws Exception {
        RecruitManager manager = new RecruitManager(200, 4);
        manager.start();
    }
}
