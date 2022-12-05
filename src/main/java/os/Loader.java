package main.java.os;

import main.java.os.interrupt.InterruptQueue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Loader {
    private final InterruptQueue interruptQueue;

    public Loader(InterruptQueue interruptQueue) {
        this.interruptQueue = interruptQueue;
    }

    public Process load(String exeName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("programs/" + exeName));
        Process process = new Process(interruptQueue);
        process.load(scanner);
        return process;
    }
}
