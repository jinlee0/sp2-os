package main.java.ui;

import main.java.os.Loader;
import main.java.os.Process;
import main.java.os.Scheduler;
import main.java.os.interrupt.InterruptQueue;
import main.java.os.interrupt.ProcessInterrupt;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends JPanel {

    private final Scheduler scheduler;
    private final InterruptQueue interruptQueue;
    private final Loader loader;

    private final List<String> programs = new ArrayList<>();

    public MainPanel(Scheduler scheduler, InterruptQueue interruptQueue) {
        super();
        this.scheduler = scheduler;
        this.interruptQueue = interruptQueue;
        this.loader = new Loader(interruptQueue);

        List.of(new File("programs/").listFiles()).forEach(file -> programs.add(file.getName()));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        MainPanel mainPanel = this;
        programs.forEach(programName -> {
            JPanel panel = new JPanel();
            mainPanel.add(panel);
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            JLabel label = new JLabel(programName);
            panel.add(label);

            JButton executeButton = new JButton("execute");
            panel.add(executeButton);
            executeButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    try {
                        ProcessInterrupt processInterrupt = interruptQueue.addProcessStart(loader.load(programName));
                        scheduler.addInterruptHandlingListener((interrupt) -> {
                            if(interrupt == processInterrupt) {
                                new ProcessFrame(scheduler, interruptQueue, interrupt.getProcess());
                            }
                        });
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, "File " + programName + " is not found");
                    }
                }
            });


        });
    }

    public void finish() {
        System.out.println("MainPanel Finished");
    }
}
