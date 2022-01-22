package controller;

import model.CharacterDetail;
import model.MusicDetail;
import view.*;
import viewmodel.CharacterDetailViewModel;
import viewmodel.MusicDetailViewModel;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FileController {
    private static JFrame frame;
    private File encounterFile;

    public FileController() {
        FileController.frame = frame;
        encounterFile = new File("encounter.csv");
        fileChooser();
    }

    public void fileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf), CSV (*.csv)", "pdf", "csv"));
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = fc.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            populate(fc.getSelectedFile());
        }
    }

    private void populate(File directory) {
        List<File> files = new ArrayList<>();
        try {
            if (directory.getAbsolutePath().contains(".csv")) {
                encounterFile = directory;
                try (Stream<String> stream = Files.lines(Paths.get(directory.getAbsolutePath()))) {
                    Map<String, List<MusicDetailViewModel>> playlistToMusicMap = new HashMap<>();
                    stream.forEach(s -> {
                        if (!s.contains(","))
                            currentTurnInitiative = Double.parseDouble(s);
                        else if (s.contains(".wav")) {
                            String[] musicDetailArray = s.split(",");
                            MusicDetailViewModel musicDetailViewModel = new MusicDetailViewModel(MusicDetail.builder()
                                    .playlistName(musicDetailArray[0])
                                    .playing(Boolean.parseBoolean(musicDetailArray[1]))
                                    .file(new File(musicDetailArray[2]))
                                    .build());
                            if (playlistToMusicMap.containsKey(musicDetailViewModel.getMusicDetail()
                                    .getPlaylistName()))
                                playlistToMusicMap.get(musicDetailViewModel.getMusicDetail()
                                        .getPlaylistName())
                                        .add(musicDetailViewModel);
                            else {
                                List<MusicDetailViewModel> musicDetailViewModelList = new LinkedList<>();
                                musicDetailViewModelList.add(musicDetailViewModel);
                                playlistToMusicMap.put(musicDetailViewModel.getMusicDetail()
                                        .getPlaylistName(), musicDetailViewModelList);
                            }
                        }
                        else {
                            String[] characterDetailArray = s.split(",");
                            CharacterDetailViewModel characterDetailViewModel = new CharacterDetailViewModel(CharacterDetail
                                    .builder()
                                    .characterName(characterDetailArray[0])
                                    .initiative(Double.parseDouble(characterDetailArray[1]))
                                    .initiativeBonus(Integer.parseInt(characterDetailArray[2]))
                                    .armorClass(Integer.parseInt(characterDetailArray[3]))
                                    .hitPoints(Integer.parseInt(characterDetailArray[4]))
                                    .file(new File(characterDetailArray[5]))
                                    .build());
                            characterPanels.add(new CharacterPanel(characterDetailViewModel));
                        }
                    });
                    playlistToMusicMap.forEach((k, p) -> playlistPanels.add(new PlaylistPanel(p)));
                }
            }
            else {
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
                        initiativeBonus = Integer.parseInt(characterSheetText.split("INITIATIVE")[1]
                                .split("SPEED")[0]
                                .replaceAll("[+ \r\n]", ""));
                        armorClass = Integer.parseInt(characterSheetText.split(Pattern.quote("(AC)"))[1]
                                .split("Armor Worn")[0]
                                .replaceAll("[ \r\n]", ""));
                        hitPoints = Integer.parseInt(characterSheetText.split("HIT POINTS")[1]
                                .split("HIT DICE")[0]
                                .replaceAll("[ \r\n]", ""));
                    } catch (NumberFormatException e) {
                        List<String> details = Arrays.asList(characterSheetText.split("\r\n"));
                        int armorHeaderIndex = details.indexOf("=== ARMOR === ");
                        String[] initiativeAndArmor = details.get(armorHeaderIndex - 5).split(" ");
                        initiativeBonus = Integer.parseInt(initiativeAndArmor[0].replaceAll("[+ \r\n]", ""));
                        armorClass = Integer.parseInt(initiativeAndArmor[1]);
                        hitPoints = Integer.parseInt(details.get(armorHeaderIndex - 2).split(" ")[0]);
                    }
                    initiative = rollInitiative(initiativeBonus);
                    CharacterDetailViewModel characterDetailViewModel = new CharacterDetailViewModel(CharacterDetail
                            .builder()
                            .characterName(characterName)
                            .initiative(initiative)
                            .initiativeBonus(initiativeBonus)
                            .armorClass(armorClass)
                            .hitPoints(hitPoints)
                            .file(file)
                            .build());
                    int finalInitiative = initiative;
                    characterPanels.forEach(p -> {
                        CharacterDetailViewModel c = p.getCharacterDetailViewModel();
                        if (c.getCharacterDetail().getInitiative() == finalInitiative)
                            characterDetailViewModel.getCharacterDetail()
                                    .setInitiative(finalInitiative + ((rollOffInitiative(characterDetailViewModel.getCharacterDetail()
                                            .getInitiativeBonus(), c.getCharacterDetail().getInitiativeBonus())) ? .5 : -.5));
                    });
                    characterPanels.add(new CharacterPanel(characterDetailViewModel));
                }
            }
            if (currentTurnInitiative == Integer.MAX_VALUE)
                currentTurnInitiative = characterPanels.get(0)
                        .getCharacterDetailViewModel()
                        .getCharacterDetail()
                        .getInitiative();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile() {
        try {
            try (PrintWriter pw = new PrintWriter(encounterFile)) {
                pw.println(currentTurnInitiative);
                characterPanels.stream()
                        .map(p -> p.getCharacterDetailViewModel().toString())
                        .forEach(pw::println);
                playlistPanels.stream()
                        .map(PlaylistPanel::getMusicDetailViewModelList)
                        .forEach(p -> p.stream()
                                .map(MusicDetailViewModel::toString)
                                .forEach(pw::println));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getEncounterFile() {
        return encounterFile;
    }
}