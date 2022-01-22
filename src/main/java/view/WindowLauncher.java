package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class WindowLauncher {
    public static void main(String[] args) {
        JFrame windowFrame = new JFrame();
        windowFrame.setLayout(new BorderLayout());
        windowFrame.setVisible(true);
        JPanel interactivePanel = new JPanel(new VerticalFlowLayout());
        OperationPanel operationPanel = new OperationPanel();
        PDFPanel pdfPanel = new PDFPanel();
        InitiativePanel initiativePanel = new InitiativePanel(pdfPanel.getPDFController());
        MusicPanel musicPanel = new MusicPanel();
        operationPanel.installButton(initiativePanel.getImportMoreCharactersButton());
        operationPanel.installButton(initiativePanel.getAddManuallyButton());
        operationPanel.installButton(musicPanel.getAddMusicButton());
        operationPanel.installButton(initiativePanel.getNextTurnButton());
        interactivePanel.add(operationPanel);
        interactivePanel.add(initiativePanel);
        interactivePanel.add(musicPanel);
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