package main.java.cpu.interrupt;

public interface EInterrupt {
    enum ENormalInterrupt implements EInterrupt{
        TIME_OUT, NONE
    }

    enum EProcessInterrupt implements EInterrupt {
        IO_START, IO_COMPLETE, PROCESS_END, PROCESS_START,
    }
}
