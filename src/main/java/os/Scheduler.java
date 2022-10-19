package main.java.os;

import main.java.cpu.CPU;
import main.java.cpu.interrupt.EInterrupt.EProcessInterrupt;
import main.java.cpu.interrupt.ProcessInterrupt;
import main.java.exception.EmptyReadyQueueException;
import main.java.power.Power;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Scheduler extends Thread{
    private final CPU cpu = CPU.getInstance();
    private final Loader loader = new Loader();

    private final ProcessQueue waitQueue = new ProcessQueue();
    private Process runningProcess = new Process();
    private InterruptHandler interruptHandler = new InterruptHandler(this);

    public Scheduler() {
        readyQueue = new ProcessQueue();
        readyQueueEmptySemaphore = new Semaphore(READY_QUEUE_MAX_SIZE, true);
        readyQueueFullSemaphore = new Semaphore(READY_QUEUE_MAX_SIZE, true);
        readyQueueEmptySemaphore.drainPermits();
        readyQueueSemaphore = new Semaphore(1, true);
    }

    public void init() {
        try {
            Scanner scanner = new Scanner(new File("data/exe1.txt"));
            this.runningProcess.load(scanner);
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Scheduler run() start");
        while (Power.isOn()) {
            interruptHandler.handle();
            while (!cpu.hasInterrupt()) {
                runningProcess.run();
            }
        }
        System.out.println("Scheduler run() end");
    }

    public void load(String processName) {
        cpu.addInterrupt(new ProcessInterrupt(EProcessInterrupt.PROCESS_START, loader.load(processName)));
    }
    public void load(Process process) {
        cpu.addInterrupt(new ProcessInterrupt(EProcessInterrupt.PROCESS_START, process));
    }

    // critical section
    private static final int READY_QUEUE_MAX_SIZE = 10;
    private final ProcessQueue readyQueue;
    private final Semaphore readyQueueEmptySemaphore; // init all acquired
    private final Semaphore readyQueueFullSemaphore;
    private final Semaphore readyQueueSemaphore;
    BlockingQueue<Process> queuee = new LinkedBlockingQueue<>(READY_QUEUE_MAX_SIZE);
    public synchronized void enReadyQueue(Process process) {
        try {
            readyQueueSemaphore.acquire();
            readyQueueFullSemaphore.acquire();
            this.readyQueue.enqueue(process);
            readyQueueEmptySemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readyQueueSemaphore.release();
        }
    }
    public synchronized Process deReadyQueue() {
        try {
            readyQueueSemaphore.acquire();
            readyQueueEmptySemaphore.acquire();
            Process process = this.readyQueue.dequeue();
            readyQueueFullSemaphore.release();
            return process;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readyQueueSemaphore.release();
        }
        return null;
    }
    public synchronized boolean isReadyQueueEmpty() {
        return readyQueue.isEmpty();
    }
    public synchronized void removeFromReadyQueue(Process interruptedProcess) {
        try {
            readyQueueSemaphore.acquire();
            readyQueue.remove(interruptedProcess);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readyQueueSemaphore.release();
        }
    }
    /////////////////////

    public Process getRunningProcess() {
        return runningProcess;
    }

    public void setRunningProcess(Process runningProcess) {
        this.runningProcess = runningProcess;
    }

    public class ProcessQueue {
        private Queue<Process> queue = new ArrayDeque<>();

        private void enqueue(Process process) {
            queue.offer(process);
        }

        private Process dequeue() {
            Process process = queue.poll();
            if(process == null) throw new EmptyReadyQueueException();
            return process;
        }

        private boolean isEmpty() {
            return queue.isEmpty();
        }

        private void remove(Process process) {
            queue.remove(process);
        }
    }

}
