package controller;

import model.MusicDetail;
import view.PlaylistPanel;
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

public class MusicController {
    private final JButton ADD_MUSIC_BUTTON;
    private final List<PlaylistPanel> PLAYLIST_PANELS;

    public MusicController(JButton addMusicButton) {
        ADD_MUSIC_BUTTON = addMusicButton;
        ADD_MUSIC_BUTTON.addActionListener(e -> addMusic());
        PLAYLIST_PANELS = new ArrayList<>();
    }

    public void addMusic() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("WAV (*.wav)", "wav"));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(ADD_MUSIC_BUTTON.getParent());
        if(returnVal == JFileChooser.APPROVE_OPTION)
            populateMusic(fc.getSelectedFile());
    }

    private void populateMusic(File directory) {
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
            playlistToMusicMap.forEach((k, p) -> PLAYLIST_PANELS.add(new PlaylistPanel(this, p)));
            refreshMusicPanels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAllMusic() {
        PLAYLIST_PANELS.forEach(PlaylistPanel::pause);
    }

    public void refreshMusicPanels() {
        PLAYLIST_PANELS.sort(Comparator.comparing(p -> p.getMusicDetailViewModelList()
                .get(0)
                .getMusicDetail()
                .getPlaylistName()));
        PLAYLIST_PANELS.forEach(p -> {
            if (p.getParent() != null)
                p.getParent().remove(p);
            p.setVisible(true);
            PLAYLIST_PANELS.get(0).getParent().add(p);
        });
        ((JFrame)PLAYLIST_PANELS.get(0).getParent().getParent().getParent()).pack();
        saveToFile();
    }

    private void saveToFile() {
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
}