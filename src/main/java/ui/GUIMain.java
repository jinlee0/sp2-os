package main.java.ui;

import main.java.Main;
import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.os.Scheduler;
import main.java.os.interrupt.InterruptQueue;

public class GUIMain {
    private MainFrame mainFrame;

    private final Main main;

    public GUIMain(Main main, Scheduler scheduler, InterruptQueue interruptQueue, Keyboard keyboard, Monitor monitor) {
        this.main = main;
        this.mainFrame = new MainFrame(scheduler, keyboard, monitor, interruptQueue, () -> finish());

    }

    public void finish() {
        main.finish();
        mainFrame.finish();
        System.out.println("GUIMain Finished");
    }
}
