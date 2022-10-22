package main.java.io;

import main.java.os.Process;
import main.java.power.Power;
import main.java.utils.SScanner;

public class Keyboard extends MyIO{
    private static final Keyboard instance = new Keyboard();

    private Keyboard(){}
    public static Keyboard getInstance() {
        return instance;
    }

    @Override
    public void run() {
        SScanner scanner = SScanner.getInstance();
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
                Thread.sleep(IO_DELAY);
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
}
