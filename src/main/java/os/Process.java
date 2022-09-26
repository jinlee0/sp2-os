package main.java.os;


import main.java.cpu.Context;
import main.java.cpu.EContext;

import java.util.List;
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

    public static class PCB {
        private Context context = new Context();

        private int pid;
        // Account
        private int oid;
        // Status
        private EStatus eStatus;
        // IO Status Information
        private List<?> ioDevices;

        private CPUContext cpuContext;

        public class CPUContext {
            // CU
            private int pc;
            // Segment Registers
            private int cs;
            private int ds;
            private int ss;
            private int hs;
            // ALU
            private int ac;
            // Memory Interface
            private int mar;
            private int mbr;
        }


        public Context getContext() {
            return context;
        }
        public int getPC() {
            return context.get(EContext.PC);
        }

        public enum EStatus {
            RUNNING, READY, WAITING, SUSPENDED
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
