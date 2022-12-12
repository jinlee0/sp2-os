package main.java.io;

import main.java.exception.InvalidInterruptCodeException;
import main.java.exception.InvalidInterruptForMonitorException;
import main.java.os.Process;
import main.java.os.interrupt.InterruptQueue;
import main.java.power.Power;
import main.java.utils.SPrinter;

public class Monitor extends MyIO{

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
        SPrinter.getInstance().println("Process_" + process.getSerialNumber() + " >> Screen >> " + process.loadMemory(process.popFromStackSegment()) + System.lineSeparator());
        interruptQueue.addWriteComplete(process);
    }

    public void initialize() {
    }

    public void finish() {
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
