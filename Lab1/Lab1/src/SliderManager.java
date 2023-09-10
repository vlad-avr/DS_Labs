public class SliderManager {
    private static final int SLEEP_FOR_TICKS = 100;
    private Object syncObject;
    private SliderUI SUI;
    public void setSUI(SliderUI targetSUI){
        SUI = targetSUI;
        syncObject = targetSUI;
    }

    private Thread initThread(int thrTargetVal){
        return new Thread(()->{
            while(true){
                synchronized(syncObject){
                    if(Thread.interrupted())
                    return;
                    SUI.RManager.onThreadsRunnableChange();
                    int prevVal = SUI.slider.getValue();
                    if(prevVal < thrTargetVal){
                        SUI.slider.setValue(prevVal+1);
                    }
                    else if(prevVal > thrTargetVal){
                        SUI.slider.setValue(prevVal-1);
                    }
                    try{
                        Thread.sleep(SLEEP_FOR_TICKS);
                    }
                    catch(InterruptedException exception){
                        return;
                    }
                }
            }
        });
    }

    public Thread getThread(int thrTargetVal, int prior, int thrId){
        Thread newThread;
        if(thrId == 1){
            newThread = initThread(thrTargetVal);
        }
        else if(thrId == 2){
            newThread = initThread(thrTargetVal);
        }
        else{
            return null;
        }
        newThread.setPriority(prior);
        newThread.setName("Thread" + String.valueOf(thrId));
        return newThread;
    }
}
