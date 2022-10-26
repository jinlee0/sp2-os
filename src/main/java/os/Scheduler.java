package main.java.os;

import main.java.exception.ProcessNotFound;
import main.java.os.interrupt.InterruptHandler;
import main.java.os.interrupt.InterruptQueue;
import main.java.power.Power;
import main.java.utils.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler extends Thread{
    private final InterruptQueue interruptQueue = InterruptQueue.getInstance();
    private final BlockingQueue<Process> readyQueue = new LinkedBlockingQueue<>(READY_QUEUE_MAX_SIZE);
    private final BlockingQueue<Process> waitQueue = new LinkedBlockingQueue<>(READY_QUEUE_MAX_SIZE);
    private Process runningProcess;
    private final InterruptHandler interruptHandler = new InterruptHandler(this);

    private static final int READY_QUEUE_MAX_SIZE = 10;

    public void run() {
        Logger.add("Scheduler run() start");
        while (Power.isOn()) {
            interruptHandler.handle();
            while (!interruptQueue.hasInterrupt()) {
                if(runningProcess == null) runningProcess = deReadyQueue();
                runningProcess.run();
            }
        }
        Logger.add("Scheduler run() end");
    }

    public void load(Process process) {
        interruptQueue.addProcessStart(process);
        if(isReadyQueueEmpty())
            interruptHandler.handleLatestInterrupt();
    }

    // critical section
    public void enReadyQueue(Process process) {
        try {
            readyQueue.put(process);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public Process deReadyQueue() {
        try {
            return readyQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean isReadyQueueEmpty() {
        return readyQueue.isEmpty();
    }
    public void removeFromReadyQueue(Process interruptedProcess) {
        readyQueue.remove(interruptedProcess);
    }
    public Process findBySerialNumber(long serialNumber) {
        for (Process process : readyQueue) {
            if(process.getSerialNumber() == serialNumber) return process;
        }
        return null;
    }
    /////////////////////

    // critical section
    public void enWaitQueue(Process process) {
        try {
            waitQueue.put(process);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void removeFromWaitQueue(Process process) {
        waitQueue.remove(process);
    }
    /////////////////////

    public Process getRunningProcess() {
        return runningProcess;
    }

    public void setRunningProcess(Process runningProcess) {
        this.runningProcess = runningProcess;
    }

    public void terminate(long processSerialNumber) {
        Process target;
        if(runningProcess != null && runningProcess.getSerialNumber() == processSerialNumber) target = runningProcess;
        else target = findBySerialNumber(processSerialNumber);
        if(target == null) throw new ProcessNotFound();
        interruptQueue.addProcessEnd(target);
    }
}
