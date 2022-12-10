package main.java.io;

import main.java.io.MyIO;
import main.java.os.Process;
import main.java.os.interrupt.InterruptQueue;

import java.util.ArrayList;
import java.util.List;

public class FileSystem extends MyIO {
    private final List<List<Integer>> files = new ArrayList<>();
    private final List<FileControlBlock> fileControlBlocks = new ArrayList<>();

    public FileSystem(InterruptQueue interruptQueue) {
        super(interruptQueue);
    }

    public void initialize(){

    }

    public void finish() {

    }

    public void run() {
        while (true) {

        }
    }

    public enum Mode {
        READ,
        WRITE,
        CLOSED,
    }

    private class FileControlBlock {
        private Mode mode;
        private Process process;
        private int currentPosition;
    }
}
