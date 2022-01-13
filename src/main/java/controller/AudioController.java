package controller;

import viewmodel.MusicDetailViewModel;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.List;

public class AudioController {
    private final List<MusicDetailViewModel> PLAYLIST;
    private final String PLAYLIST_NAME;
    private int currentMusicIndex;
    private File file;
    private Clip clip;
    private long clipTimePosition;
    private boolean playback;

    public AudioController(List<MusicDetailViewModel> playlist) {
        PLAYLIST = playlist;
        currentMusicIndex = 0;
        for (int musicIndex = 0; musicIndex < PLAYLIST.size(); ++musicIndex) {
            MusicDetailViewModel musicDetailViewModel = PLAYLIST.get(musicIndex);
            if (musicDetailViewModel.getMusicDetail().isPlaying()) {
                currentMusicIndex = musicIndex;
                break;
            }
        }
        PLAYLIST_NAME = PLAYLIST.get(currentMusicIndex)
                .getMusicDetail()
                .getPlaylistName();
        playback = false;
        changeMusic(currentMusicIndex);
    }

    private void changeMusic(int musicIndex) {
        if (playback)
            pause();
        file = PLAYLIST.get(musicIndex)
                .getMusicDetail()
                .getFile();
        clipTimePosition = PLAYLIST.get(musicIndex)
                .getMusicDetail()
                .getClipTimePosition();
    }

    public void play() {
        ResourceHandler.stopAllMusic();
        try {
            if (file.exists() && !playback) {
                toggleMusic(true);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.setMicrosecondPosition(clipTimePosition);
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (playback) {
            toggleMusic(false);
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();
            PLAYLIST.get(currentMusicIndex)
                    .getMusicDetail()
                    .setClipTimePosition(clipTimePosition);
        }
    }

    private void toggleMusic(boolean playback) {
        this.playback = playback;
        PLAYLIST.get(currentMusicIndex)
                .getMusicDetail()
                .setPlaying(this.playback);
    }

    public void previous() {
        if (isPrevious()) {
            boolean musicPlaying = playback;
            changeMusic(--currentMusicIndex);
            if (musicPlaying)
                play();
        }
    }

    public void next() {
        if (isNext()) {
            boolean musicPlaying = playback;
            changeMusic(++currentMusicIndex);
            if (musicPlaying)
                play();
        }
    }

    public String getPlaylistName() {
        return PLAYLIST_NAME;
    }

    public boolean isPrevious() {
        return currentMusicIndex > 0;
    }

    public boolean isNext() {
        return currentMusicIndex < (PLAYLIST.size() - 1);
    }

    public boolean isPlaying() {
        return playback;
    }

    public List<MusicDetailViewModel> getPlaylist() {
        return PLAYLIST;
    }
}