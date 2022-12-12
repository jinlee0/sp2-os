package main.java.hw;

import main.java.os.Scheduler;
import main.java.power.Power;
import main.java.utils.Logger;

public class CPU extends Thread {
    private final Scheduler scheduler;

    public CPU(Scheduler scheduler){
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        while (Power.isOn()) {
            scheduler.handleAllInterrupts();
            scheduler.executeInstruction();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize() {
        Logger.add("CPU run() start");
    }

    public void finish() {
        Logger.add("CPU run() end");
    }
}
