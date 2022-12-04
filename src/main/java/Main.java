package main.java;

import main.java.hw.CPU;
import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.os.Scheduler;
import main.java.os.UI;

public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        CPU cpu = new CPU(scheduler);
        cpu.start();
        UI ui = new UI(scheduler);
        ui.start();
//        new GUIMain().run();
        Monitor.getInstance().start();
        Keyboard.getInstance().start();
    }
}
