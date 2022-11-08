package main.java.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

public class Logger {
    private static final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    private static Timer autoFlushTimer;

    public static void add(String msg) {
        try {
            bw.write(msg + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void flush() {
        try {
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startAutoFlush() {
        stopAutoFlush();
        autoFlushTimer = new Timer();
        autoFlushTimer.schedule
                (new TimerTask() {
            @Override
            public void run() {
                try {
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 100L);
    }

    public static void stopAutoFlush() {
        if(autoFlushTimer!=null) autoFlushTimer.cancel();
    }
}
