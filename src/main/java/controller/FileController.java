package controller;

import model.CharacterDetail;
import model.MusicDetail;
import view.*;
import viewmodel.CharacterDetailViewModel;
import viewmodel.MusicDetailViewModel;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileController {
    private static JFrame frame;
    private static OperationPanel operationPanel;
    private static InitiativePanel initiativePanel;
    private final MusicPanel SOUND_PANEL;
    private final PDFPanel PDF_PANEL;
    private static PDFController pdfController;
    private static List<CharacterPanel> characterPanels;
    private static List<PlaylistPanel> playlistPanels;
    private static double currentTurnInitiative;
    private static boolean addingManually, removingManually;
    private static File encounterFile;

    public FileController(JFrame frame) {
        FileController.frame = frame;
        operationPanel = new OperationPanel();
        operationPanel.getImportMoreCharactersButton().addActionListener(e -> fileChooser());
        operationPanel.getAddManuallyButton().addActionListener(e -> {
            operationPanel.getAddManuallyButton().setEnabled(false);
            addCharacterManually();
        });
        operationPanel.getAddMusicButton().addActionListener(e -> addMusic());
        operationPanel.getNextTurnButton().addActionListener(e -> nextTurn());
        initiativePanel = new InitiativePanel();
        SOUND_PANEL = new MusicPanel();
        PDF_PANEL = new PDFPanel();
        pdfController = PDF_PANEL.getPDFController();
        characterPanels = new LinkedList<>();
        playlistPanels = new LinkedList<>();
        encounterFile = new File("encounter.csv");
        currentTurnInitiative = Integer.MAX_VALUE;
        addingManually = false;
        removingManually = false;
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
        sortCharacterPanels();
        refreshMusicPanels();
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

    public static void sortCharacterPanels() {
        characterPanels.sort(Comparator.comparing(p -> p.getCharacterDetailViewModel()
                .getCharacterDetail()
                .getInitiative()));
        Collections.reverse(characterPanels);
        characterPanels.forEach(p -> {
            if (p.getParent() != null)
                initiativePanel.remove(p);
            p.setVisible(true);
            initiativePanel.add(p);
        });
        selectCharacterPanelForTurn();
        frame.pack();
        saveToFile();
    }

    private static void selectCharacterPanelForTurn() {
        if (!characterPanels.isEmpty()) {
            final List<Double> initiatives = characterPanels.stream()
                    .map(c -> c.getCharacterDetailViewModel()
                            .getCharacterDetail()
                            .getInitiative())
                    .collect(Collectors.toList());
            double closestInitiative = initiatives.get(0);
            for (double initiative : initiatives)
                if (Math.abs(currentTurnInitiative - initiative) < Math.abs(currentTurnInitiative - closestInitiative))
                    closestInitiative = initiative;
            currentTurnInitiative = closestInitiative;
            characterPanels.forEach(p -> {
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

    private static void saveToFile() {
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

    public void addCharacterManually() {
        if (!addingManually) {
            addingManually = true;
            CharacterPanel characterPanel = new CharacterPanel();
            characterPanels.add(characterPanel);
            characterPanel.setVisible(true);
            initiativePanel.add(characterPanel, 0);
            frame.pack();
        }
    }

    public void addMusic() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("WAV (*.wav)", "wav"));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            populateMusic(fc.getSelectedFile());
        }
    }

    public void populateMusic(File directory) {
        List<File> files = new ArrayList<>();
        Map<String, List<MusicDetailViewModel>> playlistToMusicMap = new HashMap<>();
        try {
            Files.walk(Paths.get(directory.getAbsolutePath()))
                    .filter(Files::isRegularFile)
                    .filter(f -> f.toFile().getAbsolutePath().contains(".wav"))
                    .map(Path::toFile)
                    .forEach(files::add);
            for (File file : files) {
                MusicDetailViewModel musicDetailViewModel = new MusicDetailViewModel(MusicDetail.builder()
                        .playlistName(file.getParentFile().getName())
                        .file(file)
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
            playlistToMusicMap.forEach((k, p) -> playlistPanels.add(new PlaylistPanel(p)));
            refreshMusicPanels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshMusicPanels() {
        playlistPanels.sort(Comparator.comparing(p -> p.getMusicDetailViewModelList()
                .get(0)
                .getMusicDetail()
                .getPlaylistName()));
        playlistPanels.forEach(p -> {
            if (p.getParent() != null)
                SOUND_PANEL.remove(p);
            p.setVisible(true);
            SOUND_PANEL.add(p);
        });
        frame.pack();
        saveToFile();
    }

    public void nextTurn() {
        if (!characterPanels.isEmpty()) {
            if (currentTurnInitiative == Integer.MAX_VALUE)
                currentTurnInitiative = characterPanels.get(0)
                        .getCharacterDetailViewModel()
                        .getCharacterDetail()
                        .getInitiative();
            else {
                for (int i = 0; i < characterPanels.size(); ++i) {
                    CharacterDetailViewModel characterDetailViewModel = characterPanels.get(i).getCharacterDetailViewModel();
                    if (currentTurnInitiative == characterDetailViewModel.getCharacterDetail().getInitiative()) {
                        if (i == (characterPanels.size() - 1))
                            currentTurnInitiative = characterPanels.get(0)
                                    .getCharacterDetailViewModel()
                                    .getCharacterDetail()
                                    .getInitiative();
                        else currentTurnInitiative = characterPanels.get(i + 1)
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

    public static void removeCharacterFromInitiative(CharacterPanel characterPanel) {
        if (!removingManually) {
            removingManually = true;
            JButton verifyDelete = new JButton("Remove?");
            JButton cancelRemove = new JButton("Cancel");
            verifyDelete.addActionListener(e -> {
                removingManually = false;
                initiativePanel.remove(characterPanel);
                characterPanels.remove(characterPanel);
                characterPanel.setVisible(false);
                sortCharacterPanels();
                initiativePanel.remove(verifyDelete);
                initiativePanel.remove(cancelRemove);
            });
            cancelRemove.addActionListener(e -> {
                removingManually = false;
                initiativePanel.remove(verifyDelete);
                initiativePanel.remove(cancelRemove);
                frame.pack();
            });
            initiativePanel.add(verifyDelete);
            initiativePanel.add(cancelRemove);
            frame.pack();
        }
    }

    public static void finishAddingManually() {
        addingManually = false;
        operationPanel.getAddManuallyButton().setEnabled(true);
    }

    public static void stopAllMusic() {
        playlistPanels.forEach(PlaylistPanel::pause);
    }

    public static void viewCharacterSheet(File file) throws MalformedURLException {
        pdfController.viewCharacterSheet(file);
    }

    public OperationPanel getOperationPanel() {
        return operationPanel;
    }

    public InitiativePanel getInitiativePanel() {
        return initiativePanel;
    }

    public MusicPanel getSoundPanel() {
        return SOUND_PANEL;
    }

    public PDFPanel getPDFPanel() {
        return PDF_PANEL;
    }
}