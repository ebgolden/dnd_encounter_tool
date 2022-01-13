package view;

import controller.ResourceHandler;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class WindowLauncher {
    public static void main(String[] args) {
        JFrame windowFrame = new JFrame();
        windowFrame.setLayout(new BorderLayout());
        windowFrame.setVisible(true);
        ResourceHandler resourceHandler = new ResourceHandler(windowFrame);
        JPanel interactivePanel = new JPanel(new VerticalFlowLayout());
        interactivePanel.add(resourceHandler.getOperationPanel());
        interactivePanel.add(resourceHandler.getInitiativePanel());
        interactivePanel.add(resourceHandler.getSoundPanel());
        PDFPanel pdfPanel = resourceHandler.getPDFPanel();
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