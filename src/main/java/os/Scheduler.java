package main.java.os;

import main.java.cpu.CPU;
import main.java.cpu.interrupt.EInterrupt;
import main.java.cpu.interrupt.EInterrupt.EProcessInterrupt;
import main.java.cpu.interrupt.Interrupt;
import main.java.cpu.interrupt.NormalInterrupt;
import main.java.cpu.interrupt.ProcessInterrupt;
import main.java.exception.EmptyReadyQueueException;
import main.java.exception.NoMoreProcessException;
import main.java.power.Power;

import java.util.ArrayDeque;
import java.util.Queue;

public class Scheduler {
    private final CPU cpu = CPU.getInstance();
    private final Loader loader = new Loader();

    private final ProcessQueue readyQueue = new ProcessQueue();
    private final ProcessQueue waitQueue = new ProcessQueue();
    private Process runningProcess = new Process();
    private InterruptHandler interruptHandler = new InterruptHandler();


    public void init() {
        Process process = new Process();
        cpu.addInterrupt(new ProcessInterrupt(EProcessInterrupt.PROCESS_START, process));
    }

    public void run() {
        System.out.println("Scheduler run() start");
        while (Power.isOn()) {
            interruptHandler.handle();
            while (!cpu.hasInterrupt()) {
                runningProcess.run();
                if (runningProcess.isEnd())
                    cpu.addInterrupt(new ProcessInterrupt(EProcessInterrupt.PROCESS_END, runningProcess));
            }
        }
        System.out.println("Scheduler run() end");
    }

    public void load(String processName) {
        cpu.addInterrupt(new ProcessInterrupt(EProcessInterrupt.PROCESS_START, loader.load(processName)));
    }

    private class InterruptHandler {
        public void handle() {
            Interrupt interrupt = cpu.pollInterrupt();
            EInterrupt eInterrupt = interrupt.getEInterrupt();
            System.out.println("Handle Interrupt: " + eInterrupt);
            if(interrupt instanceof ProcessInterrupt) handle((ProcessInterrupt) interrupt);
            else if(interrupt instanceof NormalInterrupt) handle((NormalInterrupt) interrupt);
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
            EProcessInterrupt eInterrupt = interrupt.getEInterrupt();
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
            readyQueue.enqueue(interrupt.getProcess());
        }

        private void handleProcessEnd(ProcessInterrupt interrupt) {
            if(readyQueue.isEmpty()) throw new NoMoreProcessException(); // 더 이상 실행할 프로세스 없음
            Process interruptedProcess = interrupt.getProcess();
            if (interruptedProcess == runningProcess) {
                Process nextProcess = readyQueue.dequeue();
                runningProcess = nextProcess;
            } else {
                readyQueue.remove(interruptedProcess);
            }
        }

        private void handleTimeOut() {
            Process currProcess = runningProcess;
            readyQueue.enqueue(currProcess);
            Process nextProcess = readyQueue.dequeue();
            runningProcess = nextProcess;
        }
    }

    private class ProcessQueue {
        private Queue<Process> queue = new ArrayDeque<>();

        public void enqueue(Process process) {
            queue.offer(process);
        }

        public Process dequeue() {
            Process process = queue.poll();
            if(process == null) throw new EmptyReadyQueueException();
            return process;
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }

        public void remove(Process interruptedProcess) {
            queue.remove(interruptedProcess);
        }
    }

}
