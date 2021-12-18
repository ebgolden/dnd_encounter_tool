package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.File;

@Builder
@Data
@AllArgsConstructor
@ToString(includeFieldNames=false)
public class MusicDetail {
    private String playlistName;
    @ToString.Exclude
    private long clipTimePosition;
    private boolean playing;
    @ToString.Exclude
    private File file;
}