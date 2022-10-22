package main.java.os.interrupt;

import main.java.os.Process;
import main.java.os.Scheduler;
import main.java.utils.Logger;

public class InterruptHandler {
    private final Scheduler scheduler;
    private final InterruptQueue interruptQueue = InterruptQueue.getInstance();

    public InterruptHandler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void handle() {
        Interrupt interrupt = interruptQueue.pollInterrupt();
        EInterrupt eInterrupt = interrupt.getEInterrupt();
//        System.out.println("Handle Interrupt: " + eInterrupt);
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
                handleTimeOut(interrupt);
                break;
            case PROCESS_START:
                handleProcessStart(interrupt);
                break;
            case PROCESS_END:
                handleProcessEnd(interrupt);
                break;
            case IO_START:
                handleIOStart(interrupt);
                break;
            case IO_COMPLETE:
                handleIOComplete(interrupt);
                break;
            default:
                break;
        }
    }

    private void handleIOComplete(ProcessInterrupt interrupt) {
        Process process = interrupt.getProcess();
        scheduler.removeFromWaitQueue(process);
        scheduler.enReadyQueue(process);
    }

    private void handleIOStart(ProcessInterrupt interrupt) {
        Process currProcess = scheduler.getRunningProcess();
        currProcess.waiting();
        scheduler.enWaitQueue(currProcess);
        scheduler.setRunningProcess(scheduler.deReadyQueue());
    }

    private void handleProcessStart(ProcessInterrupt interrupt) {
        scheduler.enReadyQueue(interrupt.getProcess());
    }

    private void handleProcessEnd(ProcessInterrupt interrupt) {
        Process interruptedProcess = interrupt.getProcess();
        Process currProcess = scheduler.getRunningProcess();
        if (interruptedProcess == currProcess) {
            if(scheduler.isReadyQueueEmpty()) scheduler.setRunningProcess(null);
            Process nextProcess = scheduler.deReadyQueue();
            scheduler.setRunningProcess(nextProcess);
        } else {
            scheduler.removeFromReadyQueue(interruptedProcess);
        }
        // Interrupt queue에서 전부 제거
        interruptQueue.removeAllOf(currProcess);
        // Wait queue에서 전부 제거
    }

    private void handleTimeOut(ProcessInterrupt interrupt) {
        Process currProcess = scheduler.getRunningProcess();
        if(!currProcess.equals(interrupt.getProcess())) return;
        currProcess.ready();
        scheduler.enReadyQueue(currProcess);
        Process nextProcess = scheduler.deReadyQueue();
        scheduler.setRunningProcess(nextProcess);
    }

    public void handleLatestInterrupt() {
        Interrupt interrupt = interruptQueue.pollLast();
        handle(interrupt);
    }

}
