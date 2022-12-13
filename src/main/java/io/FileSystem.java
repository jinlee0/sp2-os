package main.java.io;

import main.java.exception.InvalidFileOpenModeException;
import main.java.exception.InvalidInterruptCodeException;
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
        int fileId = process.popFromStackSegment();
        Process.FileOpenMode fileOpenMode = process.closeFile(fileId);
        if(fileOpenMode == Process.FileOpenMode.WRITE) fileEditableMap.put(fileId, true);
        interruptQueue.addCloseFileComplete(process);
    }

    private void handleOpen(Process process) {
        int mode = process.popFromStackSegment();
        int fileId = process.popFromStackSegment();
        int pointerAddressForFile = process.popFromStackSegment();
        File file = new File("files/" + fileId + ".txt");
        Process.FileOpenMode fileOpenMode = Process.FileOpenMode.of(mode);
        switch (fileOpenMode) {
            case READ:
                handleOpenToRead(process, fileId, file);
                break;
            case WRITE:
                handleOpenToWrite(process, fileId, file);
                break;
            default:
                throw new InvalidFileOpenModeException();
        }
        process.openFile(file, pointerAddressForFile, fileOpenMode);
        interruptQueue.addOpenFileComplete(process);
    }

    private void handleOpenToWrite(Process process, int fileId, File file) {
        try {
            if(!file.createNewFile()){
                Boolean isEditable = fileEditableMap.get(fileId);
                if(!isEditable) {
                    Logger.add("File " + fileId + ".txt is already opened for writing. This process will be terminated");
                    interruptQueue.addProcessEnd(process);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileEditableMap.put(fileId, false);
    }

    private void handleOpenToRead(Process process, int fileId, File file) {
        if(! file.exists()) {
            Logger.add("File " + fileId + ".txt is not found. This process will be terminated");
            interruptQueue.addProcessEnd(process);
            return;
        }
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
