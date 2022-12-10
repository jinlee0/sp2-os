package main.java;

import main.java.hw.CPU;
import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.io.FileSystem;
import main.java.os.Scheduler;
import main.java.os.UI;
import main.java.os.interrupt.InterruptQueue;

public class Main {
    private final InterruptQueue interruptQueue = new InterruptQueue();
    private final Monitor monitor = new Monitor(interruptQueue);
    private final Keyboard keyboard = new Keyboard(interruptQueue);
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
