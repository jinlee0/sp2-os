package main.java.os;


import main.java.exception.CannotLoadUninitializedMemory;
import main.java.exception.NotProgramException;
import main.java.os.interrupt.InterruptQueue;

import java.util.*;

public class Process {
    private final PCB pcb = new PCB();
    private final int serialNumber;
    private static int SERIAL_NUMBER = 0;
    private final List<String> codeList = new ArrayList<>();
    private final Map<Integer, Integer> memory = new HashMap<>();
    private final InterruptQueue interruptQueue = InterruptQueue.getInstance();
    private Timer timer;

    private final static long SLEEP_MILLIS = 50L;
    private final static long TIME_OUT_MILLIS = 300L;

    public Process() {
        serialNumber = SERIAL_NUMBER++;
        pcb.pid = serialNumber;
    }

    public void run() {
        System.out.println("Process " + serialNumber);
        if (pcb.getStatus() != EStatus.RUNNING) {
            pcb.setStatus(EStatus.RUNNING);
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
        pcb.setStatus(EStatus.READY);
        timer.cancel();
    }

    private void executeOneLine() {
        int PC = pcb.getPC();
        String line = codeList.get(PC);
        pcb.getContext().set(EContext.PC, PC + 1);

        System.out.println("\tPC: "+ PC + ", " + line.split("//")[0]);
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
    }

    private void exeHALT() {
        interruptQueue.addProcessEnd(this);
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
    public List<String> getCodeList() {
        return codeList;
    }

    public class PCB {
        private Context context = new Context();

        private int pid;
        // Account
        private int oid;
        // Status
        private EStatus eStatus = EStatus.NONE;
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
        public EStatus getStatus() {
            return eStatus;
        }
        public void setStatus(EStatus eStatus) {
            this.eStatus = eStatus;
        }
    }

    private enum EStatus {
        RUNNING, READY, WAITING, SUSPENDED, NONE
    }

    private class Context {
        private final Map<EContext, Integer> contextMap = new HashMap<>();

        public Context() {
            for (EContext eRegister : EContext.values()) {
                contextMap.put(eRegister, 0);
            }
        }

        public int get(EContext eRegister) {
            return this.contextMap.get(eRegister);
        }

        public void set(EContext key, int value) {
            this.contextMap.put(key, value);
        }

    }
    private enum EContext {
        PC,
        //    IR, MBR, MAR,
        AC,
        //    PROCESS_ID,
        CS, DS, SS, HS,
        ;
    }
    private enum EOpCode {
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
        ;
    }
}
