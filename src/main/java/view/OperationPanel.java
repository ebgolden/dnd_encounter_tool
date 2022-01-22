package view;

import javax.swing.*;
import java.awt.*;

public class OperationPanel extends JPanel {
    public OperationPanel() {
        super(new FlowLayout());
    }

    public void installButton(JButton button) {
        super.add(button);
    }
}