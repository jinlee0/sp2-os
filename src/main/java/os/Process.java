package main.java.os;


import main.java.cpu.Context;
import main.java.cpu.EContext;

import java.util.Vector;

public class Process {
    private Vector<Instruction> instructions = new Vector<>();
    private PCB pcb = new PCB();
    private int serialNumber;
    private static int SERIAL_NUMBER = 0;
    public Process() {
        serialNumber = SERIAL_NUMBER++;
        instructions.add(i1);
        instructions.add(i2);
        instructions.add(i3);
        instructions.add(i4);
        instructions.add(i5);
    }

    public void run() {
        System.out.print("Process " + serialNumber + " ");
        int PC = pcb.getPC();
        instructions.get(PC).execute();
        pcb.getContext().set(EContext.PC, PC + 1);
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Context  getContext() {
        return this.pcb.getContext();
    }

    public boolean isEnd() {
        return pcb.getPC() >= instructions.size();
    }

    public class PCB {
        private Context context = new Context();

        public Context getContext() {
            return context;
        }
        public int getPC() {
            return context.get(EContext.PC);
        }
    }

    @FunctionalInterface
    private interface Instruction {
        void execute();
    }

    private Instruction i1 = () -> {
        System.out.println("instruction i1");
    };
    private Instruction i2 = () -> {
        System.out.println("instruction i2");
    };
    private Instruction i3 = () -> {
        System.out.println("instruction i3");
    };
    private Instruction i4 = () -> {
        System.out.println("instruction i4");
    };
    private Instruction i5 = () -> {
        System.out.println("instruction i5");
    };
}
