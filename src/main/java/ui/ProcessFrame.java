package main.java.ui;

import main.java.os.Process;
import main.java.os.Scheduler;
import main.java.os.interrupt.EInterrupt;
import main.java.os.interrupt.InterruptQueue;
import main.java.os.interrupt.ProcessInterrupt;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

public class ProcessFrame extends JFrame {

    public ProcessFrame(Scheduler scheduler, InterruptQueue interruptQueue, Process process) {
        super();

        this.setTitle("Process_" + process.getSerialNumber());

        JPanel processPanel = new JPanel();
        this.add(processPanel);
        processPanel.setLayout(new BoxLayout(processPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Process_" + process.getSerialNumber());
        processPanel.add(title);

        JFrame thisFrame = this;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                interruptQueue.addProcessEnd(process);
            }
        });
        scheduler.addInterruptHandlingListener(interrupt -> {
            if (interrupt.getProcess() == process && interrupt.getEInterrupt() == EInterrupt.EProcessInterrupt.PROCESS_END) {
                thisFrame.dispose();
            }
        });
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
}
