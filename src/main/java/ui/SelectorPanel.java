package main.java.ui;

import javax.swing.*;
import java.awt.*;

public class SelectorPanel extends JPanel {
    private final ProgramSelectorPanel programSelectorPanel;
    private final ProcessSelectorPanel processSelectorPanel;

    public SelectorPanel() {
        super();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(1080/3, 720));

        this.setBackground(Color.BLACK);
        this.programSelectorPanel = new ProgramSelectorPanel();
        this.add(programSelectorPanel);
        this.processSelectorPanel = new ProcessSelectorPanel();
        this.add(processSelectorPanel);

    }
}
