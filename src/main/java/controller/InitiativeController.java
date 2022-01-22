package controller;

import view.CharacterPanel;
import viewmodel.CharacterDetailViewModel;
import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class InitiativeController {
    private final JButton ADD_MANUALLY_BUTTON;
    private final FileController FILE_CONTROLLER;
    private final PDFController PDF_CONTROLLER;
    private boolean addingManually, removingManually;
    private double currentTurnInitiative;
    private final List<CharacterPanel> CHARACTER_PANELS;

    public InitiativeController(FileController fileController, PDFController pdfController, JButton addManuallyButton) {
        FILE_CONTROLLER = fileController;
        PDF_CONTROLLER = pdfController;
        ADD_MANUALLY_BUTTON = addManuallyButton;
        addingManually = false;
        removingManually = false;
        currentTurnInitiative = Integer.MAX_VALUE;
        CHARACTER_PANELS = new ArrayList<>();
    }

    public void importMoreCharacters() {
        FILE_CONTROLLER.fileChooser();
        sortCharacterPanels();
    }

    public CharacterPanel addCharacterManually() {
        if (addingManually)
            return null;
        ADD_MANUALLY_BUTTON.setEnabled(false);
        addingManually = true;
        CharacterPanel characterPanel = new CharacterPanel(this, PDF_CONTROLLER);
        CHARACTER_PANELS.add(characterPanel);
        characterPanel.setVisible(true);
        return characterPanel;
    }

    public void nextTurn() {
        if (!CHARACTER_PANELS.isEmpty()) {
            if (currentTurnInitiative == Integer.MAX_VALUE)
                currentTurnInitiative = CHARACTER_PANELS.get(0)
                        .getCharacterDetailViewModel()
                        .getCharacterDetail()
                        .getInitiative();
            else {
                for (int i = 0; i < CHARACTER_PANELS.size(); ++i) {
                    CharacterDetailViewModel characterDetailViewModel = CHARACTER_PANELS.get(i).getCharacterDetailViewModel();
                    if (currentTurnInitiative == characterDetailViewModel.getCharacterDetail().getInitiative()) {
                        if (i == (CHARACTER_PANELS.size() - 1))
                            currentTurnInitiative = CHARACTER_PANELS.get(0)
                                    .getCharacterDetailViewModel()
                                    .getCharacterDetail()
                                    .getInitiative();
                        else currentTurnInitiative = CHARACTER_PANELS.get(i + 1)
                                .getCharacterDetailViewModel()
                                .getCharacterDetail()
                                .getInitiative();
                        break;
                    }
                }
            }
            selectCharacterPanelForTurn();
            saveToFile();
        }
    }

    public void selectCharacterPanelForTurn() {
        if (!CHARACTER_PANELS.isEmpty()) {
            final List<Double> initiatives = CHARACTER_PANELS.stream()
                    .map(c -> c.getCharacterDetailViewModel()
                            .getCharacterDetail()
                            .getInitiative())
                    .collect(Collectors.toList());
            double closestInitiative = initiatives.get(0);
            for (double initiative : initiatives)
                if (Math.abs(currentTurnInitiative - initiative) < Math.abs(currentTurnInitiative - closestInitiative))
                    closestInitiative = initiative;
            currentTurnInitiative = closestInitiative;
            CHARACTER_PANELS.forEach(p -> {
                if (currentTurnInitiative == p.getCharacterDetailViewModel()
                        .getCharacterDetail()
                        .getInitiative())
                {
                    p.setBackground(Color.BLUE);
                    p.setForeground(Color.WHITE);
                }
                else {
                    p.setBackground(null);
                    p.setForeground(Color.BLACK);
                }
            });
        }
    }

    private void saveToFile() {
        try {
            try (PrintWriter pw = new PrintWriter(FILE_CONTROLLER.getEncounterFile())) {
                pw.println(currentTurnInitiative);
                CHARACTER_PANELS.stream()
                        .map(p -> p.getCharacterDetailViewModel().toString())
                        .forEach(pw::println);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeCharacterFromInitiative(CharacterPanel characterPanel) {
        if (!removingManually) {
            removingManually = true;
            JButton verifyDelete = new JButton("Remove?");
            JButton cancelRemove = new JButton("Cancel");
            verifyDelete.addActionListener(e -> {
                removingManually = false;
                characterPanel.getParent().remove(characterPanel);
                CHARACTER_PANELS.remove(characterPanel);
                characterPanel.setVisible(false);
                sortCharacterPanels();
                verifyDelete.getParent().remove(verifyDelete);
                cancelRemove.getParent().remove(cancelRemove);
            });
            cancelRemove.addActionListener(e -> {
                removingManually = false;
                verifyDelete.getParent().remove(verifyDelete);
                cancelRemove.getParent().remove(cancelRemove);
                ((JFrame)characterPanel.getParent().getParent().getParent()).pack();
            });
            characterPanel.getParent().add(verifyDelete);
            characterPanel.getParent().add(cancelRemove);
            ((JFrame)characterPanel.getParent().getParent().getParent()).pack();
        }
    }

    public void sortCharacterPanels() {
        CHARACTER_PANELS.sort(Comparator.comparing(p -> p.getCharacterDetailViewModel()
                .getCharacterDetail()
                .getInitiative()));
        Collections.reverse(CHARACTER_PANELS);
        CHARACTER_PANELS.forEach(p -> {
            if (p.getParent() != null)
                p.getParent().remove(p);
            p.setVisible(true);
            CHARACTER_PANELS.get(0).getParent().add(p);
        });
        selectCharacterPanelForTurn();
        ((JFrame)CHARACTER_PANELS.get(0).getParent().getParent().getParent()).pack();
        FILE_CONTROLLER.saveToFile();
    }

    public void finishAddingManually() {
        addingManually = false;
        ADD_MANUALLY_BUTTON.setEnabled(true);
    }

    private int rollInitiative(int initiativeBonus) {
        int initiative = new Random().nextInt(20) + 1;
        if (initiative == 20)
            initiative = 100;
        else if (initiative == 1)
            initiative = -100;
        else initiative += initiativeBonus;
        return initiative;
    }

    /*
     * Re-rolls initiative when two characters have the same
     * Returns true if the first character rolls higher and false if the second character does
     */
    private boolean rollOffInitiative(int initiativeBonus1, int initiativeBonus2) {
        int initiative1 = 0, initiative2 = 0;
        while (initiative1 == initiative2) {
            initiative1 = rollInitiative(initiativeBonus1);
            initiative2 = rollInitiative(initiativeBonus2);
        }
        return initiative1 > initiative2;
    }

    public boolean isAddingManually() {
        return addingManually;
    }
}