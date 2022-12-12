package main.java.io;

import main.java.exception.InvalidInterruptCodeException;
import main.java.exception.NotSuchFileIdException;
import main.java.os.Process;
import main.java.os.interrupt.InterruptQueue;
import main.java.power.Power;
import main.java.utils.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileSystem extends MyIO {
    private final Map<Integer, Boolean> fileEditableMap = new HashMap<>();

    public FileSystem(InterruptQueue interruptQueue) {
        super(interruptQueue);
        for (File file : new File("files/").listFiles()) {
            fileEditableMap.put(Integer.parseInt(file.getName().split("\\.")[0]), true);
        }
    }

    public void run() {
        while (Power.isOn()) {
            handle();
        }
    }

    private void handle() {
        try {
            Process process = processBlockingQueue.take();
            int code = process.popFromStackSegment();
            switch(FileCode.of(code)) {
                case OPEN:
                    handleOpen(process);
                    break;
                case CLOSE:
                    handleClose(process);
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClose(Process process) {


    }

    private void handleOpen(Process process) {
        int pointerAddressForFile = process.popFromStackSegment();
        int fileId = process.popFromStackSegment();
        Boolean isEditable = fileEditableMap.get(fileId);
        if(isEditable == null) throw new NotSuchFileIdException();
        if(!isEditable) {
            Logger.add("File " + fileId + ".txt is already opened. This process will be terminated");
            interruptQueue.addProcessEnd(process);
            return;
        }
        fileEditableMap.put(fileId, false);
        process.allocateHeap(new File("files/" + fileId + ".txt"), pointerAddressForFile);
        interruptQueue.addOpenFileComplete(process);
    }

    public void initialize(){

    }

    public void finish() {

    }

    public enum FileCode {
        OPEN(Process.IOCode.OPEN_FILE),
        CLOSE(Process.IOCode.CLOSE_FILE),

        ;
        private final Process.IOCode ioCode;

        FileCode(Process.IOCode ioCode) {
            this.ioCode = ioCode;
        }

        public static FileCode of(int code) {
            return of(Process.IOCode.of(code));
        }

        private static FileCode of(Process.IOCode ioCode) {
            for (FileCode fileCode : values()) {
                if (fileCode.ioCode == ioCode) return fileCode;
            }
            throw new InvalidInterruptCodeException();
        }
    }
}
