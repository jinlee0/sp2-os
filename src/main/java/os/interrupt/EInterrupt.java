package main.java.os.interrupt;

public interface EInterrupt {
    enum ENormalInterrupt implements EInterrupt{
        NONE,
    }

    enum EProcessInterrupt implements EInterrupt {
        TIME_OUT, READ_START, READ_COMPLETE, PROCESS_END, PROCESS_START, WRITE_START, WRITE_COMPLETE,
    }
}
