package main.java.os;

import main.java.exception.EmptyReadyQueueException;
import main.java.exception.ProcessNotFound;
import main.java.os.interrupt.InterruptHandler;
import main.java.os.interrupt.InterruptQueue;
import main.java.power.Power;
import main.java.utils.Logger;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler extends Thread{
    private final InterruptQueue interruptQueue = InterruptQueue.getInstance();
    private final BlockingQueue<Process> readyQueue = new LinkedBlockingQueue<>(READY_QUEUE_MAX_SIZE);
    private final BlockingQueue<Process> waitQueue = new LinkedBlockingQueue<>(READY_QUEUE_MAX_SIZE);
    private Process runningProcess;
    private InterruptHandler interruptHandler = new InterruptHandler(this);

    private static final int READY_QUEUE_MAX_SIZE = 10;

    public void init() {
//        try {
//            Scanner scanner1 = new Scanner(new File("data/exe1.txt"));
//            Process process1 = new Process();
//            process1.load(new Scanner(new File("data/exe1.txt")));
//            load(process1);
//            scanner1.close();
//
//            Scanner scanner2 = new Scanner(new File("data/exe1.txt"));
//            Process process2 = new Process();
//            process2.load(new Scanner(new File("data/exe1.txt")));
//            load(process2);
//            scanner2.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public void run() {
//        System.out.println("Scheduler run() start");
        Logger.add("Scheduler run() start");
        while (Power.isOn()) {
            interruptHandler.handle();
            while (!interruptQueue.hasInterrupt()) {
                if(runningProcess == null) runningProcess = deReadyQueue();
                runningProcess.run();
            }
        }
//        System.out.println("Scheduler run() end");
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
        Process target = null;
        if(runningProcess != null && runningProcess.getSerialNumber() == processSerialNumber) target = runningProcess;
        else target = findBySerialNumber(processSerialNumber);
        if(target == null) throw new ProcessNotFound();
        interruptQueue.addProcessEnd(target);
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
