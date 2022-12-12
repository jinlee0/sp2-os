package main.java.os.interrupt;

public interface EInterrupt {
    enum ENormalInterrupt implements EInterrupt{
        NONE,
    }

    enum EProcessInterrupt implements EInterrupt {
        TIME_OUT,

        READ_INT_START, READ_INT_COMPLETE,
        WRITE_INT_START, WRITE_INT_COMPLETE,

        OPEN_FILE_START, OPEN_FILE_COMPLETE,
        CLOSE_FILE_START, CLOSE_FILE_COMPLETE,

        PROCESS_START, PROCESS_END,
    }
}
