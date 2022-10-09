package main.java;

import main.java.cpu.CPU;
import main.java.exception.NoMoreProcessException;
import main.java.os.OS;
import main.java.os.UI;
import main.java.power.Power;

public class Main {

    public static void main(String[] args) {
//        UI ui = new UI();
//        ui.run();

        CPU cpu = CPU.getInstance();
        cpu.run();

        OS os = new OS();
        os.init();
        os.load("exe1.txt");
//        os.load("exe1.txt");
//        os.load("exe1.txt");
        try {
            os.run();
        } catch(NoMoreProcessException e) {
            System.out.println("***** 더 이상 실행할 프로세스가 없습니다. *****");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cpu.stop();
            System.out.println("Exit System");
        }
    }
}
