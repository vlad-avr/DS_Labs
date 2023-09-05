import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;

public class SliderUI implements ChangeListener{

    private JFrame frame;
    private JPanel panel;
    private JSlider slider;

    SliderUI(String windowName, int winWidth, int winHeight, int slideMin, int slideMax){
        frame = new JFrame(windowName, null);
        panel = new JPanel();
        frame.setSize(winWidth, winHeight);
        panel = new JPanel();
        slider = new JSlider(slideMin, slideMax);
        slider.setVisible(true);
        slider.setPaintTicks(true);
        slider.setPaintTrack(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(10);
        slider.setPreferredSize(new Dimension(winWidth, winHeight));
        panel.add(slider);
        frame.add(panel);
        frame.setVisible(true);
    }


    @Override
    public void stateChanged(ChangeEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stateChanged'");
    }
    
}
