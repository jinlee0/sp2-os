package main.java.cpu;

import java.util.Timer;
import java.util.TimerTask;

public class CPUTimer {
    private static final CPUTimer instance = new CPUTimer();

    private final Timer timer = new Timer();
    private final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            CPU.getInstance().addTimeOutInterrupt();
        }
    };

    private CPUTimer() {}

    public static CPUTimer getInstance() {
        return instance;
    }

    public void run() {
        System.out.println("Timer run() start");
        timer.schedule(timerTask, 0L, 300L);
    }

    public void stop() {
        System.out.println("Stop Timer");
        timer.cancel();
    }
}
