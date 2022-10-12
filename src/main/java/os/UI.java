package main.java.os;

import java.util.Scanner;

public class UI extends Thread {
    private final Scheduler scheduler;

    public UI(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        Loader loader = new Loader();
        Scheduler scheduler = new Scheduler();
        OS os = new OS();
        // console command
        // "r fileName" -> execute fileName
        // "q" -> quit program

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.next();
            if(command.equals("exit")) break;
            else if (command.equals("r")) {
                String fileName = scanner.next();
                os.load(fileName);
            }
            scheduler.run();
        }
    }
}
