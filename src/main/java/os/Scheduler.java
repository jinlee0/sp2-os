package main.java.os;

import main.java.io.FileSystem;
import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.os.interrupt.*;
import main.java.utils.Logger;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class Scheduler{
    // components
    private final Queue<Process> readyQueue = new ArrayDeque<>(READY_QUEUE_MAX_SIZE);
    private final Queue<Process> waitQueue = new ArrayDeque<>(READY_QUEUE_MAX_SIZE);
    private final InterruptHandler interruptHandler = new InterruptHandler(this);

    // associations
    private final InterruptQueue interruptQueue;
    private final Monitor monitor;
    private final Keyboard keyboard;
    private final FileSystem fileSystem;

    // working variables
    private Process runningProcess;

    private static final int READY_QUEUE_MAX_SIZE = 10;

    public Scheduler(InterruptQueue interruptQueue, Monitor monitor, Keyboard keyboard, FileSystem fileSystem) {
        this.interruptQueue = interruptQueue;
        this.monitor = monitor;
        this.keyboard = keyboard;
        this.fileSystem = fileSystem;
    }

    public void handleAllInterrupts() {
        while(interruptQueue.hasInterrupt())
            interruptHandler.handle();
    }

    public void executeInstruction() {
        if (runningProcess == null) {
            runningProcess = deReadyQueue();
            if (runningProcess == null) return;
        }
        runningProcess.run();
    }

    private void enReadyQueue(Process process) {
        readyQueue.offer(process);
    }
    private Process deReadyQueue() {
        return readyQueue.poll();
    }
    private boolean isReadyQueueEmpty() {
        return readyQueue.isEmpty();
    }
    private void removeFromReadyQueue(Process interruptedProcess) {
        readyQueue.remove(interruptedProcess);
    }
    public Optional<Process> findBySerialNumber(long serialNumber) {
        if(runningProcess !=null && runningProcess.getSerialNumber() == serialNumber) return Optional.of(runningProcess);
        for (Process process : readyQueue) {
            if(process.getSerialNumber() == serialNumber) return Optional.of(process);
        }
        return Optional.empty();
    }
    /////////////////////

    // critical section
    private void enWaitQueue(Process process) {
            waitQueue.offer(process);
    }
    private void removeFromWaitQueue(Process process) {
        waitQueue.remove(process);
    }
    /////////////////////

    private Process getRunningProcess() {
        return runningProcess;
    }
    private void setRunningProcess(Process runningProcess) {
        this.runningProcess = runningProcess;
    }

    public void initialize() {
    }

    public void finish() {
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
                case READ_INT_START:
                    handleReadStart(interrupt.getProcess());
                    break;
                case WRITE_INT_START:
                    handleWriteStart(interrupt.getProcess());
                    break;
                case READ_INT_COMPLETE:
                case WRITE_INT_COMPLETE:
                case OPEN_FILE_COMPLETE:
                    handleOpenFileComplete(interrupt.getProcess());
                case CLOSE_FILE_COMPLETE:
                    handleIOComplete(interrupt.getProcess());
                    break;
                case OPEN_FILE_START:
                    handleOpenFileStart(interrupt.getProcess());
                    break;
                default:
                    break;
            }
        }

        private void handleOpenFileComplete(Process process) {

        }

        private void handleOpenFileStart(Process process) {
            handleIOStart();
            fileSystem.add(process);
        }

        private void handleWriteStart(Process process) {
            handleIOStart();
            monitor.add(process);
        }

        private void handleReadStart(Process process) {
            handleIOStart();
            keyboard.add(process);
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
                scheduler.removeFromWaitQueue(process);
            }
            // Interrupt queue에서 전부 제거
            interruptQueue.removeAllOf(currProcess);
            // Wait queue에서 전부 제거
            process.finish();
        }

        private void handleTimeOut(Process process) {
            if (process == null || !process.equals(process) || runningProcess == null) return;
            process.ready();
            scheduler.enReadyQueue(process);
            Process nextProcess = scheduler.deReadyQueue();
            scheduler.setRunningProcess(nextProcess);
        }
    }
}
