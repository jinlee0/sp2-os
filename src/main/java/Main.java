package main.java;

import main.java.cpu.CPU;
import main.java.os.Loader;
import main.java.os.OS;

public class Main {
    private final CPU cpu = CPU.getInstance();
    private Loader loader = new Loader();

    public static void main(String[] args) {
        CPU cpu = CPU.getInstance();
        cpu.run();

        OS os = new OS();
        os.init();
        os.load("process1");
        os.load("process2");
        os.load("process3");
        try {
            os.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exit System");
            cpu.stop();
        }
    }

}
