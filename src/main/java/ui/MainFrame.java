package main.java.ui;

import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.os.Scheduler;
import main.java.os.interrupt.InterruptQueue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private final MainPanel mainPanel;

    public MainFrame(Scheduler scheduler, Keyboard keyboard, Monitor monitor, InterruptQueue interruptQueue, Runnable finish) {
        super();


        this.setTitle("MyOS");
        this.setLocation(new Point(0, 0));
        this.setMinimumSize(new Dimension(1080, 300));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                finish.run();
            }
        });
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.mainPanel = new MainPanel(scheduler, interruptQueue, keyboard, monitor);
        this.add(mainPanel);

        this.pack();
        this.setVisible(true);
    }


    public void finish() {
        mainPanel.finish();
        System.out.println("MainFrame Finished");
    }
}
