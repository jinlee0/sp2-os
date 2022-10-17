package main.java.cpu;

import main.java.cpu.interrupt.EInterrupt;
import main.java.cpu.interrupt.Interrupt;
import main.java.cpu.interrupt.NormalInterrupt;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.function.Supplier;

public class CPU {
    private static final CPU instance = new CPU();

    private final CPUTimer cpuTimer = CPUTimer.getInstance();
    private CPU() {
        System.out.println("CPU CREATED");
    }

    public static CPU getInstance() {
        return instance;
    }
    public void run() {
        cpuTimer.run();
    }

    // Critical Section
    private final Deque<Interrupt> interruptQueue = new ArrayDeque<>();
    private Semaphore interruptQueueSemaphore = new Semaphore(1, true);
    public void addInterrupt(Interrupt interrupt) {
        runWithInterruptQueueSemaphore(() -> interruptQueue.offer(interrupt));
    }
    public void addTimeOutInterrupt() {
        runWithInterruptQueueSemaphore(() -> interruptQueue.offerFirst(new NormalInterrupt(EInterrupt.ENormalInterrupt.TIME_OUT)));
    }
    public Interrupt pollInterrupt() {
        return runWithInterruptQueueSemaphore(() -> {
            if(interruptQueue.isEmpty()) return new NormalInterrupt(EInterrupt.ENormalInterrupt.NONE);
            return interruptQueue.poll();
        });
    }
    public boolean hasInterrupt() {
        return !interruptQueue.isEmpty();
    }
    private <T> T runWithInterruptQueueSemaphore(Supplier<T> supplier) {
        try {
            interruptQueueSemaphore.acquire();
            return supplier.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            interruptQueueSemaphore.release();
        }
        return null;
    }
    /////////////////////////


    public void stop() {
        cpuTimer.stop();
        System.out.println("Stop CPU");
    }
}
