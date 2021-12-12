package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.File;

@Builder
@Data
@AllArgsConstructor
public class MusicDetail {
    private String playlistName, musicName;
    private long clipTimePosition;
    private boolean playing;
    private File file;

    public String toString() {
        return playlistName + ","
                + musicName + ","
                + clipTimePosition + ","
                + playing + ","
                + file.getAbsolutePath();
    }
}