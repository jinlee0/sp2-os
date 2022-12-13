package main.java.ui;

import main.java.os.Process;
import main.java.os.Scheduler;
import main.java.os.interrupt.EInterrupt;
import main.java.os.interrupt.InterruptQueue;
import main.java.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ProcessFrame extends JFrame {

    public ProcessFrame(Scheduler scheduler, InterruptQueue interruptQueue, Process process) {
        super();

        this.setTitle("Process_" + process.getSerialNumber());

        JPanel processPanel = new JPanel();
//        this.add(processPanel);
        processPanel.setLayout(new BoxLayout(processPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(processPanel);
        this.add(scrollPane);
        scrollPane.setPreferredSize(new Dimension(600, 600));

        JLabel title = new JLabel("Process_" + process.getSerialNumber());
        processPanel.add(title);

        JTextArea logArea = new JTextArea();
        processPanel.add(logArea);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                interruptQueue.addProcessEnd(process);
            }
        });
        JFrame thisFrame = this;
        scheduler.addInterruptHandlingListener(interrupt -> {
            if (interrupt.getProcess() == process && interrupt.getEInterrupt() == EInterrupt.EProcessInterrupt.PROCESS_END) {
                logArea.append("end");
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            }
        });
        Logger.addLoggingListener((theProcess, message) -> {
            if(theProcess == process) logArea.append(message);
            thisFrame.pack();
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        });
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
}
