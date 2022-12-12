package main.java.vo;

public class HeapAddress extends MemoryAddress {
    private int attributeAddress;

    @Override
    public int get() {
        return address + attributeAddress;
    }

    public int getObjectAddress() {
        return address;
    }

    public int getAttributeAddress() {
        return attributeAddress;
    }

    public static HeapAddress of(int objectAddress, int attributeAddress) {
        HeapAddress heapAddress = new HeapAddress();
        heapAddress.address = objectAddress;
        heapAddress.attributeAddress = attributeAddress;
        return heapAddress;
    }
}
