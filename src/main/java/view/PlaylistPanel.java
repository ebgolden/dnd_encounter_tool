package view;

import controller.MusicController;
import controller.PlaylistController;
import viewmodel.MusicDetailViewModel;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PlaylistPanel extends JPanel {
    private final PlaylistController PLAYLIST_CONTROLLER;
    private final JButton PLAYBACK_BUTTON;

    public PlaylistPanel(MusicController musicController, List<MusicDetailViewModel> musicDetailViewModelList) {
        super();
        PLAYLIST_CONTROLLER = new PlaylistController(musicController, musicDetailViewModelList);
        JButton previousButton = new JButton("Back");
        super.add(previousButton);
        previousButton.setEnabled(PLAYLIST_CONTROLLER.isPrevious());
        PLAYBACK_BUTTON = new JButton(PLAYLIST_CONTROLLER.getPlaylistName());
        super.add(PLAYBACK_BUTTON);
        PLAYBACK_BUTTON.setPreferredSize(new Dimension(165, 26));
        PLAYBACK_BUTTON.addActionListener(e -> {
            if (PLAYLIST_CONTROLLER.isPlaying()) {
                pause();
            }
            else {
                PLAYBACK_BUTTON.setBackground(Color.GREEN);
                PLAYLIST_CONTROLLER.play();
            }
        });
        JButton nextButton = new JButton("Next");
        super.add(nextButton);
        nextButton.setEnabled(PLAYLIST_CONTROLLER.isNext());
        previousButton.addActionListener(e -> {
            if (PLAYLIST_CONTROLLER.isPrevious()) {
                PLAYLIST_CONTROLLER.previous();
                previousButton.setEnabled(PLAYLIST_CONTROLLER.isPrevious());
                nextButton.setEnabled(true);
            }
        });
        nextButton.addActionListener(e -> {
            if (PLAYLIST_CONTROLLER.isNext()) {
                PLAYLIST_CONTROLLER.next();
                nextButton.setEnabled(PLAYLIST_CONTROLLER.isNext());
                previousButton.setEnabled(true);
            }
        });
    }

    public void pause() {
        if (PLAYLIST_CONTROLLER.isPlaying()) {
            PLAYBACK_BUTTON.setBackground(null);
            PLAYLIST_CONTROLLER.pause();
        }
    }

    public List<MusicDetailViewModel> getMusicDetailViewModelList() {
        return PLAYLIST_CONTROLLER.getPlaylist();
    }
}