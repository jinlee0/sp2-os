package main.java.utils;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class SScanner {
    private final Scanner scanner = new Scanner(System.in);
    private static final SScanner instance = new SScanner();
    private final Semaphore semaphore = new Semaphore(1, true);

    public static SScanner getInstance() {
        return instance;
    }

    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    public void release() {
        semaphore.release();
    }

    public String nextLine(String s) {
        try {
            semaphore.acquire();
            System.out.print(s);
            return scanner.nextLine();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
        return null;
    }
}
