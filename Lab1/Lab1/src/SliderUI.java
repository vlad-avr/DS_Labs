import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Hashtable;

public class SliderUI extends JFrame {
    public JSlider slider;
    private JSpinner prioritySpinners[] = new JSpinner[2];
    private JButton startButton;
    private JButton startButton1;
    private JButton startButton2;
    private JButton stopButton1;
    private JButton stopButton2;
    private Label infoLabel;
    public final RunnableManager RManager = new RunnableManager();
    Locker locker = new Locker();
    private final ThreadManager ThreadManagerTaskA = new ThreadManager();
    private final ThreadManager ThreadManagerTaskB = new ThreadManager();

    public SliderUI(int width, int height){
        super("DS_Lab1");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(width, height);
        setResizable(false);
        setLayout(new BorderLayout());
        setTaskA();
    }


    void setTaskA(){
        setTopPart();
        setCenterPart();
    }

    void setTopPart(){
        JPanel topBar = new JPanel(new GridBagLayout());
        slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(10);
        slider.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        Hashtable<JLabel, Integer> sliderLables = new Hashtable<>();
        for(int i = 0; i < 100; i+=10){
            sliderLables.put(new JLabel(String.valueOf(i)), i);
        }
        slider.setLabelTable(sliderLables);
        slider.setPaintLabels(true);
        slider.setSize(new Dimension(getWidth()/2, getHeight()/2));
        topBar.add(slider);
        add(topBar, BorderLayout.NORTH);
    }

    void setCenterPart(){
        JPanel centerPanel = new JPanel(new GridBagLayout());

        SpinnerNumberModel SNM1 = new SpinnerNumberModel(1,1, 10, 1);
        prioritySpinners[0] = new JSpinner(SNM1);
        prioritySpinners[0].setSize(new Dimension(getWidth()/6, getHeight()/5));
        prioritySpinners[1] = new JSpinner(SNM1);
        prioritySpinners[1].setSize(new Dimension(getWidth()/6, getHeight()/5));

        GridBagConstraints gridConst = new GridBagConstraints();
        gridConst.insets = new Insets(5, 5, 5, 5);
        gridConst.gridx = 0;
        gridConst.gridy = 1;
        gridConst.gridwidth = 1;
        centerPanel.add(prioritySpinners[0]);
        gridConst.gridx = 1;
        centerPanel.add(prioritySpinners[1]);
        gridConst.gridx = 0;
        gridConst.gridy = 2;
        gridConst.gridwidth = 3;
        startButton = new JButton("Start");
        startButton.setSize(getWidth()/2, getHeight()/3);
        startButton.addActionListener(action->{
            if(ThreadManagerTaskA.isRunning(1)){
                return;
            }
            enableTaskA(false);
            enableTaskB(false);

            ThreadManagerTaskA.setThread(ThreadManagerTaskA.THREAD1, launchThread(0, 10));
            ThreadManagerTaskA.setThread(ThreadManagerTaskA.THREAD2, launchThread(1, 90));
        });
        // infoLabel = new La
    }

    private Thread launchThread(int ThreadID, int thrTargetVal){
        SliderManager SM = new SliderManager();
        return SM.getThread(thrTargetVal, (int)prioritySpinners[ThreadID].getValue(), ThreadID);
    }

    void enableTaskA(boolean b){
        startButton.setEnabled(b);
    }
    void enableTaskB(boolean b){
        startButton1.setEnabled(b);
        startButton2.setEnabled(b);
        stopButton1.setEnabled(b);
        stopButton2.setEnabled(b);
    }
}