package gui;

import pdf_to_text_reader.PDFToText;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class GUI {
    private static List<CharacterPanel> characterPanels;
    private static JFrame window;

    public static void main(String[] args) {
        characterPanels = new LinkedList<>();
        window = new JFrame();
        window.setLayout(new VerticalFlowLayout());
        window.setVisible(true);
        JButton addCharacters = new JButton("Import more");
        addCharacters.setVisible(true);
        window.add(addCharacters);
        addCharacters.addActionListener(e -> fileChooser());
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
        window.pack();
    }
}