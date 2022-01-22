package controller;

import utils.StringUtils;
import view.CharacterPanel;
import viewmodel.CharacterDetailViewModel;

public class CharacterController {
    private final InitiativeController INITIATIVE_CONTROLLER;
    private final PDFController PDF_CONTROLLER;
    private final CharacterDetailViewModel CHARACTER_DETAIL_VIEW_MODEL;
    private final StringUtils STRING_UTILS;
    private boolean addingManually;

    public CharacterController(InitiativeController initiativeController, PDFController pdfController, CharacterDetailViewModel characterDetailViewModel) {
        INITIATIVE_CONTROLLER = initiativeController;
        PDF_CONTROLLER = pdfController;
        CHARACTER_DETAIL_VIEW_MODEL = characterDetailViewModel;
        STRING_UTILS = new StringUtils();
        addingManually = getCharacterName() == null;
    }

    public CharacterDetailViewModel getCharacterDetailViewModel() {
        return CHARACTER_DETAIL_VIEW_MODEL;
    }

    public void processCharacterName(String characterName) {
        if ((getCharacterName() == null) || (!characterName.isEmpty() && !getCharacterName().equals(characterName))) {
            CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().setCharacterName(characterName);
            updateCharacterPanels();
        }
    }

    public void processInitiative(String initiativeValue) {
        String initiativeString = STRING_UTILS.removeNonDigits(initiativeValue);
        if (!initiativeString.isEmpty() && !initiativeString.equals(".") && !initiativeString.equals("-")) {
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
            if (initiative != getInitiative()) {
                CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().setInitiative(initiative);
                updateCharacterPanels();
            }
        }
    }

    public void processInitiativeBonus(String initiativeBonusValue) {
        String initiativeBonusString = STRING_UTILS.removeNonDigits(initiativeBonusValue);
        if (!initiativeBonusString.isEmpty()) {
            int initiativeBonus = Integer.parseInt(initiativeBonusString);
            if (initiativeBonus != getInitiativeBonus()) {
                CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().setInitiativeBonus(initiativeBonus);
                updateCharacterPanels();
            }
        }
    }

    public void processArmorClass(String armorClassValue) {
        String armorClassString = STRING_UTILS.removeNonDigits(armorClassValue);
        if (!armorClassString.isEmpty()) {
            int armorClass = Integer.parseInt(armorClassString);
            if (armorClass != getArmorClass()) {
                CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().setArmorClass(armorClass);
                updateCharacterPanels();
            }
        }
    }

    public void processHitPoints(String hitPointsValue) {
        String hitPointsString = STRING_UTILS.removeNonDigits(hitPointsValue);
        if (!hitPointsString.isEmpty()) {
            int hitPoints = Integer.parseInt(hitPointsString);
            if (hitPoints != getHitPoints()) {
                CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().setHitPoints(hitPoints);
                updateCharacterPanels();
            }
        }
    }

    private void updateCharacterPanels() {
        if (!addingManually)
            INITIATIVE_CONTROLLER.sortCharacterPanels();
    }

    public void viewCharacterSheet() {
        try {
            PDF_CONTROLLER.viewCharacterSheet(CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeCharacterFromInitiative(CharacterPanel characterPanel) {
        finishAddingManually();
        INITIATIVE_CONTROLLER.removeCharacterFromInitiative(characterPanel);
    }

    public void finishAddingManually() {
        addingManually = false;
        INITIATIVE_CONTROLLER.finishAddingManually();
        updateCharacterPanels();
    }

    public boolean isAddingManually() {
        return addingManually;
    }

    public boolean isFileAvailable() {
        return CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().getFile() != null;
    }

    public String getCharacterName() {
        return CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().getCharacterName();
    }

    public double getInitiative() {
        return CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().getInitiative();
    }

    public double getInitiativeBonus() {
        return CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().getInitiativeBonus();
    }

    public int getArmorClass() {
        return CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().getArmorClass();
    }

    public int getHitPoints() {
        return CHARACTER_DETAIL_VIEW_MODEL.getCharacterDetail().getHitPoints();
    }
}