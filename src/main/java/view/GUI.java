package view;

import model.CharacterDetail;
import model.MusicDetail;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import controller.PDFToText;
import viewmodel.CharacterDetailViewModel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

public class GUI {
    private static List<CharacterPanel> characterPanels;
    private static List<MusicPanel> musicPanels;
    private static JFrame window;
    private static JPanel interactivePanel, initiativePanel, pdfPanel, soundBoardPanel;
    private static SwingController controller;
    private static JButton addManuallyButton;
    private static double currentTurnInitiative;
    private static boolean addingManually, removingManually;

    public static void main(String[] args) {
        characterPanels = new LinkedList<>();
        musicPanels = new LinkedList<>();
        window = new JFrame();
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                window.getContentPane().removeAll();
                window.getContentPane().add(interactivePanel, BorderLayout.WEST);
                window.getContentPane().add(pdfPanel, BorderLayout.CENTER);
            }
        });
        currentTurnInitiative = Integer.MAX_VALUE;
        addingManually = false;
        removingManually = false;
        window.setLayout(new BorderLayout());
        interactivePanel = new JPanel();
        interactivePanel.setLayout(new VerticalFlowLayout());
        initiativePanel = new JPanel();
        initiativePanel.setLayout(new VerticalFlowLayout());
        soundBoardPanel = new JPanel();
        soundBoardPanel.setLayout(new GridLayout(10, 2));
        pdfPanel = new JPanel();
        pdfPanel.setLayout(new BorderLayout());
        window.setVisible(true);
        interactivePanel.add(initiativePanel);
        interactivePanel.add(soundBoardPanel);
        window.add(interactivePanel, BorderLayout.WEST);
        window.add(pdfPanel, BorderLayout.EAST);
        JPanel initiativeOperationsPanel = new JPanel(new FlowLayout());
        JButton importMoreCharactersButton = new JButton("Import more");
        importMoreCharactersButton.setVisible(true);
        initiativeOperationsPanel.add(importMoreCharactersButton);
        importMoreCharactersButton.addActionListener(e -> fileChooser());
        addManuallyButton = new JButton("Add manually");
        addManuallyButton.setVisible(true);
        initiativeOperationsPanel.add(addManuallyButton);
        addManuallyButton.addActionListener(e -> addCharacterManually());
        JButton addMusicButton = new JButton("Add music");
        addMusicButton.setVisible(true);
        initiativeOperationsPanel.add(addMusicButton);
        addMusicButton.addActionListener(e -> addMusic());
        JButton nextTurnButton = new JButton("Next turn");
        nextTurnButton.setVisible(true);
        initiativeOperationsPanel.add(nextTurnButton);
        initiativePanel.add(initiativeOperationsPanel);
        JPanel headerPanel = new JPanel();
        headerPanel.setVisible(true);
        initiativePanel.add(headerPanel);
        headerPanel.add(new JLabel("Character Name"));
        headerPanel.add(Box.createRigidArea(new Dimension(5,0)));
        headerPanel.add(new JLabel("Initiative"));
        headerPanel.add(Box.createRigidArea(new Dimension(5,0)));
        headerPanel.add(new JLabel("Initiative Bonus"));
        headerPanel.add(Box.createRigidArea(new Dimension(5,0)));
        headerPanel.add(new JLabel("Armor Class"));
        headerPanel.add(Box.createRigidArea(new Dimension(5,0)));
        headerPanel.add(new JLabel("Hit Points"));
        controller = new SwingController();
        SwingViewBuilder factory = new SwingViewBuilder(controller);
        JPanel viewerComponentPanel = factory.buildViewerPanel();
        viewerComponentPanel.setPreferredSize(new Dimension(400, 243));
        viewerComponentPanel.setMaximumSize(new Dimension(400, 243));
        ComponentKeyBinding.install(controller, viewerComponentPanel);
        controller.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
                        controller.getDocumentViewController()));
        pdfPanel.add(viewerComponentPanel, BorderLayout.CENTER);
        pdfPanel.invalidate();
        System.setProperty("org.icepdf.core.nfont.truetype.hinting", "true");
        System.setProperty("org.icepdf.core.awtFontLoading", "true");
        nextTurnButton.addActionListener(e -> nextTurn());
        fileChooser();
    }

    private static void fileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf), CSV (*.csv)", "pdf", "csv"));
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = fc.showOpenDialog(initiativePanel);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            populate(fc.getSelectedFile());
        }
    }

    private static void populate(File directory) {
        List<File> files = new ArrayList<>();
        try {
            if (directory.getAbsolutePath().contains(".csv")) {
                try (Stream<String> stream = Files.lines(Paths.get(directory.getAbsolutePath()))) {
                    Map<String, List<MusicDetail>> playlistToMusicMap = new HashMap<>();
                    stream.forEach(s -> {
                        if (!s.contains(","))
                            currentTurnInitiative = Double.parseDouble(s);
                        else if (s.contains(".wav")) {
                            String[] musicDetailArray = s.split(",");
                            MusicDetail musicDetail = MusicDetail.builder()
                                    .playlistName(musicDetailArray[0])
                                    .musicName(musicDetailArray[1])
                                    .clipTimePosition(Long.parseLong(musicDetailArray[2]))
                                    .playing(Boolean.parseBoolean(musicDetailArray[3]))
                                    .file(new File(musicDetailArray[4]))
                                    .build();
                            if (playlistToMusicMap.containsKey(musicDetail.getPlaylistName()))
                                playlistToMusicMap.get(musicDetail.getPlaylistName()).add(musicDetail);
                            else {
                                List<MusicDetail> musicDetailList = new LinkedList<>();
                                musicDetailList.add(musicDetail);
                                playlistToMusicMap.put(musicDetail.getPlaylistName(), musicDetailList);
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
                    playlistToMusicMap.forEach((k, p) -> musicPanels.add(new MusicPanel(p)));
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
            sortCharacterPanels();
            refreshMusicPanels();
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
        if (currentTurnInitiative == Integer.MAX_VALUE)
            currentTurnInitiative = characterPanels.get(0)
                    .getCharacterDetailViewModel()
                    .getCharacterDetail()
                    .getInitiative();
        selectCharacterPanelForTurn();
        window.pack();
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

    private static void nextTurn() {
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

    private static void saveToFile() {
        try {
            File csvOutputFile = new File("encounter.csv");
            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                pw.println(currentTurnInitiative);
                characterPanels.stream()
                        .map(p -> p.getCharacterDetailViewModel().toString())
                        .forEach(pw::println);
                musicPanels.stream()
                        .map(MusicPanel::getMusicDetailList)
                        .forEach(p -> p.stream()
                                .map(MusicDetail::toString)
                                .forEach(pw::println));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addCharacterManually() {
        if (!addingManually) {
            addingManually = true;
            addManuallyButton.setEnabled(false);
            CharacterPanel characterPanel = new CharacterPanel();
            characterPanels.add(characterPanel);
            characterPanel.setVisible(true);
            initiativePanel.add(characterPanel, 0);
            window.pack();
        }
    }

    private static void addMusic() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("WAV (*.wav)", "wav"));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(soundBoardPanel);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            populateMusic(fc.getSelectedFile());
        }
    }

    private static void populateMusic(File directory) {
        List<File> files = new ArrayList<>();
        List<MusicDetail> musicDetailList = new LinkedList<>();
        try {
            Files.walk(Paths.get(directory.getAbsolutePath()))
                    .filter(Files::isRegularFile)
                    .filter(f -> f.toFile().getAbsolutePath().contains(".wav"))
                    .map(Path::toFile)
                    .forEach(files::add);
            String playlistName = directory.getName();
            for (File file : files) {
                String musicName = file.getName().split("\\.")[0];
                musicDetailList.add(MusicDetail.builder()
                        .playlistName(playlistName)
                        .musicName(musicName)
                        .file(file)
                        .build());
            }
            musicPanels.add(new MusicPanel(musicDetailList));
            refreshMusicPanels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void refreshMusicPanels() {
        musicPanels.sort(Comparator.comparing(p -> p.getMusicDetailList()
                .get(0)
                .getPlaylistName()));
        musicPanels.forEach(p -> {
            if (p.getParent() != null)
                soundBoardPanel.remove(p);
            p.setVisible(true);
            soundBoardPanel.add(p);
        });
        window.pack();
        saveToFile();
    }

    public static void removeCharacterPanelFromInitiative(CharacterPanel characterPanel) {
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
                window.pack();
            });
            initiativePanel.add(verifyDelete);
            initiativePanel.add(cancelRemove);
            window.pack();
        }
    }

    public static void finishAddingManually() {
        addingManually = false;
        addManuallyButton.setEnabled(true);
    }

    public static void viewCharacterSheet(File file) throws MalformedURLException {
        controller.openDocument(file.toURI().toURL());
    }

    public static void stopAllMusic() {
        musicPanels.forEach(MusicPanel::pause);
    }
}