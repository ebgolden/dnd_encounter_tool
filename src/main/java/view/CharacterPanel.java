package view;

import controller.InitiativeController;
import model.CharacterDetail;
import utils.TextFieldFactory;
import viewmodel.CharacterDetailViewModel;
import javax.swing.*;
import java.awt.*;

public class CharacterPanel extends JPanel {
    private final InitiativeController INITIATIVE_CONTROLLER;

    public CharacterPanel() {
        this(new CharacterDetailViewModel(CharacterDetail.builder().build()));
    }

    public CharacterPanel(CharacterDetailViewModel characterDetailViewModel) {
        super();
        INITIATIVE_CONTROLLER = new InitiativeController(characterDetailViewModel);
        TextFieldFactory TEXT_FIELD_FACTORY = new TextFieldFactory();
        JTextField CHARACTER_NAME_FIELD = TEXT_FIELD_FACTORY.getTextField(INITIATIVE_CONTROLLER.getCharacterName());
        TEXT_FIELD_FACTORY.addListeners(CHARACTER_NAME_FIELD, () -> {
            INITIATIVE_CONTROLLER.processCharacterName(CHARACTER_NAME_FIELD.getText());
            return null;
        });
        JTextField INITIATIVE_FIELD = TEXT_FIELD_FACTORY.getTextField(INITIATIVE_CONTROLLER.getInitiative());
        TEXT_FIELD_FACTORY.addListeners(INITIATIVE_FIELD, () -> {
            INITIATIVE_CONTROLLER.processInitiative(INITIATIVE_FIELD.getText());
            return null;
        });
        JTextField INITIATIVE_BONUS_FIELD = TEXT_FIELD_FACTORY.getTextField(INITIATIVE_CONTROLLER.getInitiativeBonus());
        TEXT_FIELD_FACTORY.addListeners(INITIATIVE_BONUS_FIELD, () -> {
            INITIATIVE_CONTROLLER.processInitiativeBonus(INITIATIVE_BONUS_FIELD.getText());
            return null;
        });
        JTextField ARMOR_CLASS_FIELD = TEXT_FIELD_FACTORY.getTextField(INITIATIVE_CONTROLLER.getArmorClass());
        TEXT_FIELD_FACTORY.addListeners(ARMOR_CLASS_FIELD, () -> {
            INITIATIVE_CONTROLLER.processArmorClass(ARMOR_CLASS_FIELD.getText());
            return null;
        });
        JTextField HIT_POINTS_FIELD = TEXT_FIELD_FACTORY.getTextField(INITIATIVE_CONTROLLER.getHitPoints());
        TEXT_FIELD_FACTORY.addListeners(HIT_POINTS_FIELD, () -> {
            INITIATIVE_CONTROLLER.processHitPoints(HIT_POINTS_FIELD.getText());
            return null;
        });
        super.add(CHARACTER_NAME_FIELD);
        final Dimension BUFFER_DIMENSIONS = new Dimension(5,0);
        CHARACTER_NAME_FIELD.setPreferredSize(new Dimension(90, 20));
        super.add(Box.createRigidArea(BUFFER_DIMENSIONS));
        super.add(INITIATIVE_FIELD);
        INITIATIVE_FIELD.setPreferredSize(new Dimension(50, 20));
        super.add(Box.createRigidArea(BUFFER_DIMENSIONS));
        super.add(INITIATIVE_BONUS_FIELD);
        INITIATIVE_BONUS_FIELD.setPreferredSize(new Dimension(90, 20));
        super.add(Box.createRigidArea(BUFFER_DIMENSIONS));
        super.add(ARMOR_CLASS_FIELD);
        ARMOR_CLASS_FIELD.setPreferredSize(new Dimension(70, 20));
        super.add(Box.createRigidArea(BUFFER_DIMENSIONS));
        super.add(HIT_POINTS_FIELD);
        HIT_POINTS_FIELD.setPreferredSize(new Dimension(60, 20));
        super.add(Box.createRigidArea(BUFFER_DIMENSIONS));
        JButton VIEW_FILE_OR_ADD_BUTTON = new JButton(INITIATIVE_CONTROLLER.isAddingManually() ? "Add" : "Open file");
        VIEW_FILE_OR_ADD_BUTTON.setEnabled(INITIATIVE_CONTROLLER.isAddingManually() || INITIATIVE_CONTROLLER.isFileAvailable());
        super.add(VIEW_FILE_OR_ADD_BUTTON);
        VIEW_FILE_OR_ADD_BUTTON.addActionListener(e -> {
            if (!INITIATIVE_CONTROLLER.isAddingManually() && INITIATIVE_CONTROLLER.isFileAvailable())
                INITIATIVE_CONTROLLER.viewCharacterSheet();
            else {
                INITIATIVE_CONTROLLER.finishAddingManually();
                VIEW_FILE_OR_ADD_BUTTON.setText("Open file");
                VIEW_FILE_OR_ADD_BUTTON.setEnabled(false);
            }
        });
        JButton REMOVE_BUTTON = new JButton("Remove");
        super.add(REMOVE_BUTTON);
        REMOVE_BUTTON.addActionListener(e -> INITIATIVE_CONTROLLER.removeCharacterFromInitiative(this));
    }

    public CharacterDetailViewModel getCharacterDetailViewModel() {
        return INITIATIVE_CONTROLLER.getCharacterDetailViewModel();
    }
}