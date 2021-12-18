package view;

import controller.ResourceHandler;
import org.icepdf.ri.common.SwingController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.net.MalformedURLException;

public class GUI {
    private static JFrame windowFrame;
    private static JPanel interactivePanel;
    private static SwingController controller;
    private static ResourceHandler resourceHandler;

    public static void main(String[] args) {
        PDFPanel pdfPanel = new PDFPanel();
        controller = pdfPanel.getController();
        windowFrame = new JFrame();
        windowFrame.setLayout(new BorderLayout());
        windowFrame.setVisible(true);
        resourceHandler = new ResourceHandler(windowFrame);
        interactivePanel = new JPanel(new VerticalFlowLayout());
        interactivePanel.add(resourceHandler.getOperationPanel());
        interactivePanel.add(resourceHandler.getInitiativePanel());
        interactivePanel.add(resourceHandler.getSoundPanel());
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
    }

    public static void sortCharacterPanels() {
        resourceHandler.sortCharacterPanels();
    }

    public static void removeCharacterFromInitiative(CharacterPanel characterPanel) {
        resourceHandler.removeCharacterFromInitiative(characterPanel);
    }

    public static void finishAddingManually() {
        resourceHandler.finishAddingManually();
    }

    public static void viewCharacterSheet(File file) throws MalformedURLException {
        controller.openDocument(file.toURI().toURL());
    }

    public static void stopAllMusic() {
        resourceHandler.stopAllMusic();
    }
}