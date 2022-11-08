package main.java;

import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.os.Scheduler;
import main.java.os.UI;

import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler(new LinkedBlockingQueue<>());
        UI ui = new UI(scheduler);
        scheduler.start();
        ui.start();
        Monitor.getInstance().start();
        Keyboard.getInstance().start();
    }
}
