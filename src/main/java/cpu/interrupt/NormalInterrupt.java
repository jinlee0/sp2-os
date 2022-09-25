package main.java.cpu.interrupt;

import main.java.cpu.interrupt.EInterrupt.ENormalInterrupt;

public class NormalInterrupt implements Interrupt{
    private final ENormalInterrupt eNormalInterrupt;

    public NormalInterrupt(ENormalInterrupt eNormalInterrupt) {
        this.eNormalInterrupt = eNormalInterrupt;
    }

    @Override
    public ENormalInterrupt getEInterrupt() {
        return eNormalInterrupt;
    }
}
