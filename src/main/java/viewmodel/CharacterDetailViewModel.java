package viewmodel;

import model.CharacterDetail;

import java.io.File;

public class CharacterDetailViewModel {
    private final CharacterDetail CHARACTER_DETAIL;

    public CharacterDetailViewModel(CharacterDetail characterDetail) {
        CHARACTER_DETAIL = characterDetail;
        File file = CHARACTER_DETAIL.getFile();
        CHARACTER_DETAIL.setFile(((file != null) && file.exists()) ? file : null);
    }

    public CharacterDetail getCharacterDetail() {
        return CHARACTER_DETAIL;
    }

    public String toString() {
        //noinspection RegExpRedundantEscape
        return CHARACTER_DETAIL.toString()
                .replace(", ", ",")
                .split("[\\(\\)]")[1] + ","
                + ((CHARACTER_DETAIL.getFile() != null) ? CHARACTER_DETAIL.getFile().getAbsolutePath() : "-");
    }
}
