package main.java;

import main.java.os.UI;

public class Main {

    public static void main(String[] args) {
        UI ui = new UI();
        ui.run();

//        CPU cpu = CPU.getInstance();
//        cpu.run();
//
//        OS os = new OS();
//        os.init();
//        os.load("process1");
//        os.load("process2");
//        os.load("process3");
//        try {
//            os.run();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Exit System");
//            cpu.stop();
//        }
    }
}
