package gui;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
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
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(false);
        INITIATIVE_FIELD = new JFormattedTextField(formatter);
        INITIATIVE_FIELD.setText(String.valueOf(CHARACTER_DETAIL.getInitiative()));
        INITIATIVE_FIELD.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!INITIATIVE_FIELD.getText().equals("") && !INITIATIVE_FIELD.getText().equals(".") && !INITIATIVE_FIELD.getText().equals("-")) {
                        String initiativeString = INITIATIVE_FIELD.getText().replaceAll("[^\\d.-]", "");
                        if (initiativeString.contains(".")) {
                            initiativeString = initiativeString
                                    .replaceFirst("\\.", "A")
                                    .replace(".", "")
                                    .replace("A", ".");
                        }
                        if (initiativeString.contains("-")) {
                            initiativeString = initiativeString.replaceAll("-", "");
                            initiativeString = "-" + initiativeString;
                        }
                        double initiative = Double.parseDouble(initiativeString);
                        if (initiative != CHARACTER_DETAIL.getInitiative()) {
                            CHARACTER_DETAIL.setInitiative(initiative);
                            GUI.sortCharacterPanels();
                        }
                    }
                }
            }
        });
        INITIATIVE_FIELD.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}

            @Override
            public void focusLost(FocusEvent e) {
                if (!INITIATIVE_FIELD.getText().equals("") && !INITIATIVE_FIELD.getText().equals(".") && !INITIATIVE_FIELD.getText().equals("-")) {
                    String initiativeString = INITIATIVE_FIELD.getText().replaceAll("[^\\d.-]", "");
                    if (initiativeString.contains(".")) {
                        initiativeString = initiativeString
                                .replaceFirst("\\.", "A")
                                .replace(".", "")
                                .replace("A", ".");
                    }
                    if (initiativeString.contains("-")) {
                        initiativeString = initiativeString.replaceAll("-", "");
                        initiativeString = "-" + initiativeString;
                    }
                    double initiative = Double.parseDouble(initiativeString);
                    if (initiative != CHARACTER_DETAIL.getInitiative()) {
                        CHARACTER_DETAIL.setInitiative(initiative);
                        GUI.sortCharacterPanels();
                    }
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
        CHARACTER_NAME_FIELD.setPreferredSize(new Dimension(90, 20));
        super.add(Box.createRigidArea(new Dimension(5,0)));
        super.add(INITIATIVE_FIELD);
        INITIATIVE_FIELD.setPreferredSize(new Dimension(50, 20));
        super.add(Box.createRigidArea(new Dimension(5,0)));
        super.add(ARMOR_CLASS_FIELD);
        ARMOR_CLASS_FIELD.setPreferredSize(new Dimension(70, 20));
        super.add(Box.createRigidArea(new Dimension(5,0)));
        super.add(HIT_POINTS_FIELD);
        HIT_POINTS_FIELD.setPreferredSize(new Dimension(60, 20));
        super.add(Box.createRigidArea(new Dimension(5,0)));
        JButton VIEW_FILE_BUTTON = new JButton("Open file");
        super.add(VIEW_FILE_BUTTON);
        VIEW_FILE_BUTTON.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(CHARACTER_DETAIL.getFile());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    public CharacterDetail getCharacterDetail() {
        return CHARACTER_DETAIL;
    }
}