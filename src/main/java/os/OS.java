package main.java.os;

import main.java.cpu.CPU;
import main.java.exception.NoMoreProcessException;

public class OS extends Thread{
    private final Scheduler scheduler = new Scheduler();

    public void init() {
        scheduler.init();
    }

    public void run() {
        try {
            System.out.println("OS run()");
            scheduler.run();
            System.out.println();
        } catch(NoMoreProcessException e) {
            System.out.println("***** 더 이상 실행할 프로세스가 없습니다. *****");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CPU.getInstance().stop();
            System.out.println("Exit System");
        }
    }

    public void load(String processName) {
        scheduler.load(processName);
    }
}
