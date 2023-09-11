import javax.swing.*;

import java.awt.*;
import java.util.Hashtable;

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

    // Thread Managing

    private int semaphore = 1;

    private MyThread[] threads = new MyThread[2];


    public Frame() {
        setSize(650, 450);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setTaskA();
        setTaskB();
        threads[0] = new MyThread(slider, 10, "Thread1");
        threads[1] = new MyThread(slider, 90, "Thread2");
        threads[0].setPriority((int)priorThr1.getValue());
        threads[1].setPriority((int)priorThr2.getValue());
        threads[0].setLabel(infoLabel);
        threads[1].setLabel(infoLabel);
        setVisible(true);
    }

    private void setTaskA() {
        setSlider();
        setInteractable();
    }

    private void switchTaskA(boolean b){
        startBtn.setEnabled(b);
        stopBtn.setEnabled(b);
    }

    private void setTaskB() {
        JPanel Bpanel = new JPanel(new GridLayout(2, 2));
        startThr1 = new JButton("Start Thread1");
        startThr2 = new JButton("Start Thread2");
        stopThr1 = new JButton("Stop Thread1");
        stopThr2 = new JButton("Stop Thread2");
        startThr1.addActionListener(event -> {
            if (!lockSemaphore()) {
                JOptionPane.showMessageDialog(this, "Slider is occupied by Thread2");
                return;
            }
            launchThread(0, 10);
            threads[0].setPriority(Thread.MIN_PRIORITY);
            // infoLabel.setText("Thread1 is controlling the Slider now!");
            switchTaskA(false);
            stopThr2.setEnabled(false);
            startThr1.setEnabled(false);

        });
        startThr2.addActionListener(event -> {
            if (!lockSemaphore()) {
                JOptionPane.showMessageDialog(this, "Slider is occupied by Thread1");
                return;
            }
            launchThread(1, 90);
            threads[1].setPriority(Thread.MAX_PRIORITY);
 //           infoLabel.setText("Thread2 is controlling the Slider now!");
            switchTaskA(false);
            stopThr1.setEnabled(false);
            startThr2.setEnabled(false);

        });
        stopThr2.addActionListener(event -> {
            freeSemaphore();
            stopThread(1);
            stopThr1.setEnabled(true);
            startThr2.setEnabled(true);
            infoLabel.setText("Slider isn't controlled");
            switchTaskA(true);
        });
        stopThr1.addActionListener(event -> {
            freeSemaphore();
            stopThread(0);
            stopThr2.setEnabled(true);
            startThr1.setEnabled(true);
            infoLabel.setText("Slider isn't controlled");
            switchTaskA(true);

        });
        Bpanel.add(startThr1);
        Bpanel.add(startThr2);
        Bpanel.add(stopThr1);
        Bpanel.add(stopThr2);
        add(Bpanel, BorderLayout.SOUTH);
    }

    private void setSlider() {
        JPanel sliderPanel = new JPanel(new GridBagLayout());
        slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        slider.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = 0; i <= 100; i += 10) {
            labelTable.put(i, new JLabel(String.valueOf(i)));
        }
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
        sliderPanel.add(slider);
        add(sliderPanel, BorderLayout.NORTH);
    }

    private void setInteractable() {
        JPanel interactPanel = new JPanel(new GridBagLayout());

        SpinnerNumberModel spinMod1 = new SpinnerNumberModel(1, 1, 10, 1);
        priorThr1 = new JSpinner(spinMod1);
        priorThr1.addChangeListener(event -> {
            if (threads[0] != null) {
                threads[0].setPriority((int) priorThr1.getValue());
            }
        });
        SpinnerNumberModel spinMod2 = new SpinnerNumberModel(1, 1, 10, 1);
        priorThr2 = new JSpinner(spinMod2);
        priorThr2.addChangeListener(event -> {
            if (threads[1] != null) {
                threads[1].setPriority((int) priorThr2.getValue());
            }
        });

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
        startBtn.addActionListener(event -> {
            if (!areWorking()) {
                launchAll();
            }
        });
        interactPanel.add(startBtn, grid);

        grid.gridx = 0;
        grid.gridy = 3;
        stopBtn = new JButton("Stop");
        stopBtn.addActionListener(event -> {
            stopAll();
            infoLabel.setText("Slider isn't controlled");
        });
        startBtn.setSize(200, 40);
        interactPanel.add(stopBtn, grid);

        add(interactPanel, BorderLayout.CENTER);
    }

    void launchThread(int threadID, int val) {
        threads[threadID] = new MyThread(slider, val, "Thread" + String.valueOf(threadID+1));
        threads[threadID].setLabel(infoLabel);
        if(threadID == 0){
            threads[threadID].setPriority((int)priorThr1.getValue());
        }
        else{
            threads[threadID].setPriority((int)priorThr2.getValue());
        }
        threads[threadID].start();
    }

    void stopThread(int threadID) {
        threads[threadID].interrupt();
    }

    void stopAll() {
        stopThread(0);
        stopThread(1);
        startThr1.setEnabled(true);
        startThr2.setEnabled(true);
        stopThr1.setEnabled(true);
        stopThr2.setEnabled(true);
    }

    void launchAll() {
        launchThread(0, 10);
        launchThread(1, 90);

        startThr1.setEnabled(false);
        startThr2.setEnabled(false);
        stopThr1.setEnabled(false);
        stopThr2.setEnabled(false);
    }

    boolean areWorking() {
        for (MyThread thr : threads) {
            if (thr != null && thr.isAlive()) {
                return true;
            }
        }
        return false;
    }

    synchronized boolean lockSemaphore() {
        if (semaphore == 0) {
            return false;
        }
        semaphore--;
        return true;
    }

    synchronized void freeSemaphore() {
        if (semaphore < 1) {
            semaphore++;
        } else {
            throw new RuntimeException("Semaphore cannot be freed");
        }
    }
}
