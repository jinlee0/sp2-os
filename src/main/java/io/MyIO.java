package main.java.io;

import main.java.os.Process;
import main.java.os.interrupt.InterruptQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class MyIO extends Thread{
    protected final BlockingQueue<Process> processBlockingQueue = new LinkedBlockingQueue<>(TASK_SIZE);
    protected final InterruptQueue interruptQueue = InterruptQueue.getInstance();

    protected final static int TASK_SIZE = 10;

    public abstract void run();

    public void add(Process process) {
        try {
            processBlockingQueue.put(process);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
