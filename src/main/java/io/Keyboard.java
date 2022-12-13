package main.java.io;

import main.java.exception.InvalidInterruptCodeException;
import main.java.exception.InvalidInterruptForMonitorException;
import main.java.os.Process;
import main.java.os.interrupt.InterruptQueue;
import main.java.power.Power;
import main.java.utils.SScanner;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Keyboard extends MyIO{
    private final Map<Process, InputWaiter> inputWaiterMap = new HashMap<>();
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
//        SScanner scanner = SScanner.getInstance();
//        String input = "";
//        while(input.isBlank()) input = scanner.nextLine("Process_" + process.getSerialNumber() + " >> " + "Keyboard >> ");
//        int buffer = Integer.parseInt(input);
//        int address = process.popFromStackSegment();
//        process.storeToMemory(address, buffer);
//        interruptQueue.addReadIntComplete(process);
        InputWaiter inputWaiter = new InputWaiter(process);
        inputWaiter.start();
        inputWaiterMap.put(process, inputWaiter);
    }

    public void addInput(Process process, String message) {
        InputWaiter inputWaiter = inputWaiterMap.get(process);
        try {
            inputWaiter.blockingQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopAllThreadsOf(Process process) {
        InputWaiter inputWaiter = inputWaiterMap.get(process);
        if(inputWaiter!=null) {
            inputWaiter.interrupt();
            inputWaiterMap.remove(process);
        }
    }

    public void initialize() {
    }

    public void finish() {
        System.out.println("Keyboard Finished");
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

    private class InputWaiter extends Thread {
        private BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
        private final Process ownerProcess;

        public InputWaiter(Process ownerProcess) {
            this.ownerProcess = ownerProcess;
        }

        @Override
        public void run() {
            try {
                String input = blockingQueue.take();
                int buffer = Integer.parseInt(input);
                int address = ownerProcess.popFromStackSegment();
                ownerProcess.storeToMemory(address, buffer);
                interruptQueue.addReadIntComplete(ownerProcess);
            } catch (InterruptedException e) {
                // interrupted when process end (by GUI)
            }
        }
    }
}
