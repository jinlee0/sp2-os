package main.java.cpu;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private final Map<EContext, Integer> contextMap = new HashMap<>();

    public Context() {
        for (EContext eRegister : EContext.values()) {
            contextMap.put(eRegister, 0);
        }
    }

    public void loadFrom(Context other) {
        for (EContext eRegister : EContext.values()) {
            contextMap.put(eRegister, other.get(eRegister));
        }
    }

    public int get(EContext eRegister) {
        return this.contextMap.get(eRegister);
    }

    public void set(EContext key, int value) {
        this.contextMap.put(key, value);
    }
}
