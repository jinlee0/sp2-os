package main.java;

import main.java.hw.CPU;
import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.os.FileSystem;
import main.java.os.Scheduler;
import main.java.os.UI;
import main.java.os.interrupt.InterruptQueue;

public class Main {
    private final InterruptQueue interruptQueue = InterruptQueue.getInstance();
    private final Monitor monitor = Monitor.getInstance();
    private final Keyboard keyboard = Keyboard.getInstance();
    private final Scheduler scheduler = new Scheduler(interruptQueue, monitor, keyboard);
    private final CPU cpu = new CPU(scheduler);
    private final UI ui = new UI(scheduler, interruptQueue);
    private final FileSystem fileSystem = new FileSystem(interruptQueue);

    public static void main(String[] args) {
        Main main = new Main();
        main.cpu.start();
        main.ui.start();
//        new GUIMain().run();
        main.monitor.start();
        main.keyboard.start();
    }

    private void initialize() {
        scheduler.initialize();
        ui.initialize();
        cpu.initialize();
        fileSystem.initialize();
        monitor.initialize();
        keyboard.initialize();
    }

    private void finish() {
        keyboard.finish();
        monitor.finish();
        fileSystem.finish();
        cpu.finish();
        ui.finish();
        scheduler.finish();
    }
}
