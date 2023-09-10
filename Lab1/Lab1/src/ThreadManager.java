public class ThreadManager {
    public final Thread[] threads = new Thread[2];
    public final int THREAD1 = 0;
    public final int THREAD2 = 1;


    public boolean isRunning(int threadID){
        return (threads[threadID] != null && threads[threadID].isAlive());
    }

    public boolean areRunning(){
        for(Thread thr : threads){
            if(thr != null && thr.isAlive()){
                return true;
            }
        }
        return false;
    }

    public void startAllThreads(){
        for(Thread thr : threads){
            if(thr != null){
                thr.start();
            }
        }
    }

    public void stopAllThreads(){
        for(Thread thr : threads){
            if(thr != null){
                thr.interrupt();
            }
        }
    }

    public void startThread(int threadID){
        Thread thr = threads[threadID];
        if(thr != null){
            thr.start();
        }
    }

    public void stopThread(int threadID){
        Thread thr = threads[threadID];
        if(thr != null){
            thr.interrupt();
        }
    }

    public void setThread(int id, Thread thr){
        threads[id] = thr;
    }
}