package main.java.ui;

import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.os.Loader;
import main.java.os.Scheduler;
import main.java.os.interrupt.InterruptQueue;
import main.java.os.interrupt.ProcessInterrupt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends JPanel {

    private final Loader loader;

    private final List<String> programs = new ArrayList<>();

    public MainPanel(Scheduler scheduler, InterruptQueue interruptQueue, Keyboard keyboard, Monitor monitor) {
        super();
        this.loader = new Loader(interruptQueue);

        List.of(new File("programs/").listFiles()).forEach(file -> programs.add(file.getName()));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        MainPanel mainPanel = this;

        JPanel programsPanel = new JPanel();
        mainPanel.add(programsPanel);
        programsPanel.setLayout(new BoxLayout(programsPanel, BoxLayout.Y_AXIS));

        programs.forEach(programName -> {
            JPanel panel = new JPanel();
            programsPanel.add(panel);
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
                        scheduler.addInterruptHandlingListenerPerOnce((interrupt) -> {
                            if(interrupt == processInterrupt) {
                                new ProcessFrame(scheduler, interruptQueue, keyboard, monitor, interrupt.getProcess());
                                return true;
                            }
                            return false;
                        });
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, "File " + programName + " is not found");
                    }
                }
            });
        });

        TextArea runningProcessArea = new TextArea();
        mainPanel.add(runningProcessArea);
        runningProcessArea.setEditable(false);

        TextArea readyQueuArea = new TextArea();
        mainPanel.add(readyQueuArea);
        readyQueuArea.setEditable(false);

        TextArea waitingQueuArea = new TextArea();
        mainPanel.add(waitingQueuArea);
        waitingQueuArea.setEditable(false);

        scheduler.setListener(theScheduler -> {
            runningProcessArea.setText(theScheduler.toStringRunningProcess());
            readyQueuArea.setText(theScheduler.toStringReadyQueue());
            waitingQueuArea.setText(theScheduler.toStringWaitingQueue());
        });

    }

    public void finish() {
        System.out.println("MainPanel Finished");
    }
}
