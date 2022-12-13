package main.java.io;

import main.java.exception.InvalidInterruptCodeException;
import main.java.exception.InvalidInterruptForMonitorException;
import main.java.os.Process;
import main.java.os.interrupt.InterruptQueue;
import main.java.power.Power;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiConsumer;

public class Monitor extends MyIO{
    private ConcurrentLinkedDeque<BiConsumer<Process, String>> writeListeners = new ConcurrentLinkedDeque<>();

    public Monitor(InterruptQueue interruptQueue) {
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
            switch(MonitorCode.of(process.popFromStackSegment())) {
                case WRITE:
                    handleWrite(process);
                    break;
                default:
                    throw new InvalidInterruptForMonitorException();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleWrite(Process process) {
        int address = process.popFromStackSegment();
        int value = process.retrieveFromMemory(address);
        writeListeners.forEach(listener -> listener.accept(process, value +""));
//        SPrinter.getInstance().println("Process_" + process.getSerialNumber() + " >> Screen >> " + process.retrieveFromMemory(address) + System.lineSeparator());
        interruptQueue.addWriteIntComplete(process);
    }

    public void addWriteListener(BiConsumer<Process, String> listener) {
        writeListeners.add(listener);
    }

    public void removeWriteListner(BiConsumer<Process, String> writeListenerForMonitor) {
        writeListeners.remove(writeListenerForMonitor);
    }

    public void finish() {
        System.out.println("Monitor Finished");
    }

    public void initialize() {
    }

    private enum MonitorCode {
        WRITE(Process.IOCode.WRITE_INT),
        ;
        
        private final Process.IOCode ioCode;

        MonitorCode(Process.IOCode ioCode) {
            this.ioCode = ioCode;
        }

        public static MonitorCode of(int code) {
            return of(Process.IOCode.of(code));
        }

        private static MonitorCode of(Process.IOCode ioCode) {
            for (MonitorCode monitorCode : values()) {
                if(monitorCode.ioCode == ioCode) return monitorCode;
            }
            throw new InvalidInterruptCodeException();
        }
    }
}
