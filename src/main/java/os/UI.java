package main.java.os;

import main.java.exception.ProcessNotFound;
import main.java.utils.Logger;
import main.java.utils.MPrinter;
import main.java.utils.MScanner;

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class UI extends Thread {
    private final Scheduler scheduler;
    private final Loader loader;
    private final MPrinter printer = MPrinter.getInstance();
    private final MScanner scanner = MScanner.getInstance();

    public UI(Scheduler scheduler) {
        this.loader = new Loader();
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        // console command
        // "r fileName" -> execute fileName
        // "q" -> quit program
        println("UI run");
        while (true) {
            printer.print("UI >> ");
//            StringTokenizer st = new StringTokenizer(scanner.nextLine("UI >> "));
            StringTokenizer st = new StringTokenizer(scanner.nextLine());
            String command = st.nextToken();
            switch (command) {
                case "q":
                    println("End system");
                    System.exit(0);
                    return;
                case "r":
                    loadEXE(st.nextToken());
                    break;
                case "t":
                    terminateProcess(st.nextToken());
                    break;
                case "log":
                    handleLog(st);
                    break;
                default:
                    println("wrong command");
                    break;
            }
        }
    }

    private void handleLog(StringTokenizer st) {
        try {
            switch (st.nextToken()) {
                case "on":
                    Logger.startAutoFlush();
                    break;
                case "off":
                    Logger.stopAutoFlush();
                    break;
                default:
                    println("help: log [on/off]");
                    break;
            }
        } catch (NoSuchElementException e) {
            Logger.flush();
        }
    }

    private void terminateProcess(String token) {
        try {
            scheduler.terminate(Long.parseLong(token));
            println("Process_" + token + " is terminated");
        } catch (NumberFormatException e) {
            println("Serial number must be long number");
        } catch (ProcessNotFound e) {
            println("Process_" + token + " is not found");
        }
    }

    private void loadEXE(String token) {
        synchronized (MScanner.getInstance()) {
            try {
                scheduler.load(loader.load(token));
                println(token + " is loaded");
            } catch (FileNotFoundException e) {
                println("File " + token + " is not found");
            }
        }
    }

    private void println(String s) {
        printer.println(s);
    }
}
