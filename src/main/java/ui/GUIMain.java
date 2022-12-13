package main.java.ui;

import main.java.Main;
import main.java.os.Scheduler;
import main.java.os.interrupt.InterruptQueue;

public class GUIMain {
    private MainFrame mainFrame;

    private final Main main;
    private final Scheduler scheduler;
    private final InterruptQueue interruptQueue;

    public GUIMain(Main main, Scheduler scheduler, InterruptQueue interruptQueue) {
        this.main = main;
        this.scheduler = scheduler;
        this.interruptQueue = interruptQueue;
    }

    public void run() {
        this.mainFrame = new MainFrame(scheduler, interruptQueue, () -> finish());
    }

    public void finish() {
        main.finish();
        mainFrame.finish();
        System.out.println("GUIMain Finished");
    }
}
