package main.java.os;

import java.util.Scanner;

public class UI implements Runnable{
    @Override
    public void run() {
        Loader loader = new Loader();
        Scheduler scheduler = new Scheduler();
        // console command
        // "r fileName" -> execute fileName
        // "q" -> quit program

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.next();
            if(command.equals("exit")) break;
            else if (command.equals("r")) {
                String fileName = scanner.next();
                loader = new Loader();
                Process process = loader.load(fileName);
            }
            scheduler.run();
        }
    }
}
