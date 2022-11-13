package main.java;

import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.os.Scheduler;
import main.java.os.UI;
import main.java.ui.GUIMain;

import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler(new LinkedBlockingQueue<>());
        scheduler.start();
//        UI ui = new UI(scheduler);
//        ui.start();
        new GUIMain().run();
        Monitor.getInstance().start();
        Keyboard.getInstance().start();
    }
}
