package main.java.io;

import main.java.os.Process;
import main.java.os.interrupt.InterruptQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class MyIO extends Thread{
    protected final BlockingQueue<Process> processBlockingQueue = new LinkedBlockingQueue<>(TASK_SIZE);
    protected final InterruptQueue interruptQueue = InterruptQueue.getInstance();

    protected final static int TASK_SIZE = 10;
    protected final static long IO_DELAY = 300L;

    public abstract void run();
}
