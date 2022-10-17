package main.java.os;

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
            if(command.equals("exit")) break;
            else if (command.equals("r")) {
                String fileName = scanner.next();
                scheduler.load(loader.load(fileName));
            } else {
                System.out.println("wrong command");
            }
        }
    }
}
