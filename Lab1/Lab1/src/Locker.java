public class Locker {
    private final static int SLEEP_FOR_TICKS = 100;
    public static int semaphore = 1;
    private SliderUI SUI;
    public void setSUI(SliderUI targetSUI){
        SUI = targetSUI;
    }

    synchronized boolean lock(){
        while(semaphore != 0){
            try{
                wait();
            }
            catch(InterruptedException exception){
                return true;
            }
        }
        semaphore--;
        return false;
    }

    synchronized void unlock(){
        semaphore++;
        notify();
    }

    public Thread getThread(int thrTargetVal, int prior, int thrId){
        Thread newThread;
        if(thrId == 1){
            newThread = createThread(thrTargetVal, SUI.RManager::onThread1Complete);
        }
        else if(thrId == 2){
            newThread = createThread(thrTargetVal, SUI.RManager::onThread2Complete);
        }
        else{
            return null;
        }
        newThread.setPriority(prior);
        newThread.setName("Thread" + String.valueOf(thrId));
        return newThread;
    }


    private Thread createThread(int thrTargetVal, Runnable onComplete){
        return new Thread(()->{
            if(lock())
            return;
            int curVal;
            SUI.RManager.onThreadsRunnableChange();
            try{
                while(SUI.slider.getValue() < thrTargetVal){
                    curVal = SUI.slider.getValue();
                    if(Thread.interrupted()){
                        return;
                    }
                    SUI.slider.setValue(curVal+1);
                    try{
                        Thread.sleep(SLEEP_FOR_TICKS);
                    }
                    catch (InterruptedException exception){
                        return;
                    }
                }
                while(SUI.slider.getValue() > thrTargetVal){
                    curVal = SUI.slider.getValue();
                    if(Thread.interrupted()){
                        return;
                    }
                    SUI.slider.setValue(curVal-1);
                    try{
                        Thread.sleep(SLEEP_FOR_TICKS);
                    }
                    catch (InterruptedException exception){
                        return;
                    }
                }
            }
            finally{
                unlock();
                onComplete.run();
            }
        });
    }
}
