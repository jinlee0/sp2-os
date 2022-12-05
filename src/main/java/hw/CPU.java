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
        Logger.add("CPU run() start");
        while (Power.isOn()) {
            scheduler.handleAllInterrupts();
            scheduler.executeInstruction();
        }
        Logger.add("CPU run() end");
    }

    public void initialize() {
    }

    public void finish() {
    }
}
