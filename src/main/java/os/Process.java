package main.java.os;


import main.java.exception.CannotLoadUninitializedMemory;
import main.java.exception.InvalidExeFormatException;
import main.java.exception.InvalidInterruptCode;
import main.java.io.Keyboard;
import main.java.io.Monitor;
import main.java.os.interrupt.InterruptQueue;
import main.java.utils.Logger;

import java.util.*;

public class Process {
    private final PCB pcb = new PCB();
    private final int serialNumber;
    private final List<Instruction> codeSegment = new ArrayList<>();
    private final Stack<?> stackSegment = new Stack<>();
    private final Map<Integer, Integer> headSegment = new HashMap<>();
    private final Map<Integer, Integer> memory = new HashMap<>();
    private final InterruptQueue interruptQueue = InterruptQueue.getInstance();
    private Timer timer;

    private static int SERIAL_NUMBER = 0;
    private final static long SLEEP_MILLIS = 50L;
    private final static long TIME_OUT_MILLIS = 300L;

    public Process() {
        serialNumber = SERIAL_NUMBER++;
    }

    public void run() {
        Logger.add("Process" + serialNumber);
        if (pcb.getStatus() != ProcessStatus.RUNNING) {
            pcb.setStatus(ProcessStatus.RUNNING);
            timer = new Timer();
            Process thisProcess = this;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    interruptQueue.addTimeOut(thisProcess);
                }
            }, TIME_OUT_MILLIS);
        }
        executeOneLine();
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ready() {
        pcb.setStatus(ProcessStatus.READY);
        timer.cancel();
    }

    public void waiting() {
        pcb.setStatus(ProcessStatus.WAITING);
    }

    private void executeOneLine() {
        int PC = pcb.getPC();
        Instruction instruction = codeSegment.get(PC);
        pcb.getContext().set(ERegister.PC, PC + 1);
        Logger.add("\tPC: "+ PC + ", " + instruction);

        OpCode opCode = instruction.getOpCode();
        int operand = instruction.getOperand();
        switch (opCode) {
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
            case INT:
                exeINT(operand);
                break;
            case HALT:
                exeHALT();
                break;
        }
    }

    private void exeINT(int operand) {
        // process 를 웨이팅 큐로
        // IO 마치면 인터럽트 생김
        try {
            timer.cancel();
            IOCode eIOCode = IOCode.of(operand);
            switch (eIOCode) {
                case WRITE:
                    interruptQueue.addWriteStart(this);
                    Monitor.getInstance().add(this, pcb.getAC());
                    break;
                case READ:
                    interruptQueue.addReadStart(this);
                    Keyboard.getInstance().add(this);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void exeHALT() {
        interruptQueue.addProcessEnd(this);
        Logger.add(this.toString());
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
        pcb.context.set(ERegister.AC, operand);
    }
    private Integer loadMemory(int operand) {
        Integer value = memory.get(operand);
        if(value == null) throw new CannotLoadUninitializedMemory();
        return value;
    }
    public void load(Scanner scanner) {
        if(!scanner.next().equals(".program")) throw new InvalidExeFormatException();
        while (true) {
            if(!scanner.hasNextLine()) throw new InvalidExeFormatException();
            String line = scanner.nextLine().trim();
            if(line.equals("") || line.startsWith("//")) continue;
            if(line.equals(".end")) break;
            if(line.equals(".data")) loadDataSegment(scanner);
            if(line.equals(".code")) loadCodeSegment(scanner);
        }
    }
    private void loadCodeSegment(Scanner scanner) {
        while (true) {
            if(!scanner.hasNextLine()) throw new InvalidExeFormatException();
            String line = scanner.nextLine();
            if(line.equals("") || line.startsWith("//")) continue;
            if (line.equals(".codeEnd")) break;
            codeSegment.add(new Instruction(line));
        }
    }
    private void loadDataSegment(Scanner scanner) {
        while (true) {
            if(!scanner.hasNext()) throw new InvalidExeFormatException();
            String token = scanner.next();
            if(token.equals("") || token.startsWith("//")) continue;
            if(token.equals(".dataEnd")) return;
            int size = Integer.parseInt(scanner.next());
            ERegister target = null;
            if(token.equals("codeSize")) target = ERegister.CS;
            if(token.equals("dataSize")) target = ERegister.DS;
            if(token.equals("stackSize")) target = ERegister.SS;
            if(token.equals("heapSize")) target = ERegister.HS;
            pcb.context.set(target, size);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Process_").append(serialNumber).append("의 종료 직전 상태").append(System.lineSeparator());
        sb.append("\t").append("PCB").append(System.lineSeparator());
        for (ERegister eRegister : ERegister.values()) {
            sb.append("\t\t").append(eRegister).append(": ").append(pcb.context.get(eRegister)).append(System.lineSeparator());
        }
        sb.append("\t").append("Memory").append(System.lineSeparator());
        for (Integer integer : memory.keySet()) {
            sb.append("\t\t").append(integer).append(": ").append(memory.get(integer)).append(System.lineSeparator());
        }
        return sb.toString();
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setAC(int value) {
        pcb.setAC(value);
    }

    private static class Instruction {
        private final OpCode opCode;
        private final int operand;

        public Instruction(String line) {
            StringTokenizer st = new StringTokenizer(line);
            opCode = OpCode.valueOf(st.nextToken().trim().toUpperCase(Locale.ROOT));
            operand = Integer.parseInt(st.nextToken());
        }
        public OpCode getOpCode() {
            return opCode;
        }
        public int getOperand() {
            return operand;
        }
        @Override
        public String toString() {
            return "Instruction{" +
                    "opCode=" + opCode +
                    ", operand=" + operand +
                    '}';
        }
    }

    public static class PCB {
        private final Context context = new Context();

        // Status
        private ProcessStatus eStatus = ProcessStatus.NONE;

        public Context getContext() {
            return context;
        }
        public int getPC() {
            return context.get(ERegister.PC);
        }
        public int getAC() { return context.get(ERegister.AC); }
        public void setAC(int value) { context.set(ERegister.AC, value); }
        public void setPC(int address) {
            context.set(ERegister.PC, address);
        }
        public ProcessStatus getStatus() {
            return eStatus;
        }
        public void setStatus(ProcessStatus eStatus) {
            this.eStatus = eStatus;
        }
    }

    private enum ProcessStatus {
        RUNNING, READY, WAITING, SUSPENDED, NONE
    }

    private static class Context {
        private final Map<ERegister, Integer> contextMap = new HashMap<>();

        public Context() {
            for (ERegister eRegister : ERegister.values()) {
                contextMap.put(eRegister, 0);
            }
        }
        public int get(ERegister eRegister) {
            return this.contextMap.get(eRegister);
        }
        public void set(ERegister key, int value) {
            this.contextMap.put(key, value);
        }
    }
    private enum ERegister {
        PC,
        //    IR, MBR, MAR,
        AC,
        //    PROCESS_ID,
        CS, DS, SS, HS,
    }

    private enum OpCode {
        HALT,
        LDC,
        LDA,
        STA,
        ADDA,
        SUBA,
        MULA,
        DIVA,
        SHRA,
        ADDC,
        SUBC,
        MULC,
        DIVC,
        SHRC,
        BR,
        BZ,
        BN,
        BP,
        BZP,
        BZN,
        INT
    }

    public enum IOCode {
        WRITE(0), READ(1);

        private final int code;

        IOCode(int code) {
            this.code = code;
        }

        static IOCode of(int code) {
            for (IOCode value : values()) {
                if(value.code == code) return value;
            }
            throw new InvalidInterruptCode();
        }
    }
}
