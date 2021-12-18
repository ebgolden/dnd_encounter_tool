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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceHandler {
    private final JFrame FRAME;
    private final OperationPanel OPERATION_PANEL;
    private final InitiativePanel INITIATIVE_PANEL;
    private final SoundPanel SOUND_PANEL;
    private final List<CharacterPanel> CHARACTER_PANELS;
    private final List<MusicPanel> MUSIC_PANELS;
    private double currentTurnInitiative;
    private boolean addingManually, removingManually;
    private File encounterFile;

    public ResourceHandler(JFrame frame) {
        FRAME = frame;
        OPERATION_PANEL = new OperationPanel();
        OPERATION_PANEL.getImportMoreCharactersButton().addActionListener(e -> fileChooser());
        OPERATION_PANEL.getAddManuallyButton().addActionListener(e -> {
            OPERATION_PANEL.getAddManuallyButton().setEnabled(false);
            addCharacterManually();
        });
        OPERATION_PANEL.getAddMusicButton().addActionListener(e -> addMusic());
        OPERATION_PANEL.getNextTurnButton().addActionListener(e -> nextTurn());
        INITIATIVE_PANEL = new InitiativePanel();
        SOUND_PANEL = new SoundPanel();
        CHARACTER_PANELS = new LinkedList<>();
        MUSIC_PANELS = new LinkedList<>();
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
        int returnVal = fc.showOpenDialog(FRAME);
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
                            CHARACTER_PANELS.add(new CharacterPanel(characterDetailViewModel));
                        }
                    });
                    playlistToMusicMap.forEach((k, p) -> MUSIC_PANELS.add(new MusicPanel(p)));
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
                    CHARACTER_PANELS.forEach(p -> {
                        CharacterDetailViewModel c = p.getCharacterDetailViewModel();
                        if (c.getCharacterDetail().getInitiative() == finalInitiative)
                            characterDetailViewModel.getCharacterDetail()
                                    .setInitiative(finalInitiative + ((rollOffInitiative(characterDetailViewModel.getCharacterDetail()
                                            .getInitiativeBonus(), c.getCharacterDetail().getInitiativeBonus())) ? .5 : -.5));
                    });
                    CHARACTER_PANELS.add(new CharacterPanel(characterDetailViewModel));
                }
            }
            if (currentTurnInitiative == Integer.MAX_VALUE)
                currentTurnInitiative = CHARACTER_PANELS.get(0)
                        .getCharacterDetailViewModel()
                        .getCharacterDetail()
                        .getInitiative();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int rollInitiative(int initiativeBonus) {
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
    private boolean rollOffInitiative(int initiativeBonus1, int initiativeBonus2) {
        int initiative1 = 0, initiative2 = 0;
        while (initiative1 == initiative2) {
            initiative1 = rollInitiative(initiativeBonus1);
            initiative2 = rollInitiative(initiativeBonus2);
        }
        return initiative1 > initiative2;
    }

    public void sortCharacterPanels() {
        CHARACTER_PANELS.sort(Comparator.comparing(p -> p.getCharacterDetailViewModel()
                .getCharacterDetail()
                .getInitiative()));
        Collections.reverse(CHARACTER_PANELS);
        CHARACTER_PANELS.forEach(p -> {
            if (p.getParent() != null)
                INITIATIVE_PANEL.remove(p);
            p.setVisible(true);
            INITIATIVE_PANEL.add(p);
        });
        selectCharacterPanelForTurn();
        FRAME.pack();
        saveToFile();
    }

    private void selectCharacterPanelForTurn() {
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
            try (PrintWriter pw = new PrintWriter(encounterFile)) {
                pw.println(currentTurnInitiative);
                CHARACTER_PANELS.stream()
                        .map(p -> p.getCharacterDetailViewModel().toString())
                        .forEach(pw::println);
                MUSIC_PANELS.stream()
                        .map(MusicPanel::getMusicDetailViewModelList)
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
            CHARACTER_PANELS.add(characterPanel);
            characterPanel.setVisible(true);
            INITIATIVE_PANEL.add(characterPanel, 0);
            FRAME.pack();
        }
    }

    public void addMusic() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("WAV (*.wav)", "wav"));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(FRAME);
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
            playlistToMusicMap.forEach((k, p) -> MUSIC_PANELS.add(new MusicPanel(p)));
            refreshMusicPanels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshMusicPanels() {
        MUSIC_PANELS.sort(Comparator.comparing(p -> p.getMusicDetailViewModelList()
                .get(0)
                .getMusicDetail()
                .getPlaylistName()));
        MUSIC_PANELS.forEach(p -> {
            if (p.getParent() != null)
                SOUND_PANEL.remove(p);
            p.setVisible(true);
            SOUND_PANEL.add(p);
        });
        FRAME.pack();
        saveToFile();
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

    public void removeCharacterFromInitiative(CharacterPanel characterPanel) {
        if (!removingManually) {
            removingManually = true;
            JButton verifyDelete = new JButton("Remove?");
            JButton cancelRemove = new JButton("Cancel");
            verifyDelete.addActionListener(e -> {
                removingManually = false;
                INITIATIVE_PANEL.remove(characterPanel);
                CHARACTER_PANELS.remove(characterPanel);
                characterPanel.setVisible(false);
                sortCharacterPanels();
                INITIATIVE_PANEL.remove(verifyDelete);
                INITIATIVE_PANEL.remove(cancelRemove);
            });
            cancelRemove.addActionListener(e -> {
                removingManually = false;
                INITIATIVE_PANEL.remove(verifyDelete);
                INITIATIVE_PANEL.remove(cancelRemove);
                FRAME.pack();
            });
            INITIATIVE_PANEL.add(verifyDelete);
            INITIATIVE_PANEL.add(cancelRemove);
            FRAME.pack();
        }
    }

    public void finishAddingManually() {
        addingManually = false;
        OPERATION_PANEL.getAddManuallyButton().setEnabled(true);
    }

    public void stopAllMusic() {
        MUSIC_PANELS.forEach(MusicPanel::pause);
    }

    public OperationPanel getOperationPanel() {
        return OPERATION_PANEL;
    }

    public InitiativePanel getInitiativePanel() {
        return INITIATIVE_PANEL;
    }

    public SoundPanel getSoundPanel() {
        return SOUND_PANEL;
    }
}