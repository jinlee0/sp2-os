package main.java.io;

import main.java.exception.InvalidInterruptCodeException;
import main.java.exception.InvalidInterruptForMonitorException;
import main.java.os.Process;
import main.java.os.interrupt.InterruptQueue;
import main.java.power.Power;
import main.java.utils.SScanner;

public class Keyboard extends MyIO{

    public Keyboard(InterruptQueue interruptQueue) {
        super(interruptQueue);
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
        String input = "";
        while(input.isBlank()) input = scanner.nextLine("Process_" + process.getSerialNumber() + " >> " + "Keyboard >> ");
        int buffer = Integer.parseInt(input);
        int address = process.popFromStackSegment();
        process.storeToMemory(address, buffer);
        interruptQueue.addReadIntComplete(process);
    }

    public void initialize() {
    }

    public void finish() {
    }

    private enum KeyboardCode {
        READ(Process.IOCode.READ_INT),
        ;

        private final Process.IOCode ioCode;

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
            throw new InvalidInterruptCodeException();
        }
    }
}
