package view;

import controller.InitiativeController;
import controller.PDFController;

import javax.swing.*;
import java.awt.*;

public class InitiativePanel extends JPanel {
    private final InitiativeController INITIATIVE_CONTROLLER;
    private final JButton IMPORT_MORE_CHARACTERS_BUTTON, ADD_MANUALLY_BUTTON, NEXT_TURN_BUTTON;
    private final JPanel TRACKER_PANEL;

    public InitiativePanel(PDFController pdfController) {
        super(new VerticalFlowLayout());
        IMPORT_MORE_CHARACTERS_BUTTON = new JButton("Import more");
        ADD_MANUALLY_BUTTON = new JButton("Add manually");
        INITIATIVE_CONTROLLER = new InitiativeController(pdfController, ADD_MANUALLY_BUTTON);
        IMPORT_MORE_CHARACTERS_BUTTON.addActionListener(e -> INITIATIVE_CONTROLLER.importMoreCharacters());
        ADD_MANUALLY_BUTTON.addActionListener(e -> {
            if (!INITIATIVE_CONTROLLER.isAddingManually()) {
                add(INITIATIVE_CONTROLLER.addCharacterManually(), 0);
                ((JFrame) getParent()).pack();
            }
        });
        NEXT_TURN_BUTTON = new JButton("Next turn");
        NEXT_TURN_BUTTON.addActionListener(e -> INITIATIVE_CONTROLLER.nextTurn());
        JPanel headerPanel = new JPanel();
        super.add(headerPanel);
        headerPanel.add(new JLabel("Character Name"));
        headerPanel.add(Box.createRigidArea(new Dimension(5,0)));
        headerPanel.add(new JLabel("Initiative"));
        headerPanel.add(Box.createRigidArea(new Dimension(5,0)));
        headerPanel.add(new JLabel("Initiative Bonus"));
        headerPanel.add(Box.createRigidArea(new Dimension(5,0)));
        headerPanel.add(new JLabel("Armor Class"));
        headerPanel.add(Box.createRigidArea(new Dimension(5,0)));
        headerPanel.add(new JLabel("Hit Points"));
        TRACKER_PANEL = new JPanel(new VerticalFlowLayout());
        JScrollPane initiativeScrollPane = new JScrollPane(TRACKER_PANEL);
        initiativeScrollPane.setPreferredSize(new Dimension(650, 215));
        super.add(initiativeScrollPane);
    }

    @Override
    public Component add(Component comp) {
        return TRACKER_PANEL.add(comp);
    }

    @Override
    public void removeAll() {
        TRACKER_PANEL.removeAll();
    }

    @Override
    public void remove(Component comp) {
        TRACKER_PANEL.remove(comp);
    }

    @Override
    public void remove(int index) {
        TRACKER_PANEL.remove(index);
    }

    @Override
    public void remove(MenuComponent popup) {
        TRACKER_PANEL.remove(popup);
    }

    public JButton getImportMoreCharactersButton() {
        return IMPORT_MORE_CHARACTERS_BUTTON;
    }

    public JButton getAddManuallyButton() {
        return ADD_MANUALLY_BUTTON;
    }

    public JButton getNextTurnButton() {
        return NEXT_TURN_BUTTON;
    }
}