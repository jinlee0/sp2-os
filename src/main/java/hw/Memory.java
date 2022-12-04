package main.java.hw;

import main.java.os.Process;

import java.util.*;

public class Memory {
    private static final Memory instance = new Memory();
    private final Map<MemoryAddress, MemoryValue> memoryMap = new HashMap<>();


    private Memory() {}
    public static Memory getInstance() {
        return instance;
    }

    private class Segments {
        private final List<Process.Instruction> codeSegment = new ArrayList<>();
        private final Stack<Integer> stackSegment = new Stack<>();
    }

    private interface MemoryValue {
    }

    private class MemoryAddress {
        private int pointer;
        private int relativeAddress;
    }

    private class MemoryManager {
        private final Map<Process, SegmentPointers> pointersMap = new HashMap<>();

        public void allocateProcess(Process process) {
            pointersMap.put(process, new SegmentPointers(process));
        }

        private class SegmentPointers {
            private int processPointer;
            private int codeSegmentPointer;
            private int stackSegmentPointer;
            private int dataSegmentPointer;

            public SegmentPointers(Process process) {

            }
        }
    }
}
