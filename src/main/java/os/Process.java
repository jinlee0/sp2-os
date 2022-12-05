package main.java.os;


import main.java.exception.CannotLoadUninitializedMemory;
import main.java.exception.InvalidExeFormatException;
import main.java.exception.InvalidInterruptCode;
import main.java.os.interrupt.InterruptQueue;
import main.java.utils.Logger;

import java.util.*;

public class Process {
    private final PCB pcb = new PCB();
    private final List<Instruction> codeSegment = new ArrayList<>();
    private final Stack<Integer> stackSegment = new Stack<>();
    private final Map<Integer, Integer> heapSegment = new HashMap<>();
    private final Map<Integer, Integer> dataSegment = new HashMap<>();
    private Timer timer;

    private final InterruptQueue interruptQueue;

    private final int serialNumber;

    private static int SERIAL_NUMBER = 0;
    private final static long SLEEP_MILLIS = 50L;
    private final static long TIME_OUT_MILLIS = 300L;

    public Process(InterruptQueue interruptQueue) {
        this.interruptQueue = interruptQueue;
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
        timer.cancel();
    }

    private void executeOneLine() {
        int PC = pcb.getPC();
        if(PC >= codeSegment.size()) return;
        Instruction instruction = codeSegment.get(PC);
        pcb.getContext().setPC(PC + 1);
        Logger.add("\tPC: "+ PC + ", " + instruction);

        OpCode opCode = instruction.getOpCode();
        int operand = instruction.getOperand();
        opCode.execute(this, operand);
    }

    public Integer loadMemory(int operand) {
        Integer value = dataSegment.get(operand);
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
        for (Integer integer : dataSegment.keySet()) {
            sb.append("\t\t").append(integer).append(": ").append(dataSegment.get(integer)).append(System.lineSeparator());
        }
        return sb.toString();
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void storeMemory(int address, int value) {
        dataSegment.put(address, value);
    }

    public int popFromStackSegment() {
        return stackSegment.pop();
    }

    public static class Instruction {
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
            return context.getPC();
        }
        public int getAC() { return context.getAC(); }
        public void setAC(int value) { context.setAC(value); }
        public void setPC(int address) {
            context.setPC(address);
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

    public static class Context {
        private final Map<ERegister, Object> contextMap = new HashMap<>();

        public Context() {
            for (ERegister eRegister : ERegister.values()) {
                contextMap.put(eRegister, 0);
            }
        }
        private Object get(ERegister eRegister) {
            return this.contextMap.get(eRegister);
        }
        private void set(ERegister key, Object value) {
            this.contextMap.put(key, value);
        }

        public int getPC() {
            return (int) this.get(ERegister.PC);
        }
        public int getAC() {
            return (int) this.get(ERegister.AC);
        }
        public void setPC(int value) {
            this.set(ERegister.PC, value);
        }
        public void setAC(int value) {
            this.set(ERegister.AC, value);
        }
    }
    public enum ERegister {
        PC, AC,
//        IR, MBR, MAR,
        CS, DS, SS, HS,
    }

    private enum OpCode {
        HALT((process, operand) -> {
            process.interruptQueue.addProcessEnd(process);
            Logger.add(process.toString());
        }),
        LDC((process, operand) -> process.pcb.setAC(operand)),
        LDA((process, operand) -> process.pcb.setAC(process.loadMemory(operand))),
        STA((process, operand) -> process.dataSegment.put(operand, process.pcb.getAC())),
        ADDA((process, operand) -> process.pcb.setAC(process.pcb.getAC() + process.loadMemory(operand))),
        SUBA((process, operand) -> process.pcb.setAC(process.pcb.getAC() - process.loadMemory(operand))),
        MULA((process, operand) -> process.pcb.setAC(process.pcb.getAC() * process.loadMemory(operand))),
        DIVA((process, operand) -> process.pcb.setAC(process.pcb.getAC() / process.loadMemory(operand))),
        SHRA((process, operand) -> process.pcb.setAC(process.pcb.getAC() >> process.loadMemory(operand))),
        ADDC((process, operand) -> process.pcb.setAC(process.pcb.getAC() + operand)),
        SUBC((process, operand) -> process.pcb.setAC(process.pcb.getAC() - operand)),
        MULC((process, operand) -> process.pcb.setAC(process.pcb.getAC() * operand)),
        DIVC((process, operand) -> process.pcb.setAC(process.pcb.getAC() / operand)),
        SHRC((process, operand) -> process.pcb.setAC(process.pcb.getAC() >> operand)),
        BR((process, operand) -> process.pcb.setPC(operand)),
        BZ((process, operand) -> {
            if (process.pcb.getAC() == 0) process.pcb.setPC(operand);
        }),
        BN((process, operand) -> {
            if (process.pcb.getAC() < 0) process.pcb.setPC(operand);
        }),
        BP((process, operand) -> {
            if (process.pcb.getAC() > 0) process.pcb.setPC(operand);
        }),
        BZP((process, operand) -> {
            if (process.pcb.getAC() >= 0) process.pcb.setPC(operand);
        }),
        BZN((process, operand) -> {
            if (process.pcb.getAC() <= 0) process.pcb.setPC(operand);
        }),
        INT(OpCode::execute),
        PUSH((process, operand) -> {
            process.stackSegment.push(operand);
        }),

        ;

        private final Executable executable;

        OpCode(Executable executable) {
            this.executable = executable;
        }

        private static void execute(Process process, Integer operand) {
            IOCode.of(operand).execute(process, operand);
        }

        public void execute(Process process, int operand) {
            executable.execute(process, operand);
        }

        private interface Executable {
            void execute(Process process, Integer operand);
        }
    }

    public enum IOCode {
        WRITE_INT(0, process -> process.interruptQueue.addWriteStart(process)),
        READ_INT(1, process -> process.interruptQueue.addReadStart(process));

        private final int code;
        private final Executable executable;
        IOCode(int code, Executable executable) {
            this.code = code;
            this.executable = executable;
        }

        public static IOCode of(int code) {
            for (IOCode value : values()) {
                if(value.code == code) return value;
            }
            throw new InvalidInterruptCode();
        }

        public void execute(Process process, int operand) {
            process.timer.cancel();
            executable.execute(process);
            process.stackSegment.push(operand);
        }

        private interface Executable {
            void execute(Process process);
        }
    }
}
