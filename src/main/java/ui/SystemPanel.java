package main.java.ui;

import javax.swing.*;
import java.awt.*;

public class SystemPanel extends JPanel {
    private final JTextArea textArea;
    public SystemPanel() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(1080/3, 720));

        textArea.setLineWrap(true);

        textArea.append("sddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddfsfojefijdkfjk");
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane);

        scrollPane.add(new JButton());
    }
}
