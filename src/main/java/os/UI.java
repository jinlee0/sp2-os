package main.java.os;

import main.java.os.interrupt.InterruptQueue;
import main.java.power.Power;

import java.util.Scanner;

public class UI extends Thread {
    private final Scheduler scheduler;
    private final Loader loader;

    public UI(Scheduler scheduler) {
        this.loader = new Loader();
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        // console command
        // "r fileName" -> execute fileName
        // "q" -> quit program

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.next();
            if(command.equals("exit")) {
                System.exit(0);
                break;
            }
            else if (command.equals("r")) {
                String fileName = scanner.next();
                new Thread(() -> {
                    scheduler.load(loader.load(fileName));
                }).start();
            } else {
                System.out.println("wrong command");
            }
        }
    }
}
