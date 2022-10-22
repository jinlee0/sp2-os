package main.java.os.interrupt;

public interface EInterrupt {
    enum ENormalInterrupt implements EInterrupt{
        NONE,
    }

    enum EProcessInterrupt implements EInterrupt {
        TIME_OUT, IO_START, IO_COMPLETE, PROCESS_END, PROCESS_START,
    }
}
