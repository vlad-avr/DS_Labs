import java.awt.Label;

import javax.swing.JSlider;

public class MyThread extends Thread{
    private JSlider slider;
    private Label infoLabel;
    private int targetVal;

    MyThread(JSlider obj, int val, String threadName){
        slider = obj;
        targetVal = val;
        setName(threadName);
    }

    public void setLabel(Label label){
        infoLabel = label;
    }

    @Override
    public void run() {
        while(!interrupted()) {
            synchronized(slider){
                int prevValue = slider.getValue();
                infoLabel.setText(getName() + " controls Slider!");
                if (prevValue < targetVal) {
                    slider.setValue(prevValue + 1);
                }else if (prevValue > targetVal) {
                    slider.setValue(prevValue - 1);
                }
                try {
                    sleep(50);
                } catch (InterruptedException exception) {
                    return;
                }
            }
        }
    }
}
