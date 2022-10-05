package main.java.os;

import main.java.cpu.CPU;
import main.java.cpu.interrupt.EInterrupt.EProcessInterrupt;
import main.java.cpu.interrupt.ProcessInterrupt;
import main.java.exception.EmptyReadyQueueException;
import main.java.power.Power;

import java.util.ArrayDeque;
import java.util.Queue;

public class Scheduler {
    private final CPU cpu = CPU.getInstance();
    private final Loader loader = new Loader();

    private final ProcessQueue readyQueue = new ProcessQueue();
    private final ProcessQueue waitQueue = new ProcessQueue();
    private Process runningProcess = new Process();
    private InterruptHandler interruptHandler = new InterruptHandler(this);


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

    public ProcessQueue getReadyQueue() {
        return readyQueue;
    }

    public Process getRunningProcess() {
        return runningProcess;
    }

    public void setRunningProcess(Process runningProcess) {
        this.runningProcess = runningProcess;
    }

    public class ProcessQueue {
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
