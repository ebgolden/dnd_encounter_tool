package gui;

import pdf_to_text_reader.PDFToText;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class GUI {
    private static JFrame window;

    public static void main(String[] args) {
        window = new JFrame();
        window.setLayout(new VerticalFlowLayout());
        window.setVisible(true);
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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
                System.out.println(characterSheetText);
                int initiative = Integer.parseInt(characterSheetText.split("INITIATIVE")[1].split("SPEED")[0].replaceAll("[+ \r\n]", ""));
                int initiativeRoll = new Random().nextInt(21);
                if (initiativeRoll == 20)
                    initiative = 100;
                else if (initiativeRoll == 1)
                    initiative = -100;
                else initiative += initiativeRoll;
                int hitPoints = Integer.parseInt(characterSheetText.split("HIT POINTS")[1].split("HIT DICE")[0].replaceAll("[ \r\n]", ""));
                int armourClass = Integer.parseInt(characterSheetText.split(Pattern.quote("(AC)"))[1].split("Armor Worn")[0].replaceAll("[ \r\n]", ""));
                JLabel characterNameLabel = new JLabel(characterName + " initiative: " + initiative + " hit points: " + hitPoints + " armour class: " + armourClass);
                characterNameLabel.setVisible(true);
                window.add(characterNameLabel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
