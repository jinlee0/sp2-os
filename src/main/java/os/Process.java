package main.java.os;


import main.java.cpu.Context;
import main.java.cpu.EContext;

import java.util.List;
import java.util.Scanner;
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

    public boolean isEnd() {
        return pcb.getPC() >= instructions.size();
    }

    // ****************************** 1005 수업
    private int codeSize, dataSize, stackSize, heapSize;
    private Vector<String> codeList = new Vector<>();

    public void load(Scanner scanner) {
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            while (!line.equals(".end")) {
                if(line.equals(".data")) loadDataSegment(scanner);
                else if(line.equals("code")) loadCodeSegment(scanner);
                line = scanner.nextLine();
            }
        }
    }

    private void loadCodeSegment(Scanner scanner) {
        while (true) {
            String line = scanner.nextLine();
            if(line.equals(".end")) break;
            codeList.add(line);
        }
    }

    private void loadDataSegment(Scanner scanner) {
        while (true) {
            String token = scanner.next();
            if(token.equals(".code")) break;
            int size = Integer.parseInt(scanner.next());
            if(token.equals("codeSize")) this.codeSize = size;
            if(token.equals("dataSize")) this.dataSize = size;
            if(token.equals("stackSize")) this.stackSize = size;
            if(token.equals("heapSize")) this.heapSize = size;
        }
    }

    public int getCodeSize() {
        return codeSize;
    }

    public int getDataSize() {
        return dataSize;
    }

    public int getStackSize() {
        return stackSize;
    }

    public int getHeapSize() {
        return heapSize;
    }

    public Vector<String> getCodeList() {
        return codeList;
    }

    // *****************************

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
