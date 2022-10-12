package main.java;

import main.java.cpu.CPU;
import main.java.exception.NoMoreProcessException;
import main.java.os.OS;
import main.java.os.UI;
public class Main {

    public static void main(String[] args) {
//        UI ui = new UI();
//        ui.run();

        CPU cpu = CPU.getInstance();
        cpu.run();

        OS os = new OS();
        os.init();
        os.load("exe1.txt");
        os.load("exe1.txt");
        os.start();
    }
}
