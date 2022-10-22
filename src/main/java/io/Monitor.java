package main.java.io;

import main.java.os.Process;
import main.java.os.interrupt.InterruptQueue;
import main.java.power.Power;
import main.java.utils.MPrinter;
import main.java.utils.MScanner;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Monitor extends Thread{
    private static final Monitor instance = new Monitor();
    private final BlockingQueue<Task> tasks = new LinkedBlockingQueue<>(SCREEN_BUFFER_SIZE);
    private final InterruptQueue interruptQueue = InterruptQueue.getInstance();

    private final static int SCREEN_BUFFER_SIZE = 10;

    private Monitor(){}
    public static Monitor getInstance() {
        return instance;
    }

    @Override
    public void run() {
        while (Power.isOn()) {
            try {
                Task task = tasks.take();
                MPrinter.getInstance().println("Screen >> " + task.getValue());
                interruptQueue.addIOComplete(task.getOwner());
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(Process process, Object value) {
        try {
            tasks.put(new Task(process, value));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean hasMore() {
        return !tasks.isEmpty();
    }

    public class Task {
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
        public void setOwner(Process owner) {
            this.owner = owner;
        }
        public Object getValue() {
            return value;
        }
        public void setValue(Object value) {
            this.value = value;
        }
    }
}
