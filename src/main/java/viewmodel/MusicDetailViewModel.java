package viewmodel;

import model.MusicDetail;

import java.io.File;

public class MusicDetailViewModel {
    private final MusicDetail MUSIC_DETAIL;

    public MusicDetailViewModel(MusicDetail musicDetail) {
        MUSIC_DETAIL = musicDetail;
        File file = MUSIC_DETAIL.getFile();
        MUSIC_DETAIL.setFile(((file != null) && file.exists()) ? file : null);
    }

    public MusicDetail getMusicDetail() {
        return MUSIC_DETAIL;
    }

    public String toString() {
        //noinspection RegExpRedundantEscape
        return MUSIC_DETAIL.toString()
                .replace(", ", ",")
                .split("[\\(\\)]")[1] + ","
                + ((MUSIC_DETAIL.getFile() != null) ? MUSIC_DETAIL.getFile().getAbsolutePath() : "-");
    }
}
