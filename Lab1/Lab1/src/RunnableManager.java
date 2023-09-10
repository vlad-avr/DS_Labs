public class RunnableManager {
    private static Runnable thread1Runnable = null;
    private Runnable thread2Runnable = null;
    private Runnable threadsRunnable = null;

    void setThread1Runnable(Runnable runnableCommand){
        thread1Runnable = runnableCommand;
    }
    void setThread2Runnable(Runnable runnableCommand){
        thread2Runnable = runnableCommand;
    }
    void onThread1Complete(){
        if(thread1Runnable != null){
            thread1Runnable.run();
        }
    }
    void onThread2Complete(){
        if(thread2Runnable != null){
            thread2Runnable.run();
        }
    }

    void setThreadsRunnable(Runnable runnableCommand) {
        threadsRunnable = runnableCommand;
    }

    void onThreadsRunnableChange(){
        if(threadsRunnable != null){
            threadsRunnable.run();
        }
    }
}
