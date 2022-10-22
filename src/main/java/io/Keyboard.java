package main.java.io;

import main.java.os.Process;
import main.java.os.interrupt.InterruptQueue;
import main.java.power.Power;
import main.java.utils.MPrinter;
import main.java.utils.MScanner;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Keyboard extends Thread{
    private static final Keyboard instance = new Keyboard();
    private final BlockingQueue<Task> tasks = new LinkedBlockingQueue<>(KEYBOARD_BUFFER_SIZE);
    private final InterruptQueue interruptQueue = InterruptQueue.getInstance();

    private final static int KEYBOARD_BUFFER_SIZE = 10;

    private Keyboard(){}
    public static Keyboard getInstance() {
        return instance;
    }

    @Override
    public void run() {
        MScanner scanner = MScanner.getInstance();
        MPrinter printer = MPrinter.getInstance();
        while (Power.isOn()) {
            try {
                Task task = tasks.take();
                try {
                    Process owner = task.getOwner();
                    int buffer = Integer.parseInt(scanner.nextLine("Process_" + owner.getSerialNumber() + " >> " + "Keyboard >> "));
                    owner.setAC(buffer);
                    interruptQueue.addIOComplete(owner);
                } catch (NumberFormatException e) {
                    add(task.getOwner());
                }
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(Process process) {
        try {
            tasks.put(new Task(process, null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
