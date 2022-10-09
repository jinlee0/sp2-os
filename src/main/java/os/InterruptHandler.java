package main.java.os;

import main.java.cpu.CPU;
import main.java.cpu.interrupt.EInterrupt;
import main.java.cpu.interrupt.Interrupt;
import main.java.cpu.interrupt.NormalInterrupt;
import main.java.cpu.interrupt.ProcessInterrupt;
import main.java.exception.NoMoreProcessException;

class InterruptHandler {
    private final Scheduler scheduler;
    private Scheduler.ProcessQueue readyQueue;

    public InterruptHandler(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.readyQueue = scheduler.getReadyQueue();
    }

    public void handle() {
        Interrupt interrupt = CPU.getInstance().pollInterrupt();
        EInterrupt eInterrupt = interrupt.getEInterrupt();
        System.out.println("Handle Interrupt: " + eInterrupt);
        if (interrupt instanceof ProcessInterrupt) handle((ProcessInterrupt) interrupt);
        else if (interrupt instanceof NormalInterrupt) handle((NormalInterrupt) interrupt);
    }

    private void handle(NormalInterrupt interrupt) {
        EInterrupt.ENormalInterrupt eInterrupt = interrupt.getEInterrupt();
        switch (eInterrupt) {
            case TIME_OUT:
                handleTimeOut();
                break;
            default:
                break;
        }
    }

    private void handle(ProcessInterrupt interrupt) {
        EInterrupt.EProcessInterrupt eInterrupt = interrupt.getEInterrupt();
        switch (eInterrupt) {
            case PROCESS_START:
                handleProcessStart(interrupt);
                break;
            case PROCESS_END:
                handleProcessEnd(interrupt);
                break;
            case IO_START:
                break;
            case IO_COMPLETE:
                break;
            default:
                break;
        }
    }

    private void handleProcessStart(ProcessInterrupt interrupt) {
        scheduler.getReadyQueue().enqueue(interrupt.getProcess());
    }

    private void handleProcessEnd(ProcessInterrupt interrupt) {
        if (readyQueue.isEmpty()) {
            throw new NoMoreProcessException(); // 더 이상 실행할 프로세스 없음
        }
        Process interruptedProcess = interrupt.getProcess();
        Process runningProcess = scheduler.getRunningProcess();
        if (interruptedProcess == runningProcess) {
            Process nextProcess = readyQueue.dequeue();
            scheduler.setRunningProcess(nextProcess);
        } else {
            readyQueue.remove(interruptedProcess);
        }
    }

    private void handleTimeOut() {
        Process runningProcess = scheduler.getRunningProcess();
        Process currProcess = runningProcess;
        readyQueue.enqueue(currProcess);
        Process nextProcess = readyQueue.dequeue();
        scheduler.setRunningProcess(nextProcess);
    }
}
