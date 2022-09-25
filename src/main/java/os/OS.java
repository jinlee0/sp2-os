package main.java.os;

public class OS {
    private final Scheduler scheduler = new Scheduler();

    public void init() {
        scheduler.init();
    }

    public void run() {
        System.out.println("OS run()");
        scheduler.run();
        System.out.println();
    }

    public void load(String processName) {
        scheduler.load(processName);
    }
}
