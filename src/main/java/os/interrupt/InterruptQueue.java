package main.java.os.interrupt;

import main.java.os.Process;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class InterruptQueue {
    private final Semaphore interruptQueueSemaphore = new Semaphore(1, true);
    private final Deque<Interrupt> interruptQueue = new ArrayDeque<>();

    // Critical Section
    public ProcessInterrupt addProcessEnd(Process process) {
        ProcessInterrupt interrupt = new ProcessInterrupt(EInterrupt.EProcessInterrupt.PROCESS_END, process);
        addInterrupt(interrupt);
        return interrupt;
    }
    public ProcessInterrupt addProcessStart(Process process) {
        ProcessInterrupt interrupt = new ProcessInterrupt(EInterrupt.EProcessInterrupt.PROCESS_START, process);
        addInterrupt(interrupt);
        return interrupt;
    }
    private void addInterrupt(Interrupt interrupt) {
        runWithInterruptQueueSemaphore(() -> interruptQueue.offer(interrupt));
    }
    public void addTimeOut(Process process) {
        runWithInterruptQueueSemaphore(() -> interruptQueue.offerFirst(new ProcessInterrupt(EInterrupt.EProcessInterrupt.TIME_OUT, process)));
    }
    public void addReadIntStart(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.READ_INT_START, process));
    }
    public void addReadIntComplete(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.READ_INT_COMPLETE, process));
    }
    public void addWriteIntStart(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.WRITE_INT_START, process));
    }
    public void addWriteIntComplete(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.WRITE_INT_COMPLETE, process));
    }
    public void addOpenFileStart(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.OPEN_FILE_START, process));
    }

    public void addOpenFileComplete(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.OPEN_FILE_COMPLETE, process));
    }

    public void addCloseFileStart(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.CLOSE_FILE_START, process));
    }

    public void addCloseFileComplete(Process process) {
        addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.CLOSE_FILE_COMPLETE, process));
    }

    public Interrupt pollInterrupt() {
        return runWithInterruptQueueSemaphore(interruptQueue::poll);
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
