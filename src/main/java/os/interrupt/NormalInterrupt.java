package main.java.os.interrupt;

import main.java.os.interrupt.EInterrupt.ENormalInterrupt;

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
