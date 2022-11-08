package main.java.os.interrupt;

import main.java.os.Process;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class SyncQueue {
    private final Semaphore interruptQueueSemaphore = new Semaphore(1, true);
    private final Deque<Interrupt> queue = new ArrayDeque<>();

    // Critical Section
    public Interrupt pollInterrupt() {
        return runWithInterruptQueueSemaphore(() -> {
            if(queue.isEmpty()) return new NormalInterrupt(EInterrupt.ENormalInterrupt.NONE);
            return queue.poll();
        });
    }
    public Interrupt pollLast() {
        return runWithInterruptQueueSemaphore(queue::pollLast);
    }

    public boolean hasInterrupt() {
        return runWithInterruptQueueSemaphore(() -> !queue.isEmpty());
    }

    public void removeAllOf(Process currProcess) {
        runWithInterruptQueueSemaphore(() -> queue.removeIf((i) -> i instanceof ProcessInterrupt && ((ProcessInterrupt) i).getProcess() == currProcess));
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
