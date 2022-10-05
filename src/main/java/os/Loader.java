package main.java.os;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Loader {

    public Process load(String exeName) {
        try (Scanner scanner = new Scanner(new File("data/" + exeName))) {
            Process process = new Process();
            process.load(scanner);
            return process;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
