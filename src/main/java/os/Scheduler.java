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

public class Scheduler extends Thread{
    private final CPU cpu = CPU.getInstance();
    private final Loader loader = new Loader();

    private final ProcessQueue readyQueue = new ProcessQueue();
    private final ProcessQueue waitQueue = new ProcessQueue();
    private Process runningProcess = new Process();
    private InterruptHandler interruptHandler = new InterruptHandler(this);

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

    public synchronized void enReadyQueue(Process process) {
        this.readyQueue.enqueue(process);
    }
    public synchronized Process deReadyQueue() {
        return this.readyQueue.dequeue();
    }
    public synchronized boolean isReadyQueueEmpty() {
        return readyQueue.isEmpty();
    }
    public synchronized void removeFromReadyQueue(Process interruptedProcess) {
        readyQueue.remove(interruptedProcess);
    }

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

        private void remove(Process interruptedProcess) {
            queue.remove(interruptedProcess);
        }
    }

}
