package main.java.os;

import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.os.interrupt.*;
import main.java.power.Power;
import main.java.utils.Logger;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class Scheduler extends Thread{
    // components
    private final Queue<Process> readyQueue = new ArrayDeque<>(READY_QUEUE_MAX_SIZE);
    private final Queue<Process> waitQueue = new ArrayDeque<>(READY_QUEUE_MAX_SIZE);
    private final InterruptHandler interruptHandler = new InterruptHandler(this);

    // associations
    private final InterruptQueue interruptQueue = InterruptQueue.getInstance();
    private final BlockingQueue<Interrupt> fileIOCommandQueue;

    // working variables
    private Process runningProcess;

    private static final int READY_QUEUE_MAX_SIZE = 10;

    public Scheduler(BlockingQueue<Interrupt> fileIOCommandQueue) {
        this.fileIOCommandQueue = fileIOCommandQueue;
    }

    public void run() {
        Logger.add("Scheduler run() start");
        while (Power.isOn()) {
            if (interruptQueue.hasInterrupt()) interruptHandler.handle();
            else {
                if (runningProcess == null) {
                    runningProcess = deReadyQueue();
                    if (runningProcess == null) continue;
                }
                runningProcess.run();
            }
        }
        Logger.add("Scheduler run() end");
    }

    // critical section
    public void enReadyQueue(Process process) {
//        try {
            readyQueue.offer(process);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
    public Process deReadyQueue() {
//        try {
            return readyQueue.poll();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            return null;
//        }
    }
    public boolean isReadyQueueEmpty() {
        return readyQueue.isEmpty();
    }
    public void removeFromReadyQueue(Process interruptedProcess) {
        readyQueue.remove(interruptedProcess);
    }
    public Process findBySerialNumber(long serialNumber) {
        if(runningProcess !=null && runningProcess.getSerialNumber() == serialNumber) return runningProcess;
        for (Process process : readyQueue) {
            if(process.getSerialNumber() == serialNumber) return process;
        }
        return null;
    }
    /////////////////////

    // critical section
    public void enWaitQueue(Process process) {
//        try {
            waitQueue.offer(process);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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

    private class InterruptHandler {
        private final Scheduler scheduler;

        private InterruptHandler(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        public void handle() {
            Interrupt interrupt = interruptQueue.pollInterrupt();
            if(interrupt == null) return;
            EInterrupt eInterrupt = interrupt.getEInterrupt();
            Logger.add("Handle Interrupt: " + eInterrupt);
            handle(interrupt);
        }

        private void handle(Interrupt interrupt) {
            if (interrupt instanceof ProcessInterrupt) handle((ProcessInterrupt) interrupt);
            else if (interrupt instanceof NormalInterrupt) handle((NormalInterrupt) interrupt);
        }

        private void handle(NormalInterrupt interrupt) {
            EInterrupt.ENormalInterrupt eInterrupt = interrupt.getEInterrupt();
            switch (eInterrupt) {
                default:
                    break;
            }
        }

        private void handle(ProcessInterrupt interrupt) {
            EInterrupt.EProcessInterrupt eInterrupt = interrupt.getEInterrupt();
            switch (eInterrupt) {
                case TIME_OUT:
                    handleTimeOut(interrupt.getProcess());
                    break;
                case PROCESS_START:
                    handleProcessStart(interrupt.getProcess());
                    break;
                case PROCESS_END:
                    handleProcessEnd(interrupt.getProcess());
                    break;
                case READ_START:
                    handleReadStart(interrupt.getProcess());
                case WRITE_START:
                    handleWriteStart(interrupt.getProcess());
//                    handleIOStart();
                    break;
                case READ_COMPLETE:
                case WRITE_COMPLETE:
                    handleIOComplete(interrupt.getProcess());
                    break;
                default:
                    break;
            }
        }

        private void handleWriteStart(Process process) {
            handleIOStart();
            Monitor.getInstance().add(process);
        }

        private void handleReadStart(Process process) {
            handleIOStart();
            Keyboard.getInstance().add(process);
        }

        private void handleIOComplete(Process process) {
            scheduler.removeFromWaitQueue(process);
            scheduler.enReadyQueue(process);
        }

        private void handleIOStart() {
            Process currProcess = scheduler.getRunningProcess();
            currProcess.waiting();
            scheduler.enWaitQueue(currProcess);
            scheduler.setRunningProcess(scheduler.deReadyQueue());
        }

        private void handleProcessStart(Process process) {
            scheduler.enReadyQueue(process);
        }

        private void handleProcessEnd(Process process) {
            Process currProcess = scheduler.getRunningProcess();
            if (process == currProcess) {
                if(scheduler.isReadyQueueEmpty()) scheduler.setRunningProcess(null);
                Process nextProcess = scheduler.deReadyQueue();
                scheduler.setRunningProcess(nextProcess);
            } else {
                scheduler.removeFromReadyQueue(process);
            }
            // Interrupt queue에서 전부 제거
            interruptQueue.removeAllOf(currProcess);
            // Wait queue에서 전부 제거
        }

        private void handleTimeOut(Process process) {
            if(process == null) return;
            if(!process.equals(process)) return;
            process.ready();
            scheduler.enReadyQueue(process);
            Process nextProcess = scheduler.deReadyQueue();
            scheduler.setRunningProcess(nextProcess);
        }

    }
}
