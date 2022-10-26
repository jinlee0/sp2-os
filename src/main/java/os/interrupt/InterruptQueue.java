package main.java.os.interrupt;

import main.java.os.Process;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class InterruptQueue {
    private static final InterruptQueue instance = new InterruptQueue();
    private final Semaphore interruptQueueSemaphore = new Semaphore(1, true);
    private final Deque<Interrupt> interruptQueue = new ArrayDeque<>();

    private InterruptQueue() {}
    public static InterruptQueue getInstance() {
        return instance;
    }

    // Critical Section
    public void addProcessEnd(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.PROCESS_END, process));
    }
    public void addProcessStart(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.PROCESS_START, process));
    }
    private void addInterrupt(Interrupt interrupt) {
        runWithInterruptQueueSemaphore(() -> interruptQueue.offer(interrupt));
    }
    public void addTimeOut(Process process) {
        runWithInterruptQueueSemaphore(() -> interruptQueue.offerFirst(new ProcessInterrupt(EInterrupt.EProcessInterrupt.TIME_OUT, process)));
    }
    public void addIOStart(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.IO_START, process));
    }
    public void addIOComplete(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.IO_COMPLETE, process));
    }
    public Interrupt pollInterrupt() {
        return runWithInterruptQueueSemaphore(() -> {
            if(interruptQueue.isEmpty()) return new NormalInterrupt(EInterrupt.ENormalInterrupt.NONE);
            return interruptQueue.poll();
        });
    }
    public Interrupt pollLast() {
        return runWithInterruptQueueSemaphore(interruptQueue::pollLast);
    }

    public boolean hasInterrupt() {
        return runWithInterruptQueueSemaphore(() -> !interruptQueue.isEmpty());
    }

    public void removeAllOf(Process currProcess) {
        runWithInterruptQueueSemaphore(() -> interruptQueue.removeIf((i) -> i instanceof ProcessInterrupt && ((ProcessInterrupt) i).getProcess() == currProcess));
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
}
