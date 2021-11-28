package gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

public class CharacterPanel extends JPanel {
    private final CharacterDetail CHARACTER_DETAIL;
    private final JTextField CHARACTER_NAME_FIELD, INITIATIVE_FIELD, ARMOR_CLASS_FIELD, HIT_POINTS_FIELD;

    CharacterPanel(CharacterDetail characterDetail) {
        super();
        CHARACTER_DETAIL = characterDetail;
        CHARACTER_NAME_FIELD = new JTextField(characterDetail.getCharacterName());
        CHARACTER_NAME_FIELD.addActionListener(e -> CHARACTER_DETAIL.setCharacterName(CHARACTER_NAME_FIELD.getText()));
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(-100.5);
        formatter.setMaximum(100.5);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        INITIATIVE_FIELD = new JFormattedTextField(formatter);
        INITIATIVE_FIELD.setText(String.valueOf(CHARACTER_DETAIL.getInitiative()));
        INITIATIVE_FIELD.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!INITIATIVE_FIELD.getText().equals("") && (Double.parseDouble(INITIATIVE_FIELD.getText()) != CHARACTER_DETAIL.getInitiative())) {
                    CHARACTER_DETAIL.setInitiative(Double.parseDouble(INITIATIVE_FIELD.getText()));
                    GUI.sortCharacterPanels();
                    INITIATIVE_FIELD.requestFocusInWindow();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!INITIATIVE_FIELD.getText().equals("") && (Double.parseDouble(INITIATIVE_FIELD.getText()) != CHARACTER_DETAIL.getInitiative())) {
                    CHARACTER_DETAIL.setInitiative(Double.parseDouble(INITIATIVE_FIELD.getText()));
                    GUI.sortCharacterPanels();
                    INITIATIVE_FIELD.requestFocusInWindow();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!INITIATIVE_FIELD.getText().equals("") && (Double.parseDouble(INITIATIVE_FIELD.getText()) != CHARACTER_DETAIL.getInitiative())) {
                    CHARACTER_DETAIL.setInitiative(Double.parseDouble(INITIATIVE_FIELD.getText()));
                    GUI.sortCharacterPanels();
                    INITIATIVE_FIELD.requestFocusInWindow();
                }
            }
        });
        format = NumberFormat.getInstance();
        formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(100);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        ARMOR_CLASS_FIELD = new JFormattedTextField(formatter);
        ARMOR_CLASS_FIELD.setText(String.valueOf(CHARACTER_DETAIL.getArmorClass()));
        ARMOR_CLASS_FIELD.addActionListener(e -> CHARACTER_DETAIL.setArmorClass(Integer.parseInt(ARMOR_CLASS_FIELD.getText())));
        format = NumberFormat.getInstance();
        formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(1000);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        HIT_POINTS_FIELD = new JFormattedTextField(formatter);
        HIT_POINTS_FIELD.setText(String.valueOf(CHARACTER_DETAIL.getHitPoints()));
        HIT_POINTS_FIELD.addActionListener(e -> CHARACTER_DETAIL.setHitPoints(Integer.parseInt(HIT_POINTS_FIELD.getText())));
        super.add(CHARACTER_NAME_FIELD);
        super.add(INITIATIVE_FIELD);
        super.add(ARMOR_CLASS_FIELD);
        super.add(HIT_POINTS_FIELD);
    }

    public CharacterDetail getCharacterDetail() {
        return CHARACTER_DETAIL;
    }
}