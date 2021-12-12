package view;

import controller.AudioFilePlayer;
import model.MusicDetail;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MusicPanel extends JPanel {
    private final List<MusicDetail> MUSIC_DETAIL_LIST;
    private AudioFilePlayer audioFilePlayer;
    private final JButton TOGGLE_BUTTON;
    private int currentMusicIndex;

    public MusicPanel(List<MusicDetail> musicDetailList) {
        super();
        MUSIC_DETAIL_LIST = musicDetailList;
        currentMusicIndex = 0;
        for (int musicIndex = 0; musicIndex < MUSIC_DETAIL_LIST.size(); ++musicIndex) {
            MusicDetail musicDetail = MUSIC_DETAIL_LIST.get(musicIndex);
            if (musicDetail.isPlaying()) {
                currentMusicIndex = musicIndex;
                break;
            }
        }
        audioFilePlayer = new AudioFilePlayer(MUSIC_DETAIL_LIST.get(currentMusicIndex).getFile());
        JButton PREVIOUS_BUTTON = new JButton("Back");
        super.add(PREVIOUS_BUTTON);
        PREVIOUS_BUTTON.setEnabled(currentMusicIndex != 0);
        TOGGLE_BUTTON = new JButton(MUSIC_DETAIL_LIST.get(currentMusicIndex).getPlaylistName());
        super.add(TOGGLE_BUTTON);
        TOGGLE_BUTTON.setPreferredSize(new Dimension(165, 26));
        TOGGLE_BUTTON.addActionListener(e -> {
            if (audioFilePlayer.isPlaying()) {
                pause();
            }
            else {
                GUI.stopAllMusic();
                TOGGLE_BUTTON.setBackground(Color.GREEN);
                audioFilePlayer.play(MUSIC_DETAIL_LIST.get(currentMusicIndex).getClipTimePosition());
            }
        });
        JButton NEXT_BUTTON = new JButton("Next");
        super.add(NEXT_BUTTON);
        NEXT_BUTTON.setEnabled(currentMusicIndex != (MUSIC_DETAIL_LIST.size() - 1));
        PREVIOUS_BUTTON.addActionListener(e -> {
            if (currentMusicIndex > 0) {
                boolean musicPlaying = audioFilePlayer.isPlaying();
                audioFilePlayer.pause();
                MUSIC_DETAIL_LIST.get(currentMusicIndex).setClipTimePosition(audioFilePlayer.getClipTimePosition());
                MUSIC_DETAIL_LIST.get(currentMusicIndex).setPlaying(false);
                PREVIOUS_BUTTON.setEnabled(--currentMusicIndex != 0);
                NEXT_BUTTON.setEnabled(true);
                MUSIC_DETAIL_LIST.get(currentMusicIndex).setPlaying(true);
                audioFilePlayer = new AudioFilePlayer(MUSIC_DETAIL_LIST.get(currentMusicIndex).getFile());
                if (musicPlaying)
                    audioFilePlayer.play(MUSIC_DETAIL_LIST.get(currentMusicIndex).getClipTimePosition());
            }
        });
        NEXT_BUTTON.addActionListener(e -> {
            if (currentMusicIndex < (MUSIC_DETAIL_LIST.size() - 1)) {
                boolean musicPlaying = audioFilePlayer.isPlaying();
                audioFilePlayer.pause();
                MUSIC_DETAIL_LIST.get(currentMusicIndex).setClipTimePosition(audioFilePlayer.getClipTimePosition());
                MUSIC_DETAIL_LIST.get(currentMusicIndex).setPlaying(false);
                NEXT_BUTTON.setEnabled(++currentMusicIndex != (MUSIC_DETAIL_LIST.size() - 1));
                PREVIOUS_BUTTON.setEnabled(true);
                MUSIC_DETAIL_LIST.get(currentMusicIndex).setPlaying(true);
                audioFilePlayer = new AudioFilePlayer(MUSIC_DETAIL_LIST.get(currentMusicIndex).getFile());
                if (musicPlaying)
                    audioFilePlayer.play(MUSIC_DETAIL_LIST.get(currentMusicIndex).getClipTimePosition());
            }
        });
    }

    public void pause() {
        TOGGLE_BUTTON.setBackground(null);
        audioFilePlayer.pause();
        MUSIC_DETAIL_LIST.get(currentMusicIndex).setClipTimePosition(audioFilePlayer.getClipTimePosition());
    }

    public List<MusicDetail> getMusicDetailList() {
        return MUSIC_DETAIL_LIST;
    }
}