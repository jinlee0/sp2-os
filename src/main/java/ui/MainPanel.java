package main.java.ui;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    private final SystemPanel systemPanel;
    private final SelectorPanel selectorPanel;
    private final ProcessPanel processPanel;

    public MainPanel() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.systemPanel = new SystemPanel();
        this.selectorPanel = new SelectorPanel();
        this.processPanel = new ProcessPanel();

        this.add(systemPanel);
        this.add(selectorPanel);
        this.add(processPanel);
    }
}
