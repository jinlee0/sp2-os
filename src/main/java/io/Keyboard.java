package main.java.io;

import main.java.exception.InvalidInterruptCode;
import main.java.exception.InvalidInterruptForMonitorException;
import main.java.os.Process;
import main.java.power.Power;
import main.java.utils.SScanner;

public class Keyboard extends MyIO{
    private static final Keyboard instance = new Keyboard();

    private Keyboard(){}
    public static Keyboard getInstance() {
        return instance;
    }

    @Override
    public void run() {
        while (Power.isOn()) {
            handle();
        }
    }

    private void handle() {
        try {
            Process process = processBlockingQueue.take();
            int code = process.popFromStackSegment();
            System.out.println("code: " + code);
            switch(KeyboardCode.of(code)) {
                case READ:
                    handleRead(process);
                    break;
                default:
                    throw new InvalidInterruptForMonitorException();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleRead(Process process) {
        SScanner scanner = SScanner.getInstance();
        int buffer = Integer.parseInt(scanner.nextLine("Process_" + process.getSerialNumber() + " >> " + "Keyboard >> "));
        int address = process.popFromStackSegment();
        System.out.println("address: " + address);
        process.storeMemory(address, buffer);
        interruptQueue.addReadComplete(process);

    }

    public void add(Process process) {
        try {
            processBlockingQueue.put(process);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private enum KeyboardCode {
        READ(Process.IOCode.READ),
        ;

        private Process.IOCode ioCode;

        KeyboardCode(Process.IOCode ioCode) {
            this.ioCode = ioCode;
        }

        public static KeyboardCode of(int code) {
            return of(Process.IOCode.of(code));
        }

        private static KeyboardCode of(Process.IOCode ioCode) {
            for (KeyboardCode keyboardCode : values()) {
                if(keyboardCode.ioCode == ioCode) return keyboardCode;
            }
            throw new InvalidInterruptCode();
        }
    }
}
