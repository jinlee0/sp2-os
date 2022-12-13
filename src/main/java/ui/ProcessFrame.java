package main.java.ui;

import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.os.Process;
import main.java.os.Scheduler;
import main.java.os.interrupt.EInterrupt;
import main.java.os.interrupt.InterruptQueue;
import main.java.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.BiConsumer;

public class ProcessFrame extends JFrame {

    public ProcessFrame(Scheduler scheduler, InterruptQueue interruptQueue, Keyboard keyboard, Monitor monitor, Process process) {
        super();

        this.setTitle("Process_" + process.getSerialNumber());

        JPanel topPanel = new JPanel();
        this.add(topPanel);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel processPanel = new JPanel();
//        this.add(processPanel);
        processPanel.setLayout(new BoxLayout(processPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(processPanel);
        topPanel.add(scrollPane);
        scrollPane.setPreferredSize(new Dimension(600, 600));

        JLabel title = new JLabel("Process_" + process.getSerialNumber());
        processPanel.add(title);

        JTextArea logArea = new JTextArea();
        processPanel.add(logArea);
        logArea.setEditable(false);

        ////////// IO panel
        JPanel ioPanel = new JPanel();
        topPanel.add(ioPanel);
        ioPanel.setLayout(new BoxLayout(ioPanel, BoxLayout.Y_AXIS));

        TextArea monitorArea = new TextArea();
        ioPanel.add(monitorArea);
        monitorArea.setEditable(false);
        BiConsumer<Process, String> writeListenerForMonitor = (theProcess, message) -> {
            if (theProcess == process) {
                monitorArea.append("output >> " + message + System.lineSeparator());
            }
        };
        monitor.addWriteListener(writeListenerForMonitor);

        TextField keyboardField = new TextField();
        ioPanel.add(keyboardField);
        keyboardField.setEditable(false);
        keyboardField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String text = keyboardField.getText();
                    if(text.isBlank()) return;
                    keyboard.addInput(process, text);
                    monitorArea.append("input << " + text + System.lineSeparator());
                    keyboardField.setText("");
                }
            }
        });
        scheduler.addInterruptHandlingListener(interrupt -> {
            if (interrupt.getProcess() == process && interrupt.getEInterrupt() == EInterrupt.EProcessInterrupt.READ_INT_START) {
                keyboardField.setEditable(true);
            }
        });
        scheduler.addInterruptHandlingListener(interrupt -> {
            if (interrupt.getProcess() == process && interrupt.getEInterrupt() == EInterrupt.EProcessInterrupt.READ_INT_COMPLETE) {
                keyboardField.setEditable(false);
            }
        });
        ///////////
        JFrame thisFrame = this;
        BiConsumer<Process, String> loggingListener = (theProcess, message) -> {
            if (theProcess == process) logArea.append(message + System.lineSeparator());
            thisFrame.pack();
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        };
        Logger.addLoggingListener(loggingListener);

        scheduler.addInterruptHandlingListener(interrupt -> {
            if (interrupt.getProcess() == process && interrupt.getEInterrupt() == EInterrupt.EProcessInterrupt.PROCESS_END) {
                logArea.append("end");
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                keyboard.stopAllThreadsOf(process);
                monitor.removeWriteListner(writeListenerForMonitor);
                Logger.removeLoggingListner(loggingListener);
            }
        });


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                keyboard.stopAllThreadsOf(process);
                monitor.removeWriteListner(writeListenerForMonitor);
                interruptQueue.addProcessEnd(process);
                Logger.removeLoggingListner(loggingListener);
            }
        });
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
}
