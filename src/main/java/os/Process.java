package main.java.os;


import main.java.cpu.CPU;
import main.java.cpu.Context;
import main.java.cpu.EContext;
import main.java.cpu.EOpCode;
import main.java.cpu.interrupt.EInterrupt;
import main.java.cpu.interrupt.ProcessInterrupt;
import main.java.exception.CannotLoadUninitializedMemory;
import main.java.exception.NotProgramException;

import java.util.*;

public class Process {
    private PCB pcb = new PCB();
    private int serialNumber;
    private static int SERIAL_NUMBER = 0;
    private Vector<String> codeList = new Vector<>();
    private Map<Integer, Integer> memory = new HashMap<>();

    public Process() {
        serialNumber = SERIAL_NUMBER++;
        pcb.pid = serialNumber;
    }

    public void run() {
        System.out.println("Process " + serialNumber);
        executeOneLine();
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeOneLine() {
        int PC = pcb.getPC();
        String line = codeList.get(PC);
        System.out.println("\t"+line);
        StringTokenizer st = new StringTokenizer(line);
        EOpCode eOpCode = EOpCode.valueOf(st.nextToken().trim().toUpperCase(Locale.ROOT));
        int operand = Integer.parseInt(st.nextToken());
        switch (eOpCode) {
            case LDC:
                exeLDC(operand);
                break;
            case LDA:
                exeLDA(operand);
                break;
            case STA:
                exeSTA(operand);
                break;
            case ADDA:
                exeADDA(operand);
                break;
            case SUBA:
                exeSUBA(operand);
                break;
            case MULA:
                exeMULA(operand);
                break;
            case DIVA:
                exeDIVA(operand);
                break;
            case SHRA: // bit shift right
                exeSHRA(operand);
                break;
            case ADDC:
                exeADDC(operand);
                break;
            case SUBC:
                exeSUBC(operand);
                break;
            case MULC:
                exeMULC(operand);
                break;
            case DIVC:
                exeDIVC(operand);
                break;
            case SHRC:
                exeSHRC(operand);
                break;
            case BR:
                exeBR(operand);
                break;
            case BZ:
                exeBZ(operand);
                break;
            case BN:
                exeBN(operand);
                break;
            case BP:
                exeBP(operand);
                break;
            case BZN:
                exeBZN(operand);
                break;
            case BZP:
                exeBZP(operand);
                break;
            case HALT:
                exeHALT();
                break;
        }
//        System.out.println("CODE: " + line);
        pcb.getContext().set(EContext.PC, PC + 1);
    }

    private void exeHALT() {
        CPU.getInstance().addInterrupt(new ProcessInterrupt(EInterrupt.EProcessInterrupt.PROCESS_END, this));
        System.out.println(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Process_" + serialNumber + "의 종료 직전 상태").append(System.lineSeparator());
        sb.append("\t").append("PCB").append(System.lineSeparator());
        for (EContext eContext : EContext.values()) {
            sb.append("\t\t").append(eContext).append(": ").append(pcb.context.get(eContext)).append(System.lineSeparator());
        }
        sb.append("\t").append("Memory").append(System.lineSeparator());
        for (Integer integer : memory.keySet()) {
            sb.append("\t\t").append(integer).append(": ").append(memory.get(integer)).append(System.lineSeparator());
        }
        return sb.toString();
    }

    private void exeBZP(int operand) {
        if(pcb.getAC() >= 0) pcb.setPC(operand);
    }
    private void exeBZN(int operand) {
        if(pcb.getAC() <= 0) pcb.setPC(operand);
    }
    private void exeBP(int operand) {
        if(pcb.getAC() > 0) pcb.setPC(operand);
    }
    private void exeBN(int operand) {
        if(pcb.getAC() < 0) pcb.setPC(operand);
    }
    private void exeBZ(int operand) {
        if(pcb.getAC() == 0) pcb.setPC(operand);
    }
    private void exeBR(int operand) {
        pcb.setPC(operand);
    }
    private void exeSHRC(int operand) {
        pcb.setAC(pcb.getAC() >> operand);
    }
    private void exeDIVC(int operand) {
        pcb.setAC(pcb.getAC() / operand);
    }
    private void exeMULC(int operand) {
        pcb.setAC(pcb.getAC() * operand);
    }
    private void exeSUBC(int operand) {
        pcb.setAC(pcb.getAC() - operand);
    }
    private void exeADDC(int operand) {
        pcb.setAC(pcb.getAC() + operand);
    }
    private void exeSHRA(int operand) {
        pcb.setAC(pcb.getAC() >> loadMemory(operand));
    }
    private void exeDIVA(int operand) {
        pcb.setAC(pcb.getAC() / loadMemory(operand));
    }
    private void exeMULA(int operand) {
        pcb.setAC(pcb.getAC() * loadMemory(operand));
    }
    private void exeSUBA(int operand) {
        pcb.setAC(pcb.getAC() - loadMemory(operand));
    }
    private void exeADDA(int operand) {
        pcb.setAC(pcb.getAC() + loadMemory(operand));
    }
    private void exeSTA(int operand) {
        memory.put(operand, pcb.getAC());
    }
    private void exeLDA(int operand) {
        pcb.setAC(loadMemory(operand));
    }
    private void exeLDC(int operand) {
        pcb.context.set(EContext.AC, operand);
    }
    private Integer loadMemory(int operand) {
        Integer value = memory.get(operand);
        if(value == null) throw new CannotLoadUninitializedMemory();
        return value;
    }

    public boolean isEnd() {
        return pcb.getPC() >= codeList.size();
    }

    public void load(Scanner scanner) {
        if(!scanner.next().equals(".program")) throw new NotProgramException();
        while (true) {
            String line = scanner.nextLine();
            if(line.equals(".end")) break;
            if(line.equals(".data")) loadDataSegment(scanner);
            if(line.equals(".code")) loadCodeSegment(scanner);
        }
    }

    private void loadCodeSegment(Scanner scanner) {
        while (true) {
            String line = scanner.nextLine();
            if (line.equals(".codeEnd")) break;
            codeList.add(line);
        }
    }

    private void loadDataSegment(Scanner scanner) {
        while (true) {
            String token = scanner.next();
            if(token.equals(".dataEnd")) return;
            int size = Integer.parseInt(scanner.next());
            EContext target = null;
            if(token.equals("codeSize")) target = EContext.CS;
            if(token.equals("dataSize")) target = EContext.DS;
            if(token.equals("stackSize")) target = EContext.SS;
            if(token.equals("heapSize")) target = EContext.HS;
            pcb.context.set(target, size);
        }
    }

    public int getCodeSize() {
        return pcb.context.get(EContext.CS);
    }
    public int getDataSize() {
        return pcb.context.get(EContext.DS);
    }
    public int getStackSize() {
        return pcb.context.get(EContext.SS);
    }
    public int getHeapSize() {
        return pcb.context.get(EContext.HS);
    }
    public Vector<String> getCodeList() {
        return codeList;
    }

    public class PCB {
        private Context context = new Context();

        private int pid;
        // Account
        private int oid;
        // Status
        private EStatus eStatus;
        // IO Status Information
        private List<?> ioDevices;

        public Context getContext() {
            return context;
        }
        public int getPC() {
            return context.get(EContext.PC);
        }
        public int getAC() { return context.get(EContext.AC);}
        public void setAC(int value) { context.set(EContext.AC, value);}
        public void setPC(int address) {
            context.set(EContext.PC, address);
        }
    }
    public enum EStatus {
        RUNNING, READY, WAITING, SUSPENDED
    }
}
