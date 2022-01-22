package view;

import controller.MusicController;
import javax.swing.*;
import java.awt.*;

public class MusicPanel extends JPanel {
    private final JButton ADD_MUSIC_BUTTON;
    private final JPanel SOUND_BOARD_PANEL;

    public MusicPanel() {
        super(new VerticalFlowLayout());
        ADD_MUSIC_BUTTON = new JButton("Add music");
        new MusicController(ADD_MUSIC_BUTTON);
        SOUND_BOARD_PANEL = new JPanel(new GridLayout(10, 2));
        JScrollPane soundScrollPane = new JScrollPane(SOUND_BOARD_PANEL);
        soundScrollPane.setPreferredSize(new Dimension(650, 320));
        super.add(soundScrollPane);
    }

    @Override
    public Component add(Component comp) {
        return SOUND_BOARD_PANEL.add(comp);
    }

    @Override
    public void removeAll() {
        SOUND_BOARD_PANEL.removeAll();
    }

    @Override
    public void remove(Component comp) {
        SOUND_BOARD_PANEL.remove(comp);
    }

    @Override
    public void remove(int index) {
        SOUND_BOARD_PANEL.remove(index);
    }

    @Override
    public void remove(MenuComponent popup) {
        SOUND_BOARD_PANEL.remove(popup);
    }

    public JButton getAddMusicButton() {
        return ADD_MUSIC_BUTTON;
    }
}