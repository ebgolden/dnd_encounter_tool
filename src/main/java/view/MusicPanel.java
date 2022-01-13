package view;

import controller.AudioController;
import viewmodel.MusicDetailViewModel;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MusicPanel extends JPanel {
    private final AudioController AUDIO_CONTROLLER;
    private final JButton PLAYBACK_BUTTON;

    public MusicPanel(List<MusicDetailViewModel> musicDetailViewModelList) {
        super();
        AUDIO_CONTROLLER = new AudioController(musicDetailViewModelList);
        JButton previousButton = new JButton("Back");
        super.add(previousButton);
        previousButton.setEnabled(AUDIO_CONTROLLER.isPrevious());
        PLAYBACK_BUTTON = new JButton(AUDIO_CONTROLLER.getPlaylistName());
        super.add(PLAYBACK_BUTTON);
        PLAYBACK_BUTTON.setPreferredSize(new Dimension(165, 26));
        PLAYBACK_BUTTON.addActionListener(e -> {
            if (AUDIO_CONTROLLER.isPlaying()) {
                pause();
            }
            else {
                PLAYBACK_BUTTON.setBackground(Color.GREEN);
                AUDIO_CONTROLLER.play();
            }
        });
        JButton nextButton = new JButton("Next");
        super.add(nextButton);
        nextButton.setEnabled(AUDIO_CONTROLLER.isNext());
        previousButton.addActionListener(e -> {
            if (AUDIO_CONTROLLER.isPrevious()) {
                AUDIO_CONTROLLER.previous();
                previousButton.setEnabled(AUDIO_CONTROLLER.isPrevious());
                nextButton.setEnabled(true);
            }
        });
        nextButton.addActionListener(e -> {
            if (AUDIO_CONTROLLER.isNext()) {
                AUDIO_CONTROLLER.next();
                nextButton.setEnabled(AUDIO_CONTROLLER.isNext());
                previousButton.setEnabled(true);
            }
        });
    }

    public void pause() {
        if (AUDIO_CONTROLLER.isPlaying()) {
            PLAYBACK_BUTTON.setBackground(null);
            AUDIO_CONTROLLER.pause();
        }
    }

    public List<MusicDetailViewModel> getMusicDetailViewModelList() {
        return AUDIO_CONTROLLER.getPlaylist();
    }
}