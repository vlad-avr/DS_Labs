import javax.swing.JSlider;

public class MyThread extends Thread{
    private JSlider slider;
    private int targetVal;

    MyThread(JSlider obj, int val){
        slider = obj;
        targetVal = val;
    }

    @Override
    public void run() {
        while(!interrupted()) {
            synchronized(slider){
                int prevValue = slider.getValue();

                if (prevValue < targetVal) {
                    slider.setValue(prevValue + 1);
                }else if (prevValue > targetVal) {
                    slider.setValue(prevValue - 1);
                }
                try {
                    sleep(10);
                } catch (InterruptedException exception) {
                    return;
                }
            }
        }
    }
}
