package view;

import controller.FileController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class WindowLauncher {
    public static void main(String[] args) {
        JFrame windowFrame = new JFrame();
        windowFrame.setLayout(new BorderLayout());
        windowFrame.setVisible(true);
        FileController fileController = new FileController(windowFrame);
        JPanel interactivePanel = new JPanel(new VerticalFlowLayout());
        interactivePanel.add(fileController.getOperationPanel());
        interactivePanel.add(fileController.getInitiativePanel());
        interactivePanel.add(fileController.getSoundPanel());
        PDFPanel pdfPanel = fileController.getPDFPanel();
        windowFrame.add(interactivePanel, BorderLayout.WEST);
        windowFrame.add(pdfPanel, BorderLayout.EAST);
        windowFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                windowFrame.getContentPane().removeAll();
                windowFrame.getContentPane().add(interactivePanel, BorderLayout.WEST);
                windowFrame.getContentPane().add(pdfPanel, BorderLayout.CENTER);
            }
        });
        windowFrame.pack();
    }
}