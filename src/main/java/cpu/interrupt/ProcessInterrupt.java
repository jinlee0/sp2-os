package main.java.cpu.interrupt;

import main.java.cpu.interrupt.EInterrupt.EProcessInterrupt;
import main.java.os.Process;

public class ProcessInterrupt implements Interrupt {
    private final EProcessInterrupt eProcessInterrupt;
    private final Process process;

    public ProcessInterrupt(EProcessInterrupt eProcessInterrupt, Process process) {
        this.eProcessInterrupt = eProcessInterrupt;
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    @Override
    public EProcessInterrupt getEInterrupt() {
        return eProcessInterrupt;
    }
}
