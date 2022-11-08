package main.java.io;

import main.java.os.Process;
import main.java.power.Power;
import main.java.utils.SPrinter;

public class Monitor extends MyIO{
    private static final Monitor instance = new Monitor();

    private Monitor(){}
    public static Monitor getInstance() {
        return instance;
    }

    @Override
    public void run() {
        while (Power.isOn()) {
            try {
                Task task = tasks.take();
                Process owner = task.getOwner();
                SPrinter.getInstance().println("Process_" + owner.getSerialNumber() + " >> Screen >> " + task.getValue() + System.lineSeparator());
                interruptQueue.addWriteComplete(owner);
                Thread.sleep(IO_DELAY);
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
}
