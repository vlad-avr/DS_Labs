public class ThreadManager {
    private final int thread1ID = 0;
    private final int thread2ID = 1;
    private final Thread[] threads = new Thread[2];


    public void execute(int threadID){
        Thread thr = threads[threadID];
        if(thr != null){
            thr.start();
        }
    }

    public void executeAll(){
        for(Thread thr : threads){
            if(thr != null){
                thr.start();
            }
        }
    }

    public void end(int threadID){
        Thread thr = threads[threadID];
        if(thr != null){
            thr.interrupt();
        }
    }

    public void endAll(){
        for(Thread thr : threads){
            if(thr != null){
                thr.interrupt();
            }
        }
    }

    public void setPriority(int threadID, int newPriority) {
        Thread thr = threads[threadID];
        if (thr != null) {
            thr.setPriority(newPriority);
        }
    }

    public boolean areWorking(){
        for(Thread thr : threads){
            if(thr != null && thr.isAlive()){
                return true;
            }
        }
        return false;
    }

    public boolean isWorking(int threadID){
        if(threads[threadID] != null && threads[threadID].isAlive()){
            return true;
        }
        return false;
    }
}
