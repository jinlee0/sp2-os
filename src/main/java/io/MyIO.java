package main.java.io;

import main.java.os.Process;
import main.java.os.interrupt.InterruptQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class MyIO extends Thread{
    protected final BlockingQueue<Task> tasks = new LinkedBlockingQueue<>(TASK_SIZE);
    protected final InterruptQueue interruptQueue = InterruptQueue.getInstance();

    protected final static int TASK_SIZE = 10;
    protected final static long IO_DELAY = 300L;

    public abstract void run();

    public static class Task {
        private Process owner;
        private Object value;

        public Task(Process owner, Object value) {
            this.owner = owner;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Task{" +
                    "owner=" + owner +
                    ", value=" + value +
                    '}';
        }

        public Process getOwner() {
            return owner;
        }
        public Object getValue() {
            return value;
        }
    }
}
