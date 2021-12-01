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
    private final JTextField CHARACTER_NAME_FIELD, INITIATIVE_FIELD, INITIATIVE_BONUS_FIELD, ARMOR_CLASS_FIELD, HIT_POINTS_FIELD;
    private boolean addingManually;

    CharacterPanel() {
        this(new CharacterDetail(null, 0, 0, 0, 0, null));
    }

    CharacterPanel(CharacterDetail characterDetail) {
        super();
        addingManually = characterDetail.getCharacterName() == null;
        CHARACTER_DETAIL = characterDetail;
        CHARACTER_NAME_FIELD = new JFormattedTextField((characterDetail.getCharacterName() != null) ? characterDetail.getCharacterName() : "");
        CHARACTER_NAME_FIELD.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!CHARACTER_NAME_FIELD.getText().equals("")) {
                        String characterName = CHARACTER_NAME_FIELD.getText();
                        if (!characterName.equals(CHARACTER_DETAIL.getCharacterName())) {
                            CHARACTER_DETAIL.setCharacterName(characterName);
                            if (!addingManually)
                                GUI.sortCharacterPanels();
                        }
                    }
                }
            }
        });
        CHARACTER_NAME_FIELD.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}

            @Override
            public void focusLost(FocusEvent e) {
                if (!CHARACTER_NAME_FIELD.getText().equals("")) {
                    String characterName = CHARACTER_NAME_FIELD.getText();
                    if (!characterName.equals(CHARACTER_DETAIL.getCharacterName())) {
                        CHARACTER_DETAIL.setCharacterName(characterName);
                        if (!addingManually)
                            GUI.sortCharacterPanels();
                    }
                }
            }
        });
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
                            if (!addingManually)
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
                        if (!addingManually)
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
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(false);
        INITIATIVE_BONUS_FIELD = new JFormattedTextField(formatter);
        INITIATIVE_BONUS_FIELD.setText(String.valueOf(CHARACTER_DETAIL.getInitiativeBonus()));
        INITIATIVE_BONUS_FIELD.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!INITIATIVE_BONUS_FIELD.getText().equals("")) {
                        String initiativeBonusString = INITIATIVE_BONUS_FIELD.getText().replaceAll("[^\\d]", "");
                        int initiativeBonus = Integer.parseInt(initiativeBonusString);
                        if (initiativeBonus != CHARACTER_DETAIL.getInitiativeBonus()) {
                            CHARACTER_DETAIL.setInitiativeBonus(initiativeBonus);
                            if (!addingManually)
                                GUI.sortCharacterPanels();
                        }
                    }
                }
            }
        });
        INITIATIVE_BONUS_FIELD.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}

            @Override
            public void focusLost(FocusEvent e) {
                if (!INITIATIVE_BONUS_FIELD.getText().equals("")) {
                    String initiativeBonusString = INITIATIVE_BONUS_FIELD.getText().replaceAll("[^\\d]", "");
                    int initiativeBonus = Integer.parseInt(initiativeBonusString);
                    if (initiativeBonus != CHARACTER_DETAIL.getInitiativeBonus()) {
                        CHARACTER_DETAIL.setInitiativeBonus(initiativeBonus);
                        if (!addingManually)
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
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(false);
        ARMOR_CLASS_FIELD = new JFormattedTextField(formatter);
        ARMOR_CLASS_FIELD.setText(String.valueOf(CHARACTER_DETAIL.getArmorClass()));
        ARMOR_CLASS_FIELD.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!ARMOR_CLASS_FIELD.getText().equals("")) {
                        String armorClassString = ARMOR_CLASS_FIELD.getText().replaceAll("[^\\d]", "");
                        int armorClass = Integer.parseInt(armorClassString);
                        if (armorClass != CHARACTER_DETAIL.getArmorClass()) {
                            CHARACTER_DETAIL.setArmorClass(armorClass);
                            if (!addingManually)
                                GUI.sortCharacterPanels();
                        }
                    }
                }
            }
        });
        ARMOR_CLASS_FIELD.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}

            @Override
            public void focusLost(FocusEvent e) {
                if (!ARMOR_CLASS_FIELD.getText().equals("")) {
                    String armorClassString = ARMOR_CLASS_FIELD.getText().replaceAll("[^\\d]", "");
                    int armorClass = Integer.parseInt(armorClassString);
                    if (armorClass != CHARACTER_DETAIL.getArmorClass()) {
                        CHARACTER_DETAIL.setArmorClass(armorClass);
                        if (!addingManually)
                            GUI.sortCharacterPanels();
                    }
                }
            }
        });
        format = NumberFormat.getInstance();
        formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(1000);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(false);
        HIT_POINTS_FIELD = new JFormattedTextField(formatter);
        HIT_POINTS_FIELD.setText(String.valueOf(CHARACTER_DETAIL.getHitPoints()));
        HIT_POINTS_FIELD.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!HIT_POINTS_FIELD.getText().equals("")) {
                        String hitPointsString = HIT_POINTS_FIELD.getText().replaceAll("[^\\d]", "");
                        int hitPoints = Integer.parseInt(hitPointsString);
                        if (hitPoints != CHARACTER_DETAIL.getHitPoints()) {
                            CHARACTER_DETAIL.setHitPoints(hitPoints);
                            if (!addingManually)
                                GUI.sortCharacterPanels();
                        }
                    }
                }
            }
        });
        HIT_POINTS_FIELD.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}

            @Override
            public void focusLost(FocusEvent e) {
                if (!HIT_POINTS_FIELD.getText().equals("")) {
                    String hitPointsString = HIT_POINTS_FIELD.getText().replaceAll("[^\\d]", "");
                    int hitPoints = Integer.parseInt(hitPointsString);
                    if (hitPoints != CHARACTER_DETAIL.getHitPoints()) {
                        CHARACTER_DETAIL.setHitPoints(hitPoints);
                        if (!addingManually)
                            GUI.sortCharacterPanels();
                    }
                }
            }
        });
        super.add(CHARACTER_NAME_FIELD);
        CHARACTER_NAME_FIELD.setPreferredSize(new Dimension(90, 20));
        super.add(Box.createRigidArea(new Dimension(5,0)));
        super.add(INITIATIVE_FIELD);
        INITIATIVE_FIELD.setPreferredSize(new Dimension(50, 20));
        super.add(Box.createRigidArea(new Dimension(5,0)));
        super.add(INITIATIVE_BONUS_FIELD);
        INITIATIVE_BONUS_FIELD.setPreferredSize(new Dimension(90, 20));
        super.add(Box.createRigidArea(new Dimension(5,0)));
        super.add(ARMOR_CLASS_FIELD);
        ARMOR_CLASS_FIELD.setPreferredSize(new Dimension(70, 20));
        super.add(Box.createRigidArea(new Dimension(5,0)));
        super.add(HIT_POINTS_FIELD);
        HIT_POINTS_FIELD.setPreferredSize(new Dimension(60, 20));
        super.add(Box.createRigidArea(new Dimension(5,0)));
        JButton VIEW_FILE_OR_ADD_BUTTON = new JButton((!addingManually ? "Open file" : "Add"));
        VIEW_FILE_OR_ADD_BUTTON.setEnabled(addingManually || (CHARACTER_DETAIL.getFile() != null));
        super.add(VIEW_FILE_OR_ADD_BUTTON);
        VIEW_FILE_OR_ADD_BUTTON.addActionListener(e -> {
            if (!addingManually && (CHARACTER_DETAIL.getFile() != null)) {
                try {
                    Desktop.getDesktop().open(CHARACTER_DETAIL.getFile());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            else {
                addingManually = false;
                VIEW_FILE_OR_ADD_BUTTON.setText("Open file");
                VIEW_FILE_OR_ADD_BUTTON.setEnabled(false);
                GUI.finishAddingManually();
                GUI.sortCharacterPanels();
            }
        });
        JButton REMOVE_BUTTON = new JButton("Remove");
        super.add(REMOVE_BUTTON);
        REMOVE_BUTTON.addActionListener(e -> {
            GUI.finishAddingManually();
            GUI.removeCharacterPanelFromInitiative(this);
        });
    }

    public CharacterDetail getCharacterDetail() {
        return CHARACTER_DETAIL;
    }
}