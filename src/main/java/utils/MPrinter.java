package main.java.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MPrinter {
    private BufferedWriter br = new BufferedWriter(new OutputStreamWriter(System.out));
    private static final MPrinter instance = new MPrinter();

    public static MPrinter getInstance() {
        return instance;
    }
    public void println(String s) {
        print(s + System.lineSeparator());
    }
    public void print(String s) {
        MScanner scanner = MScanner.getInstance();
        try {
            scanner.acquire();
            br.write(s);
            br.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            scanner.release();
        }
    }
}
