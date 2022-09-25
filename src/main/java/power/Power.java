package main.java.power;

public class Power {
    private static EPower state = EPower.ON;

    public static void on() {
        state = EPower.ON;
        System.out.println("POWER ON");
    }
    public static void off() {
        state = EPower.OFF;
        System.out.println("POWER OFF");
    }
    public static boolean isOn() {
        return state == EPower.ON;
    }
    public static boolean isOff() {
        return state == EPower.OFF;
    }

    public enum EPower {
        ON, OFF,
        ;
    }
}
