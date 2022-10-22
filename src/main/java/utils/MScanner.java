package main.java.utils;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class MScanner {
    private final Scanner scanner = new Scanner(System.in);
    private static final MScanner instance = new MScanner();
    private final Semaphore semaphore = new Semaphore(1, true);

    public static MScanner getInstance() {
        return instance;
    }
    public String nextLine() {
        try {
            semaphore.acquire();
            return scanner.nextLine();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
        return null;
    }

    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    public void release() {
        semaphore.release();
    }
//    public String nextLine(String s) {
//        MPrinter.getInstance().print(s);
//        return nextLine();
//    }
}
