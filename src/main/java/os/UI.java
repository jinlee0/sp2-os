package main.java.os;

import main.java.exception.ProcessNotFound;
import main.java.utils.Logger;
import main.java.utils.SPrinter;
import main.java.utils.SScanner;

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class UI extends Thread {
    private final Scheduler scheduler;
    private final Loader loader;
    private final SPrinter printer = SPrinter.getInstance();
    private final SScanner scanner = SScanner.getInstance();

    public UI(Scheduler scheduler) {
        this.loader = new Loader();
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        // console command
        // "r fileName" -> execute fileName
        // "q" -> quit program
        printlnln("UI run");
        while (true) {
            StringTokenizer st = new StringTokenizer(scanner.nextLine("UI >> "));
            String command;
            try {
                command = st.nextToken();
            } catch (Exception e) {
                continue;
            }
            switch (command) {
                case "q":
                    printlnln("End system");
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
                    printlnln("wrong command");
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
                    printlnln("help: log [on/off]");
                    break;
            }
        } catch (NoSuchElementException e) {
            Logger.flush();
        }
    }

    private void terminateProcess(String token) {
        try {
            scheduler.terminate(Long.parseLong(token));
            printlnln("Process_" + token + " is terminated");
        } catch (NumberFormatException e) {
            printlnln("Serial number must be long number");
        } catch (ProcessNotFound e) {
            printlnln("Process_" + token + " is not found");
        }
    }

    private void loadEXE(String token) {
        synchronized (SScanner.getInstance()) {
            try {
                scheduler.load(loader.load(token));
                printlnln(token + " is loaded");
            } catch (FileNotFoundException e) {
                printlnln("File " + token + " is not found");
            }
        }
    }

    private void printlnln(String s) {
        println(s + System.lineSeparator());
    }
    private void println(String s) {
        printer.println(s);
    }
}
