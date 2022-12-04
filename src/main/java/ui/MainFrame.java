package main.java.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final MainPanel mainPanel;

    public MainFrame() {
        super();
        this.setTitle("MyOS");
        this.setLocation(new Point(0, 0));
        this.setMinimumSize(new Dimension(1080, 300));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.mainPanel = new MainPanel();

        this.add(mainPanel);

        this.pack();
        this.setVisible(true);
    }


}
