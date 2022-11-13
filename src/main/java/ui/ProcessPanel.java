package main.java.ui;

import main.java.os.Process;

import javax.swing.*;
import java.awt.*;

public class ProcessPanel extends JPanel {
    private Process process;

    public ProcessPanel() {
        super();
        this.setPreferredSize(new Dimension(1080/3, 720));
    }
}

