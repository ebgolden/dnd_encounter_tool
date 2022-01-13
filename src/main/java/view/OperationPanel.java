package view;

import javax.swing.*;
import java.awt.*;

public class OperationPanel extends JPanel {
    private final JButton IMPORT_MORE_CHARACTERS_BUTTON, ADD_MANUALLY_BUTTON, ADD_MUSIC_BUTTON, NEXT_TURN_BUTTON;

    public OperationPanel() {
        super(new FlowLayout());
        IMPORT_MORE_CHARACTERS_BUTTON = new JButton("Import more");
        super.add(IMPORT_MORE_CHARACTERS_BUTTON);
        ADD_MANUALLY_BUTTON = new JButton("Add manually");
        super.add(ADD_MANUALLY_BUTTON);
        ADD_MUSIC_BUTTON = new JButton("Add music");
        super.add(ADD_MUSIC_BUTTON);
        NEXT_TURN_BUTTON = new JButton("Next turn");
        super.add(NEXT_TURN_BUTTON);
    }

    public JButton getImportMoreCharactersButton() {
        return IMPORT_MORE_CHARACTERS_BUTTON;
    }

    public JButton getAddManuallyButton() {
        return ADD_MANUALLY_BUTTON;
    }

    public JButton getAddMusicButton() {
        return ADD_MUSIC_BUTTON;
    }

    public JButton getNextTurnButton() {
        return NEXT_TURN_BUTTON;
    }
}