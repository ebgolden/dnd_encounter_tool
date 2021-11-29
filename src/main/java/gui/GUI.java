package gui;

import pdf_to_text_reader.PDFToText;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class GUI {
    private static List<CharacterPanel> characterPanels;
    private static JFrame window;
    private static double currentTurnInitiative;

    public static void main(String[] args) {
        characterPanels = new LinkedList<>();
        window = new JFrame();
        currentTurnInitiative = Integer.MAX_VALUE;
        window.setLayout(new VerticalFlowLayout());
        window.setVisible(true);
        JPanel headerPanel = new JPanel();
        headerPanel.setVisible(true);
        window.add(headerPanel);
        headerPanel.add(new JLabel("Character Name"));
        headerPanel.add(Box.createRigidArea(new Dimension(5,0)));
        headerPanel.add(new JLabel("Initiative"));
        headerPanel.add(Box.createRigidArea(new Dimension(5,0)));
        headerPanel.add(new JLabel("Armor Class"));
        headerPanel.add(Box.createRigidArea(new Dimension(5,0)));
        headerPanel.add(new JLabel("Hit Points"));
        JButton addCharactersButton = new JButton("Import more");
        addCharactersButton.setVisible(true);
        window.add(addCharactersButton);
        addCharactersButton.addActionListener(e -> fileChooser());
        JButton nextTurnButton = new JButton("Next turn");
        nextTurnButton.setVisible(true);
        window.add(nextTurnButton);
        nextTurnButton.addActionListener(e -> nextTurn());
        fileChooser();
    }

    private static void fileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = fc.showOpenDialog(window);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            populate(fc.getSelectedFile());
        }
    }

    private static void populate(File directory) {
        List<File> files = new ArrayList<>();
        try {
            Files.walk(Paths.get(directory.getAbsolutePath()))
                    .filter(Files::isRegularFile)
                    .filter(f -> f.toFile().getAbsolutePath().contains(".pdf"))
                    .filter(f -> !f.toFile().getAbsolutePath().contains("(item)"))
                    .map(Path::toFile)
                    .forEach(files::add);
            PDFToText pdfToText = new PDFToText();
            for (File file : files) {
                String characterName = file.getName().split("\\.")[0];
                String characterSheetText = pdfToText.getTextFromPDF(file.getAbsolutePath());
                int initiative, initiativeBonus, hitPoints, armorClass;
                try {
                    initiativeBonus = Integer.parseInt(characterSheetText.split("INITIATIVE")[1].split("SPEED")[0].replaceAll("[+ \r\n]", ""));
                    armorClass = Integer.parseInt(characterSheetText.split(Pattern.quote("(AC)"))[1].split("Armor Worn")[0].replaceAll("[ \r\n]", ""));
                    hitPoints = Integer.parseInt(characterSheetText.split("HIT POINTS")[1].split("HIT DICE")[0].replaceAll("[ \r\n]", ""));
                } catch (NumberFormatException e) {
                    List<String> details = Arrays.asList(characterSheetText.split("\r\n"));
                    int armorHeaderIndex = details.indexOf("=== ARMOR === ");
                    String [] initiativeAndArmor = details.get(armorHeaderIndex - 5).split(" ");
                    initiativeBonus = Integer.parseInt(initiativeAndArmor[0].replaceAll("[+ \r\n]", ""));
                    armorClass = Integer.parseInt(initiativeAndArmor[1]);
                    hitPoints = Integer.parseInt(details.get(armorHeaderIndex - 2).split(" ")[0]);
                }
                initiative = rollInitiative(initiativeBonus);
                CharacterDetail characterDetail = new CharacterDetail(characterName, initiative, initiativeBonus, armorClass, hitPoints);
                int finalInitiative = initiative;
                characterPanels.forEach(p -> {
                    CharacterDetail c = p.getCharacterDetail();
                    if (c.getInitiative() == finalInitiative)
                        characterDetail.setInitiative(finalInitiative + ((rollOffInitiative(characterDetail.getInitiativeBonus(), c.getInitiativeBonus())) ? .5 : -.5));
                });
                characterPanels.add(new CharacterPanel(characterDetail));
            }
            sortCharacterPanels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int rollInitiative(int initiativeBonus) {
        int initiative = new Random().nextInt(21);
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
    private static boolean rollOffInitiative(int initiativeBonus1, int initiativeBonus2) {
        int initiative1 = 0, initiative2 = 0;
        while (initiative1 == initiative2) {
            initiative1 = rollInitiative(initiativeBonus1);
            initiative2 = rollInitiative(initiativeBonus2);
        }
        return initiative1 > initiative2;
    }

    public static void sortCharacterPanels() {
        characterPanels.sort(Comparator.comparing(p -> p.getCharacterDetail().getInitiative()));
        Collections.reverse(characterPanels);
        characterPanels.forEach(p -> {
            if (p.getParent() != null)
                window.remove(p);
            p.setVisible(true);
            window.add(p);
        });
        if (currentTurnInitiative == Integer.MAX_VALUE)
            currentTurnInitiative = characterPanels.get(0).getCharacterDetail().getInitiative();
        selectCharacterPanelForTurn();
        window.pack();
    }

    private static void selectCharacterPanelForTurn() {
        if (!characterPanels.isEmpty()) {
            characterPanels.forEach(p -> {
                if (currentTurnInitiative == p.getCharacterDetail().getInitiative())
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

    private static void nextTurn() {
        if (!characterPanels.isEmpty()) {
            if (currentTurnInitiative == Integer.MAX_VALUE)
                currentTurnInitiative = characterPanels.get(0).getCharacterDetail().getInitiative();
            else {
                for (int i = 0; i < characterPanels.size(); ++i) {
                    CharacterDetail characterDetail = characterPanels.get(i).getCharacterDetail();
                    if (currentTurnInitiative == characterDetail.getInitiative()) {
                        if (i == (characterPanels.size() - 1))
                            currentTurnInitiative = characterPanels.get(0).getCharacterDetail().getInitiative();
                        else currentTurnInitiative = characterPanels.get(i + 1).getCharacterDetail().getInitiative();
                        break;
                    }
                }
            }
            selectCharacterPanelForTurn();
        }
    }
}