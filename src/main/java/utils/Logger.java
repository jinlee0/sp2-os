package main.java.utils;

import main.java.os.Process;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiConsumer;

public class Logger {
    private static final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    private static Timer autoFlushTimer;

    private static final ConcurrentLinkedDeque<BiConsumer<Process, String>> loggingListeners = new ConcurrentLinkedDeque<>();

    public static void add(String msg) {
//        System.out.println(msg);
        try {
            bw.write("LOGGER >> " + msg + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void add(Process process, String msg) {
        loggingListeners.forEach(listener -> listener.accept(process, msg));
        add(msg);
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
        autoFlushTimer.schedule(new TimerTask() {
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

    public static void addLoggingListener(BiConsumer<Process, String> listener) {
        loggingListeners.add(listener);
    }

    public static void removeLoggingListner(BiConsumer<Process, String> loggingListener) {
        loggingListeners.remove(loggingListener);
    }

    public static void stopAutoFlush() {
        if(autoFlushTimer!=null) autoFlushTimer.cancel();
    }
}
