package view;

import javax.swing.*;
import java.awt.*;

public class InitiativePanel extends JPanel {
    private final JPanel TRACKER_PANEL;

    public InitiativePanel() {
        super(new VerticalFlowLayout());
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
}