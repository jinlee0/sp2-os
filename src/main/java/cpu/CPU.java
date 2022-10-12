package main.java.cpu;

import main.java.cpu.interrupt.EInterrupt;
import main.java.cpu.interrupt.Interrupt;
import main.java.cpu.interrupt.NormalInterrupt;

import java.util.ArrayDeque;
import java.util.Deque;

public class CPU {
    private static final CPU instance = new CPU();

    private final CPUTimer cpuTimer = CPUTimer.getInstance();
    private final Deque<Interrupt> interruptQueue = new ArrayDeque<>();

    private CPU() {
        System.out.println("CPU CREATED");
    }
    public static CPU getInstance() {
        return instance;
    }

    public void run() {
        cpuTimer.run();
    }

    public void addInterrupt(Interrupt interrupt) {
        interruptQueue.offer(interrupt);
    }

    public void addTimeOutInterrupt() {
        interruptQueue.offerFirst(new NormalInterrupt(EInterrupt.ENormalInterrupt.TIME_OUT));
    }

    public Interrupt pollInterrupt() {
        if(interruptQueue.isEmpty()) return new NormalInterrupt(EInterrupt.ENormalInterrupt.NONE);
        return interruptQueue.poll();
    }

    public boolean hasInterrupt() {
        return !interruptQueue.isEmpty();
    }

    public void stop() {
        cpuTimer.stop();
        System.out.println("Stop CPU");
    }
}
