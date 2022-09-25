package main.java.memory;

import main.java.os.Process;

import java.util.HashMap;
import java.util.Map;

public class Memory {
    private static final Memory instance = new Memory();

    private final Map<Integer, Process> memory = new HashMap<>();

    private Memory() {
        System.out.println("MEMORY CREATED");
    }

    public static Memory getInstance() {
        return instance;
    }

    public void add(Process process) {
        memory.put(process.hashCode(), process);
    }
}
