package controller;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class AudioFilePlayer {
    private File file;
    private Clip clip;
    private long clipTimePosition;
    private boolean playback;

    public AudioFilePlayer(File file) {
        this.file = file;
        clipTimePosition = 0;
        playback = false;
    }

    public void play() {
        try {
            if (file.exists() && !playback) {
                playback = true;
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

    public void play(long clipTimePosition) {
        this.clipTimePosition = clipTimePosition;
        play();
    }

    public void pause() {
        if (playback) {
            playback = false;
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();
        }
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isPlaying() {
        return playback;
    }

    public long getClipTimePosition() {
        return clipTimePosition;
    }
}