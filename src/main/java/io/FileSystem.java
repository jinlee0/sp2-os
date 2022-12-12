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
import java.util.Scanner;

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
//                    handleWrite(process);
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleOpen(Process process) {
        int fileName = process.popFromStackSegment();
        int heapAddress = process.popFromStackSegment();
        Boolean isEditable = fileEditableMap.get(fileName);
        if(isEditable == null) throw new NotSuchFileIdException();
        if(!isEditable) {
            Logger.add("File " + fileName + ".txt is already opened. This process will be terminated");
            interruptQueue.addProcessEnd(process);
            return;
        }
        fileEditableMap.put(fileName, false);

        File file = new File("files/" + fileName + ".txt");
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            int attributeAddress = 0;
            while(br.ready())
                process.storeToHeapSegment(heapAddress, attributeAddress++, br.read());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
