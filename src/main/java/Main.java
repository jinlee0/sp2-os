package main.java;

import main.java.hw.CPU;
import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.io.FileSystem;
import main.java.os.Scheduler;
import main.java.os.UI;
import main.java.os.interrupt.InterruptQueue;
import main.java.ui.GUIMain;
import main.java.utils.Logger;

public class Main {
    private final InterruptQueue interruptQueue = new InterruptQueue();
    private final Monitor monitor = new Monitor(interruptQueue);
    private final Keyboard keyboard = new Keyboard(interruptQueue);
    private final FileSystem fileSystem = new FileSystem(interruptQueue);
    private final Scheduler scheduler = new Scheduler(interruptQueue, monitor, keyboard, fileSystem);
    private final CPU cpu = new CPU(scheduler);
//    private final UI ui = new UI(this, scheduler, interruptQueue);
    private final GUIMain guiMain = new GUIMain(this, scheduler, interruptQueue, keyboard, monitor);

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
        Logger.startAutoFlush();
    }

    public void run() {
        cpu.start();
//        ui.start();
        monitor.start();
        keyboard.start();
        fileSystem.start();
    }

    public void initialize() {
        scheduler.initialize();
//        ui.initialize();
        cpu.initialize();
        fileSystem.initialize();
        monitor.initialize();
        keyboard.initialize();
    }

    public void finish() {
        keyboard.finish();
        monitor.finish();
        fileSystem.finish();
        cpu.finish();
        System.out.println("Main Finished");
    }
}
