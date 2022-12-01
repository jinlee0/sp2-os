package main.java.io;

import main.java.exception.InvalidInterruptCode;
import main.java.exception.InvalidInterruptForMonitorException;
import main.java.os.Process;
import main.java.power.Power;
import main.java.utils.SPrinter;

public class Monitor extends MyIO{
    private static final Monitor instance = new Monitor();

    private Monitor(){}
    public static Monitor getInstance() {
        return instance;
    }

    @Override
    public void run() {
        while (Power.isOn()) {
            try {
                handle();
                Thread.sleep(IO_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        SPrinter.getInstance().println("Process_" + process.getSerialNumber() + " >> Screen >> " + process.popFromStackSegment() + System.lineSeparator());
        interruptQueue.addWriteComplete(process);
    }

    public void add(Process process) {
        try {
            processBlockingQueue.put(process);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private enum MonitorCode {
        WRITE(Process.IOCode.WRITE),
        ;
        
        private Process.IOCode ioCode;

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
            throw new InvalidInterruptCode();
        }
    }
}
