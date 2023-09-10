import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.util.Hashtable;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Frame extends JFrame {
    private JSlider slider;
     // Task A UI
    private JButton startBtn;
    private JButton stopBtn;
    private Label infoLabel;
    private JSpinner priorThr1;
    private JSpinner priorThr2;
    // Task B UI
    private JButton startThr1;
    private JButton stopThr1;
    private JButton startThr2;
    private JButton stopThr2;

    public Frame(){
        setSize(650, 450);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setTaskA();
        setTaskB();

        setVisible(true);
    }

    private void setTaskA(){
        setSlider();
        setInteractable();
    }

    private void setTaskB(){
        JPanel Bpanel = new JPanel(new GridLayout(2,2));
        startThr1 = new JButton("Start Thread1");
        startThr2 = new JButton("Start Thread2");
        stopThr1 = new JButton("Stop Thread1");
        stopThr2 = new JButton("Stop Thread2");
        Bpanel.add(startThr1);
        Bpanel.add(startThr2);
        Bpanel.add(stopThr1);
        Bpanel.add(stopThr2);
        add(Bpanel, BorderLayout.SOUTH);
    }

    private void setSlider(){
        JPanel sliderPanel = new JPanel(new GridBagLayout());
        slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        slider.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for(int i = 0; i <= 100; i+= 10){
            labelTable.put(i, new JLabel(String.valueOf(i)));
        }
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
        sliderPanel.add(slider);
        add(sliderPanel, BorderLayout.NORTH);
    }

    private void setInteractable(){
        JPanel interactPanel = new JPanel(new GridBagLayout());
        
        SpinnerNumberModel spinMod1 = new SpinnerNumberModel(1, 1, 10, 1);
        priorThr1 = new JSpinner(spinMod1);
        SpinnerNumberModel spinMod2 = new SpinnerNumberModel(1, 1, 10, 1);
        priorThr2 = new JSpinner(spinMod2);
        priorThr1.setSize(100, 60);
        priorThr2.setSize(100, 60);

        GridBagConstraints grid = new GridBagConstraints();
        grid.insets = new Insets(5, 5, 5, 5);
        grid.gridx = 0;
        grid.gridy = 0;
        grid.gridwidth = 2;
        infoLabel = new Label("Slider isn't controlled");
        infoLabel.setSize(200, 30);
        interactPanel.add(infoLabel, grid);

        grid.gridy = 1;
        grid.gridwidth = 1;
        interactPanel.add(priorThr1, grid);

        grid.gridx = 1;
        interactPanel.add(priorThr2, grid);

        grid.gridx = 0;
        grid.gridy = 2;
        grid.gridwidth = 2;

        startBtn = new JButton("Start");
        startBtn.setSize(200, 40);
        interactPanel.add(startBtn, grid);
        
        grid.gridx = 0;
        grid.gridy = 3;
        stopBtn = new JButton("Stop");
        startBtn.setSize(200, 40);
        interactPanel.add(stopBtn, grid);


        add(interactPanel, BorderLayout.CENTER);
    }
}
