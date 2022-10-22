package main.java.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                StringTokenizer st = new StringTokenizer(br.readLine());
                String command = st.nextToken();
                if(command.equals("exit")) {
                    System.exit(0);
                    break;
                }
                else if (command.equals("r")) {
                    String fileName = st.nextToken();
                    new Thread(() -> {
                        scheduler.load(loader.load(fileName));
                    }).start();
                } else {
                    System.out.println("wrong command");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
