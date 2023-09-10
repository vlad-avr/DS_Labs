public class ThreadRunnableManager {
    private Runnable AllThreads = null;
    private Runnable Thread1 = null;
    private Runnable Thread2 = null;

    public void onAllThreadChange(Runnable newCommand){
        AllThreads = newCommand;
    }

    public void onSliderValueChange(){
        if(AllThreads != null){
            AllThreads.run();
        }
    }

    public void onThread1End(){
        if(Thread1 != null){
            Thread1.run();
        }
    }

    public void onThread2End(Runnable newCommand){
        if(Thread2 != null){
            Thread2.run();
        }
    }

    public void Thread1Change(Runnable newCommand){
        Thread1 = newCommand;
    }

    
    public void Thread2Change(Runnable newCommand){
        Thread1 = newCommand;
    }
}
