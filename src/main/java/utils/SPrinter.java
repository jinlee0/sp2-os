package main.java.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SPrinter {
    private BufferedWriter br = new BufferedWriter(new OutputStreamWriter(System.out));
    private static final SPrinter instance = new SPrinter();

    public static SPrinter getInstance() {
        return instance;
    }
    public void println(String s) {
        print(s + System.lineSeparator());
    }
    public void print(String s) {
        SScanner scanner = SScanner.getInstance();
        try {
            scanner.acquire();
            br.write(s);
            br.flush();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            scanner.release();
        }
    }
}
