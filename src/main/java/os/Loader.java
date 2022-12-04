package main.java.os;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Loader {

    public Process load(String exeName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("programs/" + exeName));
        Process process = new Process();
        process.load(scanner);
        return process;
    }
}
