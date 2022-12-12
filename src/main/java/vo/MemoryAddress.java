package main.java.vo;

public class MemoryAddress {
    protected int address;

    public int get(){
        return address;
    }

    public static MemoryAddress of(int address) {
        MemoryAddress memoryAddress = new MemoryAddress();
        memoryAddress.address = address;
        return memoryAddress;
    }
}
