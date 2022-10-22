package main.java.os;

import main.java.exception.ProcessNotFound;
import main.java.utils.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

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
        System.out.println("UI run");
        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("UI >> ");
                StringTokenizer st = new StringTokenizer(br.readLine());
                String command = st.nextToken();
                switch (command) {
                    case "q":
                        System.out.println("End system");
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
                        System.out.println("wrong command");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                    System.out.println("help: log [on/off]");
                    break;
            }
        } catch (NoSuchElementException e) {
            Logger.flush();
        }
    }

    private void terminateProcess(String token) {
        try {
            scheduler.terminate(Long.parseLong(token));
            System.out.println("Process_" + token + " is terminated");
        } catch (NumberFormatException e) {
            System.out.println("Serial number must be long number");
        } catch (ProcessNotFound e) {
            System.out.println("Process_" + token + " is not found");
        }
    }

    private void loadEXE(String token) {
        try {
            scheduler.load(loader.load(token));
            System.out.println(token + " is loaded");
        } catch (FileNotFoundException e) {
            System.out.println("File " + token + " is not found");
        }
    }
}
